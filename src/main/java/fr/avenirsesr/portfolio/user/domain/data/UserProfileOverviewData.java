package fr.avenirsesr.portfolio.user.domain.data;

import fr.avenirsesr.portfolio.file.domain.data.FileData;
import java.util.UUID;

public record UserProfileOverviewData(
    UUID id,
    String firstName,
    String lastName,
    String email,
    String bio,
    boolean hasUnseenNotification,
    FileData coverPhoto,
    FileData profilePhoto) {}
