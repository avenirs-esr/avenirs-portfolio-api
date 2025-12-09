package fr.avenirsesr.portfolio.additionalskill.domain.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import fr.avenirsesr.portfolio.additionalskill.domain.model.AdditionalSkill;
import fr.avenirsesr.portfolio.additionalskill.domain.model.enums.EAdditionalSkillType;
import fr.avenirsesr.portfolio.additionalskill.domain.port.output.repository.AdditionalSkillRepository;
import fr.avenirsesr.portfolio.additionalskill.infrastructure.adapter.client.ExternalSkillClient;
import fr.avenirsesr.portfolio.common.externalskill.application.adapter.dto.ExternalSkillDTO;
import fr.avenirsesr.portfolio.common.externalskill.domain.model.enums.EExternalSkillType;
import fr.avenirsesr.portfolio.common.testutils.BddLogger;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

@ExtendWith(MockitoExtension.class)
class AdditionalSkillSyncServiceImplTest {

  @Mock private AdditionalSkillRepository additionalSkillRepository;

  @Mock private ExternalSkillClient externalSkillClient;

  @InjectMocks private AdditionalSkillSyncServiceImpl service;

  @Test
  void shouldReturnExistingAdditionalSkillWhenAlreadyExists() {
    BddLogger.given("an existing AdditionalSkill in database");
    UUID externalSkillId = UUID.randomUUID();
    AdditionalSkill existing =
        AdditionalSkill.create(
            externalSkillId, "Existing Skill", EAdditionalSkillType.ROME4, List.of("A", "B"));
    when(additionalSkillRepository.findByExternalSkillId(externalSkillId))
        .thenReturn(Optional.of(existing));

    BddLogger.when("calling getOrCreateFromExternalSkill");
    Optional<AdditionalSkill> result = service.getOrCreateFromExternalSkill(externalSkillId);

    BddLogger.then("it should return the existing skill without calling external API");
    assertThat(result).isPresent();
    assertThat(result.get()).isEqualTo(existing);
    verify(externalSkillClient, never()).getById(any());
    verify(additionalSkillRepository, never()).save(any());
  }

  @Test
  void shouldCreateNewAdditionalSkillWhenNotExists() {
    BddLogger.given("no existing AdditionalSkill and a valid external skill");
    UUID externalSkillId = UUID.randomUUID();
    ExternalSkillDTO externalSkillDTO =
        new ExternalSkillDTO(
            externalSkillId,
            "Java Programming",
            List.of("IT", "Development"),
            EExternalSkillType.ROME4);

    when(additionalSkillRepository.findByExternalSkillId(externalSkillId))
        .thenReturn(Optional.empty());
    when(externalSkillClient.getById(externalSkillId)).thenReturn(Optional.of(externalSkillDTO));

    AdditionalSkill savedSkill =
        AdditionalSkill.create(
            externalSkillId,
            "Java Programming",
            EAdditionalSkillType.ROME4,
            List.of("IT", "Development"));
    when(additionalSkillRepository.save(any(AdditionalSkill.class))).thenReturn(savedSkill);

    BddLogger.when("calling getOrCreateFromExternalSkill");
    Optional<AdditionalSkill> result = service.getOrCreateFromExternalSkill(externalSkillId);

    BddLogger.then("it should create and save a new AdditionalSkill");
    assertThat(result).isPresent();

    ArgumentCaptor<AdditionalSkill> captor = ArgumentCaptor.forClass(AdditionalSkill.class);
    verify(additionalSkillRepository).save(captor.capture());

    AdditionalSkill captured = captor.getValue();
    assertThat(captured.getExternalSkillId()).isEqualTo(externalSkillId);
    assertThat(captured.getLibelle()).isEqualTo("Java Programming");
    assertThat(captured.getType()).isEqualTo(EAdditionalSkillType.ROME4);
    assertThat(captured.getPathSegments()).containsExactly("IT", "Development");
  }

  @Test
  void shouldReturnEmptyWhenExternalSkillNotFoundInInteroperability() {
    BddLogger.given("no existing AdditionalSkill and external skill not found in interoperability");
    UUID externalSkillId = UUID.randomUUID();

    when(additionalSkillRepository.findByExternalSkillId(externalSkillId))
        .thenReturn(Optional.empty());
    when(externalSkillClient.getById(externalSkillId)).thenReturn(Optional.empty());

    BddLogger.when("calling getOrCreateFromExternalSkill");
    Optional<AdditionalSkill> result = service.getOrCreateFromExternalSkill(externalSkillId);

    BddLogger.then("it should return empty and not save anything");
    assertThat(result).isEmpty();
    verify(additionalSkillRepository, never()).save(any());
  }

