package fr.avenirsesr.portfolio.file.infrastructure.adapter.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import fr.avenirsesr.portfolio.activity.domain.model.Activity;
import fr.avenirsesr.portfolio.activity.domain.model.enums.EActivityThematic;
import fr.avenirsesr.portfolio.activity.infrastructure.adapter.mapper.ActivityMapper;
import fr.avenirsesr.portfolio.common.data.domain.model.User;
import fr.avenirsesr.portfolio.common.testutils.BddLogger;
import fr.avenirsesr.portfolio.file.domain.model.ActivityBanner;
import fr.avenirsesr.portfolio.file.domain.model.EFileType;
import fr.avenirsesr.portfolio.file.infrastructure.adapter.model.ActivityBannerEntity;
import fr.avenirsesr.portfolio.user.domain.model.Staff;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.mapper.StaffMapper;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.mapper.UserMapper;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.seeder.fake.FakeStaff;
import fr.avenirsesr.portfolio.user.infrastructure.fixture.UserFixture;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ActivityBannerMapperTest {

  private ActivityBanner banner;
  private Activity activity;
  private User user;

  private final UUID id = UUID.randomUUID();
  private final UUID activityId = UUID.randomUUID();
  private final UUID userId = UUID.randomUUID();

  private final EFileType fileType = EFileType.PNG;
  private final String fileName = "filename.png";
  private final long size = 2048L;
  private final int version = 1;
  private final boolean activeVersion = true;
  private final String uri = "https://cdn.test/banner.jpg";
  private final Instant uploadedAt = Instant.parse("2024-01-01T00:00:00Z");
  private final Instant createdAt = Instant.parse("2024-01-01T00:00:00Z");
  private final Instant updatedAt = Instant.parse("2024-01-02T00:00:00Z");

  @BeforeEach
  void setUp() {
    Staff author =
        StaffMapper.INSTANCE.toDomain(
            FakeStaff.create(UserMapper.INSTANCE.fromDomain(UserFixture.create().toModel()))
                .toEntity());
    activity =
        Activity.toDomain(
            activityId,
            author,
            "Activity Title",
            EActivityThematic.SELF_KNOWLEDGE,
            "Summary",
            "<h3>Objectives</h3><p>Activity description</p>",
            "2020 - 2022",
            "label 2020",
            true,
            10,
            10,
            createdAt,
            updatedAt);

    user = User.toDomain(userId, "John", "Doe", "john.doe@email.com", createdAt, updatedAt);

    banner =
        ActivityBanner.toDomain(
            id,
            fileName,
            fileType,
            size,
            version,
            activeVersion,
            uri,
            user,
            uploadedAt,
            activity,
            createdAt,
            updatedAt);
  }

  @Test
  void shouldMapFromDomainToEntity() {
    BddLogger.given("an ActivityBanner mapper");

    BddLogger.when("mapping a domain ActivityBanner to ActivityBannerEntity");
    ActivityBannerEntity entity = ActivityBannerMapper.INSTANCE.fromDomain(banner);

    BddLogger.then("it should return a correct ActivityBannerEntity");

    assertNotNull(entity);
    assertEquals(id, entity.getId());
    assertEquals(fileType, entity.getFileType());
    assertEquals(size, entity.getSize());
    assertEquals(version, entity.getVersion());
    assertEquals(activeVersion, entity.isActiveVersion());
    assertEquals(uri, entity.getUri());
    assertEquals(userId, entity.getUploadedBy().getId());
    assertEquals(activityId, entity.getActivity().getId());
    assertEquals(uploadedAt, entity.getUploadedAt());
  }

  @Test
  void shouldMapFromEntityToDomain() {
    BddLogger.given("an ActivityBanner mapper");

    ActivityBannerEntity entity =
        ActivityBannerEntity.of(
            id,
            ActivityMapper.INSTANCE.fromDomain(activity),
            fileName,
            fileType,
            size,
            version,
            activeVersion,
            uri,
            UserMapper.INSTANCE.fromDomain(user),
            uploadedAt,
            createdAt,
            updatedAt);

    BddLogger.when("mapping an ActivityBannerEntity to domain ActivityBanner");
    ActivityBanner mapped = ActivityBannerMapper.INSTANCE.toDomain(entity);

    BddLogger.then("it should return a correct domain ActivityBanner");

    assertNotNull(mapped);
    assertEquals(id, mapped.getId());
    assertEquals(fileType, mapped.getFileType());
    assertEquals(size, mapped.getSize());
    assertEquals(version, mapped.getVersion());
    assertEquals(activeVersion, mapped.isActiveVersion());
    assertEquals(uri, mapped.getUri());
    assertEquals(userId, mapped.getUploadedBy().getId());
    assertEquals(activityId, mapped.getActivity().getId());
    assertEquals(uploadedAt, mapped.getUploadedAt());
    assertEquals(createdAt, mapped.getCreatedAt());
    assertEquals(updatedAt, mapped.getUpdatedAt());
  }
}
