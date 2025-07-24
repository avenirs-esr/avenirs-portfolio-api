package fr.avenirsesr.portfolio.additionalskill.domain.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import fr.avenirsesr.portfolio.additionalskill.domain.model.AdditionalSkill;
import fr.avenirsesr.portfolio.additionalskill.domain.model.PathSegments;
import fr.avenirsesr.portfolio.additionalskill.domain.model.SegmentDetail;
import fr.avenirsesr.portfolio.additionalskill.domain.port.output.AdditionalSkillCache;
import fr.avenirsesr.portfolio.additionalskill.infrastructure.fixture.AdditionalSkillFixture;
import fr.avenirsesr.portfolio.additionalskill.infrastructure.fixture.PathSegmentsFixture;
import fr.avenirsesr.portfolio.additionalskill.infrastructure.fixture.SegmentDetailFixture;
import fr.avenirsesr.portfolio.shared.domain.model.PageCriteria;
import fr.avenirsesr.portfolio.shared.domain.model.PageInfo;
import fr.avenirsesr.portfolio.shared.domain.model.PagedResult;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AdditionalSkillServiceImplTest {

  @Mock private AdditionalSkillCache additionalSkillCache;

  @InjectMocks private AdditionalSkillServiceImpl service;

  private static final PageCriteria DEFAULT_PAGE_CRITERIA = new PageCriteria(1, 8);

  private PagedResult<AdditionalSkill> createFakePaginatedResult(int page, int size) {
    SegmentDetail issue = SegmentDetailFixture.create().withLibelle("issue").toModel();
    SegmentDetail target = SegmentDetailFixture.create().withLibelle("target").toModel();
    SegmentDetail macroSkill = SegmentDetailFixture.create().withLibelle("macroSkill").toModel();
    SegmentDetail skill = SegmentDetailFixture.create().withLibelle("skill").toModel();
    PathSegments pathSegments =
        PathSegmentsFixture.create()
            .withIssue(issue)
            .withTarget(target)
            .withMacroSkill(macroSkill)
            .withSkill(skill)
            .toModel();
    List<AdditionalSkill> skills =
        List.of(
            AdditionalSkillFixture.create()
                .withId(UUID.randomUUID())
                .withPathSegments(pathSegments)
                .toModel(),
            AdditionalSkillFixture.create()
                .withId(UUID.randomUUID())
                .withPathSegments(pathSegments)
                .toModel());
    PageInfo pageInfo = new PageInfo(page, size, 2);
    return new PagedResult<>(skills, pageInfo);
  }

  @Test
  void shouldUseDefaultPageAndSizeWhenBothAreNull() {
    PagedResult<AdditionalSkill> expected = createFakePaginatedResult(1, 8);
    when(additionalSkillCache.findAll(DEFAULT_PAGE_CRITERIA)).thenReturn(expected);

    PagedResult<AdditionalSkill> result = service.getAdditionalSkills(new PageCriteria(null, null));

    assertThat(result).isEqualTo(expected);
    verify(additionalSkillCache).findAll(DEFAULT_PAGE_CRITERIA);
  }

  @Test
  void shouldUseDefaultPageWhenPageIsNull() {
    PagedResult<AdditionalSkill> expected = createFakePaginatedResult(1, 20);
    when(additionalSkillCache.findAll(new PageCriteria(1, 20))).thenReturn(expected);

    PagedResult<AdditionalSkill> result = service.getAdditionalSkills(new PageCriteria(1, 20));

    assertThat(result).isEqualTo(expected);
    verify(additionalSkillCache).findAll(new PageCriteria(1, 20));
  }

  @Test
  void shouldUseDefaultPageSizeWhenPageSizeIsNull() {
    PagedResult<AdditionalSkill> expected = createFakePaginatedResult(2, 8);
    when(additionalSkillCache.findAll(new PageCriteria(2, 8))).thenReturn(expected);

    PagedResult<AdditionalSkill> result = service.getAdditionalSkills(new PageCriteria(2, null));

    assertThat(result).isEqualTo(expected);
    verify(additionalSkillCache).findAll(new PageCriteria(2, 8));
  }

  @Test
  void shouldUseProvidedPageAndSize() {
    PagedResult<AdditionalSkill> expected = createFakePaginatedResult(3, 15);
    when(additionalSkillCache.findAll(new PageCriteria(3, 15))).thenReturn(expected);

    PagedResult<AdditionalSkill> result = service.getAdditionalSkills(new PageCriteria(3, 15));

    assertThat(result).isEqualTo(expected);
    verify(additionalSkillCache).findAll(new PageCriteria(3, 15));
  }

  @Test
  void shouldSearchWithDefaultPageAndSizeWhenBothAreNull() {
    PagedResult<AdditionalSkill> expected = createFakePaginatedResult(1, 8);
    when(additionalSkillCache.findBySkillTitle("java", DEFAULT_PAGE_CRITERIA)).thenReturn(expected);

    PagedResult<AdditionalSkill> result =
        service.searchAdditionalSkills("java", new PageCriteria(null, null));

    assertThat(result).isEqualTo(expected);
    verify(additionalSkillCache).findBySkillTitle("java", DEFAULT_PAGE_CRITERIA);
  }

  @Test
  void shouldSearchWithDefaultPageWhenPageIsNull() {
    PagedResult<AdditionalSkill> expected = createFakePaginatedResult(1, 20);
    when(additionalSkillCache.findBySkillTitle("java", new PageCriteria(1, 20)))
        .thenReturn(expected);

    PagedResult<AdditionalSkill> result =
        service.searchAdditionalSkills("java", new PageCriteria(null, 20));

    assertThat(result).isEqualTo(expected);
    verify(additionalSkillCache).findBySkillTitle("java", new PageCriteria(1, 20));
  }

  @Test
  void shouldSearchWithDefaultPageSizeWhenPageSizeIsNull() {
    PagedResult<AdditionalSkill> expected = createFakePaginatedResult(2, 8);
    when(additionalSkillCache.findBySkillTitle("java", new PageCriteria(2, 8)))
        .thenReturn(expected);

    PagedResult<AdditionalSkill> result =
        service.searchAdditionalSkills("java", new PageCriteria(2, null));

    assertThat(result).isEqualTo(expected);
    verify(additionalSkillCache).findBySkillTitle("java", new PageCriteria(2, 8));
  }

  @Test
  void shouldSearchWithProvidedPageAndSize() {
    PagedResult<AdditionalSkill> expected = createFakePaginatedResult(3, 15);
    when(additionalSkillCache.findBySkillTitle("java", new PageCriteria(3, 15)))
        .thenReturn(expected);

    PagedResult<AdditionalSkill> result =
        service.searchAdditionalSkills("java", new PageCriteria(3, 15));

    assertThat(result).isEqualTo(expected);
    verify(additionalSkillCache).findBySkillTitle("java", new PageCriteria(3, 15));
  }
}
