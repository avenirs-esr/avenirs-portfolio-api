package fr.avenirsesr.portfolio.staff.activity.infrastructure.adapter.seeder;

import com.fasterxml.jackson.core.type.TypeReference;
import fr.avenirsesr.portfolio.common.language.domain.model.enums.ELanguage;
import fr.avenirsesr.portfolio.common.seeder.domain.model.enums.ESeedMode;
import fr.avenirsesr.portfolio.common.utils.FileReader;
import fr.avenirsesr.portfolio.common.web.infrastructure.context.RequestContext;
import fr.avenirsesr.portfolio.common.web.infrastructure.context.RequestData;
import fr.avenirsesr.portfolio.file.domain.model.File;
import fr.avenirsesr.portfolio.file.domain.port.input.FileResourceService;
import fr.avenirsesr.portfolio.staff.activity.domain.model.Activity;
import fr.avenirsesr.portfolio.staff.activity.domain.model.SystemActivities;
import fr.avenirsesr.portfolio.staff.activity.domain.port.input.ActivityService;
import fr.avenirsesr.portfolio.staff.activity.domain.port.output.repository.ActivityRepository;
import fr.avenirsesr.portfolio.staff.activity.infrastructure.adapter.seeder.data.SystemActivityCreationData;
import fr.avenirsesr.portfolio.user.domain.model.Staff;
import fr.avenirsesr.portfolio.user.domain.port.input.StaffService;
import fr.avenirsesr.portfolio.user.domain.port.input.StudentService;
import fr.avenirsesr.portfolio.user.domain.port.input.UserService;
import fr.avenirsesr.portfolio.user.domain.port.output.repository.StaffRepository;
import fr.avenirsesr.portfolio.user.domain.port.output.repository.UserPrincipalRepository;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class SystemActivitySeeder {
  public static final String TABLE_NAME = "system-activities";
  private static final String RESOURCE_DIR = "seeder/system/discovery-cofolio/";
  private static final String PATH_FILE = RESOURCE_DIR + "activity.json";

  private final FileReader fileReader;
  private final UserService userService;
  private final StaffService staffService;
  private final UserPrincipalRepository userPrincipalRepository;
  private final StudentService studentService;
  private final StaffRepository staffRepository;
  private final ActivityService activityService;
  private final ActivityRepository activityRepository;
  private final FileResourceService fileResourceService;

  public String tableName() {
    return TABLE_NAME;
  }

  @Transactional
  public int seedAlone(ESeedMode mode) {
    log.info("Seeding {} with mode {}...", TABLE_NAME, mode);

    var data =
        fileReader
            .readJSON(PATH_FILE, new TypeReference<List<SystemActivityCreationData>>() {})
            .getFirst();

    var existing = activityRepository.findById(SystemActivities.DISCOVERY_COFOLIO_ID);
    if (existing.isPresent()) {
      if (mode == ESeedMode.INSERT_ONLY) {
        log.info("✔ {} already present, nothing to do", data.title());
        return 0;
      }
      overwrite(existing.get(), data);
      return 1;
    }

    var author = getOrCreateAuthor(data.author());
    create(author, data);

    log.info("✔ {} created", data.title());
    return 1;
  }

  private Staff getOrCreateAuthor(SystemActivityCreationData.Author author) {
    var user =
        userPrincipalRepository
            .findByEppn(author.eppn())
            .orElseGet(
                () ->
                    userService.createUser(
                        UUID.randomUUID(),
                        author.firstName(),
                        author.lastName(),
                        author.email(),
                        author.eppn()));
    if (!studentService.existsById(user.getId())) {
      studentService.createStudent(user.getId(), author.email(), null);
    }
    return staffRepository
        .findById(user.getId())
        .orElseGet(() -> staffService.createStaff(user.getId(), author.email(), null));
  }

  private void create(Staff author, SystemActivityCreationData data) {
    var activity =
        activityService.create(
            SystemActivities.DISCOVERY_COFOLIO_ID,
            author,
            data.title(),
            data.thematic(),
            data.summary(),
            data.description(),
            data.recommendedCompletionContexts(),
            null,
            null,
            data.enableReflection(),
            data.traceAllowedAssociations(),
            data.feedbackAllowedIterations(),
            data.links());

    RequestContext.set(new RequestData(Optional.of(author.getUser()), ELanguage.FRENCH));
    try {
      activity.setBanner(upload(data.bannerFileName(), data.bannerMimeType(), false));
      activity.addFile(upload(data.attachmentFileName(), data.attachmentMimeType(), true));
      activityRepository.save(activity);
    } finally {
      RequestContext.clear();
    }
  }

  private void overwrite(Activity activity, SystemActivityCreationData data) {
    activity.setTitle(data.title());
    activity.setThematic(data.thematic());
    activity.setSummary(data.summary());
    activity.setDescription(data.description());
    activity.setRecommendedCompletionContexts(data.recommendedCompletionContexts());
    activity.setEnableReflection(data.enableReflection());
    activity.setTraceAllowedAssociations(data.traceAllowedAssociations());
    activity.setFeedbackAllowedIterations(data.feedbackAllowedIterations());
    activity.setLinks(data.links());
    activityRepository.save(activity);
  }

  private File upload(String fileName, String mimeType, boolean isRestricted) {
    try (var in = new ClassPathResource(RESOURCE_DIR + fileName).getInputStream()) {
      var content = in.readAllBytes();
      return fileResourceService.upload(fileName, mimeType, content.length, content, isRestricted);
    } catch (IOException e) {
      throw new UncheckedIOException("Cannot read " + fileName, e);
    }
  }
}
