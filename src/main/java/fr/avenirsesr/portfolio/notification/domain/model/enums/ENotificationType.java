package fr.avenirsesr.portfolio.notification.domain.model.enums;

import fr.avenirsesr.portfolio.common.data.domain.model.enums.EUserCategory;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ENotificationType {
  ASK_FOR_FEEDBACK(EUserCategory.STAFF);

  private final EUserCategory restrictedTo;
}
