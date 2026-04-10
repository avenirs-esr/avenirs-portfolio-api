package fr.avenirsesr.portfolio.declaredskill.domain.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import fr.avenirsesr.portfolio.common.externalskill.application.adapter.dto.ExternalSkillDTO;
import fr.avenirsesr.portfolio.common.externalskill.domain.model.enums.EExternalSkillType;
import fr.avenirsesr.portfolio.common.testutils.BddLogger;
import fr.avenirsesr.portfolio.declaredskill.domain.model.DeclaredSkill;
import fr.avenirsesr.portfolio.declaredskill.domain.port.output.repository.DeclaredSkillRepository;
import fr.avenirsesr.portfolio.declaredskill.infrastructure.adapter.client.ExternalSkillClient;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DeclaredSkillSyncServiceImplTest {

  @Mock private DeclaredSkillRepository declaredSkillRepository;

  @Mock private ExternalSkillClient externalSkillClient;

  @InjectMocks private DeclaredSkillSyncServiceImpl service;

  @Test
  void shouldReturnExistingDeclaredSkillWhenAlreadyExists() {
    BddLogger.given("an existing DeclaredSkill in database");
    UUID id = UUID.randomUUID();
    DeclaredSkill existing =
        DeclaredSkill.create(id, "Existing Skill", EExternalSkillType.ROME4, List.of("A", "B"));
    when(declaredSkillRepository.findById(id)).thenReturn(Optional.of(existing));

    BddLogger.when("calling getOrCreateFromExternalSkill");
    Optional<DeclaredSkill> result = service.getOrCreateFromExternalSkill(id);

    BddLogger.then("it should return the existing skill without calling external API");
    assertThat(result).isPresent();
    assertThat(result.get()).isEqualTo(existing);
    verify(externalSkillClient, never()).getById(any());
    verify(declaredSkillRepository, never()).saveOrGet(any());
  }

  @Test
  void shouldCreateNewDeclaredSkillWhenNotExists() {
    BddLogger.given("no existing DeclaredSkill and a valid external skill");
    UUID id = UUID.randomUUID();
    ExternalSkillDTO externalSkillDTO =
        new ExternalSkillDTO(
            id, "Java Programming", List.of("IT", "Development"), EExternalSkillType.ROME4);

    when(declaredSkillRepository.findById(id)).thenReturn(Optional.empty());
    when(externalSkillClient.getById(id)).thenReturn(Optional.of(externalSkillDTO));

    DeclaredSkill savedSkill =
        DeclaredSkill.create(
            id, "Java Programming", EExternalSkillType.ROME4, List.of("IT", "Development"));
    when(declaredSkillRepository.saveOrGet(any(DeclaredSkill.class))).thenReturn(savedSkill);

    BddLogger.when("calling getOrCreateFromExternalSkill");
    Optional<DeclaredSkill> result = service.getOrCreateFromExternalSkill(id);

    BddLogger.then("it should create and save a new DeclaredSkill");
    assertThat(result).isPresent();

    ArgumentCaptor<DeclaredSkill> captor = ArgumentCaptor.forClass(DeclaredSkill.class);
    verify(declaredSkillRepository).saveOrGet(captor.capture());

    DeclaredSkill captured = captor.getValue();
    assertThat(captured.getId()).isEqualTo(id);
    assertThat(captured.getLibelle()).isEqualTo("Java Programming");
    assertThat(captured.getType()).isEqualTo(EExternalSkillType.ROME4);
    assertThat(captured.getPathSegments()).containsExactly("IT", "Development");
  }

  @Test
  void shouldReturnEmptyWhenExternalSkillNotFoundInInteroperability() {
    BddLogger.given("no existing DeclaredSkill and external skill not found in interoperability");
    UUID id = UUID.randomUUID();

    when(declaredSkillRepository.findById(id)).thenReturn(Optional.empty());
    when(externalSkillClient.getById(id)).thenReturn(Optional.empty());

    BddLogger.when("calling getOrCreateFromExternalSkill");
    Optional<DeclaredSkill> result = service.getOrCreateFromExternalSkill(id);

    BddLogger.then("it should return empty and not save anything");
    assertThat(result).isEmpty();
    verify(declaredSkillRepository, never()).saveOrGet(any());
  }

  @Test
  void shouldHandleNullPathSegments() {
    BddLogger.given("external skill with null pathSegments");
    UUID id = UUID.randomUUID();
    ExternalSkillDTO externalSkillDTO =
        new ExternalSkillDTO(id, "Simple Skill", null, EExternalSkillType.XXI);

    when(declaredSkillRepository.findById(id)).thenReturn(Optional.empty());
    when(externalSkillClient.getById(id)).thenReturn(Optional.of(externalSkillDTO));

    DeclaredSkill savedSkill =
        DeclaredSkill.create(id, "Simple Skill", EExternalSkillType.XXI, null);
    when(declaredSkillRepository.saveOrGet(any(DeclaredSkill.class))).thenReturn(savedSkill);

    BddLogger.when("calling getOrCreateFromExternalSkill");
    Optional<DeclaredSkill> result = service.getOrCreateFromExternalSkill(id);

    BddLogger.then("it should create skill with empty pathSegments list");
    assertThat(result).isPresent();

    ArgumentCaptor<DeclaredSkill> captor = ArgumentCaptor.forClass(DeclaredSkill.class);
    verify(declaredSkillRepository).saveOrGet(captor.capture());

    DeclaredSkill captured = captor.getValue();
    assertThat(captured.getPathSegments()).isNotNull();
    assertThat(captured.getPathSegments()).isEmpty();
  }

  @Test
  void shouldMapExternalSkillTypeToDeclaredSkillType() {
    BddLogger.given("external skills with different types");
    UUID id1 = UUID.randomUUID();
    UUID id2 = UUID.randomUUID();

    ExternalSkillDTO rome4Skill =
        new ExternalSkillDTO(id1, "ROME4 Skill", List.of(), EExternalSkillType.ROME4);
    ExternalSkillDTO xxiSkill =
        new ExternalSkillDTO(id2, "XXI Skill", List.of(), EExternalSkillType.XXI);

    when(declaredSkillRepository.findById(any())).thenReturn(Optional.empty());
    when(externalSkillClient.getById(id1)).thenReturn(Optional.of(rome4Skill));
    when(externalSkillClient.getById(id2)).thenReturn(Optional.of(xxiSkill));

    when(declaredSkillRepository.saveOrGet(any(DeclaredSkill.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    BddLogger.when("creating skills with different types");
    service.getOrCreateFromExternalSkill(id1);
    service.getOrCreateFromExternalSkill(id2);

    BddLogger.then("types should be correctly mapped");
    ArgumentCaptor<DeclaredSkill> captor = ArgumentCaptor.forClass(DeclaredSkill.class);
    verify(declaredSkillRepository, times(2)).saveOrGet(captor.capture());

    List<DeclaredSkill> savedSkills = captor.getAllValues();
    assertThat(savedSkills.get(0).getType()).isEqualTo(EExternalSkillType.ROME4);
    assertThat(savedSkills.get(1).getType()).isEqualTo(EExternalSkillType.XXI);
  }

  @Test
  void shouldReturnExistingDeclaredSkillWhenSaveFailsWithUniqueConstraint() {
    BddLogger.given("no existing DeclaredSkill at first check but a concurrent insert occurs");
    UUID id = UUID.randomUUID();
    ExternalSkillDTO externalSkillDTO =
        new ExternalSkillDTO(
            id, "Concurrent Skill", List.of("IT", "Development"), EExternalSkillType.ROME4);

    DeclaredSkill existingSkill =
        DeclaredSkill.create(
            id, "Concurrent Skill", EExternalSkillType.ROME4, List.of("IT", "Development"));

    when(declaredSkillRepository.findById(id))
        .thenReturn(Optional.empty())
        .thenReturn(Optional.of(existingSkill));
    when(externalSkillClient.getById(id)).thenReturn(Optional.of(externalSkillDTO));
    when(declaredSkillRepository.saveOrGet(any(DeclaredSkill.class))).thenReturn(existingSkill);

    BddLogger.when("calling getOrCreateFromExternalSkill while a concurrent insert happens");
    Optional<DeclaredSkill> result = service.getOrCreateFromExternalSkill(id);

    BddLogger.then("it should return the existing skill created by the concurrent operation");
    assertThat(result).isPresent();
    assertThat(result.get()).isEqualTo(existingSkill);

    verify(declaredSkillRepository, times(1)).findById(id);
    verify(declaredSkillRepository).saveOrGet(any(DeclaredSkill.class));
  }
}
