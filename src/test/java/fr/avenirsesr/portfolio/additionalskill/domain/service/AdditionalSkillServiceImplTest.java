package fr.avenirsesr.portfolio.additionalskill.domain.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import fr.avenirsesr.portfolio.additionalskill.domain.exception.AdditionalSkillNotFoundException;
import fr.avenirsesr.portfolio.additionalskill.domain.exception.DuplicateAdditionalSkillException;
import fr.avenirsesr.portfolio.additionalskill.domain.model.AdditionalSkill;
import fr.avenirsesr.portfolio.additionalskill.domain.model.AdditionalSkillProgress;
import fr.avenirsesr.portfolio.additionalskill.domain.model.enums.EAdditionalSkillLevel;
import fr.avenirsesr.portfolio.additionalskill.domain.model.enums.EAdditionalSkillType;
import fr.avenirsesr.portfolio.additionalskill.domain.port.output.repository.AdditionalSkillProgressRepository;
import fr.avenirsesr.portfolio.additionalskill.domain.port.output.repository.AdditionalSkillRepository;
import fr.avenirsesr.portfolio.shared.domain.model.PageCriteria;
import fr.avenirsesr.portfolio.shared.domain.model.PagedResult;
import fr.avenirsesr.portfolio.testutils.BddLogger;
import fr.avenirsesr.portfolio.user.domain.model.Student;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AdditionalSkillServiceImplTest {

  @Mock private AdditionalSkillRepository additionalSkillRepository;
  @Mock private AdditionalSkillProgressRepository additionalSkillProgressRepository;

  @InjectMocks private AdditionalSkillServiceImpl service;

  @Test
  void getAdditionalSkillsProgresses_shouldDelegateToRepositoryAndReturnResult() {
    BddLogger.given("the method getAdditionalSkillsProgresses");
    Student student = mock(Student.class);
    PageCriteria criteria = new PageCriteria(1, 8);
    PagedResult<AdditionalSkillProgress> expected = mock(PagedResult.class);

    BddLogger.when("calling the method with a given student");
    when(additionalSkillProgressRepository.findAllByStudent(student, criteria))
        .thenReturn(expected);

    PagedResult<AdditionalSkillProgress> result =
        service.getAdditionalSkillsProgresses(student, criteria);

    BddLogger.then(
        "it should return the expected paged additional skill progress and delegate to repository");
    assertThat(result).isSameAs(expected);
    verify(additionalSkillProgressRepository).findAllByStudent(student, criteria);
  }

  @Test
  void createAdditionalSkillProgress_shouldSaveWhenSkillIsAvailableAndNotDuplicate() {
    BddLogger.given("the method createAdditionalSkillProgress");
    Student student = mock(Student.class);
    UUID skillId = UUID.randomUUID();
    EAdditionalSkillType type = EAdditionalSkillType.ROME4;
    EAdditionalSkillLevel level = EAdditionalSkillLevel.BEGINNER;
    AdditionalSkill additionalSkill = mock(AdditionalSkill.class);

    BddLogger.when("calling the method with an available and not duplicate skill");
    when(additionalSkillRepository.findById(skillId)).thenReturn(Optional.of(additionalSkill));
    when(additionalSkillProgressRepository.additionalSkillProgressAlreadyExists(any()))
        .thenReturn(false);

    service.createAdditionalSkillProgress(student, skillId, type, level);

    BddLogger.then("it should save the additional skill");
    verify(additionalSkillRepository).findById(skillId);
    verify(additionalSkillProgressRepository).additionalSkillProgressAlreadyExists(any());
    verify(additionalSkillProgressRepository).save(any(AdditionalSkillProgress.class));
  }

  @Test
  void createAdditionalSkillProgress_shouldThrowDuplicateWhenAlreadyExists() {
    BddLogger.given("the method createAdditionalSkillProgress");
    Student student = mock(Student.class);
    UUID skillId = UUID.randomUUID();
    EAdditionalSkillType type = EAdditionalSkillType.ROME4;
    EAdditionalSkillLevel level = EAdditionalSkillLevel.BEGINNER;
    AdditionalSkill additionalSkill = mock(AdditionalSkill.class);

    BddLogger.when("calling the method with a duplicate skill");
    when(additionalSkillRepository.findById(skillId)).thenReturn(Optional.of(additionalSkill));
    when(additionalSkillProgressRepository.additionalSkillProgressAlreadyExists(any()))
        .thenReturn(true);

    BddLogger.then("it should throw a DuplicateAdditionalSkillException and not save the skill");
    assertThrows(
        DuplicateAdditionalSkillException.class,
        () -> service.createAdditionalSkillProgress(student, skillId, type, level));

    verify(additionalSkillRepository).findById(skillId);
    verify(additionalSkillProgressRepository).additionalSkillProgressAlreadyExists(any());
    verify(additionalSkillProgressRepository, never()).save(any());
  }

  @Test
  void createAdditionalSkillProgress_shouldRethrowWhenSkillNotFound() {
    BddLogger.given("the method createAdditionalSkillProgress");
    Student student = mock(Student.class);
    UUID skillId = UUID.randomUUID();
    EAdditionalSkillType type = EAdditionalSkillType.ROME4;
    EAdditionalSkillLevel level = EAdditionalSkillLevel.BEGINNER;

    BddLogger.when("calling the method with an unknown skill");
    when(additionalSkillRepository.findById(skillId))
        .thenThrow(new AdditionalSkillNotFoundException());

    BddLogger.then("it should throw a AdditionalSkillNotFoundException");
    assertThrows(
        AdditionalSkillNotFoundException.class,
        () -> service.createAdditionalSkillProgress(student, skillId, type, level));

    verify(additionalSkillRepository).findById(skillId);
    verifyNoInteractions(additionalSkillProgressRepository);
  }
}
