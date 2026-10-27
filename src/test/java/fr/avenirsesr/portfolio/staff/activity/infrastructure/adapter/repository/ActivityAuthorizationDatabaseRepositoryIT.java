package fr.avenirsesr.portfolio.staff.activity.infrastructure.adapter.repository;

import static org.assertj.core.api.Assertions.assertThat;

import fr.avenirsesr.portfolio.common.data.domain.model.PageCriteria;
import fr.avenirsesr.portfolio.common.testutils.BddLogger;
import fr.avenirsesr.portfolio.shared.infrastructure.ContainerConfigurationTest;
import fr.avenirsesr.portfolio.shared.infrastructure.adapter.seeder.SeederRunner;
import fr.avenirsesr.portfolio.staff.activity.domain.model.enums.EActivityRight;
import fr.avenirsesr.portfolio.staff.activity.infrastructure.adapter.model.ActivityAuthorizationEntity;
import fr.avenirsesr.portfolio.staff.activity.infrastructure.adapter.model.ActivityEntity;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.model.StaffEntity;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@Transactional
class ActivityAuthorizationDatabaseRepositoryIT extends ContainerConfigurationTest {

  private static final UUID STAFF_ID = UUID.fromString("57ff122f-ff76-4d95-b6c1-61a4efeabd80");
  private static final UUID ANOTHER_STAFF_ID =
      UUID.fromString("5fcd76c5-017c-4d61-99d2-62a6f199c94e");

  private final UUID institutionId = UUID.randomUUID();
  private final UUID otherInstitutionId = UUID.randomUUID();

  @Autowired private ActivityAuthorizationDatabaseRepository repository;
  @Autowired private ActivityAuthorizationJpaRepository jpaRepository;
  @Autowired private EntityManager entityManager;

  private ActivityEntity alpha;
  private ActivityEntity beta;
  private ActivityEntity outOfScope;

  @BeforeAll
  void setup(@Autowired SeederRunner seederRunner) {
    seederRunner.run();
  }

  @BeforeEach
  void givenThreeActivitiesAndTheirAuthorizations() {
    List<ActivityEntity> activities =
        entityManager
            .createQuery("select a from ActivityEntity a order by a.id", ActivityEntity.class)
            .setMaxResults(3)
            .getResultList();
    assertThat(activities).hasSize(3);
    alpha = activities.get(0);
    beta = activities.get(1);
    outOfScope = activities.get(2);
    alpha.setTitle("Alpha");
    alpha.setTargetInstitutionIds(List.of(institutionId));
    beta.setTitle("Beta");
    beta.setTargetInstitutionIds(List.of(institutionId, otherInstitutionId));
    outOfScope.setTitle("Aardvark");
    outOfScope.setTargetInstitutionIds(List.of(otherInstitutionId));

    activities.forEach(activity -> jpaRepository.deleteAllByActivityId(activity.getId()));
    entityManager.flush();

    grant(STAFF_ID, alpha, EActivityRight.EDIT);
    grant(STAFF_ID, beta, EActivityRight.EDIT, EActivityRight.FEEDBACK);
    grant(STAFF_ID, outOfScope, EActivityRight.LEADER);
    grant(ANOTHER_STAFF_ID, alpha, EActivityRight.EDIT, EActivityRight.PUBLISH);
    entityManager.flush();
    entityManager.clear();
  }

  @Test
  void findPage_should_return_the_activities_of_the_institutions_sorted_by_title() {
    BddLogger.given("a staff holding rights on activities of two institutions");

    BddLogger.when("getting the first page of the activities targeting the first institution");
    var firstPage =
        repository.findPageByStaffIdAndTargetInstitutionIds(
            STAFF_ID, Set.of(institutionId), new PageCriteria(0, 1));
    var secondPage =
        repository.findPageByStaffIdAndTargetInstitutionIds(
            STAFF_ID, Set.of(institutionId), new PageCriteria(1, 1));

    BddLogger.then("only the activities of the institution should be paged, sorted by title");
    assertThat(firstPage.pageInfo().totalElements()).isEqualTo(2);
    assertThat(firstPage.content())
        .singleElement()
        .satisfies(
            summary -> {
              assertThat(summary.activityId()).isEqualTo(alpha.getId());
              assertThat(summary.activityTitle()).isEqualTo("Alpha");
              assertThat(summary.rights()).containsExactly(EActivityRight.EDIT);
            });
    assertThat(secondPage.content())
        .singleElement()
        .satisfies(
            summary -> {
              assertThat(summary.activityId()).isEqualTo(beta.getId());
              assertThat(summary.rights())
                  .containsExactlyInAnyOrder(EActivityRight.EDIT, EActivityRight.FEEDBACK);
            });
  }

  @Test
  void findPage_should_list_an_activity_once_when_it_targets_several_of_the_institutions() {
    BddLogger.given("an activity targeting both institutions");

    BddLogger.when("getting the activities of both institutions");
    var page =
        repository.findPageByStaffIdAndTargetInstitutionIds(
            STAFF_ID, Set.of(institutionId, otherInstitutionId), new PageCriteria(0, 10));

    BddLogger.then("each activity should appear once, sorted by title");
    assertThat(page.pageInfo().totalElements()).isEqualTo(3);
    assertThat(page.content())
        .extracting(summary -> summary.activityTitle())
        .containsExactly("Aardvark", "Alpha", "Beta");
  }

  @Test
  void findPage_should_be_empty_without_institution() {
    BddLogger.when("getting the activities of no institution");
    var page =
        repository.findPageByStaffIdAndTargetInstitutionIds(
            STAFF_ID, Set.of(), new PageCriteria(0, 10));

    BddLogger.then("the page should be empty");
    assertThat(page.content()).isEmpty();
    assertThat(page.pageInfo().totalElements()).isZero();
  }

  @Test
  void exists_should_only_consider_the_activities_of_the_institutions() {
    BddLogger.given("a staff holding rights on activities of two institutions");

    BddLogger.then("it should hold some in each institution, and none in another one");
    assertThat(repository.existsByStaffIdAndTargetInstitutionIds(STAFF_ID, Set.of(institutionId)))
        .isTrue();
    assertThat(
            repository.existsByStaffIdAndTargetInstitutionIds(STAFF_ID, Set.of(otherInstitutionId)))
        .isTrue();
    assertThat(
            repository.existsByStaffIdAndTargetInstitutionIds(STAFF_ID, Set.of(UUID.randomUUID())))
        .isFalse();
    assertThat(repository.existsByStaffIdAndTargetInstitutionIds(STAFF_ID, Set.of())).isFalse();
  }

  private void grant(UUID staffId, ActivityEntity activity, EActivityRight... rights) {
    var now = Instant.now();
    entityManager.persist(
        ActivityAuthorizationEntity.of(
            UUID.randomUUID(),
            activity.getId(),
            entityManager.find(StaffEntity.class, staffId),
            Set.of(rights),
            now,
            now));
  }
}
