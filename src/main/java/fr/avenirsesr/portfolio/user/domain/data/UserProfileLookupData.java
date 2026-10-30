package fr.avenirsesr.portfolio.user.domain.data;

import fr.avenirsesr.portfolio.common.security.accesscontrol.domain.model.enums.EUniverse;
import java.util.Set;
import java.util.UUID;

/**
 * Cofolio profile of a person identified by its eppn. A null {@code userId} means no account was
 * ever created for that eppn, which is how the back-office tells that the person never logged in.
 * {@code universes} tells which universes the account really belongs to: it holds both {@link
 * EUniverse#STAFF} and {@link EUniverse#STUDENT} for a person owning the two records, and is empty
 * for an account owning none of them.
 */
public record UserProfileLookupData(
    String eppn, UUID userId, UUID profilePictureId, Set<EUniverse> universes) {

  public static UserProfileLookupData neverConnected(String eppn) {
    return new UserProfileLookupData(eppn, null, null, Set.of());
  }

  public boolean hasConnected() {
    return userId != null;
  }
}
