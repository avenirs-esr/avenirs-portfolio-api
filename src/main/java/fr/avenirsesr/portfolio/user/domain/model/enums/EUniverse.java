package fr.avenirsesr.portfolio.user.domain.model.enums;

import static fr.avenirsesr.portfolio.common.security.accesscontrol.domain.model.enums.ERole.ROLE_STAFF;
import static fr.avenirsesr.portfolio.common.security.accesscontrol.domain.model.enums.ERole.ROLE_STAFF_ACTIVITY_AUTHOR;
import static fr.avenirsesr.portfolio.common.security.accesscontrol.domain.model.enums.ERole.ROLE_STAFF_ACTIVITY_VIEWER;
import static fr.avenirsesr.portfolio.common.security.accesscontrol.domain.model.enums.ERole.ROLE_STAFF_FEEDBACK_RESPONDER;
import static fr.avenirsesr.portfolio.common.security.accesscontrol.domain.model.enums.ERole.ROLE_STAFF_FEEDBACK_SUPERVISOR;
import static fr.avenirsesr.portfolio.common.security.accesscontrol.domain.model.enums.ERole.ROLE_STAFF_TRACE_REVIEWER;
import static fr.avenirsesr.portfolio.common.security.accesscontrol.domain.model.enums.ERole.ROLE_STUDENT;
import static fr.avenirsesr.portfolio.common.security.accesscontrol.domain.model.enums.ERole.ROLE_STUDENT_ACTIVITY;
import static fr.avenirsesr.portfolio.common.security.accesscontrol.domain.model.enums.ERole.ROLE_STUDENT_EMPLOYMENT_KIT;
import static fr.avenirsesr.portfolio.common.security.accesscontrol.domain.model.enums.ERole.ROLE_STUDENT_FEEDBACK;
import static fr.avenirsesr.portfolio.common.security.accesscontrol.domain.model.enums.ERole.ROLE_STUDENT_PORTFOLIO;
import static fr.avenirsesr.portfolio.common.security.accesscontrol.domain.model.enums.ERole.ROLE_STUDENT_REFERENCE_DATA;

import fr.avenirsesr.portfolio.common.security.accesscontrol.domain.model.enums.ERole;
import java.util.Collection;
import java.util.EnumSet;
import java.util.Set;

public enum EUniverse {
  STUDENT(
      ROLE_STUDENT,
      ROLE_STUDENT_PORTFOLIO,
      ROLE_STUDENT_ACTIVITY,
      ROLE_STUDENT_FEEDBACK,
      ROLE_STUDENT_EMPLOYMENT_KIT,
      ROLE_STUDENT_REFERENCE_DATA),
  STAFF(
      ROLE_STAFF,
      ROLE_STAFF_TRACE_REVIEWER,
      ROLE_STAFF_ACTIVITY_VIEWER,
      ROLE_STAFF_ACTIVITY_AUTHOR,
      ROLE_STAFF_FEEDBACK_RESPONDER,
      ROLE_STAFF_FEEDBACK_SUPERVISOR),
  ;

  private final Set<ERole> opening;

  EUniverse(ERole first, ERole... others) {
    this.opening = EnumSet.of(first, others);
  }

  public static Set<EUniverse> of(Collection<ERole> roles) {
    var universes = EnumSet.noneOf(EUniverse.class);
    for (var universe : values()) {
      if (roles.stream().anyMatch(universe.opening::contains)) {
        universes.add(universe);
      }
    }
    return universes;
  }
}
