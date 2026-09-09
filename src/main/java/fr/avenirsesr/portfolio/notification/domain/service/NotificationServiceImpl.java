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
import java.util.*;
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
    notifyAll(List.of(notification));
  }

  @Override
  public void notifyAll(List<? extends BaseNotification> notifications) {
    var builtNotifications =
        notifications.stream()
            .map(BaseNotification::build)
            .filter(n -> n.getUser().isNotificationEnabled())
            .toList();

    if (builtNotifications.isEmpty()) {
      return;
    }

    var savedNotifications = notificationRepository.saveAll(builtNotifications);

    List<UUID> staffIds = new ArrayList<>();
    List<UUID> studentIds = new ArrayList<>();
    for (var savedNotification : savedNotifications) {
      var userId = savedNotification.getUser().getId();
      switch (savedNotification.getUserCategory()) {
        case STAFF -> staffIds.add(userId);
        case STUDENT -> studentIds.add(userId);
        case null -> {
          staffIds.add(userId);
          studentIds.add(userId);
        }
      }
    }

    setStaffUnseenNotification(staffIds, true);
    setStudentUnseenNotification(studentIds, true);

    log.debug("[{}] notification(s) created", savedNotifications.size());
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
      case STAFF -> setStaffUnseenNotification(List.of(userId), false);
      case STUDENT -> setStudentUnseenNotification(List.of(userId), false);
      default -> throw new IllegalArgumentException("Invalid user category");
    }

    return notificationRepository.findByUserAndCategory(userId, userCategory, pageCriteria);
  }

  private void setStaffUnseenNotification(List<UUID> staffIds, boolean seen) {
    if (staffIds.isEmpty()) {
      return;
    }
    var staff = staffRepository.findAllById(List.copyOf(staffIds));
    staff.forEach(s -> s.setHasUnseenNotification(seen));
    staffRepository.saveAll(staff);
  }

  private void setStudentUnseenNotification(List<UUID> studentIds, boolean seen) {
    if (studentIds.isEmpty()) {
      return;
    }
    var students = studentRepository.findAllById(List.copyOf(studentIds));
    students.forEach(s -> s.setHasUnseenNotification(seen));
    studentRepository.saveAll(students);
  }
}
