package fr.avenirsesr.portfolio.file.infrastructure.adapter.seeder;

import com.fasterxml.jackson.core.type.TypeReference;
import fr.avenirsesr.portfolio.common.data.domain.model.enums.EUserCategory;
import fr.avenirsesr.portfolio.common.language.domain.model.enums.ELanguage;
import fr.avenirsesr.portfolio.common.seeder.infrastructure.adapter.data.ESeederSource;
import fr.avenirsesr.portfolio.common.utils.FileReader;
import fr.avenirsesr.portfolio.common.validation.infrastructure.adapter.utils.ValidationUtils;
import fr.avenirsesr.portfolio.common.web.infrastructure.context.RequestContext;
import fr.avenirsesr.portfolio.common.web.infrastructure.context.RequestData;
import fr.avenirsesr.portfolio.file.domain.exception.FileStorageException;
import fr.avenirsesr.portfolio.file.domain.model.EFileCategory;
import fr.avenirsesr.portfolio.file.domain.model.EUserPhotoType;
import fr.avenirsesr.portfolio.file.domain.model.File;
import fr.avenirsesr.portfolio.file.domain.port.input.FileResourceService;
import fr.avenirsesr.portfolio.file.infrastructure.adapter.mapper.FileMapper;
import fr.avenirsesr.portfolio.file.infrastructure.adapter.model.FileEntity;
import fr.avenirsesr.portfolio.file.infrastructure.adapter.seeder.data.UserPhotoCreationData;
import fr.avenirsesr.portfolio.file.infrastructure.adapter.seeder.fake.FakeUserPhoto;
import fr.avenirsesr.portfolio.user.domain.port.output.repository.UserRepository;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.model.StaffEntity;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.model.StudentEntity;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.model.UserEntity;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.Nullable;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
public class UserPhotoSeeder {
  private static final String PATH_FILE = "seeder/user-photos.json";
  private final FileReader fileReader;
  private final UserRepository userRepository;
  private final FileResourceService fileResourceService;

  @Value("${seeder.source}")
  private ESeederSource seederSource;

  public UserPhotoSeeder(
      FileReader fileReader,
      UserRepository userRepository,
      @Qualifier("MockFileResourceService") FileResourceService fileResourceService) {
    this.fileReader = fileReader;
    this.userRepository = userRepository;
    this.fileResourceService = fileResourceService;
  }

  @Transactional
  public List<FileEntity> seed(List<StudentEntity> students, List<StaffEntity> staffs) {
    ValidationUtils.requireNonEmpty(students, "students cannot be empty");
    ValidationUtils.requireNonEmpty(staffs, "staffs cannot be empty");
    log.info("Seeding user photos...");

    List<UserPhotoCreationData> creationData =
        switch (seederSource) {
          case CSV -> fileReader.readJSON(PATH_FILE, new TypeReference<>() {});
          case FAKER ->
              buildFakePhotos(
                  Stream.concat(
                          students.stream().map(StudentEntity::getUser),
                          staffs.stream().map(StaffEntity::getUser))
                      .toList());
        };

    List<File> userPhotos = new ArrayList<>();

    for (UserPhotoCreationData data : creationData) {
      try {
        var user = userRepository.findById(data.userId());
        RequestContext.set(new RequestData(user, ELanguage.FRENCH));
        EFileCategory fileCategory = getEFileCategory(data);

        userPhotos.add(
            fileResourceService.upload(
                data.userId(),
                fileCategory,
                data.fileName(),
                data.fileType().getMimeType(),
                data.fileSize(),
                null));
      } catch (FileStorageException e) {
        log.error("Error uploading user photo", e);
      }
    }

    log.info("✔ {} user photos created", userPhotos.size());
    return userPhotos.stream().map(FileMapper.INSTANCE::fromDomain).toList();
  }

  private static @Nullable EFileCategory getEFileCategory(UserPhotoCreationData data) {
    EFileCategory fileCategory = null;
    if (data.photoType() == EUserPhotoType.PROFILE && data.userCategory() == EUserCategory.STUDENT)
      fileCategory = EFileCategory.STUDENT_PROFILE_PICTURE;
    else if (data.photoType() == EUserPhotoType.PROFILE
        && data.userCategory() == EUserCategory.STAFF)
      fileCategory = EFileCategory.STAFF_PROFILE_PICTURE;
    else if (data.photoType() == EUserPhotoType.COVER
        && data.userCategory() == EUserCategory.STUDENT)
      fileCategory = EFileCategory.STUDENT_COVER_PICTURE;
    else if (data.photoType() == EUserPhotoType.COVER && data.userCategory() == EUserCategory.STAFF)
      fileCategory = EFileCategory.STAFF_COVER_PICTURE;
    return fileCategory;
  }

  private List<UserPhotoCreationData> buildFakePhotos(List<UserEntity> users) {
    List<UserPhotoCreationData> fakePhotos = new ArrayList<>();
    for (UserEntity user : users) {
      var fakePhoto = FakeUserPhoto.of(user).toEntity();
      fakePhotos.add(
          new UserPhotoCreationData(
              fakePhoto.getElementId(),
              switch (fakePhoto.getFileCategory()) {
                case STUDENT_COVER_PICTURE, STUDENT_PROFILE_PICTURE -> EUserCategory.STUDENT;
                case STAFF_COVER_PICTURE, STAFF_PROFILE_PICTURE -> EUserCategory.STAFF;
                default -> throw new IllegalStateException("Unexpected value: " + fakePhoto);
              },
              switch (fakePhoto.getFileCategory()) {
                case STAFF_PROFILE_PICTURE, STUDENT_PROFILE_PICTURE -> EUserPhotoType.PROFILE;
                case STAFF_COVER_PICTURE, STUDENT_COVER_PICTURE -> EUserPhotoType.COVER;
                default -> throw new IllegalStateException("Unexpected value: " + fakePhoto);
              },
              fakePhoto.getFileName(),
              fakePhoto.getFileType(),
              fakePhoto.getSize()));
    }
    return fakePhotos;
  }
}
