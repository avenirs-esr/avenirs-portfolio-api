package fr.avenirsesr.portfolio.student.progress.declared.activity.domain.service;

import fr.avenirsesr.portfolio.activity.domain.exception.ActivityNotFoundException;
import fr.avenirsesr.portfolio.activity.domain.model.Activity;
import fr.avenirsesr.portfolio.activity.domain.port.output.repository.ActivityRepository;
import fr.avenirsesr.portfolio.common.data.domain.FetchGraph;
import fr.avenirsesr.portfolio.shared.domain.port.input.LoggedInUserService;
import fr.avenirsesr.portfolio.student.progress.declared.activity.domain.exception.DeclaredActivityAlreadyExistException;
import fr.avenirsesr.portfolio.student.progress.declared.activity.domain.model.DeclaredActivity;
import fr.avenirsesr.portfolio.student.progress.declared.activity.domain.port.input.DeclaredActivityService;
import fr.avenirsesr.portfolio.student.progress.declared.activity.domain.port.output.repository.DeclaredActivityRepository;
import fr.avenirsesr.portfolio.user.domain.model.Student;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
public class DeclaredActivityServiceImpl implements DeclaredActivityService {
  private final DeclaredActivityRepository declaredActivityRepository;
  private final ActivityRepository activityRepository;
  private final LoggedInUserService loggedInUserService;

  @Override
  public DeclaredActivity subscribe(UUID activityId) {
    Student student = loggedInUserService.getLoggedInStudent();
    Activity activity =
        activityRepository.findById(activityId).orElseThrow(ActivityNotFoundException::new);

    var graph = FetchGraph.init().fetch("activity");

    List<DeclaredActivity> declaredActivityList =
        declaredActivityRepository.findAllByStudent(student, graph);
    if (declaredActivityList.stream()
        .anyMatch(declaredActivity -> declaredActivity.getActivity().equals(activity))) {
      throw new DeclaredActivityAlreadyExistException();
    }

    DeclaredActivity declaredActivity =
        DeclaredActivity.create(student, activity, false, null, null, null);
    return declaredActivityRepository.save(declaredActivity);
  }
}
