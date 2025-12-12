package fr.avenirsesr.portfolio.student.progress.declared.program.domain.port.input;

import fr.avenirsesr.portfolio.student.progress.declared.program.domain.model.DeclaredProgram;
import fr.avenirsesr.portfolio.student.progress.declared.program.domain.model.enums.EProgramStatus;
import java.time.LocalDate;
import java.util.UUID;

public interface DeclaredProgramService {
  DeclaredProgram create(
      UUID studentId,
      EProgramStatus status,
      String title,
      String description,
      String organization,
      String result,
      String sourceOfInformation,
      String link,
      LocalDate startDate,
      LocalDate endDate);

  DeclaredProgram create(
      String title,
      String description,
      String organization,
      String result,
      String sourceOfInformation,
      String link,
      LocalDate startDate,
      LocalDate endDate);

  DeclaredProgram getById(UUID declaredProgramId);
}
