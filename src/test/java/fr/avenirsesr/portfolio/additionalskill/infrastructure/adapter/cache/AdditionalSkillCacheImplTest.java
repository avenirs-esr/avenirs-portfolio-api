package fr.avenirsesr.portfolio.additionalskill.infrastructure.adapter.cache;

import static org.junit.jupiter.api.Assertions.*;

import fr.avenirsesr.portfolio.additionalskill.domain.exception.AdditionalSkillNotFoundException;
import fr.avenirsesr.portfolio.additionalskill.domain.model.AdditionalSkill;
import fr.avenirsesr.portfolio.shared.domain.model.PageCriteria;
import fr.avenirsesr.portfolio.shared.domain.model.PagedResult;
import fr.avenirsesr.portfolio.testutils.BddLogger;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AdditionalSkillCacheImplTest {

  private AdditionalSkillCacheImpl cache;

  @BeforeEach
  void setUp() {
    cache = new AdditionalSkillCacheImpl("/mock/mock-additional-skills.json");
  }

  @Test
  void shouldReturnAllSkillsPaged() {
    BddLogger.given("a new AdditionalSkillCacheImpl");

    BddLogger.when("searching all skills");
    PagedResult<AdditionalSkill> result = cache.findAll(new PageCriteria(0, 5));

    BddLogger.then("it should return all skills paged");
    assertNotNull(result);
    assertTrue(result.content().size() <= 5);
  }

  @Test
  void shouldSearchSkillsByTitle() {
    BddLogger.given("a new AdditionalSkillCacheImpl");

    BddLogger.when("searching skills by title");
    PagedResult<AdditionalSkill> result = cache.findBySkillTitle("acc", new PageCriteria(0, 5));

    BddLogger.then("it should return all skills filtered by the title");
    assertNotNull(result);
    assertTrue(
        result.content().stream()
            .anyMatch(
                skill ->
                    skill.getPathSegments().getSkill().getLibelle().toLowerCase().contains("acc")));
  }

  @Test
  void shouldFindSkillById() {
    BddLogger.given("a new AdditionalSkillCacheImpl");

    BddLogger.when("searching a skill by id and finding it");
    AdditionalSkill existing = cache.findAll(new PageCriteria(0, 1)).content().getFirst();
    AdditionalSkill found = cache.findById(existing.getId());

    BddLogger.then("it should return the found skill");
    assertEquals(existing.getId(), found.getId());
  }

  @Test
  void shouldThrowExceptionIfSkillIdNotFound() {
    BddLogger.given("a new AdditionalSkillCacheImpl");

    BddLogger.when("searching a skill by id and not finding it");
    UUID invalidId = UUID.randomUUID();

    BddLogger.then("it should throw AdditionalSkillNotFoundException");
    assertThrows(AdditionalSkillNotFoundException.class, () -> cache.findById(invalidId));
  }

  @Test
  void shouldReturnSubsetOfSkillsByIds() {
    BddLogger.given("a new AdditionalSkillCacheImpl");

    BddLogger.when("searching skills by ids and finding some");
    List<AdditionalSkill> skills = cache.findAll(new PageCriteria(0, 10)).content();
    List<UUID> ids = skills.stream().map(AdditionalSkill::getId).limit(3).toList();
    List<AdditionalSkill> foundSkills = cache.findAllByIds(ids);

    BddLogger.then("it should return the found skills");
    assertEquals(3, foundSkills.size());
    assertTrue(foundSkills.stream().allMatch(skill -> ids.contains(skill.getId())));
  }

  @Test
  void shouldThrowRuntimeExceptionWhenJsonFileNotFound() {
    BddLogger.given("a new AdditionalSkillCacheImpl");

    BddLogger.when("the JSON file of the cache is not found");
    AdditionalSkillCacheImpl invalidCache = new AdditionalSkillCacheImpl("/invalid/path.json");

    BddLogger.then("it should throw RuntimeException");
    assertThrows(
        RuntimeException.class,
        () -> invalidCache.findAll(new PageCriteria(0, 5)),
        "Unable to load mock additional skills");
  }
}
