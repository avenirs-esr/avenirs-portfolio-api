package fr.avenirsesr.portfolio.user.application.adapter.mapper;

import fr.avenirsesr.portfolio.user.application.adapter.dto.ProfileOverviewDTO;
import fr.avenirsesr.portfolio.user.domain.model.Student;
import fr.avenirsesr.portfolio.user.domain.model.Teacher;

public interface ProfileOverviewMapper {
  static ProfileOverviewDTO userStudentDomainToDto(Student student) {
    return new ProfileOverviewDTO(
        student.getUser().getFirstName(),
        student.getUser().getLastName(),
        student.getBio(),
        student.getProfilePicture(),
        student.getCoverPicture());
  }

  static ProfileOverviewDTO userTeacherDomainToDto(Teacher teacher) {
    return new ProfileOverviewDTO(
        teacher.getUser().getFirstName(),
        teacher.getUser().getLastName(),
        teacher.getBio(),
        teacher.getProfilePicture(),
        teacher.getCoverPicture());
  }
}
