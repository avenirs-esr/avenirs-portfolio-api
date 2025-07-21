package fr.avenirsesr.portfolio.additionalskill.domain.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import fr.avenirsesr.portfolio.additionalskill.domain.model.AdditionalSkill;
import fr.avenirsesr.portfolio.additionalskill.domain.model.AdditionalSkillsPaginated;
import fr.avenirsesr.portfolio.additionalskill.domain.model.PathSegments;
import fr.avenirsesr.portfolio.additionalskill.domain.model.SegmentDetail;
import fr.avenirsesr.portfolio.additionalskill.domain.port.output.AdditionalSkillCache;
import fr.avenirsesr.portfolio.additionalskill.infrastructure.fixture.AdditionalSkillFixture;
import fr.avenirsesr.portfolio.additionalskill.infrastructure.fixture.PathSegmentsFixture;
import fr.avenirsesr.portfolio.additionalskill.infrastructure.fixture.SegmentDetailFixture;
import fr.avenirsesr.portfolio.shared.domain.model.PageInfo;
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

  private AdditionalSkillsPaginated createFakePaginatedResult(int page, int size) {
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
    PageInfo pageInfo = new PageInfo(size, 2, 1, page);
    return new AdditionalSkillsPaginated(skills, pageInfo);
  }

  @Test
  void shouldUseDefaultPageAndSizeWhenBothAreNull() {
    AdditionalSkillsPaginated expected = createFakePaginatedResult(0, 8);
    when(additionalSkillCache.findAll(0, 8)).thenReturn(expected);

    AdditionalSkillsPaginated result = service.getAdditionalSkills(null, null);

    assertThat(result).isEqualTo(expected);
    verify(additionalSkillCache).findAll(0, 8);
  }

  @Test
  void shouldUseDefaultPageWhenPageIsNull() {
    AdditionalSkillsPaginated expected = createFakePaginatedResult(0, 20);
    when(additionalSkillCache.findAll(0, 20)).thenReturn(expected);

    AdditionalSkillsPaginated result = service.getAdditionalSkills(null, 20);

    assertThat(result).isEqualTo(expected);
    verify(additionalSkillCache).findAll(0, 20);
  }

  @Test
  void shouldUseDefaultPageSizeWhenPageSizeIsNull() {
    AdditionalSkillsPaginated expected = createFakePaginatedResult(2, 8);
    when(additionalSkillCache.findAll(2, 8)).thenReturn(expected);

    AdditionalSkillsPaginated result = service.getAdditionalSkills(2, null);

    assertThat(result).isEqualTo(expected);
    verify(additionalSkillCache).findAll(2, 8);
  }

  @Test
  void shouldUseProvidedPageAndSize() {
    AdditionalSkillsPaginated expected = createFakePaginatedResult(3, 15);
    when(additionalSkillCache.findAll(3, 15)).thenReturn(expected);

    AdditionalSkillsPaginated result = service.getAdditionalSkills(3, 15);

    assertThat(result).isEqualTo(expected);
    verify(additionalSkillCache).findAll(3, 15);
  }
}
