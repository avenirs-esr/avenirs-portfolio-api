package fr.avenirsesr.portfolio.file.infrastructure.adapter.repository;

import fr.avenirsesr.portfolio.file.domain.model.EUserPhotoType;
import fr.avenirsesr.portfolio.file.domain.model.UserPhoto;
import fr.avenirsesr.portfolio.file.domain.port.output.repository.UserPhotoRepository;
import fr.avenirsesr.portfolio.file.infrastructure.adapter.mapper.UserPhotoMapper;
import fr.avenirsesr.portfolio.file.infrastructure.adapter.model.UserPhotoEntity;
import fr.avenirsesr.portfolio.file.infrastructure.adapter.specification.UserResourceSpecification;
import fr.avenirsesr.portfolio.shared.infrastructure.adapter.repository.GenericJpaRepositoryAdapter;
import fr.avenirsesr.portfolio.user.domain.model.User;
import fr.avenirsesr.portfolio.user.domain.model.enums.EUserCategory;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.mapper.UserMapper;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public class UserPhotoDatabaseRepository
    extends GenericJpaRepositoryAdapter<UserPhoto, UserPhotoEntity> implements UserPhotoRepository {
  public UserPhotoDatabaseRepository(UserPhotoJpaRepository jpaRepository) {
    super(jpaRepository, jpaRepository, UserPhotoMapper::fromDomain, UserPhotoMapper::toDomain);
  }

  @Override
  public List<UserPhoto> findAllByUser(User user, EUserCategory userCategory, EUserPhotoType type) {
    return jpaSpecificationExecutor
        .findAll(UserResourceSpecification.ofUser(UserMapper.fromDomain(user), userCategory, type))
        .stream()
        .map(UserPhotoMapper::toDomain)
        .toList();
  }

  @Override
  public Optional<UserPhoto> findActiveByUser(
      User user, EUserCategory userCategory, EUserPhotoType type) {
    return jpaSpecificationExecutor
        .findOne(
            UserResourceSpecification.ofUser(UserMapper.fromDomain(user), userCategory, type)
                .and(UserResourceSpecification.onlyActiveVersion()))
        .stream()
        .map(UserPhotoMapper::toDomain)
        .findAny();
  }
}
