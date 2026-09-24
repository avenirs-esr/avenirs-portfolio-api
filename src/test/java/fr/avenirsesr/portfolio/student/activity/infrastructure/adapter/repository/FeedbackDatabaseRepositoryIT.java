package fr.avenirsesr.portfolio.student.activity.infrastructure.adapter.repository;

import static org.assertj.core.api.Assertions.assertThat;

import fr.avenirsesr.portfolio.common.file.domain.model.enums.EFileType;
import fr.avenirsesr.portfolio.common.testutils.BddLogger;
import fr.avenirsesr.portfolio.file.infrastructure.adapter.model.FileEntity;
import fr.avenirsesr.portfolio.shared.infrastructure.ContainerConfigurationTest;
import fr.avenirsesr.portfolio.shared.infrastructure.adapter.seeder.SeederRunner;
import fr.avenirsesr.portfolio.student.activity.domain.port.output.repository.DeclaredActivityRepository;
import fr.avenirsesr.portfolio.student.activity.infrastructure.adapter.model.FeedbackEntity;
import fr.avenirsesr.portfolio.student.activity.infrastructure.adapter.specification.FeedbackSpecification;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.model.UserEntity;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@Transactional
class FeedbackDatabaseRepositoryIT extends ContainerConfigurationTest {

  private static final UUID DECLARED_ACTIVITY_ID =
      UUID.fromString("e5baf6b6-6c2b-4a5e-9c4f-8e2a6b1d3f05");
  private static final UUID OTHER_DECLARED_ACTIVITY_ID =
      UUID.fromString("a1f6c2d2-6c2b-4a5e-9c4f-8e2a6b1d3f01");
  private static final UUID STUDENT_USER_ID =
      UUID.fromString("0a8700ab-90b6-4a38-8338-acbdd4fbcd3d");

  @Autowired private FeedbackDatabaseRepository feedbackDatabaseRepository;
  @Autowired private FeedbackJpaRepository feedbackJpaRepository;
  @Autowired private DeclaredActivityRepository declaredActivityRepository;
  @Autowired private EntityManager entityManager;

  @BeforeAll
  void setup(@Autowired SeederRunner seederRunner) {
    seederRunner.run();
  }

  @Test
  void deleteByDeclaredActivityId_should_delete_its_feedbacks_with_their_attachment_links() {
    BddLogger.given("a declared activity whose feedback has an attachment");
    List<FeedbackEntity> feedbacks = feedbacksOf(DECLARED_ACTIVITY_ID);
    List<FeedbackEntity> otherFeedbacks = feedbacksOf(OTHER_DECLARED_ACTIVITY_ID);
    assertThat(feedbacks).isNotEmpty();
    assertThat(otherFeedbacks).isNotEmpty();

    FileEntity attachment =
        FileEntity.of(
            UUID.randomUUID(),
            EFileType.PDF,
            "attachment.pdf",
            1L,
            "attachments/attachment.pdf",
            entityManager.find(UserEntity.class, STUDENT_USER_ID),
            Instant.now(),
            false,
            Instant.now(),
            Instant.now());
    entityManager.persist(attachment);
    feedbacks.getFirst().getAttachments().add(attachment);
    entityManager.flush();
    entityManager.clear();

    BddLogger.when("deleting the feedbacks of the declared activity");
    feedbackDatabaseRepository.deleteByDeclaredActivityId(
        declaredActivityRepository.findById(DECLARED_ACTIVITY_ID).orElseThrow());
    entityManager.flush();
    entityManager.clear();

    BddLogger.then("its feedbacks and their attachment links are deleted, nothing else");
    assertThat(feedbacksOf(DECLARED_ACTIVITY_ID)).isEmpty();
    assertThat(attachmentLinksOf(attachment)).isZero();
    assertThat(entityManager.find(FileEntity.class, attachment.getId())).isNotNull();
    assertThat(feedbacksOf(OTHER_DECLARED_ACTIVITY_ID))
        .extracting(FeedbackEntity::getId)
        .containsExactlyInAnyOrderElementsOf(
            otherFeedbacks.stream().map(FeedbackEntity::getId).toList());
  }

  private List<FeedbackEntity> feedbacksOf(UUID declaredActivityId) {
    return feedbackJpaRepository.findAll(
        FeedbackSpecification.hasDeclaredActivityId(declaredActivityId));
  }

  private long attachmentLinksOf(FileEntity attachment) {
    return ((Number)
            entityManager
                .createNativeQuery("select count(*) from feedback_attachments where file_id = :id")
                .setParameter("id", attachment.getId())
                .getSingleResult())
        .longValue();
  }
}
