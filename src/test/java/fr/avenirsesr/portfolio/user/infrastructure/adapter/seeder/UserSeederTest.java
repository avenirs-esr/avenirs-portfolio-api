package fr.avenirsesr.portfolio.user.infrastructure.adapter.seeder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import fr.avenirsesr.portfolio.shared.infrastructure.adapter.seeder.SeederConfig;
import fr.avenirsesr.portfolio.user.domain.port.output.repository.ExternalUserRepository;
import fr.avenirsesr.portfolio.user.domain.port.output.repository.UserRepository;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.model.UserEntity;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class UserSeederTest {

  @Autowired private UserSeeder userSeeder;

  @Autowired private UserRepository userRepository;
  @Autowired private ExternalUserRepository externalUserRepository;

  private static List<UserEntity> users;

  @BeforeAll
  static void setUp(@Autowired UserSeeder userSeeder) {
    users = userSeeder.seed();
  }

  @Test
  void seed_shouldReturnNonEmptyUsersList() {
    assertNotNull(users);
    assertFalse(users.isEmpty());

    int expectedTotal =
        SeederConfig.USERS_NB_OF_STUDENT
            + SeederConfig.USERS_NB_OF_TEACHER
            + SeederConfig.USERS_NB_OF_BOTH;
    assertEquals(expectedTotal, users.size());
  }

  @Test
  void seed_shouldHaveStudentsAndTeachers() {
    long studentCount = users.stream().filter(u -> u.getStudent() != null).count();
    long teacherCount = users.stream().filter(u -> u.getTeacher() != null).count();

    assertTrue(studentCount >= SeederConfig.USERS_NB_OF_STUDENT);
    assertTrue(teacherCount >= SeederConfig.USERS_NB_OF_TEACHER);
  }

  @Test
  void seed_shouldCallRepositories() {
    // Mock les repositories pour vérifier les appels
    UserRepository mockUserRepo = mock(UserRepository.class);
    ExternalUserRepository mockExternalRepo = mock(ExternalUserRepository.class);
    UserSeeder seederWithMock = new UserSeeder(mockUserRepo, mockExternalRepo);

    List<UserEntity> result = seederWithMock.seed();

    verify(mockUserRepo, atLeastOnce()).saveAll(any());
    verify(mockExternalRepo, atLeastOnce()).saveAll(any());
    verify(mockUserRepo, atLeastOnce()).saveAllStudents(any());
    verify(mockUserRepo, atLeastOnce()).saveAllTeachers(any());
  }
}
