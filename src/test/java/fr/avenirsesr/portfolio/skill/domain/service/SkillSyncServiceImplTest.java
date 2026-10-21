package fr.avenirsesr.portfolio.skill.domain.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import fr.avenirsesr.portfolio.common.skill.application.adapter.dto.SkillCatalogDTO;
import fr.avenirsesr.portfolio.common.skill.application.adapter.dto.SkillNodeDTO;
import fr.avenirsesr.portfolio.common.skill.domain.model.enums.ESkillKind;
import fr.avenirsesr.portfolio.common.skill.domain.model.enums.ESkillReferentialType;
import fr.avenirsesr.portfolio.common.testutils.BddLogger;
import fr.avenirsesr.portfolio.skill.domain.model.NationalSkillDetails;
import fr.avenirsesr.portfolio.skill.domain.model.Skill;
import fr.avenirsesr.portfolio.skill.domain.model.SkillReferential;
import fr.avenirsesr.portfolio.skill.domain.port.output.client.SkillCatalogClient;
import fr.avenirsesr.portfolio.skill.domain.port.output.repository.NationalSkillDetailsRepository;
import fr.avenirsesr.portfolio.skill.domain.port.output.repository.SkillReferentialRepository;
import fr.avenirsesr.portfolio.skill.domain.port.output.repository.SkillRepository;
import fr.avenirsesr.portfolio.skill.infrastructure.fixture.SkillFixture;
import fr.avenirsesr.portfolio.skill.infrastructure.fixture.SkillReferentialFixture;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SkillSyncServiceImplTest {

  @Mock private SkillCatalogClient skillCatalogClient;
  @Mock private SkillRepository skillRepository;
  @Mock private SkillReferentialRepository skillReferentialRepository;
  @Mock private NationalSkillDetailsRepository nationalSkillDetailsRepository;

  @InjectMocks private SkillSyncServiceImpl service;

  private SkillReferential referential;

  private static final UUID DOMAIN_ID = UUID.randomUUID();
  private static final UUID ISSUE_ID = UUID.randomUUID();
  private static final UUID SKILL_ID = UUID.randomUUID();

  @BeforeEach
  void setUp() {
    referential = SkillReferentialFixture.create().withType(ESkillReferentialType.XXI).toModel();
  }

  private SkillNodeDTO node(
      UUID id, String code, String title, UUID parentId, ESkillKind kind, boolean assessable) {
    return new SkillNodeDTO(id, code, title, null, parentId, kind, assessable);
  }

  private SkillCatalogDTO catalogOf(List<SkillNodeDTO> nodes) {
    return new SkillCatalogDTO(ESkillReferentialType.XXI, "XXI", "XXI", nodes);
  }

  @SuppressWarnings("unchecked")
  private List<Skill> capturedSavedSkills() {
    ArgumentCaptor<List<Skill>> captor = ArgumentCaptor.forClass(List.class);
    verify(skillRepository).saveAll(captor.capture());
    return captor.getValue();
  }

  @Test
  void shouldSkipTheSynchronizationWhenNoCatalogIsAvailable() {
    BddLogger.given("an unreachable interoperability service");
    when(skillCatalogClient.fetchNationalCatalog(ESkillReferentialType.ROME4))
        .thenReturn(Optional.empty());

    BddLogger.when("synchronizing");
    service.syncNationalCatalog(ESkillReferentialType.ROME4);

    BddLogger.then("nothing is written");
    verifyNoInteractions(
        skillRepository, skillReferentialRepository, nationalSkillDetailsRepository);
  }

  @Test
  void shouldCreateTheReferentialWhenItDoesNotExistYet() {
    BddLogger.given("a catalog for a referential that is not stored yet");
    when(skillReferentialRepository.findNationalByType(ESkillReferentialType.XXI))
        .thenReturn(Optional.empty());
    when(skillReferentialRepository.save(any())).thenReturn(referential);
    when(skillRepository.findAllByReferential(referential)).thenReturn(List.of());

    BddLogger.when("applying the catalog");
    service.applyCatalog(catalogOf(List.of(node(SKILL_ID, "1", "skill", null, null, true))));

    BddLogger.then("the referential is created without institution");
    ArgumentCaptor<SkillReferential> captor = ArgumentCaptor.forClass(SkillReferential.class);
    verify(skillReferentialRepository).save(captor.capture());
    assertThat(captor.getValue().getType()).isEqualTo(ESkillReferentialType.XXI);
    assertThat(captor.getValue().isNational()).isTrue();
  }

  @Test
  void shouldLinkEachSkillToItsParentEvenWhenNodesComeInAnyOrder() {
    BddLogger.given("a catalog listing the children before their parents");
    when(skillReferentialRepository.findNationalByType(ESkillReferentialType.XXI))
        .thenReturn(Optional.of(referential));
    when(skillReferentialRepository.save(referential)).thenReturn(referential);
    when(skillRepository.findAllByReferential(referential)).thenReturn(List.of());

    var catalog =
        catalogOf(
            List.of(
                node(SKILL_ID, "1", "skill", ISSUE_ID, null, true),
                node(ISSUE_ID, null, "issue", DOMAIN_ID, ESkillKind.ISSUE, false),
                node(DOMAIN_ID, null, "domain", null, ESkillKind.DOMAIN, false)));

    BddLogger.when("applying the catalog");
    service.applyCatalog(catalog);

    BddLogger.then("the hierarchy is rebuilt from the root down");
    List<Skill> saved = capturedSavedSkills();
    assertThat(saved).extracting(Skill::getTitle).containsExactly("domain", "issue", "skill");

    Skill skill = saved.stream().filter(s -> s.getId().equals(SKILL_ID)).findFirst().orElseThrow();
    assertThat(skill.getPathSegments()).containsExactly("domain", "issue");
    assertThat(skill.isAssessable()).isTrue();
  }

  @Test
  void shouldUpdateAnExistingSkillAndUnarchiveIt() {
    BddLogger.given("a stored skill that is archived and renamed upstream");
    Skill existing =
        SkillFixture.create()
            .withId(SKILL_ID)
            .withReferential(referential)
            .withTitle("old title")
            .withExternalCode("old code")
            .withArchived(true)
            .toModel();

    when(skillReferentialRepository.findNationalByType(ESkillReferentialType.XXI))
        .thenReturn(Optional.of(referential));
    when(skillReferentialRepository.save(referential)).thenReturn(referential);
    when(skillRepository.findAllByReferential(referential)).thenReturn(List.of(existing));

    BddLogger.when("applying a catalog holding the same identifier");
    service.applyCatalog(catalogOf(List.of(node(SKILL_ID, "1", "new title", null, null, true))));

    BddLogger.then("the stored skill is updated in place and unarchived");
    List<Skill> saved = capturedSavedSkills();
    assertThat(saved)
        .singleElement()
        .satisfies(
            skill -> {
              assertThat(skill.getId()).isEqualTo(SKILL_ID);
              assertThat(skill.getTitle()).isEqualTo("new title");
              assertThat(skill.getExternalCode()).isEqualTo("1");
              assertThat(skill.isArchived()).isFalse();
            });
    verify(skillRepository).archiveAll(List.of());
  }

  @Test
  void shouldArchiveTheSkillsThatLeftTheCatalog() {
    BddLogger.given("a stored skill that the catalog no longer holds");
    Skill removed =
        SkillFixture.create().withReferential(referential).withTitle("removed").toModel();
    Skill kept =
        SkillFixture.create()
            .withId(SKILL_ID)
            .withReferential(referential)
            .withTitle("kept")
            .toModel();

    when(skillReferentialRepository.findNationalByType(ESkillReferentialType.XXI))
        .thenReturn(Optional.of(referential));
    when(skillReferentialRepository.save(referential)).thenReturn(referential);
    when(skillRepository.findAllByReferential(referential)).thenReturn(List.of(removed, kept));

    BddLogger.when("applying the catalog");
    service.applyCatalog(catalogOf(List.of(node(SKILL_ID, "1", "kept", null, null, true))));

    BddLogger.then("only the missing skill is archived");
    verify(skillRepository).archiveAll(List.of(removed.getId()));
  }

  @Test
  void shouldReplaceTheNationalDetailsOfTheCategoryNodes() {
    BddLogger.given("a catalog holding a category and a skill");
    when(skillReferentialRepository.findNationalByType(ESkillReferentialType.XXI))
        .thenReturn(Optional.of(referential));
    when(skillReferentialRepository.save(referential)).thenReturn(referential);
    when(skillRepository.findAllByReferential(referential)).thenReturn(List.of());

    BddLogger.when("applying the catalog");
    service.applyCatalog(
        catalogOf(
            List.of(
                node(DOMAIN_ID, null, "domain", null, ESkillKind.DOMAIN, false),
                node(SKILL_ID, "1", "skill", DOMAIN_ID, null, true))));

    BddLogger.then("the previous details are dropped and only the category keeps a kind");
    verify(nationalSkillDetailsRepository).deleteAllBySkillIds(anyList());

    @SuppressWarnings("unchecked")
    ArgumentCaptor<List<NationalSkillDetails>> captor = ArgumentCaptor.forClass(List.class);
    verify(nationalSkillDetailsRepository).saveAll(captor.capture());
    assertThat(captor.getValue())
        .singleElement()
        .satisfies(
            details -> {
              assertThat(details.getSkill().getId()).isEqualTo(DOMAIN_ID);
              assertThat(details.getKind()).isEqualTo(ESkillKind.DOMAIN);
            });
  }

  @Test
  void shouldNotSaveAnyDetailsWhenNoNodeCarriesAKind() {
    BddLogger.given("a flat catalog of assessable skills");
    when(skillReferentialRepository.findNationalByType(ESkillReferentialType.XXI))
        .thenReturn(Optional.of(referential));
    when(skillReferentialRepository.save(referential)).thenReturn(referential);
    when(skillRepository.findAllByReferential(referential)).thenReturn(List.of());

    BddLogger.when("applying the catalog");
    service.applyCatalog(catalogOf(List.of(node(SKILL_ID, "1", "skill", null, null, true))));

    BddLogger.then("no detail is written");
    verify(nationalSkillDetailsRepository, never()).saveAll(anyList());
  }

  @Test
  void shouldSynchronizeEveryNationalReferential() {
    BddLogger.given("no reachable catalog");
    when(skillCatalogClient.fetchNationalCatalog(any())).thenReturn(Optional.empty());

    BddLogger.when("synchronizing all the national catalogs");
    service.syncNationalCatalogs();

    BddLogger.then("the four national referentials are requested");
    verify(skillCatalogClient).fetchNationalCatalog(ESkillReferentialType.ROME4);
    verify(skillCatalogClient).fetchNationalCatalog(ESkillReferentialType.XXI);
    verify(skillCatalogClient).fetchNationalCatalog(ESkillReferentialType.CASOC);
    verify(skillCatalogClient).fetchNationalCatalog(ESkillReferentialType.CASOL);
  }
}
