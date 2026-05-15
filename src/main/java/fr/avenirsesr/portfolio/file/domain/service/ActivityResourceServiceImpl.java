package fr.avenirsesr.portfolio.file.domain.service;

import fr.avenirsesr.portfolio.activity.domain.model.Activity;
import fr.avenirsesr.portfolio.file.domain.data.FileData;
import fr.avenirsesr.portfolio.file.domain.exception.FileNotFoundException;
import fr.avenirsesr.portfolio.file.domain.exception.FileSizeTooBigException;
import fr.avenirsesr.portfolio.file.domain.exception.FileTypeNotSupportedException;
import fr.avenirsesr.portfolio.file.domain.model.ActivityBanner;
import fr.avenirsesr.portfolio.file.domain.model.shared.EFileType;
import fr.avenirsesr.portfolio.file.domain.model.shared.FileResource;
import fr.avenirsesr.portfolio.file.domain.port.input.ActivityResourceService;
import fr.avenirsesr.portfolio.file.domain.port.output.repository.ActivityBannerRepository;
import fr.avenirsesr.portfolio.file.domain.port.output.service.FileStorageService;
import fr.avenirsesr.portfolio.file.infrastructure.configuration.FileStorageConstants;
import fr.avenirsesr.portfolio.shared.domain.port.input.LoggedInUserService;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
public class ActivityResourceServiceImpl implements ActivityResourceService {
  private static final String ACTIVITY_BANNER_PATH = "/storage/activities/";
  public static final List<EFileType> ALLOWED_FILE_TYPES =
      List.of(EFileType.PNG, EFileType.JPEG, EFileType.GIF, EFileType.WEBP, EFileType.PJPEG);

  private final FileStorageService fileStorageService;
  private final LoggedInUserService loggedInUserService;
  private final ActivityBannerRepository activityBannerRepository;

  @Override
  public ActivityBanner uploadBannerFor(
      Activity activity, String fileName, String mimeType, long size, byte[] content) {
    var loggedInUser = loggedInUserService.getLoggedInUser();

    var fileResource =
        new FileResource(
            UUID.randomUUID(), fileName, EFileType.fromMimeType(mimeType), size, content);

    if (!ALLOWED_FILE_TYPES.contains(fileResource.fileType())) {
      throw new FileTypeNotSupportedException();
    }
    if (fileResource.fileType().getSizeLimit().isLessThan(size)) {
      throw new FileSizeTooBigException();
    }

    var uri = fileStorageService.upload(fileResource);

    var allBanners = activityBannerRepository.findAllByActivity(activity);
    var version =
        allBanners.stream().map(ActivityBanner::getVersion).max(Integer::compareTo).orElse(0) + 1;
    allBanners.forEach(a -> a.setActiveVersion(false));

    var banner =
        ActivityBanner.create(
            fileResource.id(),
            fileName,
            fileResource.fileType(),
            fileResource.size(),
            version,
            uri,
            loggedInUser,
            activity);

    activityBannerRepository.save(banner);
    log.info("New banner {} saved for activity: {}", banner, activity);
    return banner;
  }

  @Override
  public ActivityBanner getActivityFile(UUID fileID) {
    return activityBannerRepository.findById(fileID).orElseThrow(FileNotFoundException::new);
  }

  @Override
  public byte[] fetchContent(ActivityBanner activityFile) {
    return fileStorageService.get(activityFile.getUri());
  }

  @Override
  public FileData getActivityBanner(Activity activity) {
    var banner = activityBannerRepository.findActiveByActivity(activity);
    return banner
        .map(
            b ->
                new FileData(
                    Optional.of(b.getId()),
                    Optional.of(b.getFileName()),
                    ACTIVITY_BANNER_PATH + b.getId()))
        .orElse(
            new FileData(
                Optional.empty(), Optional.empty(), FileStorageConstants.DEFAULT_COVER_FILE_URL));
  }
}
