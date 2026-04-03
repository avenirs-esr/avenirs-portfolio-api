package fr.avenirsesr.portfolio.file.domain.service;

import fr.avenirsesr.portfolio.common.data.domain.model.User;
import fr.avenirsesr.portfolio.common.data.domain.model.enums.EUserCategory;
import fr.avenirsesr.portfolio.common.security.domain.exception.UserNotAuthorizedException;
import fr.avenirsesr.portfolio.file.domain.data.FileData;
import fr.avenirsesr.portfolio.file.domain.exception.FileNotFoundException;
import fr.avenirsesr.portfolio.file.domain.exception.FileSizeTooBigException;
import fr.avenirsesr.portfolio.file.domain.exception.FileTypeNotSupportedException;
import fr.avenirsesr.portfolio.file.domain.model.EUserPhotoType;
import fr.avenirsesr.portfolio.file.domain.model.UserPhoto;
import fr.avenirsesr.portfolio.file.domain.model.shared.EFileType;
import fr.avenirsesr.portfolio.file.domain.model.shared.FileResource;
import fr.avenirsesr.portfolio.file.domain.port.input.UserResourceService;
import fr.avenirsesr.portfolio.file.domain.port.output.repository.UserPhotoRepository;
import fr.avenirsesr.portfolio.file.domain.port.output.service.FileStorageService;
import fr.avenirsesr.portfolio.file.infrastructure.configuration.FileStorageConstants;
import fr.avenirsesr.portfolio.shared.domain.port.input.LoggedInUserService;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
public class UserResourceServiceImpl implements UserResourceService {
  public static final List<EFileType> ALLOWED_FILE_TYPES =
      List.of(EFileType.PNG, EFileType.JPEG, EFileType.GIF, EFileType.WEBP, EFileType.PJPEG);

  private final FileStorageService fileStorageService;
  private final UserPhotoRepository userPhotoRepository;
  private final LoggedInUserService loggedInUserService;

  @Override
  public FileData getUserPhotoUrl(User user, EUserCategory userCategory, EUserPhotoType type) {

    Optional<UserPhoto> userPhoto =
        userPhotoRepository.findActiveByUser(user, userCategory, type).stream().findFirst();

    return new FileData(
        userPhoto.map(UserPhoto::getId),
        userPhoto.map(UserPhoto::getName),
        userPhoto
            .map(photo -> FileStorageConstants.PHOTO_ENDPOINT_PREFIX + "/" + photo.getId())
            .orElse(
                switch (type) {
                  case PROFILE -> FileStorageConstants.DEFAULT_PROFILE_FILE_URL;
                  case COVER -> FileStorageConstants.DEFAULT_COVER_FILE_URL;
                }));
  }

  @Override
  public byte[] fetchContent(UserPhoto userPhoto) throws IOException {
    return fileStorageService.get(userPhoto.getUri());
  }

  @Override
  public UserPhoto getUserPhotoById(UUID fileId) {
    return userPhotoRepository.findById(fileId).orElseThrow(FileNotFoundException::new);
  }

  @Override
  public UserPhoto uploadPhoto(
      EUserCategory category,
      EUserPhotoType photoType,
      String fileName,
      String mimeType,
      long size,
      byte[] content)
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

    var allPhotos = userPhotoRepository.findAllByUser(loggedInUser, category, photoType);
    var version =
        allPhotos.stream().map(UserPhoto::getVersion).max(Integer::compareTo).orElse(0) + 1;
    allPhotos.forEach(a -> a.setActiveVersion(false));

    var photo =
        UserPhoto.create(
            fileResource.id(),
            fileResource.fileName(),
            fileResource.fileType(),
            fileResource.size(),
            version,
            true,
            uri,
            loggedInUser,
            loggedInUser,
            category,
            photoType);

    userPhotoRepository.saveAll(Stream.concat(allPhotos.stream(), Stream.of(photo)).toList());
    log.info("New user photo saved: {}", photo);

    return photo;
  }

  @Override
  public void deletePhoto(UUID fileId) {
    var loggedInUser = loggedInUserService.getLoggedInUser();
    var fileResource =
        userPhotoRepository
            .findById(fileId)
            .orElseThrow(
                () -> {
                  log.error("No user photo with id {} found", fileId);
                  return new FileNotFoundException();
                });
    if (!fileResource.getUser().equals(loggedInUser)) {
      throw new UserNotAuthorizedException();
    }

    try {
      userPhotoRepository.removeFromDatabase(fileResource);
      fileStorageService.delete(fileResource.getId());
      log.info("User photo deleted: {}", fileResource);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
