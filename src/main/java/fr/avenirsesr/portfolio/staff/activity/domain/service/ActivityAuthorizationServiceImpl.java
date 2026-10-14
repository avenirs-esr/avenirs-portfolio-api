package fr.avenirsesr.portfolio.staff.activity.domain.service;

import fr.avenirsesr.portfolio.common.security.domain.exception.UserNotAuthorizedException;
import fr.avenirsesr.portfolio.shared.domain.port.input.LoggedInUserService;
import fr.avenirsesr.portfolio.staff.activity.domain.data.ActivityAuthorizationDefinition;
import fr.avenirsesr.portfolio.staff.activity.domain.exception.ActivityNotFoundException;
import fr.avenirsesr.portfolio.staff.activity.domain.exception.ActivityResponsibleRequiredException;
import fr.avenirsesr.portfolio.staff.activity.domain.model.ActivityAuthorization;
import fr.avenirsesr.portfolio.staff.activity.domain.model.enums.EActivityRight;
import fr.avenirsesr.portfolio.staff.activity.domain.port.input.ActivityAuthorizationService;
import fr.avenirsesr.portfolio.staff.activity.domain.port.output.repository.ActivityAuthorizationRepository;
import fr.avenirsesr.portfolio.staff.activity.domain.port.output.repository.ActivityDraftRepository;
import fr.avenirsesr.portfolio.staff.activity.domain.port.output.repository.ActivityRepository;
import fr.avenirsesr.portfolio.user.domain.exception.UserIsNotStaffException;
import fr.avenirsesr.portfolio.user.domain.model.Staff;
import fr.avenirsesr.portfolio.user.domain.port.output.repository.StaffRepository;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
public class ActivityAuthorizationServiceImpl implements ActivityAuthorizationService {
  private final ActivityAuthorizationRepository activityAuthorizationRepository;
  private final ActivityRepository activityRepository;
  private final ActivityDraftRepository activityDraftRepository;
  private final StaffRepository staffRepository;
  private final LoggedInUserService loggedInUserService;

  @Override
  public boolean hasRight(Staff staff, UUID activityId, EActivityRight right) {
    return findAuthorization(staff, activityId)
        .map(authorization -> authorization.has(right))
        .orElse(false);
  }

  @Override
  public boolean hasAnyRight(Staff staff, UUID activityId, Set<EActivityRight> rights) {
    return findAuthorization(staff, activityId)
        .map(authorization -> authorization.hasAnyOf(rights))
        .orElse(false);
  }

  @Override
  public boolean hasAnyRight(Staff staff, UUID activityId) {
    return findAuthorization(staff, activityId)
        .map(authorization -> !authorization.getRights().isEmpty())
        .orElse(false);
  }

  @Override
  public void checkRight(Staff staff, UUID activityId, EActivityRight right) {
    if (!hasRight(staff, activityId, right)) {
      throw new UserNotAuthorizedException();
    }
  }

  @Override
  public void checkAnyRight(Staff staff, UUID activityId, Set<EActivityRight> rights) {
    if (!hasAnyRight(staff, activityId, rights)) {
      throw new UserNotAuthorizedException();
    }
  }

  @Override
  public void checkAnyRight(Staff staff, UUID activityId) {
    if (!hasAnyRight(staff, activityId)) {
      throw new UserNotAuthorizedException();
    }
  }

  @Override
  public Set<EActivityRight> getRights(Staff staff, UUID activityId) {
    return findAuthorization(staff, activityId)
        .map(ActivityAuthorization::getRights)
        .orElseGet(() -> EnumSet.noneOf(EActivityRight.class));
  }

  @Override
  public Set<EActivityRight> getLoggedInStaffRights(UUID activityId) {
    try {
      return getRights(loggedInUserService.getLoggedInStaff(), activityId);
    } catch (UserIsNotStaffException e) {
      return EnumSet.noneOf(EActivityRight.class);
    }
  }

  @Override
  public List<ActivityAuthorization> getActivityAuthorizations(UUID activityId) {
    requireActivityOrDraft(activityId);
    checkAnyRight(loggedInUserService.getLoggedInStaff(), activityId);
    return activityAuthorizationRepository.findAllByActivityId(activityId);
  }

