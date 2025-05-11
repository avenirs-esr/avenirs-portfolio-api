package fr.avenirsesr.portfolio.api.domain.model;

import lombok.Getter;

import java.util.Set;
import java.util.UUID;

@Getter
public class ProgramProgress {
    private final UUID id;
    private final Program program;
    private final Student student;
    private final Set<Skill> skills;

    private ProgramProgress(UUID id, Program program, Student student, Set<Skill> skills) {
        this.id = id;
        this.program = program;
        this.student = student;
        this.skills = skills;
    }

    public static ProgramProgress create(Program program, Student student, Set<Skill> skills) {
        return new ProgramProgress(UUID.randomUUID(), program, student, skills);
    }

    public static ProgramProgress toDomain(UUID id, Program program, Student student, Set<Skill> skills) {
        return new ProgramProgress(id, program, student, skills);
    }
}
