package fr.avenirsesr.portfolio.student.progress.declared.activity.application.adapter.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(
    requiredProperties = {
      "newFeedbacks",
      "pendingFeedbacks",
      "processedFeedbacks",
      "totalFeedbacks"
    })
public record FeedbackDashboardDTO(
    int newFeedbacks, int pendingFeedbacks, int processedFeedbacks, int totalFeedbacks) {}
