package fr.avenirsesr.portfolio.shared.domain.model.enums;

import lombok.Getter;

@Getter
public enum EErrorCode {
  USER_NOT_FOUND("User not found"),
  USER_ALREADY_EXISTS("User already exists"),
  USER_NOT_AUTHORIZED("User not authorized"),
  USER_CATEGORY_NOT_FOUND("User category not found"),
  BAD_IMAGE_SIZE("Image pageSize is not valid"),
  BAD_IMAGE_TYPE("Image type is not valid"),
  PROGRAM_NOT_FOUND("Program not found"),
  TRAINING_PATH_NOT_FOUND("Training path not found"),
  SKILL_NOT_FOUND("Skill not found"),
  SKILL_LEVEL_NOT_FOUND("Skill level not found"),
  TRACE_NOT_FOUND("Trace not found"),
  AMS_NOT_FOUND("AMS not found"),
  USER_IS_NOT_STUDENT_EXCEPTION("User is not student"),
  LANGUAGE_NOT_SUPPORTED("Language not supported"),
  INVALID_ARGUMENT_TYPE("The type of the argument is invalid"),
  ADDITIONAL_SKILL_NOT_AVAILABLE("Additional skill not available"),
  ADDITIONAL_SKILL_NOT_FOUND("Additional skill not found"),
  STUDENT_ADDITIONAL_ALREADY_EXIST("This additional skill is already assigned to the student"),
  ;

  private final String message;

  EErrorCode(String message) {
    this.message = message;
  }
}
