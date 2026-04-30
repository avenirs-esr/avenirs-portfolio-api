package fr.avenirsesr.portfolio.trace.infrastructure.adapter.specification;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import fr.avenirsesr.portfolio.common.language.domain.model.enums.ELanguage;
import fr.avenirsesr.portfolio.file.domain.model.shared.EFileType;
import fr.avenirsesr.portfolio.file.infrastructure.adapter.model.TraceAttachmentEntity;
import fr.avenirsesr.portfolio.shared.infrastructure.ContainerConfigurationTest;
import fr.avenirsesr.portfolio.trace.domain.filter.ETraceFilterKey;
import fr.avenirsesr.portfolio.trace.domain.model.ETraceStatus;
import fr.avenirsesr.portfolio.trace.infrastructure.adapter.model.TraceEntity;
import fr.avenirsesr.portfolio.trace.infrastructure.adapter.repository.TraceJpaRepository;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.model.StudentEntity;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.model.UserEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.*;
import jakarta.transaction.Transactional;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.TestPropertySource;

@Transactional
@TestPropertySource(properties = {"seeder.enabled=false"})
class TraceSpecificationIT extends ContainerConfigurationTest {

  @Autowired private TraceJpaRepository traceJpaRepository;
  @Autowired private EntityManager entityManager;

  private UserEntity user;
  private StudentEntity student;

  @BeforeEach
  void setup() {
    user = persistUser();
    student = persistStudent(user);
    entityManager.flush();
    entityManager.clear();
  }

  @Test
  void shouldFilterByFileType_onlyActiveAttachmentCounts() {
    TraceEntity pdfActive = persistTrace("pdfActive");
    persistAttachment(pdfActive, "doc.pdf", EFileType.PDF, true);

    TraceEntity pdfInactive = persistTrace("pdfInactive");
    persistAttachment(pdfInactive, "old.pdf", EFileType.PDF, false);

    TraceEntity pngActive = persistTrace("pngActive");
    persistAttachment(pngActive, "img.png", EFileType.PNG, true);

    entityManager.flush();
    entityManager.clear();

    TraceFilterSpecificationBuilder builder = new TraceFilterSpecificationBuilder();
    Specification<TraceEntity> spec =
        builder.getSpecification(ETraceFilterKey.FILE_TYPE, List.of(EFileType.PDF));

    List<TraceEntity> result = traceJpaRepository.findAll(ofTestUser(user.getId()).and(spec));

    assertThat(result).extracting(TraceEntity::getId).contains(pdfActive.getId());
    assertThat(result).extracting(TraceEntity::getId).doesNotContain(pdfInactive.getId());
    assertThat(result).extracting(TraceEntity::getId).doesNotContain(pngActive.getId());
  }

  @Test
  void shouldReturnAllWhenFileTypeIsNullOrEmpty() {
    TraceEntity t1 = persistTrace("t1");
    TraceEntity t2 = persistTrace("t2");

    entityManager.flush();
    entityManager.clear();

    TraceFilterSpecificationBuilder builder = new TraceFilterSpecificationBuilder();

    Specification<TraceEntity> specNull = builder.getSpecification(ETraceFilterKey.FILE_TYPE, null);
    Specification<TraceEntity> specEmpty =
        builder.getSpecification(ETraceFilterKey.FILE_TYPE, List.of());

    List<TraceEntity> resultNull =
        traceJpaRepository.findAll(ofTestUser(user.getId()).and(specNull));
    List<TraceEntity> resultEmpty =
        traceJpaRepository.findAll(ofTestUser(user.getId()).and(specEmpty));

    assertThat(resultNull).extracting(TraceEntity::getId).contains(t1.getId(), t2.getId());
    assertThat(resultEmpty).extracting(TraceEntity::getId).contains(t1.getId(), t2.getId());
  }

  @Test
  void shouldSearchByTitle_aiUse_personalNote_andAttachmentName() {
    TraceEntity byTitle = persistTrace("Projet Java");
    TraceEntity byAiUse = persistTrace("x");
    byAiUse.setAiUseJustification("Utilisation Java");
    TraceEntity byPersonalNote = persistTrace("y");
    byPersonalNote.setPersonalNote("note java");
    TraceEntity byAttachment = persistTrace("z");
    persistAttachment(byAttachment, "rapport-java.pdf", EFileType.PDF, true);

    TraceEntity other = persistTrace("Autre sujet");
    persistAttachment(other, "random.pdf", EFileType.PDF, true);

    entityManager.flush();
    entityManager.clear();

    List<TraceEntity> result =
        traceJpaRepository.findAll(
            ofTestUser(user.getId()).and(TraceSpecification.search("java", ELanguage.FRENCH)));

    assertThat(result).extracting(TraceEntity::getId).contains(byTitle.getId());
    assertThat(result).extracting(TraceEntity::getId).contains(byAiUse.getId());
    assertThat(result).extracting(TraceEntity::getId).contains(byPersonalNote.getId());
    assertThat(result).extracting(TraceEntity::getId).contains(byAttachment.getId());
    assertThat(result).extracting(TraceEntity::getId).doesNotContain(other.getId());
  }

