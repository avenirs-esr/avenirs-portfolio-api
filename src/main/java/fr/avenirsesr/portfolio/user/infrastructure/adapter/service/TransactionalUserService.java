package fr.avenirsesr.portfolio.user.infrastructure.adapter.service;

import fr.avenirsesr.portfolio.common.data.domain.model.User;
import fr.avenirsesr.portfolio.common.data.domain.model.enums.EUserCategory;
import fr.avenirsesr.portfolio.user.domain.port.input.UserService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
public class TransactionalUserService implements UserService {

  private final UserService delegate;

  @Override
  public User getUser(UUID id) {
    return delegate.getUser(id);
  }

  @Override
  @Transactional
  public User getUserByEppn(String eppn) {
    return delegate.getUserByEppn(eppn);
  }

  @Override
  @Transactional
  public void updateProfile(EUserCategory userCategory, String email, String bio) {
    delegate.updateProfile(userCategory, email, bio);
  }

  @Override
  public void updateNotificationPreferences(boolean notificationEnabled) {
    delegate.updateNotificationPreferences(notificationEnabled);
  }

  @Override
  @Transactional
  public User createUser(UUID id, String firstname, String lastname, String email, String eppn) {
    return delegate.createUser(id, firstname, lastname, email, eppn);
  }
}
