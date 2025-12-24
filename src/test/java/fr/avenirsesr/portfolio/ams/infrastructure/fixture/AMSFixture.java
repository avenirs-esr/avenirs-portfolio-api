package fr.avenirsesr.portfolio.ams.infrastructure.fixture;

import fr.avenirsesr.portfolio.ams.domain.model.AMS;
import fr.avenirsesr.portfolio.ams.domain.model.enums.EAmsStatus;
import fr.avenirsesr.portfolio.ams.infrastructure.adapter.seeder.FakeAMS;
import fr.avenirsesr.portfolio.common.language.domain.model.enums.ELanguage;
import fr.avenirsesr.portfolio.user.domain.model.Student;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.mapper.StudentMapper;
import fr.avenirsesr.portfolio.user.infrastructure.fixture.StudentFixture;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AMSFixture {

  private UUID id;
  private Student student;
  private String title;
  private ELanguage language = ELanguage.FRENCH;
  private Instant startDate;
  private Instant endDate;
  private Instant createdAt;
  private Instant updatedAt;

  private AMSFixture() {
    var fakeStudent = StudentFixture.create().toModel();
    var base = FakeAMS.of(StudentMapper.INSTANCE.fromDomain(fakeStudent)).toEntity();
    this.id = base.getId();
    this.student = fakeStudent;
    this.title = "fake ams title";
    this.createdAt = base.getCreatedAt();
    this.updatedAt = base.getUpdatedAt();
  }

  public static AMSFixture create() {
    return new AMSFixture();
  }

  public AMSFixture withId(UUID id) {
    this.id = id;
    return this;
  }

  public AMSFixture withStudent(Student student) {
    this.student = student;
    return this;
  }

  public List<AMS> withCount(int count) {
    List<AMS> amses = new ArrayList<AMS>();
    for (int i = 0; i < count; i++) {
      amses.add(create().toModel());
    }
    return amses;
  }

  public AMSFixture withTitle(String title) {
    this.title = title;
    return this;
  }

  public AMSFixture withLanguage(ELanguage language) {
    this.language = language;
    return this;
  }

  public AMSFixture withStartDate(Instant startDate) {
    this.startDate = startDate;
    return this;
  }

  public AMSFixture withEndDate(Instant endDate) {
    this.endDate = endDate;
    return this;
  }

  public AMSFixture withCreatedAt(Instant createdAt) {
    this.createdAt = createdAt;
    return this;
  }

  public AMSFixture withUpdatedAt(Instant updatedAt) {
    this.updatedAt = updatedAt;
    return this;
  }

  public AMS toModel() {
    return AMS.toDomain(
        id, student, title, startDate, endDate, EAmsStatus.NOT_STARTED, createdAt, updatedAt);
  }
}
