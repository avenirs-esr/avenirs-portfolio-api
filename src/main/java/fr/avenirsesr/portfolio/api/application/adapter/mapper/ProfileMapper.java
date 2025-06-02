package fr.avenirsesr.portfolio.api.application.adapter.mapper;

import fr.avenirsesr.portfolio.api.application.adapter.dto.ProfileDTO;
import fr.avenirsesr.portfolio.api.domain.model.Student;
import fr.avenirsesr.portfolio.api.domain.model.Teacher;

public interface ProfileMapper {
  static ProfileDTO userStudentDomainToDto(Student student) {
    return new ProfileDTO(
        student.getUser().getFirstName(),
        student.getUser().getLastName(),
        student.getBio(),
        student.getProfilePicture(),
        student.getCoverPicture());
  }

  static ProfileDTO userTeacherDomainToDto(Teacher teacher) {
    return new ProfileDTO(
        teacher.getUser().getFirstName(),
        teacher.getUser().getLastName(),
        teacher.getBio(),
        teacher.getProfilePicture(),
        teacher.getCoverPicture());
  }
}
