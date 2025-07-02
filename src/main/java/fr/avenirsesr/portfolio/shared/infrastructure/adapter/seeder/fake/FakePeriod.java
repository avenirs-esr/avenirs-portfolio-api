package fr.avenirsesr.portfolio.shared.infrastructure.adapter.seeder.fake;

import java.time.Instant;
import java.time.LocalDate;
import java.time.Month;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.util.function.Function;
import lombok.Getter;

@Getter
public class FakePeriod<T extends Temporal> {

  private static final FakerProvider faker = new FakerProvider();
  private static final int academicYear = 2024;

  private final Function<LocalDate, T> localDateConverter;
  private final Function<Instant, T> instantConverter;

  private T startDate;
  private T endDate;

  public static FakePeriod<Instant> createInstantPeriod() {
    return new FakePeriod<>(
        localDate -> {
          int hour = faker.call().number().numberBetween(8, 20);
          int minute = faker.call().number().numberBetween(0, 60);
          return localDate.atTime(hour, minute).atZone(ZoneId.systemDefault()).toInstant();
        },
        instant -> instant);
  }

  private FakePeriod(
      Function<LocalDate, T> localDateConverter, Function<Instant, T> instantConverter) {
    this.localDateConverter = localDateConverter;
    this.instantConverter = instantConverter;
  }

  public void initStartDateInAcademicPeriodBeforeMay() {
    LocalDate startBoundary = LocalDate.of(academicYear, Month.SEPTEMBER, 1);
    LocalDate endBoundary = LocalDate.of(academicYear + 1, Month.MAY, 31);

    long startEpochDay = startBoundary.toEpochDay();
    long endEpochDay = endBoundary.toEpochDay();

    long randomDay =
        startEpochDay
            + faker.call().number().numberBetween(0, (int) (endEpochDay - startEpochDay + 1));

    LocalDate randomLocalDate = LocalDate.ofEpochDay(randomDay);
    startDate = localDateConverter.apply(randomLocalDate);
  }

  public void initEndDateInAcademicPeriodAfterStartDate() {
    if (startDate == null) {
      initStartDateInAcademicPeriodBeforeMay();
    }

    Instant startInstant = convertStartDateToInstant();
    Instant minimumEndDate = startInstant.plus(24, ChronoUnit.HOURS);

    Instant baseEndDate = calculateBaseEndDate(minimumEndDate);

    if (!(startDate instanceof LocalDate)) {
      baseEndDate = adjustTimeForInstantType(startInstant, baseEndDate);
    }

    endDate = instantConverter.apply(baseEndDate);
  }

  private Instant convertStartDateToInstant() {
    if (startDate instanceof LocalDate) {
      return ((LocalDate) startDate).atStartOfDay(ZoneId.systemDefault()).toInstant();
    } else {
      return (Instant) startDate;
    }
  }

  private Instant calculateBaseEndDate(Instant minimumEndDate) {
    LocalDate julyFirstBoundary = LocalDate.of(academicYear + 1, Month.JULY, 1);
    Instant julyFirstInstant = julyFirstBoundary.atStartOfDay(ZoneId.systemDefault()).toInstant();

    if (minimumEndDate.isAfter(julyFirstInstant)) {
      return julyFirstInstant.minus(1, ChronoUnit.DAYS);
    }

    return calculateRandomEndDate(minimumEndDate, julyFirstBoundary);
  }

  private Instant calculateRandomEndDate(Instant minimumEndDate, LocalDate julyFirstBoundary) {
    long daysUntilJulyFirst =
        ChronoUnit.DAYS.between(
            minimumEndDate.atZone(ZoneId.systemDefault()).toLocalDate(), julyFirstBoundary);

    int additionalDays =
        faker.call().number().numberBetween(1, (int) Math.min(180, daysUntilJulyFirst));

    return minimumEndDate.plus(additionalDays, ChronoUnit.DAYS);
  }

  private Instant adjustTimeForInstantType(Instant startInstant, Instant baseEndDate) {
    int hour = faker.call().number().numberBetween(8, 20);
    int minute = faker.call().number().numberBetween(0, 60);
    LocalDate endLocalDate = baseEndDate.atZone(ZoneId.systemDefault()).toLocalDate();
    Instant adjustedEndDate =
        endLocalDate.atTime(hour, minute).atZone(ZoneId.systemDefault()).toInstant();

    if (ChronoUnit.HOURS.between(startInstant, adjustedEndDate) < 24) {
      return startInstant.plus(24, ChronoUnit.HOURS);
    }

    return adjustedEndDate;
  }

  public static FakePeriod<Instant> createMin24hoursInstantPeriodInAcademicPeriod() {
    FakePeriod<Instant> fakePeriod = createInstantPeriod();
    fakePeriod.initStartDateInAcademicPeriodBeforeMay();
    fakePeriod.initEndDateInAcademicPeriodAfterStartDate();
    return fakePeriod;
  }
}
