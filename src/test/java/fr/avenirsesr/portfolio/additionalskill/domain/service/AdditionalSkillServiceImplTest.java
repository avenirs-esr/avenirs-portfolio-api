package fr.avenirsesr.portfolio.additionalskill.domain.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import fr.avenirsesr.portfolio.additionalskill.domain.exception.AdditionalSkillNotFoundException;
import fr.avenirsesr.portfolio.additionalskill.domain.exception.DuplicateAdditionalSkillException;
import fr.avenirsesr.portfolio.additionalskill.domain.model.AdditionalSkill;
import fr.avenirsesr.portfolio.additionalskill.domain.model.AdditionalSkillProgress;
import fr.avenirsesr.portfolio.additionalskill.domain.model.enums.EAdditionalSkillLevel;
import fr.avenirsesr.portfolio.additionalskill.domain.model.enums.EAdditionalSkillType;
import fr.avenirsesr.portfolio.additionalskill.domain.port.output.AdditionalSkillCache;
import fr.avenirsesr.portfolio.additionalskill.domain.port.output.repository.AdditionalSkillProgressRepository;
import fr.avenirsesr.portfolio.shared.domain.model.PageCriteria;
import fr.avenirsesr.portfolio.shared.domain.model.PagedResult;
import fr.avenirsesr.portfolio.user.domain.model.Student;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AdditionalSkillServiceImplTest {

  @Mock private AdditionalSkillCache additionalSkillCache;
  @Mock private AdditionalSkillProgressRepository additionalSkillProgressRepository;

  @InjectMocks private AdditionalSkillServiceImpl service;

  @Test
  void getAdditionalSkillsProgresses_shouldDelegateToRepositoryAndReturnResult() {
    Student student = mock(Student.class);
    PageCriteria criteria = new PageCriteria(1, 8);
    PagedResult<AdditionalSkillProgress> expected = mock(PagedResult.class);

    when(additionalSkillProgressRepository.findAllByStudent(student, criteria))
        .thenReturn(expected);

    PagedResult<AdditionalSkillProgress> result =
        service.getAdditionalSkillsProgresses(student, criteria);

    assertThat(result).isSameAs(expected);
    verify(additionalSkillProgressRepository).findAllByStudent(student, criteria);
    verifyNoInteractions(additionalSkillCache);
  }

  @Test
  void searchAdditionalSkills_shouldDelegateToCacheAndReturnResult() {
    String keyword = "java";
    PageCriteria criteria = new PageCriteria(1, 8);
    PagedResult<AdditionalSkill> expected = mock(PagedResult.class);

    when(additionalSkillCache.findBySkillTitle(keyword, criteria)).thenReturn(expected);

    PagedResult<AdditionalSkill> result = service.searchAdditionalSkills(keyword, criteria);

    assertThat(result).isSameAs(expected);
    verify(additionalSkillCache).findBySkillTitle(keyword, criteria);
    verifyNoInteractions(additionalSkillProgressRepository);
  }

  @Test
  void createAdditionalSkillProgress_shouldSaveWhenSkillIsAvailableAndNotDuplicate() {
    Student student = mock(Student.class);
    UUID skillId = UUID.randomUUID();
    EAdditionalSkillType type = EAdditionalSkillType.ROME4;
    EAdditionalSkillLevel level = EAdditionalSkillLevel.BEGINNER;
    AdditionalSkill additionalSkill = mock(AdditionalSkill.class);

    when(additionalSkillCache.findById(skillId)).thenReturn(additionalSkill);
    when(additionalSkillProgressRepository.additionalSkillProgressAlreadyExists(any()))
        .thenReturn(false);

    service.createAdditionalSkillProgress(student, skillId, type, level);

    verify(additionalSkillCache).findById(skillId);
    verify(additionalSkillProgressRepository).additionalSkillProgressAlreadyExists(any());
    verify(additionalSkillProgressRepository).save(any(AdditionalSkillProgress.class));
  }

  @Test
  void createAdditionalSkillProgress_shouldThrowDuplicateWhenAlreadyExists() {
    Student student = mock(Student.class);
    UUID skillId = UUID.randomUUID();
    EAdditionalSkillType type = EAdditionalSkillType.ROME4;
    EAdditionalSkillLevel level = EAdditionalSkillLevel.BEGINNER;
    AdditionalSkill additionalSkill = mock(AdditionalSkill.class);

    when(additionalSkillCache.findById(skillId)).thenReturn(additionalSkill);
    when(additionalSkillProgressRepository.additionalSkillProgressAlreadyExists(any()))
        .thenReturn(true);

    assertThrows(
        DuplicateAdditionalSkillException.class,
        () -> service.createAdditionalSkillProgress(student, skillId, type, level));

    verify(additionalSkillCache).findById(skillId);
    verify(additionalSkillProgressRepository).additionalSkillProgressAlreadyExists(any());
    verify(additionalSkillProgressRepository, never()).save(any());
  }

  @Test
  void createAdditionalSkillProgress_shouldRethrowWhenSkillNotFound() {
    Student student = mock(Student.class);
    UUID skillId = UUID.randomUUID();
    EAdditionalSkillType type = EAdditionalSkillType.ROME4;
    EAdditionalSkillLevel level = EAdditionalSkillLevel.BEGINNER;

    when(additionalSkillCache.findById(skillId)).thenThrow(new AdditionalSkillNotFoundException());

    assertThrows(
        AdditionalSkillNotFoundException.class,
        () -> service.createAdditionalSkillProgress(student, skillId, type, level));

    verify(additionalSkillCache).findById(skillId);
    verifyNoInteractions(additionalSkillProgressRepository);
  }
}
