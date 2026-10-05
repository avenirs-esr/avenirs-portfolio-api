package fr.avenirsesr.portfolio.staff.activity.application.adapter.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(
    requiredProperties = {
      "uniqueStudentViews",
      "enrolledStudents",
      "unsubscriptionsLast30Days",
      "inactiveStudentsLast30Days"
    })
public record ActivityDashboardDTO(
    int uniqueStudentViews,
    int enrolledStudents,
    int unsubscriptionsLast30Days,
    int inactiveStudentsLast30Days) {}
