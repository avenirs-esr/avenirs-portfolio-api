package fr.avenirsesr.portfolio.user.application.adapter.dto;

import fr.avenirsesr.portfolio.common.security.accesscontrol.domain.model.enums.EUniverse;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import java.util.UUID;

/**
 * Profile of a person looked up by eppn. {@code hasConnected} is false when no account exists yet,
 * in which case the user id and the profile picture are null. {@code universes} lists the universes
 * whose record the account owns, possibly both and possibly none: the user id can be used as a
 * staff id only when it contains {@code STAFF}. The picture is exposed as a file id, to be read
 * through the public {@code /storage/{fileId}} endpoint: the caller is a service, so the origin
 * seen here is the internal one and cannot be turned into a browsable url.
 */
@Schema(requiredProperties = {"eppn", "hasConnected", "universes"})
public record UserProfileDTO(
    String eppn,
    boolean hasConnected,
    UUID userId,
    UUID profilePictureId,
    List<EUniverse> universes) {}
