package fr.avenirsesr.portfolio.api.domain.model;

import lombok.Getter;

import java.util.UUID;

@Getter
public class Program {
    private final UUID id;
    private final Institution institution;
    private final String name;

    private Program(UUID id, Institution institution, String name) {
        this.id = id;
        this.institution = institution;
        this.name = name;
    }

    public static Program create(Institution institution, String name) {
        return new Program(UUID.randomUUID(), institution, name);
    }

    public static Program toDomain(UUID id, Institution institution, String name) {
        return new Program(id, institution, name);
    }
}
