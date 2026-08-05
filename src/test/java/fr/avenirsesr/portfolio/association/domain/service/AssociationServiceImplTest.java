package fr.avenirsesr.portfolio.association.domain.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

import fr.avenirsesr.portfolio.student.association.domain.data.AssociationData;
import fr.avenirsesr.portfolio.student.association.domain.exception.AssociationAlreadyExistException;
import fr.avenirsesr.portfolio.student.association.domain.exception.AssociationDoesNotExistException;
import fr.avenirsesr.portfolio.student.association.domain.model.Association;
import fr.avenirsesr.portfolio.student.association.domain.model.EAssociationType;
import fr.avenirsesr.portfolio.student.association.domain.port.output.repository.AssociationRepository;
import fr.avenirsesr.portfolio.student.association.domain.service.AssociationServiceImpl;
import fr.avenirsesr.portfolio.student.trace.domain.model.Trace;
import fr.avenirsesr.portfolio.user.domain.model.Student;
import fr.avenirsesr.portfolio.user.infrastructure.fixture.StudentFixture;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AssociationServiceImplTest {

  @Mock private AssociationRepository associationRepository;

  @InjectMocks private AssociationServiceImpl service;

  private Student student;

  @BeforeEach
  void setUp() {
    student = StudentFixture.create().toModel();
  }

  @Test
  void createAll_should_save_associations_when_valid() {

    UUID traceId = UUID.randomUUID();
    UUID activityId = UUID.randomUUID();

    var data =
        List.of(new AssociationData(activityId, traceId, EAssociationType.DECLARED_ACTIVITY_TRACE));

    when(associationRepository.findAllIn(data)).thenReturn(List.of());

    when(associationRepository.saveAll(any())).thenAnswer(i -> i.getArguments()[0]);

    var result = service.createAll(data);

    assertThat(result).hasSize(1);

    verify(associationRepository).saveAll(any());
  }

  @Test
  void createAll_should_throw_AssociationAlreadyExistException() {

    UUID traceId = UUID.randomUUID();
    UUID activityId = UUID.randomUUID();

    var data =
        List.of(new AssociationData(activityId, traceId, EAssociationType.DECLARED_ACTIVITY_TRACE));

    when(associationRepository.findAllIn(data)).thenReturn(List.of(mock(Association.class)));

    assertThatThrownBy(() -> service.createAll(data))
        .isInstanceOf(AssociationAlreadyExistException.class);
  }

  @Test
  void deleteAllByIds_should_delete_when_all_ids_exist() {

    UUID id1 = UUID.randomUUID();
    UUID id2 = UUID.randomUUID();

    Association a1 = mock(Association.class);
    Association a2 = mock(Association.class);

    when(a1.getId()).thenReturn(id1);
    when(a2.getId()).thenReturn(id2);

    when(associationRepository.findAllById(List.of(id1, id2))).thenReturn(List.of(a1, a2));

    service.deleteAllByIds(List.of(id1, id2));

    verify(associationRepository).removeAllFromDatabase(List.of(a1, a2));
  }

  @Test
  void deleteAllByIds_should_throw_when_one_id_not_found() {

    UUID id1 = UUID.randomUUID();
    UUID id2 = UUID.randomUUID();

    Association a1 = mock(Association.class);

    when(a1.getId()).thenReturn(id1);

    when(associationRepository.findAllById(List.of(id1, id2))).thenReturn(List.of(a1));

    assertThatThrownBy(() -> service.deleteAllByIds(List.of(id1, id2)))
        .isInstanceOf(AssociationDoesNotExistException.class);

    verify(associationRepository, never()).removeAllFromDatabase(anyList());
  }

  @Test
  void deleteAllByIds_should_throw_when_none_found() {

    UUID id1 = UUID.randomUUID();

    when(associationRepository.findAllById(List.of(id1))).thenReturn(List.of());

    assertThatThrownBy(() -> service.deleteAllByIds(List.of(id1)))
        .isInstanceOf(AssociationDoesNotExistException.class);

    verify(associationRepository, never()).removeAllFromDatabase(anyList());
  }

  @Test
  void countAllOf_should_delegate_to_repository() {
    UUID traceId = UUID.randomUUID();
    Map<UUID, Long> expected = Map.of(traceId, 2L);

    when(associationRepository.countAllOf(
            List.of(traceId), Trace.class, EAssociationType.DECLARED_ACTIVITY_TRACE))
        .thenReturn(expected);

    var result =
        service.countAllOf(List.of(traceId), Trace.class, EAssociationType.DECLARED_ACTIVITY_TRACE);

    assertThat(result).isSameAs(expected);
  }

  @Test
  void countAllOf_should_return_empty_map_without_querying_when_ids_empty() {
    var result =
        service.countAllOf(List.of(), Trace.class, EAssociationType.DECLARED_ACTIVITY_TRACE);

    assertThat(result).isEmpty();
    verify(associationRepository, never()).countAllOf(any(), any(), any());
  }
}
