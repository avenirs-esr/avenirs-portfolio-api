package fr.avenirsesr.portfolio.file.domain.model.enums;

import java.util.EnumSet;
import java.util.Set;

public enum EFileCategory {
  ACTIVITY_BANNER(imageFileTypes()),
  ACTIVITY_FILE(EnumSet.allOf(EFileType.class)),
  STAFF_COVER_PICTURE(imageFileTypes()),
  STAFF_PROFILE_PICTURE(imageFileTypes()),
  STUDENT_COVER_PICTURE(imageFileTypes()),
  STUDENT_PROFILE_PICTURE(imageFileTypes()),
  TRACE_ATTACHMENT(EnumSet.allOf(EFileType.class));

  private final Set<EFileType> allowedFileTypes;

  EFileCategory(Set<EFileType> allowedFileTypes) {
    this.allowedFileTypes = EnumSet.copyOf(allowedFileTypes);
  }

  public boolean allows(EFileType fileType) {
    return allowedFileTypes.contains(fileType);
  }

  private static EnumSet<EFileType> imageFileTypes() {
    return EnumSet.of(
        EFileType.PNG, EFileType.JPEG, EFileType.GIF, EFileType.WEBP, EFileType.PJPEG);
  }
}
