package fr.avenirsesr.portfolio.api.infrastructure.adapter.repository;

import fr.avenirsesr.portfolio.api.domain.model.Student;
import fr.avenirsesr.portfolio.api.domain.model.Teacher;
import fr.avenirsesr.portfolio.api.domain.model.User;
import fr.avenirsesr.portfolio.api.domain.port.output.repository.UserRepository;
import fr.avenirsesr.portfolio.api.infrastructure.adapter.model.UserEntity;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class UserDatabaseRepository extends GenericJpaRepositoryAdapter<User, UserEntity>
        implements UserRepository {

    public UserDatabaseRepository(UserJpaRepository jpaRepository) {
        super(jpaRepository, UserEntity::fromDomain);
    }

    @Override
    public User findById(UUID id) {
        Optional<UserEntity> userEntity = jpaRepository.findById(id);

        if (userEntity.isPresent()) {
            return null;
        }

        return userEntityToModel(jpaRepository.findById(id).get());
    }

    private User userEntityToModel(UserEntity userEntity) {
        User user = User.toDomain(
            userEntity.getId(),
            userEntity.getFirstName(),
            userEntity.getLastName(),
            userEntity.getEmail(),
            null,
            null
        );
        user.setStudent(Student.entityToModel(user, userEntity.getStudent()));
        user.setTeacher(Teacher.entityToModel(user, userEntity.getTeacher()));
        return user;
    }
}
