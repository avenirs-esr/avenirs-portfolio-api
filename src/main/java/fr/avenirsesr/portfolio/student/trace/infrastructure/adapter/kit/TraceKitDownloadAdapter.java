package fr.avenirsesr.portfolio.student.trace.infrastructure.adapter.kit;

import fr.avenirsesr.portfolio.file.domain.model.File;
import fr.avenirsesr.portfolio.shared.domain.port.input.LoggedInUserService;
import fr.avenirsesr.portfolio.student.kit.domain.model.KitMediaEntry;
import fr.avenirsesr.portfolio.student.kit.domain.port.output.KitDownloadPort;
import fr.avenirsesr.portfolio.student.trace.domain.filter.TraceFilter;
import fr.avenirsesr.portfolio.student.trace.domain.model.Trace;
import fr.avenirsesr.portfolio.student.trace.domain.port.output.repository.TraceRepository;
import fr.avenirsesr.portfolio.user.domain.model.Student;
import java.util.List;
import java.util.stream.Stream;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class TraceKitDownloadAdapter implements KitDownloadPort {
  private final LoggedInUserService loggedInUserService;
  private final TraceRepository traceRepository;

  @Override
  public List<KitMediaEntry> getMedia() {
    // Requires the same authorization as the TraceController::downloadAttachment endpoint, to keep
    // access consistent.
    if (!hasRequiredAuthority("trace:list:own")) {
      return List.of();
    }

    // TODO: Remove the explicit `Student` relationship and switch to a role and/or permission check
    // (see #2473)
    Student student = loggedInUserService.getLoggedInStudent();
    TraceFilter filter = new TraceFilter(null, null, null, true);
    List<Trace> traces = traceRepository.findAll(student, filter);
    return traces.stream().flatMap(this::toEntry).toList();
  }

  // TODO: Create generic helpers to check the permissions/roles of the currently logged-in user
  // (see #2473)
  private boolean hasRequiredAuthority(String authority) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    return authentication != null
        && authentication.getAuthorities().stream()
            .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals(authority));
  }

  private Stream<KitMediaEntry> toEntry(Trace trace) {
    Stream<File> attachment = trace.getAttachment().stream();
    return attachment.map(file -> new KitMediaEntry(file, List.of("traces", trace.getTitle())));
  }
}
