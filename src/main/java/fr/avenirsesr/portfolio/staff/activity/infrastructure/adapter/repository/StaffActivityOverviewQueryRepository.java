package fr.avenirsesr.portfolio.staff.activity.infrastructure.adapter.repository;

import fr.avenirsesr.portfolio.common.data.domain.model.PageCriteria;
import fr.avenirsesr.portfolio.common.data.domain.model.PageInfo;
import fr.avenirsesr.portfolio.common.data.domain.model.PagedResult;
import fr.avenirsesr.portfolio.staff.activity.domain.data.ActivityStaffOverviewData;
import fr.avenirsesr.portfolio.staff.activity.domain.model.enums.EActivityRight;
import fr.avenirsesr.portfolio.staff.activity.domain.model.enums.EActivityStatus;
import fr.avenirsesr.portfolio.staff.activity.domain.model.enums.EActivityThematic;
import fr.avenirsesr.portfolio.staff.activity.domain.port.output.repository.StaffActivityOverviewRepository;
import fr.avenirsesr.portfolio.user.domain.model.Staff;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Repository;

@Repository
public class StaffActivityOverviewQueryRepository implements StaffActivityOverviewRepository {
  private static final String ANY_RIGHT_ON_ACTIVITY =
      """
      EXISTS (
          SELECT 1
          FROM activity_authorization aa
          JOIN activity_authorization_rights ar ON ar.activity_authorization_id = aa.id
          WHERE aa.activity_id = a.id
            AND aa.staff_id = :staffId
      )
      """;

  private static final String MANAGEMENT_RIGHT_ON_ACTIVITY =
      """
      EXISTS (
          SELECT 1
          FROM activity_authorization aa
          JOIN activity_authorization_rights ar ON ar.activity_authorization_id = aa.id
          WHERE aa.activity_id = a.id
            AND aa.staff_id = :staffId
            AND ar.activity_right IN (:managementRights)
      )
      """;

  private static final String MANAGEMENT_RIGHT_ON_DRAFT =
      """
      EXISTS (
          SELECT 1
          FROM activity_draft_authorization da
          JOIN activity_draft_authorization_rights dr
            ON dr.activity_draft_authorization_id = da.id
          WHERE da.activity_draft_id = d.id
            AND da.staff_id = :staffId
            AND dr.activity_right IN (:managementRights)
      )
      """;

  private static final String PUBLISHED_SELECT =
      """
      SELECT
          a.id         AS activity_id,
          a.title      AS title,
          a.thematic   AS thematic,
          a.status     AS status,
          a.updated_at AS updated_at
      FROM activity a
      WHERE a.status = 'PUBLISHED'
        AND
      """
          + ANY_RIGHT_ON_ACTIVITY;

  private static final String UNPUBLISHED_SELECT =
      """
      SELECT
          a.id         AS activity_id,
          a.title      AS title,
          a.thematic   AS thematic,
          a.status     AS status,
          a.updated_at AS updated_at
      FROM activity a
      WHERE a.status = 'UNPUBLISHED'
        AND
      """
          + MANAGEMENT_RIGHT_ON_ACTIVITY;

  private static final String DRAFT_SELECT =
      """
      SELECT
          d.id         AS activity_id,
          d.title      AS title,
          d.thematic   AS thematic,
          'DRAFT'      AS status,
          d.updated_at AS updated_at
      FROM activity_draft d
      WHERE
      """
          + MANAGEMENT_RIGHT_ON_DRAFT;

  @PersistenceContext private EntityManager em;

  @Override
  public PagedResult<ActivityStaffOverviewData> findAllAccessibleByStaffAndStatus(
      Staff staff, EActivityStatus activityStatus, PageCriteria pageCriteria) {

    String unified =
        switch (activityStatus) {
          case PUBLISHED -> PUBLISHED_SELECT;
          case UNPUBLISHED -> UNPUBLISHED_SELECT;
          case DRAFT -> DRAFT_SELECT;
          case null ->
              String.join(" UNION ALL ", PUBLISHED_SELECT, UNPUBLISHED_SELECT, DRAFT_SELECT);
        };

    String dataSql = "SELECT * FROM (" + unified + ") AS unified ORDER BY updated_at DESC";
    String countSql = "SELECT COUNT(*) FROM (" + unified + ") AS total";

    Query dataQuery =
        bindParameters(em.createNativeQuery(dataSql), staff, activityStatus)
            .setFirstResult(pageCriteria.page() * pageCriteria.pageSize())
            .setMaxResults(pageCriteria.pageSize());
    Query countQuery = bindParameters(em.createNativeQuery(countSql), staff, activityStatus);

    List<Object[]> rows = dataQuery.getResultList();
    long total = ((Number) countQuery.getSingleResult()).longValue();

    List<ActivityStaffOverviewData> content = rows.stream().map(this::mapRow).toList();

    return new PagedResult<>(
        content, new PageInfo(pageCriteria.page(), pageCriteria.pageSize(), total));
  }

  private Query bindParameters(Query query, Staff staff, EActivityStatus activityStatus) {
    query.setParameter("staffId", staff.getId());
    if (activityStatus != EActivityStatus.PUBLISHED) {
      query.setParameter(
          "managementRights", EActivityRight.MANAGEMENT_RIGHTS.stream().map(Enum::name).toList());
    }
    return query;
  }

  private ActivityStaffOverviewData mapRow(Object[] row) {
    String thematic = toNullableString(row[2]);
    return new ActivityStaffOverviewData(
        toUUID(row[0]),
        toString(row[1]),
        thematic == null ? null : EActivityThematic.valueOf(thematic),
        List.of(),
        EActivityStatus.valueOf(toString(row[3])),
        toInstant(row[4]));
  }

  private String toString(Object value) {
    return switch (value) {
      case String s -> s;
      case byte[] b -> new String(b, StandardCharsets.UTF_8);
      default ->
          throw new IllegalArgumentException("Type inattendu pour String : " + value.getClass());
    };
  }

  private String toNullableString(Object value) {
    return value == null ? null : toString(value);
  }

  private UUID toUUID(Object value) {
    return switch (value) {
      case UUID u -> u;
      case String s -> UUID.fromString(s);
      case byte[] b -> {
        ByteBuffer bb = ByteBuffer.wrap(b);
        yield new UUID(bb.getLong(), bb.getLong());
      }
      default ->
          throw new IllegalArgumentException("Type inattendu pour UUID : " + value.getClass());
    };
  }

  private Instant toInstant(Object value) {
    return switch (value) {
      case Instant i -> i;
      case Timestamp ts -> ts.getTimestamp().toInstant();
      case OffsetDateTime odt -> odt.toInstant();
      case LocalDateTime ldt -> ldt.toInstant(ZoneOffset.UTC);
      case byte[] b -> Instant.parse(new String(b, StandardCharsets.UTF_8));
      default ->
          throw new IllegalArgumentException("Type inattendu pour Instant : " + value.getClass());
    };
  }
}
