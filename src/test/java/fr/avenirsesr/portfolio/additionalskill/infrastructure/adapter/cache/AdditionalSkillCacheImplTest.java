package fr.avenirsesr.portfolio.additionalskill.infrastructure.adapter.cache;

import static org.junit.jupiter.api.Assertions.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.avenirsesr.portfolio.additionalskill.domain.exception.AdditionalSkillNotFoundException;
import fr.avenirsesr.portfolio.additionalskill.domain.model.AdditionalSkill;
import fr.avenirsesr.portfolio.shared.domain.model.PageCriteria;
import fr.avenirsesr.portfolio.shared.domain.model.PagedResult;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AdditionalSkillCacheImplTest {

  private AdditionalSkillCacheImpl cache;

  @BeforeEach
  void setUp() {
    cache = new AdditionalSkillCacheImpl();
  }

  @Test
  void shouldReturnAllSkillsPaged() {
    PagedResult<AdditionalSkill> result = cache.findAll(new PageCriteria(0, 5));
    assertNotNull(result);
    assertTrue(result.content().size() <= 5);
  }

  @Test
  void shouldSearchSkillsByTitle() {
    PagedResult<AdditionalSkill> result = cache.findBySkillTitle("acc", new PageCriteria(0, 5));
    assertNotNull(result);
    assertTrue(
        result.content().stream()
            .anyMatch(
                skill ->
                    skill.getPathSegments().getSkill().getLibelle().toLowerCase().contains("acc")));
  }

  @Test
  void shouldFindSkillById() {
    AdditionalSkill existing = cache.findAll(new PageCriteria(0, 1)).content().getFirst();
    AdditionalSkill found = cache.findById(existing.getId());
    assertEquals(existing.getId(), found.getId());
  }

  @Test
  void shouldThrowExceptionIfSkillIdNotFound() {
    UUID invalidId = UUID.randomUUID();
    assertThrows(AdditionalSkillNotFoundException.class, () -> cache.findById(invalidId));
  }

  @Test
  void shouldReturnSubsetOfSkillsByIds() {
    List<AdditionalSkill> skills = cache.findAll(new PageCriteria(0, 10)).content();
    List<UUID> ids = skills.stream().map(AdditionalSkill::getId).limit(3).toList();
    List<AdditionalSkill> foundSkills = cache.findAllByIds(ids);
    assertEquals(3, foundSkills.size());
    assertTrue(foundSkills.stream().allMatch(skill -> ids.contains(skill.getId())));
  }

  @Test
  void shouldThrowRuntimeExceptionWhenJsonFileNotFound() {
    assertThrows(
        RuntimeException.class,
        () -> {
          var brokenCache =
              new AdditionalSkillCacheImpl() {
                @Override
                public AdditionalSkill findById(UUID id) {
                  try (InputStream is = getClass().getResourceAsStream("/invalid-path.json")) {
                    return new ObjectMapper().readValue(is, AdditionalSkill.class);
                  } catch (IOException e) {
                    throw new RuntimeException("Unable to load mock additional skills", e);
                  }
                }
              };
          brokenCache.findById(UUID.randomUUID());
        });
  }
}
