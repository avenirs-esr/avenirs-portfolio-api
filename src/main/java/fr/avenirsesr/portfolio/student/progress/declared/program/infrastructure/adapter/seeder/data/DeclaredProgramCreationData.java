package fr.avenirsesr.portfolio.student.progress.declared.program.infrastructure.adapter.seeder.data;

import fr.avenirsesr.portfolio.student.progress.declared.program.domain.model.enums.EProgramStatus;
import java.time.LocalDate;
import java.util.UUID;

public record DeclaredProgramCreationData(
    UUID studentId,
    String title,
    EProgramStatus status,
    String description,
    String organization,
    String result,
    String sourceOfInformation,
    LocalDate startDate,
    LocalDate endDate) {}
