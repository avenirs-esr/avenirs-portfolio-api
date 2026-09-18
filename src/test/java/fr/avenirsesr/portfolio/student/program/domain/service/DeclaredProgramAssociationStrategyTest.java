package fr.avenirsesr.portfolio.student.program.domain.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import fr.avenirsesr.portfolio.common.data.domain.model.PageCriteria;
import fr.avenirsesr.portfolio.common.data.domain.model.PageInfo;
import fr.avenirsesr.portfolio.common.data.domain.model.PagedResult;
import fr.avenirsesr.portfolio.common.security.domain.exception.UserNotAuthorizedException;
import fr.avenirsesr.portfolio.shared.domain.port.input.LoggedInUserService;
import fr.avenirsesr.portfolio.student.association.domain.data.AssociationSearchResultData;
import fr.avenirsesr.portfolio.student.association.domain.filter.AssociationSearchFilter;
import fr.avenirsesr.portfolio.student.association.domain.model.Association;
import fr.avenirsesr.portfolio.student.association.domain.model.EAssociationContextType;
import fr.avenirsesr.portfolio.student.association.domain.model.EAssociationType;
import fr.avenirsesr.portfolio.student.program.domain.exception.DeclaredProgramNotFoundException;
import fr.avenirsesr.portfolio.student.program.domain.model.DeclaredProgram;
import fr.avenirsesr.portfolio.student.program.domain.port.input.DeclaredProgramService;
import fr.avenirsesr.portfolio.student.skill.domain.model.DeclaredSkillProgress;
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
class DeclaredProgramAssociationStrategyTest {

  @Mock private DeclaredProgramService declaredProgramService;
  @Mock private LoggedInUserService loggedInUserService;

  @InjectMocks private DeclaredProgramAssociationStrategy strategy;

  private DeclaredProgram declaredProgramOf(UUID id, Student student) {
    var declaredProgram = mock(DeclaredProgram.class);
    when(declaredProgram.getId()).thenReturn(id);
    when(declaredProgram.getStudent()).thenReturn(student);

    return declaredProgram;
  }

  @Test
  void getContextType_should_return_the_declared_program_context() {
    assertThat(strategy.getContextType()).isEqualTo(EAssociationContextType.DECLARED_PROGRAM);
  }

  @Test
  void checkLoggedInStudentOwns_should_throw_when_a_declared_program_does_not_exist() {
    UUID declaredProgramId = UUID.randomUUID();

    when(declaredProgramService.findAllByIds(List.of(declaredProgramId))).thenReturn(List.of());

    assertThatThrownBy(() -> strategy.checkLoggedInStudentOwns(List.of(declaredProgramId)))
        .isInstanceOf(DeclaredProgramNotFoundException.class);
  }

  @Test
  void checkLoggedInStudentCanAssociate_should_throw_when_it_belongs_to_another_student() {
    UUID declaredProgramId = UUID.randomUUID();

    var declaredProgram = declaredProgramOf(declaredProgramId, StudentFixture.create().toModel());

    when(declaredProgramService.findAllByIds(List.of(declaredProgramId)))
        .thenReturn(List.of(declaredProgram));
    when(loggedInUserService.getLoggedInStudent()).thenReturn(StudentFixture.create().toModel());

    assertThatThrownBy(
            () ->
                strategy.checkLoggedInStudentCanAssociate(
                    declaredProgramId, EAssociationType.DECLARED_PROGRAM_DECLARED_SKILL, 1))
        .isInstanceOf(UserNotAuthorizedException.class);
  }

  @Test
  void checkLoggedInStudentCanUnassociate_should_pass_when_the_student_owns_the_programs() {
    UUID declaredProgramId = UUID.randomUUID();
    var loggedInStudent = StudentFixture.create().toModel();

    var declaredProgram = declaredProgramOf(declaredProgramId, loggedInStudent);

    when(declaredProgramService.findAllByIds(List.of(declaredProgramId)))
        .thenReturn(List.of(declaredProgram));
    when(loggedInUserService.getLoggedInStudent()).thenReturn(loggedInStudent);

    strategy.checkLoggedInStudentCanUnassociate(List.of(declaredProgramId));
  }

  @Test
  void search_should_return_the_declared_programs_with_their_organization() {
    UUID declaredProgramId = UUID.randomUUID();
    var pageCriteria = new PageCriteria(0, 10);

    var declaredProgram = mock(DeclaredProgram.class);
    when(declaredProgram.getId()).thenReturn(declaredProgramId);
    when(declaredProgram.getTitle()).thenReturn("Stage développeur web");
    when(declaredProgram.getOrganization()).thenReturn("TechNova");

    when(declaredProgramService.search("kw", pageCriteria))
        .thenReturn(new PagedResult<>(List.of(declaredProgram), new PageInfo(0, 10, 1)));

    var result = strategy.search("kw", AssociationSearchFilter.NONE, pageCriteria);

    assertThat(result.content())
        .containsExactly(
            new AssociationSearchResultData(
                declaredProgramId, "Stage développeur web", "TechNova", false));
  }

  @Test
  void toAssociatedElements_should_return_the_declared_programs_of_the_associations() {
    UUID declaredProgramId = UUID.randomUUID();
    UUID declaredSkillId = UUID.randomUUID();

    var declaredProgram = mock(DeclaredProgram.class);
    when(declaredProgram.getId()).thenReturn(declaredProgramId);

    var association =
        Association.create(
            declaredProgramId, declaredSkillId, EAssociationType.DECLARED_PROGRAM_DECLARED_SKILL);

    when(declaredProgramService.findAllByIds(List.of(declaredProgramId)))
        .thenReturn(List.of(declaredProgram));

    var result =
        strategy.toAssociatedElements(List.of(association), DeclaredSkillProgress.class, false);

    assertThat(result.declaredProgramAssociations())
        .singleElement()
        .satisfies(
            data -> {
              assertThat(data.associationId()).isEqualTo(association.getId());
              assertThat(data.declaredProgram()).isEqualTo(declaredProgram);
            });
  }
}
