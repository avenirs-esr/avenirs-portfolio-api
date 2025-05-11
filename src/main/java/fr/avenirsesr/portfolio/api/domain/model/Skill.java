package fr.avenirsesr.portfolio.api.domain.model;

import lombok.Getter;

import java.util.Set;
import java.util.UUID;

@Getter
public class Skill {
    private final UUID id;
    private final String name;
    private final ProgramProgress programProgress;
    private final Set<SkillLevel> skillLevels;

    private Skill(UUID id, String name, ProgramProgress programProgress, Set<SkillLevel> skillLevels) {
        this.id = id;
        this.name = name;
        this.programProgress = programProgress;
        this.skillLevels = skillLevels;
    }

    public static Skill create(String name, ProgramProgress programProgress, Set<SkillLevel> skillLevels) {
        return new Skill(UUID.randomUUID(), name, programProgress, skillLevels);
    }

    public static Skill toDomain(UUID id, String name, ProgramProgress programProgress, Set<SkillLevel> skillLevels) {
        return new Skill(id, name, programProgress, skillLevels);
    }
}
