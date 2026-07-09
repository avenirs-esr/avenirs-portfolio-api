package fr.avenirsesr.portfolio.notification.domain.model.enums;

import fr.avenirsesr.portfolio.common.data.domain.model.enums.EUserCategory;
import fr.avenirsesr.portfolio.notification.domain.model.notification.parameters.ActivityModifiedParameters;
import fr.avenirsesr.portfolio.notification.domain.model.notification.parameters.AskForFeedbackParameters;
import fr.avenirsesr.portfolio.notification.domain.model.notification.parameters.NotificationParameters;
import java.util.List;
import java.util.function.Function;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ENotificationType {
  ACTIVITY_MODIFIED(EUserCategory.STUDENT, ActivityModifiedParameters::fromStringList),
  ASK_FOR_FEEDBACK(EUserCategory.STAFF, AskForFeedbackParameters::fromStringList);

  private final EUserCategory restrictedTo;
  private final Function<List<String>, NotificationParameters> parametersFactory;

  public NotificationParameters toParameters(List<String> rawParameters) {
    return parametersFactory.apply(rawParameters);
  }
}
