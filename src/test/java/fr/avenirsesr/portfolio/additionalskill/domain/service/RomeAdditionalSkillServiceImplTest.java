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
import fr.avenirsesr.portfolio.additionalskill.domain.port.output.OpenSearch;
import fr.avenirsesr.portfolio.additionalskill.domain.port.output.RomeAdditionalSkillApi;
import fr.avenirsesr.portfolio.additionalskill.domain.port.output.repository.AdditionalSkillRepository;
import fr.avenirsesr.portfolio.additionalskill.domain.port.output.repository.Rome4VersionRepository;
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

  @Mock private OpenSearch openSearch;

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
    // When
    service.cleanAndCreateAdditionalSkillIndex();

    // Then
    verify(openSearch).cleanAndCreateAdditionalSkillIndex();
  }

  // --- synchronizeAndIndexAdditionalSkills ---
  @Test
  void shouldSaveAndIndexAdditionalSkills_WhenNewSkills() {
    // Given
    List<AdditionalSkill> inputSkills = List.of(additionalSkill1, additionalSkill2);
    when(additionalSkillRepository.findByPathSegmentsSkillCodeIn(anyList())).thenReturn(List.of());
    when(additionalSkillRepository.saveAll(anyList()))
        .thenAnswer(invocation -> invocation.getArgument(0));

    // When
    List<AdditionalSkill> result = service.synchronizeAndIndexAdditionalSkills(inputSkills);

    // Then
    assertThat(result).hasSize(2);
    verify(additionalSkillRepository)
        .findByPathSegmentsSkillCodeIn(List.of("skillCode1", "skillCode2"));
    verify(additionalSkillRepository).saveAll(anyList());
    verify(openSearch).indexAll(result);
  }

  @Test
  void shouldUpdateExistingSkillAndIndex() {
    // Given
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

    // When
    List<AdditionalSkill> result =
        service.synchronizeAndIndexAdditionalSkills(List.of(additionalSkill1));

    // Then
    assertThat(result).hasSize(1);
    assertThat(result.getFirst().getType()).isEqualTo(EAdditionalSkillType.ROME4);
    verify(openSearch).indexAll(result);
  }

  // --- checkRomeVersionUpdated ---
  @Test
  void shouldSaveNewVersion_WhenNoExistingVersion() {
    // Given
    Rome4Version newVersion = Rome4Version.create(1, Instant.now());
    when(romeAdditionalSkillApi.fetchRomeVersion()).thenReturn(newVersion);
    when(rome4VersionRepository.findFirstByOrderByVersionDesc()).thenReturn(Optional.empty());

    // When
    boolean result = service.checkRomeVersionUpdated();

    // Then
    assertTrue(result);
    verify(rome4VersionRepository).save(any(Rome4Version.class));
  }

  @Test
  void shouldSaveNewVersion_WhenNewerVersionFound() {
    // Given
    Rome4Version oldVersion = Rome4Version.create(1, Instant.now());
    Rome4Version newVersion = Rome4Version.create(2, Instant.now());

    when(romeAdditionalSkillApi.fetchRomeVersion()).thenReturn(newVersion);
    when(rome4VersionRepository.findFirstByOrderByVersionDesc())
        .thenReturn(Optional.of(oldVersion));

    // When
    boolean result = service.checkRomeVersionUpdated();

    // Then
    assertTrue(result);
    verify(rome4VersionRepository).save(any(Rome4Version.class));
  }

  @Test
  void shouldNotSave_WhenVersionIsUpToDate() {
    // Given
    Rome4Version oldVersion = Rome4Version.create(2, Instant.now());
    Rome4Version newVersion = Rome4Version.create(2, Instant.now());

    when(romeAdditionalSkillApi.fetchRomeVersion()).thenReturn(newVersion);
    when(rome4VersionRepository.findFirstByOrderByVersionDesc())
        .thenReturn(Optional.of(oldVersion));

    // When
    boolean result = service.checkRomeVersionUpdated();

    // Then
    assertFalse(result);
    verify(rome4VersionRepository, never()).save(any());
  }
}
