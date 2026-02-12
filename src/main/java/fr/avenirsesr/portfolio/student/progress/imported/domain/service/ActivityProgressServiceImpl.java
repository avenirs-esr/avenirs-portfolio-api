package fr.avenirsesr.portfolio.student.progress.imported.domain.service;

import fr.avenirsesr.portfolio.activity.domain.exception.ActivityNotFoundException;
import fr.avenirsesr.portfolio.activity.domain.model.Activity;
import fr.avenirsesr.portfolio.activity.domain.port.output.repository.ActivityRepository;
import fr.avenirsesr.portfolio.shared.domain.port.input.LoggedInUserService;
import fr.avenirsesr.portfolio.student.progress.imported.domain.model.ActivityProgress;
import fr.avenirsesr.portfolio.student.progress.imported.domain.model.enums.EActivityProgressStatus;
import fr.avenirsesr.portfolio.student.progress.imported.domain.port.input.ActivityProgressService;
import fr.avenirsesr.portfolio.student.progress.imported.domain.port.output.repository.ActivityProgressRepository;
import fr.avenirsesr.portfolio.user.domain.model.Student;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;

@Slf4j
@AllArgsConstructor
public class ActivityProgressServiceImpl implements ActivityProgressService {
    private final ActivityProgressRepository activityProgressRepository;
    private final ActivityRepository activityRepository;
    private final LoggedInUserService loggedInUserService;

    @Override
    public void subscribe(UUID activityId) {
        Student student = loggedInUserService.getLoggedInStudent();
        Activity activity = activityRepository.findById(activityId).orElseThrow(ActivityNotFoundException::new);
        ActivityProgress activityProgress = ActivityProgress.create(student, activity, EActivityProgressStatus.SUBSCRIBED, null, null, null);
        activityProgressRepository.save(activityProgress);
    }
}
