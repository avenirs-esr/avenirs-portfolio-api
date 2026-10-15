package fr.avenirsesr.portfolio.staff.activity.domain.model.enums;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

public enum EActivityRight {
  LEADER,
  EDIT,
  MANAGE_AUTHORIZATIONS,
  FEEDBACK,
  EVALUATION,
  PUBLISH,
  ;

  public static final Set<EActivityRight> MANAGEMENT_RIGHTS =
      Collections.unmodifiableSet(EnumSet.of(LEADER, EDIT, MANAGE_AUTHORIZATIONS, PUBLISH));

  public static Set<EActivityRight> all() {
    return EnumSet.allOf(EActivityRight.class);
  }
}