  @Test
  void shouldReturnAllWhenSearchKeywordIsNullBlankOrQueryNullBranch() {
    TraceEntity t1 = persistTrace("t1");
    TraceEntity t2 = persistTrace("t2");
    entityManager.flush();
    entityManager.clear();

    List<TraceEntity> resultNull =
        traceJpaRepository.findAll(
            ofTestUser(user.getId()).and(TraceSpecification.search(null, ELanguage.FRENCH)));
    List<TraceEntity> resultBlank =
        traceJpaRepository.findAll(
            ofTestUser(user.getId()).and(TraceSpecification.search("   ", ELanguage.FRENCH)));

    assertThat(resultNull).extracting(TraceEntity::getId).contains(t1.getId(), t2.getId());
    assertThat(resultBlank).extracting(TraceEntity::getId).contains(t1.getId(), t2.getId());

    Root<TraceEntity> root = mock(Root.class);
    CriteriaBuilder cb = mock(CriteriaBuilder.class);
    CriteriaQuery<?> query = null;

    Predicate conj = mock(Predicate.class);
    when(cb.conjunction()).thenReturn(conj);

    Predicate p = TraceSpecification.search("java", ELanguage.FRENCH).toPredicate(root, query, cb);

    assertThat(p).isSameAs(conj);
  }

  @Test
  void shouldCoverAssociatedQueryNullBranch() {
    Root<TraceEntity> root = mock(Root.class);
    CriteriaBuilder cb = mock(CriteriaBuilder.class);
    CriteriaQuery<?> query = null;

    Predicate p = TraceSpecification.associated().toPredicate(root, query, cb);

    assertThat(p).isNull();
  }

  @Test
  void shouldCoverFilterBuilderQueryNullBranches() {
    TraceFilterSpecificationBuilder builder = new TraceFilterSpecificationBuilder();

    Root<TraceEntity> root = mock(Root.class);
    CriteriaBuilder cb = mock(CriteriaBuilder.class);
    CriteriaQuery<?> query = null;

    Specification<TraceEntity> fileTypeSpec =
        builder.getSpecification(ETraceFilterKey.FILE_TYPE, List.of(EFileType.PDF));
    Specification<TraceEntity> skillSpec =
        builder.getSpecification(ETraceFilterKey.SKILL, List.of(UUID.randomUUID()));
    Specification<TraceEntity> statusSpec =
        builder.getSpecification(
            ETraceFilterKey.STATUS, List.of(ETraceStatus.ASSOCIATED_EVALUATED));

    assertThat(fileTypeSpec.toPredicate(root, query, cb)).isNull();
    assertThat(skillSpec.toPredicate(root, query, cb)).isNull();
    assertThat(statusSpec.toPredicate(root, query, cb)).isNull();
  }

  private Specification<TraceEntity> ofTestUser(UUID userId) {
    return (root, query, cb) -> cb.equal(root.get("user").get("id"), userId);
  }

  private UserEntity persistUser() {
    UserEntity u =
        UserEntity.of(
            UUID.randomUUID(),
            "firstname",
            "lastname",
            "test@gmail.com",
            Instant.now(),
            Instant.now());
    entityManager.persist(u);
    return u;
  }

  private StudentEntity persistStudent(UserEntity u) {
    StudentEntity s =
        StudentEntity.of(u, "student@univ.com", "student", Instant.now(), Instant.now());
    entityManager.persist(s);
    return s;
  }

  private TraceEntity persistTrace(String title) {
    TraceEntity trace =
        TraceEntity.of(
            UUID.randomUUID(),
            user,
            title,
            ELanguage.FRENCH,
            false,
            null,
            null,
            null,
            Instant.now(),
            Instant.now(),
            null);
    entityManager.persist(trace);
    return trace;
  }

  private TraceAttachmentEntity persistAttachment(
      TraceEntity trace, String name, EFileType type, boolean active) {
    TraceAttachmentEntity att =
        TraceAttachmentEntity.of(
            UUID.randomUUID(),
            trace,
            name,
            type,
            1L,
            1,
            active,
            "uri",
            user,
            Instant.now(),
            Instant.now(),
            Instant.now());
    entityManager.persist(att);
    return att;
  }
}