  @Test
  void shouldHandleNullPathSegments() {
    BddLogger.given("external skill with null pathSegments");
    UUID externalSkillId = UUID.randomUUID();
    ExternalSkillDTO externalSkillDTO =
        new ExternalSkillDTO(externalSkillId, "Simple Skill", null, EExternalSkillType.XXI);

    when(additionalSkillRepository.findByExternalSkillId(externalSkillId))
        .thenReturn(Optional.empty());
    when(externalSkillClient.getById(externalSkillId)).thenReturn(Optional.of(externalSkillDTO));

    AdditionalSkill savedSkill =
        AdditionalSkill.create(externalSkillId, "Simple Skill", EAdditionalSkillType.XXI, null);
    when(additionalSkillRepository.save(any(AdditionalSkill.class))).thenReturn(savedSkill);

    BddLogger.when("calling getOrCreateFromExternalSkill");
    Optional<AdditionalSkill> result = service.getOrCreateFromExternalSkill(externalSkillId);

    BddLogger.then("it should create skill with empty pathSegments list");
    assertThat(result).isPresent();

    ArgumentCaptor<AdditionalSkill> captor = ArgumentCaptor.forClass(AdditionalSkill.class);
    verify(additionalSkillRepository).save(captor.capture());

    AdditionalSkill captured = captor.getValue();
    assertThat(captured.getPathSegments()).isNotNull();
    assertThat(captured.getPathSegments()).isEmpty();
  }

  @Test
  void shouldMapExternalSkillTypeToAdditionalSkillType() {
    BddLogger.given("external skills with different types");
    UUID externalSkillId1 = UUID.randomUUID();
    UUID externalSkillId2 = UUID.randomUUID();

    ExternalSkillDTO rome4Skill =
        new ExternalSkillDTO(externalSkillId1, "ROME4 Skill", List.of(), EExternalSkillType.ROME4);
    ExternalSkillDTO xxiSkill =
        new ExternalSkillDTO(externalSkillId2, "XXI Skill", List.of(), EExternalSkillType.XXI);

    when(additionalSkillRepository.findByExternalSkillId(any())).thenReturn(Optional.empty());
    when(externalSkillClient.getById(externalSkillId1)).thenReturn(Optional.of(rome4Skill));
    when(externalSkillClient.getById(externalSkillId2)).thenReturn(Optional.of(xxiSkill));

    when(additionalSkillRepository.save(any(AdditionalSkill.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    BddLogger.when("creating skills with different types");
    service.getOrCreateFromExternalSkill(externalSkillId1);
    service.getOrCreateFromExternalSkill(externalSkillId2);

    BddLogger.then("types should be correctly mapped");
    ArgumentCaptor<AdditionalSkill> captor = ArgumentCaptor.forClass(AdditionalSkill.class);
    verify(additionalSkillRepository, times(2)).save(captor.capture());

    List<AdditionalSkill> savedSkills = captor.getAllValues();
    assertThat(savedSkills.get(0).getType()).isEqualTo(EAdditionalSkillType.ROME4);
    assertThat(savedSkills.get(1).getType()).isEqualTo(EAdditionalSkillType.XXI);
  }

  @Test
  void shouldReturnExistingAdditionalSkillWhenSaveFailsWithUniqueConstraint() {
    BddLogger.given("no existing AdditionalSkill at first check but a concurrent insert occurs");
    UUID externalSkillId = UUID.randomUUID();
    ExternalSkillDTO externalSkillDTO =
        new ExternalSkillDTO(
            externalSkillId,
            "Concurrent Skill",
            List.of("IT", "Development"),
            EExternalSkillType.ROME4);

    AdditionalSkill existingSkill =
        AdditionalSkill.create(
            externalSkillId,
            "Concurrent Skill",
            EAdditionalSkillType.ROME4,
            List.of("IT", "Development"));

    when(additionalSkillRepository.findByExternalSkillId(externalSkillId))
        .thenReturn(Optional.empty())
        .thenReturn(Optional.of(existingSkill));
    when(externalSkillClient.getById(externalSkillId)).thenReturn(Optional.of(externalSkillDTO));
    when(additionalSkillRepository.save(any(AdditionalSkill.class)))
        .thenThrow(new DataIntegrityViolationException("duplicate key"));

    BddLogger.when("calling getOrCreateFromExternalSkill while a concurrent insert happens");
    Optional<AdditionalSkill> result = service.getOrCreateFromExternalSkill(externalSkillId);

    BddLogger.then("it should return the existing skill created by the concurrent operation");
    assertThat(result).isPresent();
    assertThat(result.get()).isEqualTo(existingSkill);

    verify(additionalSkillRepository, times(2)).findByExternalSkillId(externalSkillId);
    verify(additionalSkillRepository).save(any(AdditionalSkill.class));
  }
}
