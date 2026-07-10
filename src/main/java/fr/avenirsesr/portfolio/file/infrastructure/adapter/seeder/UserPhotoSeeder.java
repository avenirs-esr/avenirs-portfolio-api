package fr.avenirsesr.portfolio.file.infrastructure.adapter.seeder;

import com.fasterxml.jackson.core.type.TypeReference;
import fr.avenirsesr.portfolio.common.language.domain.model.enums.ELanguage;
import fr.avenirsesr.portfolio.common.seeder.infrastructure.adapter.data.ESeederSource;
import fr.avenirsesr.portfolio.common.utils.FileReader;
import fr.avenirsesr.portfolio.common.validation.infrastructure.adapter.utils.ValidationUtils;
import fr.avenirsesr.portfolio.common.web.infrastructure.context.RequestContext;
import fr.avenirsesr.portfolio.common.web.infrastructure.context.RequestData;
import fr.avenirsesr.portfolio.file.domain.exception.FileStorageException;
import fr.avenirsesr.portfolio.file.domain.model.File;
import fr.avenirsesr.portfolio.file.domain.port.input.FileResourceService;
import fr.avenirsesr.portfolio.file.infrastructure.adapter.mapper.FileMapper;
import fr.avenirsesr.portfolio.file.infrastructure.adapter.model.FileEntity;
import fr.avenirsesr.portfolio.file.infrastructure.adapter.seeder.data.EUserPhotoSlot;
import fr.avenirsesr.portfolio.file.infrastructure.adapter.seeder.data.UserPhotoCreationData;
import fr.avenirsesr.portfolio.file.infrastructure.adapter.seeder.fake.FakeUserPhoto;
import fr.avenirsesr.portfolio.user.domain.port.output.repository.StaffRepository;
import fr.avenirsesr.portfolio.user.domain.port.output.repository.StudentRepository;
import fr.avenirsesr.portfolio.user.domain.port.output.repository.UserRepository;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.model.StaffEntity;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.model.StudentEntity;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.model.UserEntity;
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
public class UserPhotoSeeder {
  private static final String PATH_FILE = "seeder/user-photos.json";
  private final FileReader fileReader;
  private final UserRepository userRepository;
  private final StaffRepository staffRepository;
  private final StudentRepository studentRepository;
  private final FileResourceService fileResourceService;

  @Value("${seeder.source}")
  private ESeederSource seederSource;

  public UserPhotoSeeder(
      FileReader fileReader,
      UserRepository userRepository,
      StaffRepository staffRepository,
      StudentRepository studentRepository,
      @Qualifier("MockFileResourceService") FileResourceService fileResourceService) {
    this.fileReader = fileReader;
    this.userRepository = userRepository;
    this.staffRepository = staffRepository;
    this.studentRepository = studentRepository;
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

        var file =
            fileResourceService.upload(
                data.fileName(), data.fileType().getMimeType(), data.fileSize(), null, true);
        linkPhotoToOwner(data.userId(), data.photoType(), file);
        userPhotos.add(file);
      } catch (FileStorageException e) {
        log.error("Error uploading user photo", e);
      }
    }

    log.info("✔ {} user photos created", userPhotos.size());
    return userPhotos.stream().map(FileMapper.INSTANCE::fromDomain).toList();
  }

  private void linkPhotoToOwner(UUID userId, EUserPhotoSlot photoType, File file) {
    switch (photoType) {
      case STAFF_PROFILE_PICTURE ->
          staffRepository
              .findById(userId)
              .ifPresent(
                  staff -> {
                    staff.setProfilePicture(file);
                    staffRepository.save(staff);
                  });
      case STAFF_COVER_PICTURE ->
          staffRepository
              .findById(userId)
              .ifPresent(
                  staff -> {
                    staff.setCoverPicture(file);
                    staffRepository.save(staff);
                  });
      case STUDENT_PROFILE_PICTURE ->
          studentRepository
              .findById(userId)
              .ifPresent(
                  student -> {
                    student.setProfilePicture(file);
                    studentRepository.save(student);
                  });
      case STUDENT_COVER_PICTURE ->
          studentRepository
              .findById(userId)
              .ifPresent(
                  student -> {
                    student.setCoverPicture(file);
                    studentRepository.save(student);
                  });
    }
  }

  private List<UserPhotoCreationData> buildFakePhotos(List<UserEntity> users) {
    return users.stream().map(FakeUserPhoto::of).toList();
  }
}
