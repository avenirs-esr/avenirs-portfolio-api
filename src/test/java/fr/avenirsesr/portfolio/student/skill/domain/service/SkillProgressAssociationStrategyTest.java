package fr.avenirsesr.portfolio.student.skill.domain.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import fr.avenirsesr.portfolio.common.data.domain.model.PageCriteria;
import fr.avenirsesr.portfolio.common.data.domain.model.PageInfo;
import fr.avenirsesr.portfolio.common.data.domain.model.PagedResult;
import fr.avenirsesr.portfolio.common.security.domain.exception.UserNotAuthorizedException;
import fr.avenirsesr.portfolio.common.skill.domain.model.enums.ESkillReferentialType;
import fr.avenirsesr.portfolio.shared.domain.port.input.LoggedInUserService;
import fr.avenirsesr.portfolio.skill.domain.model.Skill;
import fr.avenirsesr.portfolio.skill.infrastructure.fixture.SkillFixture;
import fr.avenirsesr.portfolio.student.association.domain.data.AssociationSearchResultData;
import fr.avenirsesr.portfolio.student.association.domain.filter.AssociationSearchFilter;
import fr.avenirsesr.portfolio.student.association.domain.model.Association;
import fr.avenirsesr.portfolio.student.association.domain.model.EAssociationContextType;
import fr.avenirsesr.portfolio.student.association.domain.model.EAssociationType;
import fr.avenirsesr.portfolio.student.skill.domain.data.SkillProgressAssociationCount;
import fr.avenirsesr.portfolio.student.skill.domain.data.SkillProgressData;
import fr.avenirsesr.portfolio.student.skill.domain.exception.SkillProgressNotFoundException;
import fr.avenirsesr.portfolio.student.skill.domain.model.SkillProgress;
import fr.avenirsesr.portfolio.student.skill.domain.port.input.SkillProgressService;
import fr.avenirsesr.portfolio.student.trace.domain.model.Trace;
import fr.avenirsesr.portfolio.user.domain.model.Student;
import fr.avenirsesr.portfolio.user.infrastructure.fixture.StudentFixture;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SkillProgressAssociationStrategyTest {

  @Mock private SkillProgressService skillProgressService;
  @Mock private LoggedInUserService loggedInUserService;

  @InjectMocks private SkillProgressAssociationStrategy strategy;

  private SkillProgress skillProgressOf(UUID id, Student student) {
    var skillProgress = mock(SkillProgress.class);
    when(skillProgress.getId()).thenReturn(id);
    when(skillProgress.getStudent()).thenReturn(student);

    return skillProgress;
  }

  @Test
  void getContextType_should_return_the_skill_progress_context() {
    assertThat(strategy.getContextType()).isEqualTo(EAssociationContextType.DECLARED_SKILL);
  }

  @Test
  void checkLoggedInStudentOwns_should_throw_when_a_skill_progress_does_not_exist() {
    UUID skillProgressId = UUID.randomUUID();

    when(skillProgressService.findAllSkillProgressesByIds(List.of(skillProgressId)))
        .thenReturn(List.of());

    assertThatThrownBy(() -> strategy.checkLoggedInStudentOwns(List.of(skillProgressId)))
        .isInstanceOf(SkillProgressNotFoundException.class);
  }

  @Test
  void checkLoggedInStudentCanUnassociate_should_throw_when_it_belongs_to_another_student() {
    UUID skillProgressId = UUID.randomUUID();

    var skillProgress = skillProgressOf(skillProgressId, StudentFixture.create().toModel());

    when(skillProgressService.findAllSkillProgressesByIds(List.of(skillProgressId)))
        .thenReturn(List.of(skillProgress));
    when(loggedInUserService.getLoggedInStudent()).thenReturn(StudentFixture.create().toModel());

    assertThatThrownBy(() -> strategy.checkLoggedInStudentCanUnassociate(List.of(skillProgressId)))
        .isInstanceOf(UserNotAuthorizedException.class);
  }

  @Test
  void search_should_return_the_skill_progresses_with_their_type() {
    UUID skillProgressId = UUID.randomUUID();
    var pageCriteria = new PageCriteria(0, 10);

    Skill skill = SkillFixture.create().withTitle("Communication").toModel();

    var skillProgress = mock(SkillProgress.class);
    when(skillProgress.getId()).thenReturn(skillProgressId);
    when(skillProgress.getSkill()).thenReturn(skill);

    when(skillProgressService.searchSkillProgress("kw", pageCriteria))
        .thenReturn(new PagedResult<>(List.of(skillProgress), new PageInfo(0, 10, 1)));

    var result = strategy.search("kw", AssociationSearchFilter.NONE, pageCriteria);

    assertThat(result.content())
        .containsExactly(
            new AssociationSearchResultData(
                skillProgressId, "Communication", ESkillReferentialType.ROME4.name(), false));
  }

  @Test
  void toAssociatedElements_should_return_the_skill_progresses_of_the_associations() {
    UUID traceId = UUID.randomUUID();
    UUID skillProgressId = UUID.randomUUID();

    var skillProgress = mock(SkillProgress.class);
    when(skillProgress.getId()).thenReturn(skillProgressId);

    Skill skill = SkillFixture.create().toModel();
    var data = new SkillProgressData(skillProgress, skill, new SkillProgressAssociationCount(0, 0));

    var association =
        Association.create(traceId, skillProgressId, EAssociationType.TRACE_DECLARED_SKILL);

    when(skillProgressService.findAllSkillProgressesByIds(List.of(skillProgressId)))
        .thenReturn(List.of(skillProgress));
    when(skillProgressService.getSkillProgressData(List.of(skillProgress)))
        .thenReturn(List.of(data));

    var result = strategy.toAssociatedElements(List.of(association), Trace.class, false);

    assertThat(result.skillProgressAssociations())
        .singleElement()
        .satisfies(
            association1 -> {
              assertThat(association1.associationId()).isEqualTo(association.getId());
              assertThat(association1.skillProgress()).isEqualTo(data);
            });
  }
}
