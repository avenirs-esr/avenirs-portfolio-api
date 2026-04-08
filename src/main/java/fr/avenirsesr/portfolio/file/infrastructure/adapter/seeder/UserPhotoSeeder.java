package fr.avenirsesr.portfolio.file.infrastructure.adapter.seeder;

import com.fasterxml.jackson.core.type.TypeReference;
import fr.avenirsesr.portfolio.common.language.domain.model.enums.ELanguage;
import fr.avenirsesr.portfolio.common.seeder.infrastructure.adapter.data.ESeederSource;
import fr.avenirsesr.portfolio.common.validation.infrastructure.adapter.utils.ValidationUtils;
import fr.avenirsesr.portfolio.common.web.infrastructure.context.RequestContext;
import fr.avenirsesr.portfolio.common.web.infrastructure.context.RequestData;
import fr.avenirsesr.portfolio.file.domain.model.UserPhoto;
import fr.avenirsesr.portfolio.file.domain.port.input.UserResourceService;
import fr.avenirsesr.portfolio.file.infrastructure.adapter.mapper.UserPhotoMapper;
import fr.avenirsesr.portfolio.file.infrastructure.adapter.model.UserPhotoEntity;
import fr.avenirsesr.portfolio.file.infrastructure.adapter.seeder.data.UserPhotoCreationData;
import fr.avenirsesr.portfolio.file.infrastructure.adapter.seeder.fake.FakeUserPhoto;
import fr.avenirsesr.portfolio.shared.infrastructure.utils.FileReader;
import fr.avenirsesr.portfolio.user.domain.port.output.repository.UserRepository;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.model.StaffEntity;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.model.StudentEntity;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.model.UserEntity;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
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
  private final UserResourceService userResourceService;

  @Value("${seeder.source}")
  private ESeederSource seederSource;

  public UserPhotoSeeder(
      FileReader fileReader,
      UserRepository userRepository,
      @Qualifier("MockUserResourceService") UserResourceService userResourceService) {
    this.fileReader = fileReader;
    this.userRepository = userRepository;
    this.userResourceService = userResourceService;
  }

  @Transactional
  public List<UserPhotoEntity> seed(List<StudentEntity> students, List<StaffEntity> staffs) {
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

    List<UserPhoto> userPhotos = new ArrayList<>();

    for (UserPhotoCreationData data : creationData) {
      try {
        var user = userRepository.findById(data.userId());
        RequestContext.set(new RequestData(user, ELanguage.FRENCH));
        userPhotos.add(
            userResourceService.uploadPhoto(
                data.userCategory(),
                data.photoType(),
                data.fileName(),
                data.fileType().getMimeType(),
                data.fileSize(),
                null));
      } catch (IOException e) {
        log.error("Error uploading user photo", e);
      }
    }

    log.info("✔ {} user photos created", userPhotos.size());
    return userPhotos.stream().map(UserPhotoMapper.INSTANCE::fromDomain).toList();
  }

  private List<UserPhotoCreationData> buildFakePhotos(List<UserEntity> users) {
    List<UserPhotoCreationData> fakePhotos = new ArrayList<>();
    for (UserEntity user : users) {
      var fakePhoto = FakeUserPhoto.of(user).toEntity();
      fakePhotos.add(
          new UserPhotoCreationData(
              fakePhoto.getUser().getId(),
              fakePhoto.getUserCategory(),
              fakePhoto.getUserPhotoType(),
              fakePhoto.getName(),
              fakePhoto.getFileType(),
              fakePhoto.getSize()));
    }
    return fakePhotos;
  }
}
