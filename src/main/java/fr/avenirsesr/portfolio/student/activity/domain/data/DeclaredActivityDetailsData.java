package fr.avenirsesr.portfolio.student.activity.domain.data;

import fr.avenirsesr.portfolio.staff.activity.domain.data.SubActivityOverviewData;
import fr.avenirsesr.portfolio.student.activity.domain.model.DeclaredActivity;
import java.util.List;

public record DeclaredActivityDetailsData(
    DeclaredActivity declaredActivity,
    List<SubActivityOverviewData> subActivities,
    List<FeedbackData> feedbacks) {}
