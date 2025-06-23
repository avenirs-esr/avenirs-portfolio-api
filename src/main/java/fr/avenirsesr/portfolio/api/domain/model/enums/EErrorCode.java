package fr.avenirsesr.portfolio.api.domain.model.enums;

import lombok.Getter;

@Getter
public enum EErrorCode {
  USER_NOT_FOUND("USER_NOT_FOUND", "User not found"),
  USER_ALREADY_EXISTS("USER_ALREADY_EXISTS", "User already exists"),
  USER_NOT_AUTHORIZED("USER_NOT_AUTHORIZED", "User not authorized"),
  USER_CATEGORY_NOT_FOUND("USER_CATEGORY_NOT_FOUND", "User category not found"),
  BAD_IMAGE_SIZE("BAD_IMAGE_SIZE", "Image pageSize is not valid"),
  BAD_IMAGE_TYPE("BAD_IMAGE_TYPE", "Image type is not valid"),
  PROGRAM_NOT_FOUND("PROGRAM_NOT_FOUND", "Program not found"),
  PROGRAM_PROGRESS_NOT_FOUND("PROGRAM_PROGRESS_NOT_FOUND", "Program progress not found"),
  SKILL_NOT_FOUND("SKILL_NOT_FOUND", "Skill not found"),
  SKILL_LEVEL_NOT_FOUND("SKILL_LEVEL_NOT_FOUND", "Skill level not found"),
  TRACE_NOT_FOUND("TRACE_NOT_FOUND", "TRACE not found"),
  AMS_NOT_FOUND("AMS_NOT_FOUND", "AMS not found"),
  USER_IS_NOT_STUDENT_EXCEPTION("USER_IS_NOT_STUDENT_EXCEPTION", "User is not student"),
  LANGUAGE_NOT_SUPPORTED("LANGUAGE_NOT_SUPPORTED", "Language not supported"),
  INVALID_ARGUMENT_TYPE("INVALID_ARGUMENT_TYPE", "The type of the argument is invalid"),
  ;

  private final String code;
  private final String message;

  EErrorCode(String code, String message) {
    this.code = code;
    this.message = message;
  }
}
