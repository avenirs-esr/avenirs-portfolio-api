package fr.avenirsesr.portfolio.api.infrastructure.adapter.seeder;

import lombok.Getter;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Month;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.util.function.Function;

@Getter
public class FakePeriod<T extends Temporal> {

  private static final FakerProvider faker = new FakerProvider();
  private static final int academicYear = 2024;
  
  private final Function<LocalDate, T> localDateConverter;
  private final Function<Instant, T> instantConverter;
  
  private T startDate;
  private T endDate;

  public static FakePeriod<LocalDate> createLocalDatePeriod() {
    return new FakePeriod<>(
        localDate -> localDate,
        instant -> instant.atZone(ZoneId.systemDefault()).toLocalDate()
    );
  }
  
  public static FakePeriod<Instant> createInstantPeriod() {
    return new FakePeriod<>(
        localDate -> {
          int hour = faker.call().number().numberBetween(8, 20);
          int minute = faker.call().number().numberBetween(0, 60);
          return localDate.atTime(hour, minute).atZone(ZoneId.systemDefault()).toInstant();
        },
        instant -> instant
    );
  }

  private FakePeriod(Function<LocalDate, T> localDateConverter, Function<Instant, T> instantConverter) {
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

    Instant startInstant;
    if (startDate instanceof LocalDate) {
      startInstant = ((LocalDate) startDate).atStartOfDay(ZoneId.systemDefault()).toInstant();
    } else {
      startInstant = (Instant) startDate;
    }

    Instant minimumEndDate = startInstant.plus(24, ChronoUnit.HOURS);

    LocalDate julyFirstBoundary = LocalDate.of(academicYear + 1, Month.JULY, 1);
    Instant julyFirstInstant = julyFirstBoundary.atStartOfDay(ZoneId.systemDefault()).toInstant();

    if (minimumEndDate.isAfter(julyFirstInstant)) {
      endDate = instantConverter.apply(julyFirstInstant.minus(1, ChronoUnit.DAYS));
      return;
    }

    long daysUntilJulyFirst =
        ChronoUnit.DAYS.between(
            minimumEndDate.atZone(ZoneId.systemDefault()).toLocalDate(), julyFirstBoundary);

    int additionalDays =
        faker.call().number().numberBetween(1, (int) Math.min(180, daysUntilJulyFirst));

    Instant baseEndDate = minimumEndDate.plus(additionalDays, ChronoUnit.DAYS);
    
    if (!(startDate instanceof LocalDate)) {
      int hour = faker.call().number().numberBetween(8, 20);
      int minute = faker.call().number().numberBetween(0, 60);
      LocalDate endLocalDate = baseEndDate.atZone(ZoneId.systemDefault()).toLocalDate();
      baseEndDate = endLocalDate.atTime(hour, minute).atZone(ZoneId.systemDefault()).toInstant();
      
      if (ChronoUnit.HOURS.between(startInstant, baseEndDate) < 24) {
        baseEndDate = startInstant.plus(24, ChronoUnit.HOURS);
      }
    }
    
    endDate = instantConverter.apply(baseEndDate);
  }

  public static FakePeriod<LocalDate> createMin24hoursLocalDatePeriodInAcademicPeriod() {
    FakePeriod<LocalDate> fakePeriod = createLocalDatePeriod();
    fakePeriod.initStartDateInAcademicPeriodBeforeMay();
    fakePeriod.initEndDateInAcademicPeriodAfterStartDate();
    return fakePeriod;
  }
  
  public static FakePeriod<Instant> createMin24hoursInstantPeriodInAcademicPeriod() {
    FakePeriod<Instant> fakePeriod = createInstantPeriod();
    fakePeriod.initStartDateInAcademicPeriodBeforeMay();
    fakePeriod.initEndDateInAcademicPeriodAfterStartDate();
    return fakePeriod;
  }
}
