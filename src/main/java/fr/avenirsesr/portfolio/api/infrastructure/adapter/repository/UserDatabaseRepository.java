package fr.avenirsesr.portfolio.api.infrastructure.adapter.repository;

import fr.avenirsesr.portfolio.api.domain.model.Student;
import fr.avenirsesr.portfolio.api.domain.model.Teacher;
import fr.avenirsesr.portfolio.api.domain.model.User;
import fr.avenirsesr.portfolio.api.domain.port.output.UserRepository;
import fr.avenirsesr.portfolio.api.infrastructure.adapter.model.StudentEntity;
import fr.avenirsesr.portfolio.api.infrastructure.adapter.model.TeacherEntity;
import fr.avenirsesr.portfolio.api.infrastructure.adapter.model.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class UserDatabaseRepository implements UserRepository {

    private final UserJpaRepository jpaRepository;

    @Override
    public User findById(UUID id) {
        Optional<UserEntity> userEntity = jpaRepository.findById(id);

        if (userEntity.isPresent()) {
            return null;
        }

        return userEntityToModel(jpaRepository.findById(id).get());
    }

    @Override
    public User save(User user) {
        UserEntity userEntity = userModelToEntity(user);
        userEntity = jpaRepository.save(userEntity);
        return userEntityToModel(userEntity);
    }

    private UserEntity userModelToEntity(User user) {
        return new UserEntity(
            user.getId(),
            user.getFirstName(),
            user.getLastName(),
            user.getEmail(),
            studentModelToEntity(user.getStudent()),
            teacherModelToEntity(user.getTeacher())
        );
    }

    private User userEntityToModel(UserEntity userEntity) {
        return User.toDomain(
            userEntity.getId(),
            userEntity.getFirstName(),
            userEntity.getLastName(),
            userEntity.getEmail(),
            studentEntityToModel(userEntity.getStudent()),
            teacherEntityToModel(userEntity.getTeacher())
        );
    }

    private StudentEntity studentModelToEntity(Student student) {
        return new StudentEntity(
            student.getBio(),
            student.getProfilePicture(),
            student.getCoverPicture()
        );
    }

    private Student studentEntityToModel(StudentEntity studentEntity) {
        return Student.toDomain(
                studentEntity .getId(),
            entity.getName(),
            entity.getUserType()
        );
    }

    private TeacherEntity teacherModelToEntity(Teacher teacher) {
        return new TeacherEntity(
            teacher.getBio(),
            teacher.getProfilePicture(),
            teacher.getCoverPicture()
        );
    }

    private Teacher teacherEntityToModel(TeacherEntity teacherEntity) {
        return null;    // TODO : remplir correctement l'objet
        /*
        return new User(
            entity.getId(),
            entity.getName(),
            entity.getUserType()
        );
        */
    }
}
