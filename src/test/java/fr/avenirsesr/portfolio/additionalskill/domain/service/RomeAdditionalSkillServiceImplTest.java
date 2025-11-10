package fr.avenirsesr.portfolio.additionalskill.domain.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import fr.avenirsesr.portfolio.additionalskill.domain.model.AdditionalSkill;
import fr.avenirsesr.portfolio.additionalskill.domain.model.AdditionalSkillCategory;
import fr.avenirsesr.portfolio.additionalskill.domain.model.enums.EAdditionalSkillCategoryType;
import fr.avenirsesr.portfolio.additionalskill.domain.model.enums.EAdditionalSkillType;
import fr.avenirsesr.portfolio.additionalskill.domain.port.output.repository.AdditionalSkillRepository;
import fr.avenirsesr.portfolio.common.testutils.BddLogger;
import fr.avenirsesr.portfolio.interoperability.additionalskill.rome.domain.model.Rome4Version;
import fr.avenirsesr.portfolio.interoperability.additionalskill.rome.domain.port.output.RomeAdditionalSkillApi;
import fr.avenirsesr.portfolio.interoperability.additionalskill.rome.domain.port.output.repository.Rome4VersionRepository;
import fr.avenirsesr.portfolio.interoperability.additionalskill.rome.domain.service.RomeAdditionalSkillServiceImpl;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RomeAdditionalSkillServiceImplTest {

  @Mock private AdditionalSkillRepository additionalSkillRepository;

  @Mock private Rome4VersionRepository rome4VersionRepository;

  @Mock private RomeAdditionalSkillApi romeAdditionalSkillApi;

  @InjectMocks private RomeAdditionalSkillServiceImpl service;

  private AdditionalSkill additionalSkill1;
  private AdditionalSkill additionalSkill2;

  @BeforeEach
  void setUp() {
    var domain1 =
        AdditionalSkillCategory.of("domainLibelle1", null, EAdditionalSkillCategoryType.DOMAIN);
    var issue1 =
        AdditionalSkillCategory.of("issueLibelle1", domain1, EAdditionalSkillCategoryType.ISSUE);
    var target1 =
        AdditionalSkillCategory.of("targetLibelle1", issue1, EAdditionalSkillCategoryType.TARGET);
    var macro1 =
        AdditionalSkillCategory.of(
            "macroSkillLibelle1", target1, EAdditionalSkillCategoryType.MACRO_SKILL);

    additionalSkill1 =
        AdditionalSkill.create("skillLibelle1", "skillCode1", macro1, EAdditionalSkillType.ROME4);

    var domain2 =
        AdditionalSkillCategory.of("domainLibelle2", null, EAdditionalSkillCategoryType.DOMAIN);
    var issue2 =
        AdditionalSkillCategory.of("issueLibelle2", domain2, EAdditionalSkillCategoryType.ISSUE);
    var targe2 =
        AdditionalSkillCategory.of("targetLibelle2", issue2, EAdditionalSkillCategoryType.TARGET);
    var macro2 =
        AdditionalSkillCategory.of(
            "macroSkillLibelle2", targe2, EAdditionalSkillCategoryType.MACRO_SKILL);

    additionalSkill2 =
        AdditionalSkill.create("skillLibelle2", "skillCode2", macro2, EAdditionalSkillType.ROME4);
  }

  // --- synchronizeAndIndexAdditionalSkills ---
  @Test
  void shouldSaveAndIndexAdditionalSkills_WhenNewSkills() {
    BddLogger.given("the method synchronizeAndIndexAdditionalSkills");
    List<AdditionalSkill> inputSkills = List.of(additionalSkill1, additionalSkill2);
    when(additionalSkillRepository.findAllByExternalId(anyList())).thenReturn(List.of());
    when(additionalSkillRepository.saveAll(anyList()))
        .thenAnswer(invocation -> invocation.getArgument(0));

    BddLogger.when(
        "calling the method from the RomeAdditionalSkillServiceImpl service with new skills");
    List<AdditionalSkill> result = service.synchronizeAndSaveAdditionalSkills(inputSkills);

    BddLogger.then("it should save and index additional skills");
    assertThat(result).hasSize(2);
    verify(additionalSkillRepository).findAllByExternalId(List.of("skillCode1", "skillCode2"));
    verify(additionalSkillRepository).saveAll(anyList());
  }

  @Test
  void shouldUpdateExistingSkillAndIndex() {
    BddLogger.given("the method synchronizeAndIndexAdditionalSkills");
    var domain1 =
        AdditionalSkillCategory.of("domainLibelle1", null, EAdditionalSkillCategoryType.DOMAIN);
    var issue1 =
        AdditionalSkillCategory.of("issueLibelle1", domain1, EAdditionalSkillCategoryType.ISSUE);
    var target1 =
        AdditionalSkillCategory.of("targetLibelle1", issue1, EAdditionalSkillCategoryType.TARGET);
    var macro1 =
        AdditionalSkillCategory.of(
            "macroSkillLibelle1", target1, EAdditionalSkillCategoryType.MACRO_SKILL);
    AdditionalSkill existingSkill =
        AdditionalSkill.create("skillLibelle1", "skillCode1", macro1, EAdditionalSkillType.ROME4);

    when(additionalSkillRepository.findAllByExternalId(anyList()))
        .thenReturn(List.of(existingSkill));
    when(additionalSkillRepository.saveAll(anyList()))
        .thenAnswer(invocation -> invocation.getArgument(0));

    BddLogger.when(
        "calling the method from the RomeAdditionalSkillServiceImpl service with existing skills");
    List<AdditionalSkill> result =
        service.synchronizeAndSaveAdditionalSkills(List.of(additionalSkill1));

    BddLogger.then("it should update existing additional skill and index");
    assertThat(result).hasSize(1);
    assertThat(result.getFirst().getType()).isEqualTo(EAdditionalSkillType.ROME4);
  }

  // --- checkRomeVersionUpdated ---
  @Test
  void shouldSaveNewVersion_WhenNoExistingVersion() {
    BddLogger.given("the method checkRomeVersionUpdated");
    Rome4Version newVersion = Rome4Version.create(1, Instant.now());
    when(romeAdditionalSkillApi.fetchRomeVersion()).thenReturn(newVersion);
    when(rome4VersionRepository.findFirstByOrderByVersionDesc()).thenReturn(Optional.empty());

    BddLogger.when(
        "calling the method from the RomeAdditionalSkillServiceImpl service with no existing"
            + " version");
    boolean result = service.checkRomeVersionUpdated();

    BddLogger.then("it should save the new version");
    assertTrue(result);
    verify(rome4VersionRepository).save(any(Rome4Version.class));
  }

  @Test
  void shouldSaveNewVersion_WhenNewerVersionFound() {
    BddLogger.given("the method checkRomeVersionUpdated");
    Rome4Version oldVersion = Rome4Version.create(1, Instant.now());
    Rome4Version newVersion = Rome4Version.create(2, Instant.now());

    when(romeAdditionalSkillApi.fetchRomeVersion()).thenReturn(newVersion);
    when(rome4VersionRepository.findFirstByOrderByVersionDesc())
        .thenReturn(Optional.of(oldVersion));

    BddLogger.when(
        "calling the method from the RomeAdditionalSkillServiceImpl service with newer version");
    boolean result = service.checkRomeVersionUpdated();

    BddLogger.then("it should save the new version");
    assertTrue(result);
    verify(rome4VersionRepository).save(any(Rome4Version.class));
  }

  @Test
  void shouldNotSave_WhenVersionIsUpToDate() {
    BddLogger.given("the method checkRomeVersionUpdated");
    Rome4Version oldVersion = Rome4Version.create(2, Instant.now());
    Rome4Version newVersion = Rome4Version.create(2, Instant.now());

    when(romeAdditionalSkillApi.fetchRomeVersion()).thenReturn(newVersion);
    when(rome4VersionRepository.findFirstByOrderByVersionDesc())
        .thenReturn(Optional.of(oldVersion));

    BddLogger.when(
        "calling the method from the RomeAdditionalSkillServiceImpl service with up-to-date"
            + " version");
    boolean result = service.checkRomeVersionUpdated();

    BddLogger.then("it should not save the new version");
    assertFalse(result);
    verify(rome4VersionRepository, never()).save(any());
  }
}
