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
import fr.avenirsesr.portfolio.additionalskill.domain.model.PathSegments;
import fr.avenirsesr.portfolio.additionalskill.domain.model.Rome4Version;
import fr.avenirsesr.portfolio.additionalskill.domain.model.SegmentDetail;
import fr.avenirsesr.portfolio.additionalskill.domain.model.enums.EAdditionalSkillType;
import fr.avenirsesr.portfolio.additionalskill.domain.port.output.OpenSearchIndex;
import fr.avenirsesr.portfolio.additionalskill.domain.port.output.RomeAdditionalSkillApi;
import fr.avenirsesr.portfolio.additionalskill.domain.port.output.repository.AdditionalSkillRepository;
import fr.avenirsesr.portfolio.additionalskill.domain.port.output.repository.Rome4VersionRepository;
import fr.avenirsesr.portfolio.common.testutils.BddLogger;
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

  @Mock private OpenSearchIndex openSearchIndex;

  @InjectMocks private RomeAdditionalSkillServiceImpl service;

  private AdditionalSkill additionalSkill1;
  private AdditionalSkill additionalSkill2;

  @BeforeEach
  void setUp() {
    additionalSkill1 =
        AdditionalSkill.create(
            PathSegments.create(
                SegmentDetail.create("skillCode1", "skillLibelle1"),
                SegmentDetail.create("macroSkillCode1", "macroSkillLibelle1"),
                SegmentDetail.create("targetCode1", "targetLibelle1"),
                SegmentDetail.create("issueCode1", "issueLibelle1"),
                SegmentDetail.create("domainCode1", "domainLibelle1")),
            EAdditionalSkillType.ROME4);

    additionalSkill2 =
        AdditionalSkill.create(
            PathSegments.create(
                SegmentDetail.create("skillCode2", "skillLibelle2"),
                SegmentDetail.create("macroSkillCode2", "macroSkillLibelle2"),
                SegmentDetail.create("targetCode2", "targetLibelle2"),
                SegmentDetail.create("issueCode2", "issueLibelle2"),
                SegmentDetail.create("domainCode2", "domainLibelle2")),
            EAdditionalSkillType.ROME4);
  }

  // --- cleanAndCreateAdditionalSkillIndex ---
  @Test
  void shouldDelegateCleanAndCreateAdditionalSkillIndex() {
    BddLogger.given("the method cleanAndCreateAdditionalSkillIndex");

    BddLogger.when("calling the method from the RomeAdditionalSkillServiceImpl service");
    service.cleanAndCreateAdditionalSkillIndex();

    BddLogger.then("it should delegate the method to openSearchIndex");
    verify(openSearchIndex).cleanAndCreateAdditionalSkillIndex();
  }

  // --- synchronizeAndIndexAdditionalSkills ---
  @Test
  void shouldSaveAndIndexAdditionalSkills_WhenNewSkills() {
    BddLogger.given("the method synchronizeAndIndexAdditionalSkills");
    List<AdditionalSkill> inputSkills = List.of(additionalSkill1, additionalSkill2);
    when(additionalSkillRepository.findByPathSegmentsSkillCodeIn(anyList())).thenReturn(List.of());
    when(additionalSkillRepository.saveAll(anyList()))
        .thenAnswer(invocation -> invocation.getArgument(0));

    BddLogger.when(
        "calling the method from the RomeAdditionalSkillServiceImpl service with new skills");
    List<AdditionalSkill> result = service.synchronizeAndIndexAdditionalSkills(inputSkills);

    BddLogger.then("it should save and index additional skills");
    assertThat(result).hasSize(2);
    verify(additionalSkillRepository)
        .findByPathSegmentsSkillCodeIn(List.of("skillCode1", "skillCode2"));
    verify(additionalSkillRepository).saveAll(anyList());
    verify(openSearchIndex).indexAll(result);
  }

  @Test
  void shouldUpdateExistingSkillAndIndex() {
    BddLogger.given("the method synchronizeAndIndexAdditionalSkills");
    AdditionalSkill existingSkill =
        AdditionalSkill.create(
            PathSegments.create(
                SegmentDetail.create("skillCode1", "newSkillLibelle1"),
                SegmentDetail.create("macroSkillCode1", "macroSkillLibelle1"),
                SegmentDetail.create("targetCode1", "targetLibelle1"),
                SegmentDetail.create("issueCode1", "issueLibelle1"),
                SegmentDetail.create("domainCode1", "domainLibelle1")),
            EAdditionalSkillType.ROME4);

    when(additionalSkillRepository.findByPathSegmentsSkillCodeIn(anyList()))
        .thenReturn(List.of(existingSkill));
    when(additionalSkillRepository.saveAll(anyList()))
        .thenAnswer(invocation -> invocation.getArgument(0));

    BddLogger.when(
        "calling the method from the RomeAdditionalSkillServiceImpl service with existing skills");
    List<AdditionalSkill> result =
        service.synchronizeAndIndexAdditionalSkills(List.of(additionalSkill1));

    BddLogger.then("it should update existing additional skill and index");
    assertThat(result).hasSize(1);
    assertThat(result.getFirst().getType()).isEqualTo(EAdditionalSkillType.ROME4);
    verify(openSearchIndex).indexAll(result);
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
