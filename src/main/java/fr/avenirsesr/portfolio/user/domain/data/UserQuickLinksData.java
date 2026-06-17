package fr.avenirsesr.portfolio.user.domain.data;

import java.util.UUID;

public record UserQuickLinksData(
    UUID userId,
    String firstname,
    String lastname,
    boolean hasUnseenNotification,
    int unreadNotifications,
    boolean notificationEnabled) {}
