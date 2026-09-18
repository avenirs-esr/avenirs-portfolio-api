package fr.avenirsesr.portfolio.student.skill.domain.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import fr.avenirsesr.portfolio.common.data.domain.model.PageCriteria;
import fr.avenirsesr.portfolio.common.data.domain.model.PageInfo;
import fr.avenirsesr.portfolio.common.data.domain.model.PagedResult;
import fr.avenirsesr.portfolio.common.externalskill.domain.model.enums.EExternalSkillType;
import fr.avenirsesr.portfolio.common.security.domain.exception.UserNotAuthorizedException;
import fr.avenirsesr.portfolio.shared.domain.port.input.LoggedInUserService;
import fr.avenirsesr.portfolio.student.association.domain.data.AssociationSearchResultData;
import fr.avenirsesr.portfolio.student.association.domain.model.Association;
import fr.avenirsesr.portfolio.student.association.domain.model.EAssociationContextType;
import fr.avenirsesr.portfolio.student.association.domain.model.EAssociationType;
import fr.avenirsesr.portfolio.student.skill.domain.exception.DeclaredSkillProgressNotFoundException;
import fr.avenirsesr.portfolio.student.skill.domain.model.DeclaredSkill;
import fr.avenirsesr.portfolio.student.skill.domain.model.DeclaredSkillProgress;
import fr.avenirsesr.portfolio.student.skill.domain.port.input.DeclaredSkillProgressService;
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
class DeclaredSkillAssociationContextHandlerTest {

  @Mock private DeclaredSkillProgressService declaredSkillProgressService;
  @Mock private LoggedInUserService loggedInUserService;

  @InjectMocks private DeclaredSkillAssociationContextHandler handler;

  private DeclaredSkillProgress declaredSkillProgressOf(UUID id, Student student) {
    var declaredSkillProgress = mock(DeclaredSkillProgress.class);
    when(declaredSkillProgress.getId()).thenReturn(id);
    when(declaredSkillProgress.getStudent()).thenReturn(student);

    return declaredSkillProgress;
  }

  @Test
  void getContextType_should_return_the_declared_skill_context() {
    assertThat(handler.getContextType()).isEqualTo(EAssociationContextType.DECLARED_SKILL);
  }

  @Test
  void checkLoggedInStudentOwns_should_throw_when_a_declared_skill_does_not_exist() {
    UUID declaredSkillProgressId = UUID.randomUUID();

    when(declaredSkillProgressService.findAllDeclaredSkillProgressesByIds(
            List.of(declaredSkillProgressId)))
        .thenReturn(List.of());

    assertThatThrownBy(() -> handler.checkLoggedInStudentOwns(List.of(declaredSkillProgressId)))
        .isInstanceOf(DeclaredSkillProgressNotFoundException.class);
  }

  @Test
  void checkLoggedInStudentCanUnassociate_should_throw_when_it_belongs_to_another_student() {
    UUID declaredSkillProgressId = UUID.randomUUID();

    var declaredSkillProgress =
        declaredSkillProgressOf(declaredSkillProgressId, StudentFixture.create().toModel());

    when(declaredSkillProgressService.findAllDeclaredSkillProgressesByIds(
            List.of(declaredSkillProgressId)))
        .thenReturn(List.of(declaredSkillProgress));
    when(loggedInUserService.getLoggedInStudent()).thenReturn(StudentFixture.create().toModel());

    assertThatThrownBy(
            () -> handler.checkLoggedInStudentCanUnassociate(List.of(declaredSkillProgressId)))
        .isInstanceOf(UserNotAuthorizedException.class);
  }

  @Test
  void search_should_return_the_declared_skills_with_their_type() {
    UUID declaredSkillProgressId = UUID.randomUUID();
    var pageCriteria = new PageCriteria(0, 10);

    var declaredSkill = mock(DeclaredSkill.class);
    when(declaredSkill.getLibelle()).thenReturn("Communication");
    when(declaredSkill.getType()).thenReturn(EExternalSkillType.ROME4);

    var declaredSkillProgress = mock(DeclaredSkillProgress.class);
    when(declaredSkillProgress.getId()).thenReturn(declaredSkillProgressId);
    when(declaredSkillProgress.getSkill()).thenReturn(declaredSkill);

    when(declaredSkillProgressService.searchDeclaredSkill("kw", pageCriteria))
        .thenReturn(new PagedResult<>(List.of(declaredSkillProgress), new PageInfo(0, 10, 1)));

    var result = handler.search("kw", pageCriteria);

    assertThat(result.content())
        .containsExactly(
            new AssociationSearchResultData(
                declaredSkillProgressId, "Communication", EExternalSkillType.ROME4.name(), false));
  }

  @Test
  void toAssociatedElements_should_return_the_declared_skills_of_the_associations() {
    UUID traceId = UUID.randomUUID();
    UUID declaredSkillProgressId = UUID.randomUUID();

    var declaredSkillProgress = mock(DeclaredSkillProgress.class);
    when(declaredSkillProgress.getId()).thenReturn(declaredSkillProgressId);

    var association =
        Association.create(traceId, declaredSkillProgressId, EAssociationType.TRACE_DECLARED_SKILL);

    when(declaredSkillProgressService.findAllDeclaredSkillProgressesByIds(
            List.of(declaredSkillProgressId)))
        .thenReturn(List.of(declaredSkillProgress));

    var result = handler.toAssociatedElements(List.of(association), Trace.class, false);

    assertThat(result.declaredSkillAssociations())
        .singleElement()
        .satisfies(
            data -> {
              assertThat(data.associationId()).isEqualTo(association.getId());
              assertThat(data.declaredSkill()).isEqualTo(declaredSkillProgress);
            });
  }
}
