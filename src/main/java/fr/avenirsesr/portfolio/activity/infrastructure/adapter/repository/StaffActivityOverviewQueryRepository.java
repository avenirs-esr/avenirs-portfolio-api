package fr.avenirsesr.portfolio.activity.infrastructure.adapter.repository;

import fr.avenirsesr.portfolio.activity.domain.data.ActivityStaffOverviewData;
import fr.avenirsesr.portfolio.activity.domain.model.enums.EActivityStatus;
import fr.avenirsesr.portfolio.activity.domain.model.enums.EActivityThematic;
import fr.avenirsesr.portfolio.activity.domain.port.output.repository.StaffActivityOverviewRepository;
import fr.avenirsesr.portfolio.common.data.domain.model.PageCriteria;
import fr.avenirsesr.portfolio.common.data.domain.model.PageInfo;
import fr.avenirsesr.portfolio.common.data.domain.model.PagedResult;
import fr.avenirsesr.portfolio.user.domain.model.Staff;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.Timestamp;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Repository;

@Repository
public class StaffActivityOverviewQueryRepository implements StaffActivityOverviewRepository {
  @PersistenceContext private EntityManager em;

  @Override
  public PagedResult<ActivityStaffOverviewData> findAllByAuthor(
      Staff author, PageCriteria pageCriteria) {

    String unionSql =
        """
        SELECT *
          FROM (
              SELECT
                  a.id            AS activity_id,
                  a.title         AS title,
                  a.thematic      AS thematic,
                  a.author_id     AS author_id,
                  a.status        AS status,
                  a.updated_at    AS updated_at
              FROM activity a
              WHERE a.author_id = :authorId

              UNION ALL

              SELECT
                  d.id            AS activity_id,
                  d.title         AS title,
                  d.thematic      AS thematic,
                  d.author_id     AS author_id,
                  'DRAFT'         AS status,
                  d.updated_at    AS updated_at
              FROM activity_draft d
              WHERE d.author_id = :authorId
          ) AS unified

          ORDER BY updated_at DESC
        """;

    String countSql =
        """
            SELECT COUNT(*) FROM (
                SELECT id FROM activity       WHERE author_id = :authorId
                UNION ALL
                SELECT id FROM activity_draft WHERE author_id = :authorId
            ) AS total
        """;

    Query dataQuery =
        em.createNativeQuery(unionSql)
            .setParameter("authorId", author.getId())
            .setFirstResult(pageCriteria.page() * pageCriteria.pageSize())
            .setMaxResults(pageCriteria.pageSize());

    Query countQuery = em.createNativeQuery(countSql).setParameter("authorId", author.getId());

    List<Object[]> rows = dataQuery.getResultList();
    long total = ((Number) countQuery.getSingleResult()).longValue();

    List<ActivityStaffOverviewData> content =
        rows.stream().map(row -> mapRow(row, author)).toList();

    return new PagedResult<>(
        content, new PageInfo(pageCriteria.page(), pageCriteria.pageSize(), total));
  }

  private ActivityStaffOverviewData mapRow(Object[] row, Staff author) {
    return new ActivityStaffOverviewData(
        toUUID(row[0]),
        toString(row[1]),
        EActivityThematic.valueOf(toString(row[2])),
        author,
        EActivityStatus.valueOf(toString(row[4])),
        toInstant(row[5]));
  }

  private String toString(Object value) {
    return switch (value) {
      case String s -> s;
      case byte[] b -> new String(b, StandardCharsets.UTF_8);
      default ->
          throw new IllegalArgumentException("Type inattendu pour String : " + value.getClass());
    };
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
      case byte[] b -> Instant.parse(new String(b, StandardCharsets.UTF_8));
      default ->
          throw new IllegalArgumentException("Type inattendu pour Instant : " + value.getClass());
    };
  }
}
