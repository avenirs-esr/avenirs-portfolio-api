package fr.avenirsesr.portfolio.ams.infrastructure.adapter.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import fr.avenirsesr.portfolio.ams.domain.model.AMS;
import fr.avenirsesr.portfolio.ams.domain.model.enums.EAmsStatus;
import fr.avenirsesr.portfolio.ams.infrastructure.adapter.model.AMSEntity;
import fr.avenirsesr.portfolio.ams.infrastructure.adapter.model.AMSTranslationEntity;
import fr.avenirsesr.portfolio.ams.infrastructure.fixture.AMSFixture;
import fr.avenirsesr.portfolio.common.language.domain.model.enums.ELanguage;
import fr.avenirsesr.portfolio.common.testutils.BddLogger;
import fr.avenirsesr.portfolio.user.domain.model.User;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.mapper.UserMapper;
import fr.avenirsesr.portfolio.user.infrastructure.fixture.UserFixture;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AMSMapperTest {

  private AMS ams;
  private User user;
  private final String title = "Test AMS Title";
  private final ELanguage language = ELanguage.FRENCH;
  private final Instant startDate = Instant.parse("2023-01-01T00:00:00Z");
  private final Instant endDate = Instant.parse("2023-12-31T23:59:59Z");
  private final EAmsStatus status = EAmsStatus.IN_PROGRESS;
  private final UUID id = UUID.randomUUID();

  @BeforeEach
  void setUp() {
    user = UserFixture.create().toModel();

    ams =
        AMSFixture.create()
            .withId(id)
            .withUser(user)
            .withTitle(title)
            .withLanguage(language)
            .withStartDate(startDate)
            .withEndDate(endDate)
            .toModel();

    ams.setStatus(status);
  }

  @Test
  void shouldMapFromDomainToEntity() {
    BddLogger.given("an AMS mapper");
    BddLogger.when("mapping a domain AMS to AMSEntity");
    AMSEntity entity = AMSMapper.fromDomain(ams);

    BddLogger.then("it should return a correct AMSEntity");
    assertNotNull(entity);
    assertEquals(id, entity.getId());
    assertEquals(user.getId(), entity.getUser().getId());
    assertEquals(status, entity.getStatus());
    assertEquals(startDate, entity.getStartDate());
    assertEquals(endDate, entity.getEndDate());
  }

  @Test
  void shouldMapFromEntityToDomain() {
    BddLogger.given("an AMS mapper");
    AMSEntity entity = new AMSEntity();
    entity.setId(id);
    entity.setUser(UserMapper.fromDomain(user));
    entity.setStatus(status);
    entity.setStartDate(startDate);
    entity.setEndDate(endDate);

    AMSTranslationEntity translationEntity = new AMSTranslationEntity();
    translationEntity.setLanguage(language);
    translationEntity.setTitle(title);
    translationEntity.setAms(entity);

    Set<AMSTranslationEntity> translations = new HashSet<>();
    translations.add(translationEntity);
    entity.setTranslations(translations);

    BddLogger.when("mapping an AMSEntity to domain AMS");
    AMS mappedAms = AMSMapper.toDomain(entity);

    BddLogger.then("it should return a correct domain AMS");
    assertNotNull(mappedAms);
    assertEquals(id, mappedAms.getId());
    assertEquals(user.getId(), mappedAms.getUser().getId());
    assertEquals(title, mappedAms.getTitle());
    assertEquals(status, mappedAms.getStatus());
  }

  @Test
  void shouldMapWithEmptyCollections() {
    BddLogger.given("an AMS mapper");
    AMSEntity entity = new AMSEntity();
    entity.setId(id);
    entity.setUser(UserMapper.fromDomain(user));
    entity.setStatus(status);
    entity.setStartDate(startDate);
    entity.setEndDate(endDate);

    assertNotNull(entity);

    AMSTranslationEntity translationEntity = new AMSTranslationEntity();
    translationEntity.setLanguage(language);
    translationEntity.setTitle(title);
    translationEntity.setAms(entity);

    Set<AMSTranslationEntity> translations = new HashSet<>();
    translations.add(translationEntity);
    entity.setTranslations(translations);

    BddLogger.when("mapping an empty collection to domain AMS");
    AMS mappedAms = AMSMapper.toDomain(entity);

    BddLogger.then("it should return a correct domain AMS");
    assertNotNull(mappedAms);
  }
}
