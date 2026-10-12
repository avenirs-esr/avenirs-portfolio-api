package fr.avenirsesr.portfolio.user.infrastructure.adapter.service;

import fr.avenirsesr.portfolio.common.file.domain.port.output.service.FileUploaderProvider;
import fr.avenirsesr.portfolio.shared.domain.port.input.LoggedInUserService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LoggedInUserFileUploaderProvider implements FileUploaderProvider {
  private final LoggedInUserService loggedInUserService;

  @Override
  public UUID currentUploaderId() {
    return loggedInUserService.getLoggedInUser().getId();
  }
}
