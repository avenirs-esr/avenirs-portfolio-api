package fr.avenirsesr.portfolio.student.progress.declared.activity.domain.service;

import static fr.avenirsesr.portfolio.common.validation.domain.constraints.FieldMaxLengths.REFLECTION_LENGTH;
import static fr.avenirsesr.portfolio.common.validation.domain.utils.FieldValidationUtils.validateDateOrder;
import static fr.avenirsesr.portfolio.common.validation.domain.utils.FieldValidationUtils.validateOptionalTextMaxLength;

import fr.avenirsesr.portfolio.activity.application.adapter.dto.ActivityPeriodRequest;
import fr.avenirsesr.portfolio.activity.domain.exception.ActivityNotFoundException;
import fr.avenirsesr.portfolio.activity.domain.model.Activity;
import fr.avenirsesr.portfolio.activity.domain.port.output.repository.ActivityRepository;
import fr.avenirsesr.portfolio.common.data.domain.FetchGraph;
import fr.avenirsesr.portfolio.common.data.domain.model.PageCriteria;
import fr.avenirsesr.portfolio.common.data.domain.model.PagedResult;
import fr.avenirsesr.portfolio.common.security.domain.exception.UserNotAuthorizedException;
import fr.avenirsesr.portfolio.common.testutils.BddLogger;
import fr.avenirsesr.portfolio.shared.domain.port.input.LoggedInUserService;
import fr.avenirsesr.portfolio.student.progress.declared.activity.domain.exception.DeclaredActivityAlreadyExistException;
import fr.avenirsesr.portfolio.student.progress.declared.activity.domain.exception.DeclaredActivityAlreadyFinishedException;
import fr.avenirsesr.portfolio.student.progress.declared.activity.domain.exception.DeclaredActivityDatesException;
import fr.avenirsesr.portfolio.student.progress.declared.activity.domain.exception.DeclaredActivityHasNotStartedException;
import fr.avenirsesr.portfolio.student.progress.declared.activity.domain.exception.DeclaredActivityNotFoundException;
import fr.avenirsesr.portfolio.student.progress.declared.activity.domain.exception.DeclaredActivityStartDateBeforeSubscriptionException;
import fr.avenirsesr.portfolio.student.progress.declared.activity.domain.model.DeclaredActivity;
import fr.avenirsesr.portfolio.student.progress.declared.activity.domain.port.input.DeclaredActivityService;
import fr.avenirsesr.portfolio.student.progress.declared.activity.domain.port.output.repository.DeclaredActivityRepository;
import fr.avenirsesr.portfolio.user.domain.model.Student;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
public class DeclaredActivityServiceImpl implements DeclaredActivityService {
  private final DeclaredActivityRepository declaredActivityRepository;
  private final ActivityRepository activityRepository;
  private final LoggedInUserService loggedInUserService;

  @Override
  public PagedResult<DeclaredActivity> getDeclaredActivities(PageCriteria pageCriteria) {
    Student student = loggedInUserService.getLoggedInStudent();
    var graph = FetchGraph.init().fetch("activity");

    return declaredActivityRepository.findStudentActivitiesByProgressAndDate(
        student, pageCriteria, graph);
  }

  @Override
  public List<DeclaredActivity> getAllDeclaredActivitiesOf(Student student) {
    var graph = FetchGraph.init().fetch("activity");
    return declaredActivityRepository.findAllByStudent(student, graph);
  }

  @Override
  public boolean isSubscribedTo(Activity activity) {
    Student student = loggedInUserService.getLoggedInStudent();
    return declaredActivityRepository.isSubscribedTo(student, activity);
  }

  @Override
  public DeclaredActivity subscribe(UUID activityId, LocalDate startDate, LocalDate endDate) {
    Student student = loggedInUserService.getLoggedInStudent();
    Activity activity =
        activityRepository.findById(activityId).orElseThrow(ActivityNotFoundException::new);

    if (declaredActivityRepository.isSubscribedTo(student, activity)) {
      throw new DeclaredActivityAlreadyExistException();
    }

    validateActivityDates(startDate, endDate);

    DeclaredActivity declaredActivity =
        DeclaredActivity.create(student, activity, null, null, startDate, endDate, null);
    return declaredActivityRepository.save(declaredActivity);
  }

  @Override
  public void unsubscribeMultiple(List<UUID> activityIds) {
    Student student = loggedInUserService.getLoggedInStudent();

    List<DeclaredActivity> declaredActivities =
        declaredActivityRepository.findAllByActivityIdAndStudent(
            activityIds, student, FetchGraph.init().fetch("activity").add("student").fetch("user"));

    if (!declaredActivities.stream()
        .map(declaredActivity -> declaredActivity.getActivity().getId())
        .collect(Collectors.toSet())
        .containsAll(activityIds)) {
      throw new DeclaredActivityNotFoundException();
    }

    declaredActivityRepository.removeAllFromDatabase(declaredActivities);
  }

