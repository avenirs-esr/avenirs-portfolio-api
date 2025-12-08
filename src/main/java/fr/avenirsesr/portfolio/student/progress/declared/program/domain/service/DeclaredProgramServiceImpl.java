package fr.avenirsesr.portfolio.student.progress.declared.program.domain.service;

import fr.avenirsesr.portfolio.common.security.domain.exception.UserNotAuthorizedException;
import fr.avenirsesr.portfolio.student.progress.declared.program.domain.model.DeclaredProgram;
import fr.avenirsesr.portfolio.student.progress.declared.program.domain.model.enums.EProgramStatus;
import fr.avenirsesr.portfolio.student.progress.declared.program.domain.port.input.DeclaredProgramService;
import fr.avenirsesr.portfolio.student.progress.declared.program.domain.port.output.DeclaredProgramRepository;
import fr.avenirsesr.portfolio.user.domain.model.Student;
import fr.avenirsesr.portfolio.user.domain.port.output.repository.StudentRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.util.UUID;

@Slf4j
@AllArgsConstructor
public class DeclaredProgramServiceImpl implements DeclaredProgramService {
    private final StudentRepository studentRepository;
    private final DeclaredProgramRepository declaredProgramRepository;

    @Override
    public DeclaredProgram create(
            UUID studentId,
            EProgramStatus status,
            String title,
            String description,
            String organization,
            String result,
            String sourceOfInformation,
            String link,
            LocalDate startDate,
            LocalDate endDate) {
        Student student = studentRepository.findById(studentId).orElseThrow(UserNotAuthorizedException::new);
        if (!student.getId().equals(studentId)) {
            throw new UserNotAuthorizedException(
                    "Student not authorized. loggedIn student : %s student is provided : %s"
                            .formatted(student, studentId));
        }
        var declaredProgram =
                DeclaredProgram.create(
                        student,
                        status,
                        title,
                        description,
                        organization,
                        result,
                        sourceOfInformation,
                        link,
                        startDate,
                        endDate);

        return declaredProgramRepository.save(declaredProgram);
    }
}
