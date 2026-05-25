package fr.avenirsesr.portfolio.user.domain.data;

import fr.avenirsesr.portfolio.file.domain.data.FileData;

public record UserProfileOverviewData(
    String firstName,
    String lastName,
    String email,
    String bio,
    FileData coverPhoto,
    FileData profilePhoto) {}
