package fr.avenirsesr.portfolio.file.infrastructure.adapter.seeder.fake;

import fr.avenirsesr.portfolio.common.data.domain.model.enums.EUserCategory;
import fr.avenirsesr.portfolio.common.seeder.domain.port.output.SharedDataGenerator;
import fr.avenirsesr.portfolio.common.seeder.infrastructure.adapter.data.DataGeneratorProvider;
import fr.avenirsesr.portfolio.file.domain.model.enums.EFileType;
import fr.avenirsesr.portfolio.file.domain.port.output.seeder.FileDataGenerator;
import fr.avenirsesr.portfolio.file.infrastructure.adapter.seeder.data.EUserPhotoSlot;
import fr.avenirsesr.portfolio.file.infrastructure.adapter.seeder.data.UserPhotoCreationData;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.model.UserEntity;
import java.util.List;

public class FakeUserPhoto {
  private static final DataGeneratorProvider<SharedDataGenerator> dataGenerator =
      new DataGeneratorProvider<SharedDataGenerator>()
          .init(FakeUserPhoto.class, SharedDataGenerator.class);
  private static final DataGeneratorProvider<FileDataGenerator> fileDataGenerator =
      new DataGeneratorProvider<FileDataGenerator>()
          .init(FakeUserPhoto.class, FileDataGenerator.class);

  private FakeUserPhoto() {}

  public static UserPhotoCreationData of(UserEntity user) {
    var fileType = dataGenerator.with("file-type").pickIn(List.of(EFileType.PNG, EFileType.JPEG));
    var slot =
        dataGenerator
            .with("category")
            .pickIn(
                List.of(
                    EUserPhotoSlot.STUDENT_COVER_PICTURE,
                    EUserPhotoSlot.STUDENT_PROFILE_PICTURE,
                    EUserPhotoSlot.STAFF_COVER_PICTURE,
                    EUserPhotoSlot.STAFF_PROFILE_PICTURE));
    var userCategory =
        switch (slot) {
          case STUDENT_COVER_PICTURE, STUDENT_PROFILE_PICTURE -> EUserCategory.STUDENT;
          case STAFF_COVER_PICTURE, STAFF_PROFILE_PICTURE -> EUserCategory.STAFF;
        };
    return new UserPhotoCreationData(
        user.getId(),
        userCategory,
        slot,
        fileDataGenerator.with("file-name").fileName(fileType),
        fileType,
        dataGenerator.with("fileSize").number((int) fileType.getSizeLimit().bytes()));
  }
}
