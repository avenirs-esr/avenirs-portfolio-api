package fr.avenirsesr.portfolio.file.domain.service;

import fr.avenirsesr.portfolio.activity.domain.model.Activity;
import fr.avenirsesr.portfolio.file.domain.exception.FileSizeTooBigException;
import fr.avenirsesr.portfolio.file.domain.exception.FileTypeNotSupportedException;
import fr.avenirsesr.portfolio.file.domain.model.ActivityBanner;
import fr.avenirsesr.portfolio.file.domain.model.shared.EFileType;
import fr.avenirsesr.portfolio.file.domain.model.shared.FileResource;
import fr.avenirsesr.portfolio.file.domain.port.input.ActivityResourceService;
import fr.avenirsesr.portfolio.file.domain.port.output.repository.ActivityBannerRepository;
import fr.avenirsesr.portfolio.file.domain.port.output.service.FileStorageService;
import fr.avenirsesr.portfolio.shared.domain.port.input.LoggedInUserService;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
public class ActivityResourceServiceImpl implements ActivityResourceService {
  public static final List<EFileType> ALLOWED_FILE_TYPES =
      List.of(EFileType.PNG, EFileType.JPEG, EFileType.GIF, EFileType.WEBP, EFileType.PJPEG);

  private final FileStorageService fileStorageService;
  private final LoggedInUserService loggedInUserService;
  private final ActivityBannerRepository activityBannerRepository;

  @Override
  public ActivityBanner uploadBannerFor(
      Activity activity, String fileName, String mimeType, long size, byte[] content)
      throws IOException {
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
}