  @Override
  public List<ActivityAuthorization> defineActivityAuthorizations(
      UUID activityId, List<ActivityAuthorizationDefinition> definitions) {
    requireActivityOrDraft(activityId);
    checkRight(
        loggedInUserService.getLoggedInStaff(), activityId, EActivityRight.MANAGE_AUTHORIZATIONS);

    var rightsByStaffId = mergeRightsByStaffId(definitions);
    if (rightsByStaffId.values().stream()
        .noneMatch(rights -> rights.containsAll(EActivityRight.MANAGEMENT_RIGHTS))) {
      throw new ActivityResponsibleRequiredException();
    }
    var staffsById = findStaffsById(rightsByStaffId.keySet());

    var existingByStaffId =
        activityAuthorizationRepository.findAllByActivityId(activityId).stream()
            .collect(
                Collectors.toMap(
                    authorization -> authorization.getStaff().getId(), Function.identity()));

    var removed =
        existingByStaffId.entrySet().stream()
            .filter(entry -> !rightsByStaffId.containsKey(entry.getKey()))
            .map(Map.Entry::getValue)
            .toList();
    if (!removed.isEmpty()) {
      activityAuthorizationRepository.removeAllFromDatabase(removed);
    }

    var toSave = new ArrayList<ActivityAuthorization>();
    rightsByStaffId.forEach(
        (staffId, rights) -> {
          var existing = existingByStaffId.get(staffId);
          if (existing == null) {
            toSave.add(ActivityAuthorization.create(activityId, staffsById.get(staffId), rights));
          } else {
            existing.setRights(rights);
            toSave.add(existing);
          }
        });

    var saved = activityAuthorizationRepository.saveAll(toSave);
    log.info("Defined {} authorizations on activity {}", saved.size(), activityId);
    return saved;
  }

  @Override
  public void grantAllRights(UUID activityId, Staff staff) {
    var authorization =
        findAuthorization(staff, activityId)
            .orElseGet(() -> ActivityAuthorization.create(activityId, staff, Set.of()));
    authorization.setRights(EActivityRight.all());
    activityAuthorizationRepository.save(authorization);
  }

  @Override
  public List<Staff> getStaffsWithRight(UUID activityId, EActivityRight right) {
    return activityAuthorizationRepository.findAllByActivityId(activityId).stream()
        .filter(authorization -> authorization.has(right))
        .map(ActivityAuthorization::getStaff)
        .toList();
  }

  @Override
  public Map<UUID, List<Staff>> getResponsiblesByActivityIds(Collection<UUID> activityIds) {
    if (activityIds.isEmpty()) {
      return Map.of();
    }
    return activityAuthorizationRepository.findAllByActivityIds(activityIds).stream()
        .filter(ActivityAuthorization::isResponsible)
        .collect(
            Collectors.groupingBy(
                ActivityAuthorization::getActivityId,
                Collectors.mapping(ActivityAuthorization::getStaff, Collectors.toList())));
  }

  @Override
  public void deleteAllOfActivity(UUID activityId) {
    activityAuthorizationRepository.deleteAllByActivityId(activityId);
  }

  private Optional<ActivityAuthorization> findAuthorization(Staff staff, UUID activityId) {
    if (staff == null || activityId == null) {
      return Optional.empty();
    }
    return activityAuthorizationRepository.findByActivityIdAndStaffId(activityId, staff.getId());
  }

  private void requireActivityOrDraft(UUID activityId) {
    if (!activityRepository.existsById(activityId)
        && !activityDraftRepository.existsById(activityId)) {
      throw new ActivityNotFoundException();
    }
  }

  private Map<UUID, Set<EActivityRight>> mergeRightsByStaffId(
      List<ActivityAuthorizationDefinition> definitions) {
    var rightsByStaffId = new LinkedHashMap<UUID, Set<EActivityRight>>();
    if (definitions == null) {
      return rightsByStaffId;
    }
    definitions.stream()
        .filter(definition -> definition.staffId() != null)
        .filter(definition -> definition.rights() != null && !definition.rights().isEmpty())
        .forEach(
            definition ->
                rightsByStaffId
                    .computeIfAbsent(
                        definition.staffId(), ignored -> EnumSet.noneOf(EActivityRight.class))
                    .addAll(definition.rights()));
    return rightsByStaffId;
  }

  private Map<UUID, Staff> findStaffsById(Set<UUID> staffIds) {
    var staffsById =
        staffRepository.findAllById(List.copyOf(staffIds)).stream()
            .collect(Collectors.toMap(Staff::getId, Function.identity()));
    if (!staffsById.keySet().containsAll(staffIds)) {
      throw new UserIsNotStaffException();
    }
    return staffsById;
  }
}
