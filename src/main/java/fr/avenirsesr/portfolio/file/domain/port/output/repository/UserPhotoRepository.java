package fr.avenirsesr.portfolio.file.domain.port.output.repository;

import fr.avenirsesr.portfolio.file.domain.model.EUserPhotoType;
import fr.avenirsesr.portfolio.file.domain.model.UserPhoto;
import fr.avenirsesr.portfolio.shared.domain.port.output.repository.GenericRepositoryPort;
import fr.avenirsesr.portfolio.user.domain.model.User;
import fr.avenirsesr.portfolio.user.domain.model.enums.EUserCategory;
import java.util.List;
import java.util.Optional;

public interface UserPhotoRepository extends GenericRepositoryPort<UserPhoto> {
  List<UserPhoto> findAllByUser(User user, EUserCategory userCategory, EUserPhotoType type);

  Optional<UserPhoto> findActiveByUser(User user, EUserCategory userCategory, EUserPhotoType type);
}
