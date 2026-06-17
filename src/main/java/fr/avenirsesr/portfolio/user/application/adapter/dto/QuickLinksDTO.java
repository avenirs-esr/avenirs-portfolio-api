package fr.avenirsesr.portfolio.user.application.adapter.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.UUID;

@Schema(
    requiredProperties = {
      "userId",
      "firstname",
      "lastname",
      "hasUnseenNotification",
      "unreadNotifications",
      "notificationEnabled"
    })
public record QuickLinksDTO(
    UUID userId,
    String firstname,
    String lastname,
    boolean hasUnseenNotification,
    int unreadNotifications,
    boolean notificationEnabled) {}
