package fr.avenirsesr.portfolio.student.experience.domain.service;

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
import fr.avenirsesr.portfolio.student.experience.domain.exception.DeclaredExperienceNotFoundException;
import fr.avenirsesr.portfolio.student.experience.domain.model.DeclaredExperience;
import fr.avenirsesr.portfolio.student.experience.domain.model.enums.EExperienceType;
import fr.avenirsesr.portfolio.student.experience.domain.port.input.DeclaredExperienceService;
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
class DeclaredExperienceAssociationContextHandlerTest {

  @Mock private DeclaredExperienceService declaredExperienceService;
  @Mock private LoggedInUserService loggedInUserService;

  @InjectMocks private DeclaredExperienceAssociationContextHandler handler;

  private DeclaredExperience declaredExperienceOf(UUID id, Student student) {
    var declaredExperience = mock(DeclaredExperience.class);
    when(declaredExperience.getId()).thenReturn(id);
    when(declaredExperience.getStudent()).thenReturn(student);

    return declaredExperience;
  }

  @Test
  void getContextType_should_return_the_declared_experience_context() {
    assertThat(handler.getContextType()).isEqualTo(EAssociationContextType.DECLARED_EXPERIENCE);
  }

  @Test
  void checkLoggedInStudentOwns_should_throw_when_a_declared_experience_does_not_exist() {
    UUID declaredExperienceId = UUID.randomUUID();

    when(declaredExperienceService.findAllByIds(List.of(declaredExperienceId)))
        .thenReturn(List.of());

    assertThatThrownBy(() -> handler.checkLoggedInStudentOwns(List.of(declaredExperienceId)))
        .isInstanceOf(DeclaredExperienceNotFoundException.class);
  }

  @Test
  void checkLoggedInStudentCanAssociate_should_throw_when_it_belongs_to_another_student() {
    UUID declaredExperienceId = UUID.randomUUID();

    var declaredExperience =
        declaredExperienceOf(declaredExperienceId, StudentFixture.create().toModel());

    when(declaredExperienceService.findAllByIds(List.of(declaredExperienceId)))
        .thenReturn(List.of(declaredExperience));
    when(loggedInUserService.getLoggedInStudent()).thenReturn(StudentFixture.create().toModel());

    assertThatThrownBy(
            () ->
                handler.checkLoggedInStudentCanAssociate(
                    declaredExperienceId, EAssociationType.TRACE_DECLARED_EXPERIENCE, 1))
        .isInstanceOf(UserNotAuthorizedException.class);
  }

  @Test
  void search_should_return_the_declared_experiences_with_their_type() {
    UUID declaredExperienceId = UUID.randomUUID();
    var pageCriteria = new PageCriteria(0, 10);

    var declaredExperience = mock(DeclaredExperience.class);
    when(declaredExperience.getId()).thenReturn(declaredExperienceId);
    when(declaredExperience.getTitle()).thenReturn("Backend developer");
    when(declaredExperience.getExperienceType()).thenReturn(EExperienceType.PROFESSIONAL);

    when(declaredExperienceService.search("kw", pageCriteria))
        .thenReturn(new PagedResult<>(List.of(declaredExperience), new PageInfo(0, 10, 1)));

    var result = handler.search("kw", AssociationSearchFilter.NONE, pageCriteria);

    assertThat(result.content())
        .containsExactly(
            new AssociationSearchResultData(
                declaredExperienceId,
                "Backend developer",
                EExperienceType.PROFESSIONAL.name(),
                false));
  }

  @Test
  void search_should_return_a_declared_experience_without_type() {
    UUID declaredExperienceId = UUID.randomUUID();
    var pageCriteria = new PageCriteria(0, 10);

    var declaredExperience = mock(DeclaredExperience.class);
    when(declaredExperience.getId()).thenReturn(declaredExperienceId);
    when(declaredExperience.getTitle()).thenReturn("Backend developer");
    when(declaredExperience.getExperienceType()).thenReturn(null);

    when(declaredExperienceService.search("kw", pageCriteria))
        .thenReturn(new PagedResult<>(List.of(declaredExperience), new PageInfo(0, 10, 1)));

    var result = handler.search("kw", AssociationSearchFilter.NONE, pageCriteria);

    assertThat(result.content())
        .containsExactly(
            new AssociationSearchResultData(
                declaredExperienceId, "Backend developer", null, false));
  }

  @Test
  void toAssociatedElements_should_return_the_declared_experiences_of_the_associations() {
    UUID traceId = UUID.randomUUID();
    UUID declaredExperienceId = UUID.randomUUID();

    var declaredExperience = mock(DeclaredExperience.class);
    when(declaredExperience.getId()).thenReturn(declaredExperienceId);

    var association =
        Association.create(
            traceId, declaredExperienceId, EAssociationType.TRACE_DECLARED_EXPERIENCE);

    when(declaredExperienceService.findAllByIds(List.of(declaredExperienceId)))
        .thenReturn(List.of(declaredExperience));

    var result = handler.toAssociatedElements(List.of(association), Trace.class, false);

    assertThat(result.declaredExperienceAssociations())
        .singleElement()
        .satisfies(
            data -> {
              assertThat(data.associationId()).isEqualTo(association.getId());
              assertThat(data.declaredExperience()).isEqualTo(declaredExperience);
            });
  }
}
