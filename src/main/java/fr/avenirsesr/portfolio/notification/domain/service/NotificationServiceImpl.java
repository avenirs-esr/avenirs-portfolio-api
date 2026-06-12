package fr.avenirsesr.portfolio.notification.domain.service;

import fr.avenirsesr.portfolio.common.data.domain.model.PageCriteria;
import fr.avenirsesr.portfolio.common.data.domain.model.PagedResult;
import fr.avenirsesr.portfolio.common.data.domain.model.enums.EUserCategory;
import fr.avenirsesr.portfolio.notification.domain.exception.NotificationNotFoundException;
import fr.avenirsesr.portfolio.notification.domain.model.Notification;
import fr.avenirsesr.portfolio.notification.domain.model.notification.BaseNotification;
import fr.avenirsesr.portfolio.notification.domain.port.input.NotificationService;
import fr.avenirsesr.portfolio.notification.domain.port.output.repository.NotificationRepository;
import fr.avenirsesr.portfolio.shared.domain.port.input.LoggedInUserService;
import fr.avenirsesr.portfolio.user.domain.port.output.repository.StaffRepository;
import fr.avenirsesr.portfolio.user.domain.port.output.repository.StudentRepository;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
public class NotificationServiceImpl implements NotificationService {

  private final NotificationRepository notificationRepository;
  private final StudentRepository studentRepository;
  private final StaffRepository staffRepository;
  private final LoggedInUserService loggedInUserService;

  @Override
  public void notify(BaseNotification notification) {
    // Todo: check if user has enabled notifications
    var savedNotification = notificationRepository.save(notification.build());

    var userId = savedNotification.getUser().getId();
    switch (savedNotification.getUserCategory()) {
      case STAFF -> setStaffUnseenNotification(userId, true);
      case STUDENT -> setStudentUnseenNotification(userId, true);
      case null -> {
        setStaffUnseenNotification(userId, true);
        setStudentUnseenNotification(userId, true);
      }
    }

    log.debug("[{}] created", savedNotification);
  }

  @Override
  public void markAsSeen(UUID id) {
    log.debug("Marking notification [{}] as seen", id);
    Notification notification =
        notificationRepository.findById(id).orElseThrow(NotificationNotFoundException::new);
    notification.setSeen(true);
    notificationRepository.save(notification);
  }

  @Override
  public PagedResult<Notification> getNotifications(
      EUserCategory userCategory, PageCriteria pageCriteria) {
    UUID userId = loggedInUserService.getLoggedInUser().getId();
    log.debug("Fetching notifications for user [{}] with category [{}]", userId, userCategory);

    switch (userCategory) {
      case STAFF -> setStaffUnseenNotification(userId, false);
      case STUDENT -> setStudentUnseenNotification(userId, false);
      default -> throw new IllegalArgumentException("Invalid user category");
    }

    return notificationRepository.findByUserAndCategory(userId, userCategory, pageCriteria);
  }

  private void setStaffUnseenNotification(UUID staffId, boolean seen) {
    staffRepository
        .findById(staffId)
        .ifPresent(
            staff -> {
              staff.setHasUnseenNotification(seen);
              staffRepository.save(staff);
            });
  }

  private void setStudentUnseenNotification(UUID studentId, boolean seen) {
    studentRepository
        .findById(studentId)
        .ifPresent(
            student -> {
              student.setHasUnseenNotification(seen);
              studentRepository.save(student);
            });
  }
}
