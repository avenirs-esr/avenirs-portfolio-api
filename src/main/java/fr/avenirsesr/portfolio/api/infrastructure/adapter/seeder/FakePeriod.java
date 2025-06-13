package fr.avenirsesr.portfolio.api.infrastructure.adapter.seeder;

import lombok.Getter;

import java.time.Instant;
import java.time.LocalDate;
import java.time.Month;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
public class FakePeriod {

  private static final FakerProvider faker = new FakerProvider();

  @Value("${fake.academic.year:2024}")
  private int academicYear;

  private Instant startDate;
  private Instant endDate;

  private FakePeriod() {}

  public void initStartDateInAcademicPeriodBeforeMay() {
    LocalDate startBoundary = LocalDate.of(academicYear, Month.SEPTEMBER, 1);
    LocalDate endBoundary = LocalDate.of(academicYear + 1, Month.MAY, 31);

    long startEpochDay = startBoundary.toEpochDay();
    long endEpochDay = endBoundary.toEpochDay();

    long randomDay =
        startEpochDay
            + faker.call().number().numberBetween(0, (int) (endEpochDay - startEpochDay + 1));

    startDate = LocalDate.ofEpochDay(randomDay).atStartOfDay(ZoneId.systemDefault()).toInstant();
  }

  public void initEndDateInAcademicPeriodAfterStartDate() {
    if (startDate == null) {
      initStartDateInAcademicPeriodBeforeMay();
    }

    Instant minimumEndDate = startDate.plus(24, ChronoUnit.HOURS);

    LocalDate julyFirstBoundary = LocalDate.of(academicYear + 1, Month.JULY, 1);
    Instant julyFirstInstant = julyFirstBoundary.atStartOfDay(ZoneId.systemDefault()).toInstant();

    if (minimumEndDate.isAfter(julyFirstInstant)) {
      endDate = julyFirstInstant.minus(1, ChronoUnit.DAYS);
      return;
    }

    long daysUntilJulyFirst =
        ChronoUnit.DAYS.between(
            minimumEndDate.atZone(ZoneId.systemDefault()).toLocalDate(), julyFirstBoundary);

    int additionalDays =
        faker.call().number().numberBetween(1, (int) Math.min(180, daysUntilJulyFirst));

    endDate = minimumEndDate.plus(additionalDays, ChronoUnit.DAYS);
  }

  static FakePeriod createMin24hoursPeriodInAcademicPeriod() {
    FakePeriod fakePeriod = new FakePeriod();
    fakePeriod.initStartDateInAcademicPeriodBeforeMay();
    fakePeriod.initEndDateInAcademicPeriodAfterStartDate();
    return fakePeriod;
  }
}
