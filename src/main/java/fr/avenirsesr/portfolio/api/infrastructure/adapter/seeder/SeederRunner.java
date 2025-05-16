package fr.avenirsesr.portfolio.api.infrastructure.adapter.seeder;

import fr.avenirsesr.portfolio.api.domain.model.User;
import fr.avenirsesr.portfolio.api.domain.model.enums.ENavigationField;
import fr.avenirsesr.portfolio.api.domain.model.enums.ESkillLevelStatus;
import fr.avenirsesr.portfolio.api.domain.model.enums.EUserCategory;
import fr.avenirsesr.portfolio.api.domain.port.output.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Set;

@Slf4j
@Service
public class SeederRunner implements CommandLineRunner {
    @Value("${seeder.enabled:false}")
    private boolean seedEnabled;

    private final UserRepository userRepository;

    public SeederRunner(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void run(String... args) {
        if (seedEnabled) {
            log.info("Seeder is enabled: seeding stared");
            var users = List.of(
                    FakeUser.create()
                            .withEmail()
                            .withStudent().toModel(),
                    FakeUser.create()
                            .withEmail()
                            .withStudent()
                            .withTeacher().toModel()
            );

            var externalUsers = users.stream()
                    .map(user -> FakeExternalUser.of(user, user.getStudent() != null ? EUserCategory.STUDENT : EUserCategory.TEACHER).toModel()).toList();

            var institutions = List.of(
                    FakeInstitution.create().toModel(),
                    FakeInstitution.create().withEnabledFiled(Set.of(ENavigationField.APC)).toModel(),
                    FakeInstitution.create().withEnabledFiled(Set.of(ENavigationField.LIFE_PROJECT)).toModel()
            );

            var programs = institutions.stream()
                    .map(institution -> FakeProgram.of(institution).toModel())
                    .toList();

            var programProgresses = users.stream()
                    .map(User::getStudent)
                    .filter(Objects::nonNull)
                    .map(student -> {
                        var skills = Set.of(
                                FakeSkill.of(
                                        Set.of(
                                                FakeSkillLevel.create().withStatus(ESkillLevelStatus.VALIDATED).toModel(),
                                                FakeSkillLevel.create().withStatus(ESkillLevelStatus.FAILED).toModel(),
                                                FakeSkillLevel.create().withStatus(ESkillLevelStatus.UNDER_REVIEW).toModel()
                                        )
                                ).toModel(),
                                FakeSkill.of(
                                        Set.of(
                                                FakeSkillLevel.create().withStatus(ESkillLevelStatus.VALIDATED).toModel(),
                                                FakeSkillLevel.create().withStatus(ESkillLevelStatus.VALIDATED).toModel(),
                                                FakeSkillLevel.create().withStatus(ESkillLevelStatus.TO_BE_EVALUATED).toModel()
                                        )
                                ).toModel(),
                                FakeSkill.of(
                                        Set.of(
                                                FakeSkillLevel.create().withStatus(ESkillLevelStatus.VALIDATED).toModel(),
                                                FakeSkillLevel.create().toModel()
                                        )
                                ).toModel()
                        );

                        return FakeProgramProgress.of(programs.getFirst(), student, skills).toModel();
                    })
                    .toList();

            userRepository.saveAll(users);

            log.info("{} users created", users.size());
            log.info("{} externalUsers created", externalUsers.size());
            log.info("{} institutions created", institutions.size());
            log.info("{} programs created", programs.size());
            log.info("{} programProgresses created", programProgresses.size());

            log.info("Seeding successfully finished");

        } else log.info("Seeder is disabled: seeding skipped");
    }
}
