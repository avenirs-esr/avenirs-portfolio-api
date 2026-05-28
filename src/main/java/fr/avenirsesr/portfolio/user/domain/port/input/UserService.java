package fr.avenirsesr.portfolio.user.domain.port.input;

import fr.avenirsesr.portfolio.common.data.domain.model.User;
import fr.avenirsesr.portfolio.common.data.domain.model.enums.EUserCategory;
import fr.avenirsesr.portfolio.common.user.domain.port.output.BaseUserService;
import java.util.UUID;

@SuppressWarnings("PMD.MissingOverride")
public interface UserService extends BaseUserService {
  User getUser(UUID id);

  void updateProfile(EUserCategory userCategory, String email, String bio);

  User createUser(UUID id, String firstname, String lastname, String email, String eppn);
}
