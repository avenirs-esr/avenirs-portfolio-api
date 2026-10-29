package fr.avenirsesr.portfolio.notification.domain.model.notification.parameters;

import java.util.List;

public sealed interface NotificationParameters
    permits AskForFeedbackParameters,
        ActivityModifiedParameters,
        AssessmentSentParameters,
        AssessmentRequestNotificationParameters,
        AssessmentModifiedParameters {

  List<String> toStringList();
}
