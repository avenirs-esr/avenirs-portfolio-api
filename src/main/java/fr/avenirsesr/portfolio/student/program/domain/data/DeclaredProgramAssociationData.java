package fr.avenirsesr.portfolio.student.program.domain.data;

import fr.avenirsesr.portfolio.student.program.domain.model.DeclaredProgram;
import java.util.UUID;

public record DeclaredProgramAssociationData(UUID associationId, DeclaredProgram declaredProgram) {}
