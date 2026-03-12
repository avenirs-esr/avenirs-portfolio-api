package fr.avenirsesr.portfolio.association.domain.service;

import fr.avenirsesr.portfolio.association.domain.data.ActivityTraceAssociationData;
import fr.avenirsesr.portfolio.association.domain.exception.AssociationAlreadyExistException;
import fr.avenirsesr.portfolio.association.domain.model.ActivityTraceAssociation;
import fr.avenirsesr.portfolio.association.domain.port.input.ActivityTraceAssociationService;
import fr.avenirsesr.portfolio.association.domain.port.output.repository.ActivityTraceAssociationRepository;
import fr.avenirsesr.portfolio.common.security.domain.exception.UserNotAuthorizedException;
import fr.avenirsesr.portfolio.shared.domain.port.input.LoggedInUserService;
import fr.avenirsesr.portfolio.student.progress.declared.activity.domain.exception.DeclaredActivityNotFoundException;
import fr.avenirsesr.portfolio.student.progress.declared.activity.domain.model.DeclaredActivity;
import fr.avenirsesr.portfolio.student.progress.declared.activity.domain.port.output.repository.DeclaredActivityRepository;
import fr.avenirsesr.portfolio.trace.domain.exception.AssociationDoesNotExistException;
import fr.avenirsesr.portfolio.trace.domain.exception.TraceNotFoundException;
import fr.avenirsesr.portfolio.trace.domain.model.Trace;
import fr.avenirsesr.portfolio.trace.domain.port.output.repository.TraceRepository;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ActivityTraceAssociationServiceImpl implements ActivityTraceAssociationService {
  private final LoggedInUserService loggedInUserService;
  private final ActivityTraceAssociationRepository activityTraceAssociationRepository;
  private final TraceRepository traceRepository;
  private final DeclaredActivityRepository declaredActivityRepository;

  @Override
  public List<ActivityTraceAssociation> createAll(
      List<ActivityTraceAssociationData> associationsData) {
    var loggedInStudent = loggedInUserService.getLoggedInStudent();

    var traces =
        traceRepository.findAllById(
            associationsData.stream().map(ActivityTraceAssociationData::traceId).toList());
    var activities =
        declaredActivityRepository.findAllById(
            associationsData.stream()
                .map(ActivityTraceAssociationData::declaredActivityId)
                .toList());

    if (!new HashSet<>(activities.stream().map(DeclaredActivity::getId).toList())
        .containsAll(
            associationsData.stream()
                .map(ActivityTraceAssociationData::declaredActivityId)
                .toList())) throw new DeclaredActivityNotFoundException();

    if (!new HashSet<>(traces.stream().map(Trace::getId).toList())
        .containsAll(associationsData.stream().map(ActivityTraceAssociationData::traceId).toList()))
      throw new TraceNotFoundException();

    if (activities.stream().anyMatch(a -> !a.getStudent().equals(loggedInStudent))
        || traces.stream().anyMatch(t -> !t.getUser().equals(loggedInStudent.getUser())))
      throw new UserNotAuthorizedException();

    if (!activityTraceAssociationRepository.findAllIn(associationsData).isEmpty()) {
      throw new AssociationAlreadyExistException();
    }

    var associations =
        associationsData.stream()
            .map(
                association -> {
                  var activity =
                      activities.stream()
                          .filter(a -> a.getId().equals(association.declaredActivityId()))
                          .findAny()
                          .orElseThrow();
                  var trace =
                      traces.stream()
                          .filter(t -> t.getId().equals(association.traceId()))
                          .findAny()
                          .orElseThrow();
                  return ActivityTraceAssociation.create(activity, trace);
                })
            .toList();

    return activityTraceAssociationRepository.saveAll(associations);
  }

  @Override
  public List<ActivityTraceAssociation> getAllOf(DeclaredActivity declaredActivity) {
    return activityTraceAssociationRepository.findAllOf(declaredActivity);
  }

  @Override
  public List<ActivityTraceAssociation> getAllOf(Trace trace) {
    return activityTraceAssociationRepository.findAllOf(trace);
  }

  @Override
  public void deleteAllByIds(List<UUID> ids) {
    var activities = activityTraceAssociationRepository.findAllById(ids);

    if (!new HashSet<>(activities.stream().map(ActivityTraceAssociation::getId).toList())
        .containsAll(ids)) {
      throw new AssociationDoesNotExistException();
    }

    activityTraceAssociationRepository.removeAllFromDatabase(activities);
  }
}
