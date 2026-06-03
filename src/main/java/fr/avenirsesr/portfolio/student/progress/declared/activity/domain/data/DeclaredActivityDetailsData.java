package fr.avenirsesr.portfolio.student.progress.declared.activity.domain.data;

import fr.avenirsesr.portfolio.student.progress.declared.activity.domain.model.DeclaredActivity;
import fr.avenirsesr.portfolio.student.progress.declared.activity.domain.model.Feedback;
import java.util.List;

public record DeclaredActivityDetailsData(
    DeclaredActivity declaredActivity, List<Feedback> feedbacks) {}
