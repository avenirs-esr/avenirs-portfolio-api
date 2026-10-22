package fr.avenirsesr.portfolio.user.domain.data;

import java.util.UUID;

/**
 * Cofolio profile of a person identified by its eppn. A null {@code userId} means no account was
 * ever created for that eppn, which is how the back-office tells that the person never logged in.
 */
public record UserProfileLookupData(String eppn, UUID userId, UUID profilePictureId) {

  public static UserProfileLookupData neverConnected(String eppn) {
    return new UserProfileLookupData(eppn, null, null);
  }

  public boolean hasConnected() {
    return userId != null;
  }
}
