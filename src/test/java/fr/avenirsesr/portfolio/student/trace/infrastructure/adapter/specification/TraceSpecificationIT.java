package fr.avenirsesr.portfolio.student.trace.infrastructure.adapter.specification;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import fr.avenirsesr.portfolio.common.language.domain.model.enums.ELanguage;
import fr.avenirsesr.portfolio.file.domain.model.enums.EFileType;
import fr.avenirsesr.portfolio.file.infrastructure.adapter.model.FileEntity;
import fr.avenirsesr.portfolio.shared.infrastructure.ContainerConfigurationTest;
import fr.avenirsesr.portfolio.student.trace.domain.filter.ETraceFilterKey;
import fr.avenirsesr.portfolio.student.trace.domain.model.ETraceStatus;
import fr.avenirsesr.portfolio.student.trace.domain.model.enums.ETraceAuthorType;
import fr.avenirsesr.portfolio.student.trace.infrastructure.adapter.model.TraceEntity;
import fr.avenirsesr.portfolio.student.trace.infrastructure.adapter.repository.TraceJpaRepository;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.model.StudentEntity;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.model.UserEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.*;
import jakarta.transaction.Transactional;
import java.time.Instant;
import java.util.ArrayList;
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
  void shouldFilterValorizedTraces() {
    TraceEntity valorized = persistTrace("valorized");
    valorized.setValorized(true);
    TraceEntity notValorized = persistTrace("not-valorized");

    entityManager.flush();
    entityManager.clear();

    List<TraceEntity> valorizedResult =
        traceJpaRepository.findAll(
            ofTestUser(user.getId()).and(TraceSpecification.valorized(true)));
    List<TraceEntity> notValorizedResult =
        traceJpaRepository.findAll(
            ofTestUser(user.getId()).and(TraceSpecification.valorized(false)));

    assertThat(valorizedResult).extracting(TraceEntity::getId).contains(valorized.getId());
    assertThat(valorizedResult).extracting(TraceEntity::getId).doesNotContain(notValorized.getId());
    assertThat(notValorizedResult).extracting(TraceEntity::getId).contains(notValorized.getId());
    assertThat(notValorizedResult).extracting(TraceEntity::getId).doesNotContain(valorized.getId());
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

  @Test
  void shouldReturnNullSpecificationWhenIsValorizedIsNull() {
    TraceFilterSpecificationBuilder builder = new TraceFilterSpecificationBuilder();

    Specification<TraceEntity> spec = builder.getSpecification(ETraceFilterKey.IS_VALORIZED, null);

    assertThat(spec).isNull();
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
            false,
            Instant.now(),
            Instant.now());
    entityManager.persist(u);
    return u;
  }

  private StudentEntity persistStudent(UserEntity u) {
    StudentEntity s =
        StudentEntity.of(
            u,
            "student@univ.com",
            null,
            null,
            "student",
            false,
            new ArrayList<>(),
            null,
            null,
            Instant.now(),
            Instant.now());
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
            ETraceAuthorType.PERSONAL,
            null,
            null,
            null,
            null,
            false,
            Instant.now(),
            Instant.now());
    entityManager.persist(trace);
    return trace;
  }

  private FileEntity persistAttachment(
      TraceEntity trace, String name, EFileType type, boolean active) {
    FileEntity att =
        FileEntity.of(
            UUID.randomUUID(),
            type,
            name,
            1L,
            "uri",
            user,
            Instant.now(),
            true,
            Instant.now(),
            Instant.now());
    entityManager.persist(att);
    trace.setAttachment(att);
    entityManager.persist(trace);
    return att;
  }
}
