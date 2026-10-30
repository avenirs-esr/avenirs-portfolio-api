package fr.avenirsesr.portfolio.user.domain.data;

import java.util.UUID;

/**
 * Cofolio profile of a person identified by its eppn. A null {@code userId} means no account was
 * ever created for that eppn, which is how the back-office tells that the person never logged in.
 * {@code staff} and {@code student} tell which universes the account really belongs to: an account
 * may exist without belonging to any of them.
 */
public record UserProfileLookupData(
    String eppn, UUID userId, UUID profilePictureId, boolean staff, boolean student) {

  public static UserProfileLookupData neverConnected(String eppn) {
    return new UserProfileLookupData(eppn, null, null, false, false);
  }

  public boolean hasConnected() {
    return userId != null;
  }
}
