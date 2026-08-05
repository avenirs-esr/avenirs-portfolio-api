package fr.avenirsesr.portfolio.file.infrastructure.adapter.seeder;

import com.fasterxml.jackson.core.type.TypeReference;
import fr.avenirsesr.portfolio.common.seeder.infrastructure.adapter.data.ESeederSource;
import fr.avenirsesr.portfolio.common.utils.FileReader;
import fr.avenirsesr.portfolio.common.validation.infrastructure.adapter.utils.ValidationUtils;
import fr.avenirsesr.portfolio.file.domain.exception.FileStorageException;
import fr.avenirsesr.portfolio.file.domain.model.File;
import fr.avenirsesr.portfolio.file.domain.port.input.FileResourceService;
import fr.avenirsesr.portfolio.file.infrastructure.adapter.mapper.FileMapper;
import fr.avenirsesr.portfolio.file.infrastructure.adapter.model.FileEntity;
import fr.avenirsesr.portfolio.file.infrastructure.adapter.seeder.data.ActivityFileCreationData;
import fr.avenirsesr.portfolio.file.infrastructure.adapter.seeder.fake.FakeActivityFile;
import fr.avenirsesr.portfolio.staff.activity.domain.port.output.repository.ActivityDraftRepository;
import fr.avenirsesr.portfolio.staff.activity.domain.port.output.repository.ActivityRepository;
import fr.avenirsesr.portfolio.staff.activity.infrastructure.adapter.model.ActivityDraftEntity;
import fr.avenirsesr.portfolio.staff.activity.infrastructure.adapter.model.ActivityEntity;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
public class ActivityFileSeeder {
  private static final String PATH_FILE = "seeder/activity-files.json";
  private final FileReader fileReader;
  private final FileResourceService fileResourceService;
  private final ActivityDraftRepository activityDraftRepository;
  private final ActivityRepository activityRepository;

  @Value("${seeder.source}")
  private ESeederSource seederSource;

  public ActivityFileSeeder(
      FileReader fileReader,
      @Qualifier("MockFileResourceService") FileResourceService fileResourceService,
      ActivityDraftRepository activityDraftRepository,
      ActivityRepository activityRepository) {
    this.fileReader = fileReader;
    this.fileResourceService = fileResourceService;
    this.activityDraftRepository = activityDraftRepository;
    this.activityRepository = activityRepository;
  }

  @Transactional
  public List<FileEntity> seed(List<ActivityDraftEntity> drafts, List<ActivityEntity> activities) {
    ValidationUtils.requireNonEmpty(drafts, "drafts cannot be empty");
    log.info("Seeding activity files...");

    List<ActivityFileCreationData> creationData =
        switch (seederSource) {
          case CSV -> fileReader.readJSON(PATH_FILE, new TypeReference<>() {});
          case FAKER ->
              buildFakeActivityFile(
                  Stream.concat(
                          drafts.stream().map(ActivityDraftEntity::getId),
                          activities.stream().map(ActivityEntity::getId))
                      .toList());
        };

    List<File> activityFiles = new ArrayList<>();

    for (ActivityFileCreationData data : creationData) {
      try {
        var file =
            fileResourceService.upload(
                data.fileName(), data.fileType().getMimeType(), data.fileSize(), null, true);
        linkFileToActivityOrDraft(data.activityId(), file);
        activityFiles.add(file);
      } catch (FileStorageException e) {
        log.error("Error uploading activity file", e);
      }
    }

    log.info("✔ {} activity files created", activityFiles.size());
    return activityFiles.stream().map(FileMapper.INSTANCE::fromDomain).toList();
  }

  private void linkFileToActivityOrDraft(UUID activityId, File file) {
    activityDraftRepository
        .findById(activityId)
        .ifPresentOrElse(
            draft -> {
              draft.addFile(file);
              activityDraftRepository.save(draft);
            },
            () ->
                activityRepository
                    .findById(activityId)
                    .ifPresent(
                        activity -> {
                          activity.addFile(file);
                          activityRepository.save(activity);
                        }));
  }

  private List<ActivityFileCreationData> buildFakeActivityFile(List<UUID> activityIds) {
    return activityIds.stream().map(FakeActivityFile::create).toList();
  }
}
