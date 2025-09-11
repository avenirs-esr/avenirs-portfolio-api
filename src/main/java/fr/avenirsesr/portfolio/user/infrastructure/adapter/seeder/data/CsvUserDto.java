package fr.avenirsesr.portfolio.user.infrastructure.adapter.seeder.data;

public record CsvUserDto(
    String firstName,
    String lastName,
    String email,
    String studentDescription,
    String teacherDescription) {}