  @Override
  public DeclaredActivity finish(UUID declaredActivityId) {
    Student student = loggedInUserService.getLoggedInStudent();
    DeclaredActivity declaredActivity =
        declaredActivityRepository
            .findById(declaredActivityId)
            .orElseThrow(DeclaredActivityNotFoundException::new);

    if (!declaredActivity.getStudent().equals(student)) {
      throw new UserNotAuthorizedException();
    }

    if (declaredActivity.getStartedAt().isEmpty()) {
      throw new DeclaredActivityHasNotStartedException();
    }

    if (declaredActivity.getFinishedAt().isPresent()) {
      throw new DeclaredActivityAlreadyFinishedException();
    }

    declaredActivity.setFinishedAt(Instant.now());

    return declaredActivityRepository.save(declaredActivity);
  }

  private static void fieldsValidation(String reflection) {
    validateOptionalTextMaxLength("reflection", reflection, REFLECTION_LENGTH);
  }

  @Override
  public void updateReflection(UUID declaredActivityId, String reflection) {
    Student student = loggedInUserService.getLoggedInStudent();
    DeclaredActivity declaredActivity =
        declaredActivityRepository
            .findById(declaredActivityId)
            .orElseThrow(DeclaredActivityNotFoundException::new);
    if (!declaredActivity.getStudent().equals(student)) {
      throw new UserNotAuthorizedException();
    }
    fieldsValidation(reflection);
    if (declaredActivity.getFinishedAt().isPresent()) {
      throw new DeclaredActivityAlreadyFinishedException();
    }
    declaredActivity.setReflection(reflection);

    if (declaredActivity.getStartedAt().isEmpty()) {
      declaredActivity.setStartedAt(Instant.now());
      declaredActivityRepository.save(declaredActivity);
    }
  }

  @Override
  public DeclaredActivity getDeclaredActivityDetails(UUID declaredActivityId) {
    Student student = loggedInUserService.getLoggedInStudent();
    var graph = FetchGraph.init().fetch("activity").add("student").fetch("user");

    DeclaredActivity declaredActivity =
        declaredActivityRepository
            .findById(declaredActivityId, graph)
            .orElseThrow(DeclaredActivityNotFoundException::new);

    if (!declaredActivity.getStudent().equals(student)) {
      throw new UserNotAuthorizedException();
    }

    return declaredActivity;
  }

  @Override
  public void updatePeriod(UUID declaredActivityId, ActivityPeriodRequest request) {
    BddLogger.given("Un étudiant connecté souhaite modifier les dates de son activité");

    var student = loggedInUserService.getLoggedInStudent();
    BddLogger.and("Une DeclaredActivity existante avec l'id : " + declaredActivityId);

    var declaredActivity =
        declaredActivityRepository
            .findById(declaredActivityId)
            .orElseThrow(() -> new DeclaredActivityNotFoundException("Activity not found"));

    if (!declaredActivity.getStudent().equals(student)) {
      BddLogger.then("L'étudiant n'est pas autorisé à modifier cette activité");
      throw new UserNotAuthorizedException();
    }

    BddLogger.when("Les dates sont validées selon les règles métier");
    validateDates(request, declaredActivity.getCreatedAt());

    BddLogger.then("Les dates de début et de fin sont mises à jour");
    declaredActivity.setStartDate(request.startDate());
    declaredActivity.setEndDate(request.endDate());

    declaredActivityRepository.save(declaredActivity);
  }

  private void validateDates(ActivityPeriodRequest request, Instant subscribeDate) {

    var startDate = request.startDate();
    var endDate = request.endDate();

    BddLogger.given("Vérification que startDate et endDate sont cohérentes");

    if ((startDate == null) != (endDate == null)) {
      throw new DeclaredActivityDatesException();
    }

    if (startDate != null) {
      BddLogger.and("Validation de l'ordre des dates");
      validateDateOrder(startDate, endDate);

      LocalDate inscriptionDate = subscribeDate.atZone(ZoneId.systemDefault()).toLocalDate();

      if (startDate.isBefore(inscriptionDate)) {
        BddLogger.then("La date de début est antérieure à la date d'inscription");
        throw new DeclaredActivityStartDateBeforeSubscriptionException();
      }
    }
  }

  private void validateActivityDates(LocalDate startDate, LocalDate endDate) {
    if ((startDate == null) != (endDate == null)) {
      throw new DeclaredActivityDatesException();
    }

    if (startDate != null) {
      validateDateOrder(startDate, endDate);

      if (startDate.isBefore(LocalDate.now())) {
        throw new DeclaredActivityStartDateBeforeSubscriptionException();
      }
    }
  }
}
