package fr.avenirsesr.portfolio.staff.activity.domain.service;

import fr.avenirsesr.portfolio.common.security.domain.exception.UserNotAuthorizedException;
import fr.avenirsesr.portfolio.shared.domain.port.input.LoggedInUserService;
import fr.avenirsesr.portfolio.staff.activity.domain.data.ActivityAuthorizationDefinition;
import fr.avenirsesr.portfolio.staff.activity.domain.exception.ActivityDraftNotFoundException;
import fr.avenirsesr.portfolio.staff.activity.domain.exception.ActivityNotFoundException;
import fr.avenirsesr.portfolio.staff.activity.domain.exception.ActivityResponsibleRequiredException;
import fr.avenirsesr.portfolio.staff.activity.domain.model.ActivityAuthorization;
import fr.avenirsesr.portfolio.staff.activity.domain.model.enums.EActivityRight;
import fr.avenirsesr.portfolio.staff.activity.domain.port.input.ActivityAuthorizationService;
import fr.avenirsesr.portfolio.staff.activity.domain.port.output.repository.ActivityAuthorizationRepository;
import fr.avenirsesr.portfolio.staff.activity.domain.port.output.repository.ActivityDraftAuthorizationRepository;
import fr.avenirsesr.portfolio.staff.activity.domain.port.output.repository.ActivityDraftRepository;
import fr.avenirsesr.portfolio.staff.activity.domain.port.output.repository.ActivityRepository;
import fr.avenirsesr.portfolio.staff.activity.domain.port.output.repository.StaffAuthorizationRepository;
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
  private final ActivityDraftAuthorizationRepository activityDraftAuthorizationRepository;
  private final ActivityRepository activityRepository;
  private final ActivityDraftRepository activityDraftRepository;
  private final StaffRepository staffRepository;
  private final LoggedInUserService loggedInUserService;

  // ── published activities ────────────────────────────────────────────

  @Override
  public boolean hasRight(Staff staff, UUID activityId, EActivityRight right) {
    return has(activityAuthorizationRepository, staff, activityId, right);
  }

  @Override
  public boolean hasAnyRight(Staff staff, UUID activityId, Set<EActivityRight> rights) {
    return hasAny(activityAuthorizationRepository, staff, activityId, rights);
  }

  @Override
  public boolean hasAnyRight(Staff staff, UUID activityId) {
    return hasAny(activityAuthorizationRepository, staff, activityId);
  }

  @Override
  public void checkRight(Staff staff, UUID activityId, EActivityRight right) {
    requireGranted(has(activityAuthorizationRepository, staff, activityId, right));
  }

  @Override
  public void checkAnyRight(Staff staff, UUID activityId, Set<EActivityRight> rights) {
    requireGranted(hasAny(activityAuthorizationRepository, staff, activityId, rights));
  }

  @Override
  public Set<EActivityRight> getRights(Staff staff, UUID activityId) {
    return rightsOf(activityAuthorizationRepository, staff, activityId);
  }

  @Override
  public Set<EActivityRight> getLoggedInStaffRights(UUID activityId) {
    return loggedInStaff()
        .map(staff -> rightsOf(activityAuthorizationRepository, staff, activityId))
        .orElseGet(() -> EnumSet.noneOf(EActivityRight.class));
  }

  @Override
  public List<ActivityAuthorization> getActivityAuthorizations(UUID activityId) {
    requireActivity(activityId);
    requireGranted(
        hasAny(
            activityAuthorizationRepository, loggedInUserService.getLoggedInStaff(), activityId));
    return activityAuthorizationRepository.findAllByActivityId(activityId);
  }

  @Override
  public List<ActivityAuthorization> defineActivityAuthorizations(
      UUID activityId, List<ActivityAuthorizationDefinition> definitions) {
    requireActivity(activityId);
    checkRight(
        loggedInUserService.getLoggedInStaff(), activityId, EActivityRight.MANAGE_AUTHORIZATIONS);
    return define(activityAuthorizationRepository, activityId, definitions);
  }

  @Override
  public void grantAllRights(UUID activityId, Staff staff) {
    grantAll(activityAuthorizationRepository, activityId, staff);
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
    return responsiblesOf(activityAuthorizationRepository, activityIds);
  }

  @Override
  public void deleteAllOfActivity(UUID activityId) {
    activityAuthorizationRepository.deleteAllByActivityId(activityId);
  }

  // ── drafts ──────────────────────────────────────────────────────────

  @Override
  public boolean hasDraftRight(Staff staff, UUID activityDraftId, EActivityRight right) {
    return has(activityDraftAuthorizationRepository, staff, activityDraftId, right);
  }

  @Override
  public boolean hasAnyDraftRight(Staff staff, UUID activityDraftId, Set<EActivityRight> rights) {
    return hasAny(activityDraftAuthorizationRepository, staff, activityDraftId, rights);
  }

  @Override
  public void checkDraftRight(Staff staff, UUID activityDraftId, EActivityRight right) {
    requireGranted(has(activityDraftAuthorizationRepository, staff, activityDraftId, right));
  }

  @Override
  public void checkAnyDraftRight(Staff staff, UUID activityDraftId, Set<EActivityRight> rights) {
    requireGranted(hasAny(activityDraftAuthorizationRepository, staff, activityDraftId, rights));
  }

  @Override
  public Set<EActivityRight> getLoggedInStaffDraftRights(UUID activityDraftId) {
    return loggedInStaff()
        .map(staff -> rightsOf(activityDraftAuthorizationRepository, staff, activityDraftId))
        .orElseGet(() -> EnumSet.noneOf(EActivityRight.class));
  }

  @Override
  public List<ActivityAuthorization> getDraftAuthorizations(UUID activityDraftId) {
    requireDraft(activityDraftId);
    requireGranted(
        hasAny(
            activityDraftAuthorizationRepository,
            loggedInUserService.getLoggedInStaff(),
            activityDraftId));
    return activityDraftAuthorizationRepository.findAllByActivityId(activityDraftId);
  }

  @Override
  public List<ActivityAuthorization> defineDraftAuthorizations(
      UUID activityDraftId, List<ActivityAuthorizationDefinition> definitions) {
    requireDraft(activityDraftId);
    checkDraftRight(
        loggedInUserService.getLoggedInStaff(),
        activityDraftId,
        EActivityRight.MANAGE_AUTHORIZATIONS);
    return define(activityDraftAuthorizationRepository, activityDraftId, definitions);
  }

  @Override
  public void grantAllDraftRights(UUID activityDraftId, Staff staff) {
    grantAll(activityDraftAuthorizationRepository, activityDraftId, staff);
  }

  @Override
  public Map<UUID, List<Staff>> getResponsiblesByDraftIds(Collection<UUID> activityDraftIds) {
    return responsiblesOf(activityDraftAuthorizationRepository, activityDraftIds);
  }

  @Override
  public void deleteAllOfDraft(UUID activityDraftId) {
    activityDraftAuthorizationRepository.deleteAllByActivityId(activityDraftId);
  }

  // ── transitions between an activity and its draft ───────────────────

  @Override
  public void copyActivityAuthorizationsToDraft(UUID activityId) {
    copy(activityAuthorizationRepository, activityDraftAuthorizationRepository, activityId);
  }

  @Override
  public void applyDraftAuthorizationsToActivity(UUID activityId) {
    copy(activityDraftAuthorizationRepository, activityAuthorizationRepository, activityId);
    deleteAllOfDraft(activityId);
  }

  private void copy(
      StaffAuthorizationRepository source, StaffAuthorizationRepository target, UUID activityId) {
    var authorizations = source.findAllByActivityId(activityId);
    if (authorizations.isEmpty()) {
      return;
    }

    var rightsByStaffId = new LinkedHashMap<UUID, Set<EActivityRight>>();
    var staffsById = new LinkedHashMap<UUID, Staff>();
    authorizations.forEach(
        authorization -> {
          rightsByStaffId.put(authorization.getStaff().getId(), authorization.getRights());
          staffsById.put(authorization.getStaff().getId(), authorization.getStaff());
        });

    replace(target, activityId, rightsByStaffId, staffsById);
    log.info("Copied {} authorizations of activity {}", authorizations.size(), activityId);
  }

  // ── shared implementation ───────────────────────────────────────────

  private boolean has(
      StaffAuthorizationRepository repository, Staff staff, UUID activityId, EActivityRight right) {
    return find(repository, staff, activityId)
        .map(authorization -> authorization.has(right))
        .orElse(false);
  }

  private boolean hasAny(
      StaffAuthorizationRepository repository,
      Staff staff,
      UUID activityId,
      Set<EActivityRight> rights) {
    return find(repository, staff, activityId)
        .map(authorization -> authorization.hasAnyOf(rights))
        .orElse(false);
  }

  private boolean hasAny(StaffAuthorizationRepository repository, Staff staff, UUID activityId) {
    return find(repository, staff, activityId)
        .map(authorization -> !authorization.getRights().isEmpty())
        .orElse(false);
  }

  private Set<EActivityRight> rightsOf(
      StaffAuthorizationRepository repository, Staff staff, UUID activityId) {
    return find(repository, staff, activityId)
        .map(ActivityAuthorization::getRights)
        .orElseGet(() -> EnumSet.noneOf(EActivityRight.class));
  }

  private Optional<ActivityAuthorization> find(
      StaffAuthorizationRepository repository, Staff staff, UUID activityId) {
    if (staff == null || activityId == null) {
      return Optional.empty();
    }
    return repository.findByActivityIdAndStaffId(activityId, staff.getId());
  }

  private List<ActivityAuthorization> define(
      StaffAuthorizationRepository repository,
      UUID activityId,
      List<ActivityAuthorizationDefinition> definitions) {
    var rightsByStaffId = mergeRightsByStaffId(definitions);
    if (rightsByStaffId.values().stream()
        .noneMatch(rights -> rights.containsAll(EActivityRight.MANAGEMENT_RIGHTS))) {
      throw new ActivityResponsibleRequiredException();
    }

    var saved =
        replace(repository, activityId, rightsByStaffId, findStaffsById(rightsByStaffId.keySet()));
    log.info("Defined {} authorizations on activity {}", saved.size(), activityId);
    return saved;
  }

  private List<ActivityAuthorization> replace(
      StaffAuthorizationRepository repository,
      UUID activityId,
      Map<UUID, Set<EActivityRight>> rightsByStaffId,
      Map<UUID, Staff> staffsById) {
    var existingByStaffId =
        repository.findAllByActivityId(activityId).stream()
            .collect(
                Collectors.toMap(
                    authorization -> authorization.getStaff().getId(), Function.identity()));

    var removed =
        existingByStaffId.entrySet().stream()
            .filter(entry -> !rightsByStaffId.containsKey(entry.getKey()))
            .map(Map.Entry::getValue)
            .toList();
    if (!removed.isEmpty()) {
      repository.removeAllFromDatabase(removed);
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

    return repository.saveAll(toSave);
  }

  private void grantAll(StaffAuthorizationRepository repository, UUID activityId, Staff staff) {
    var authorization =
        find(repository, staff, activityId)
            .orElseGet(() -> ActivityAuthorization.create(activityId, staff, Set.of()));
    authorization.setRights(EActivityRight.all());
    repository.save(authorization);
  }

  private Map<UUID, List<Staff>> responsiblesOf(
      StaffAuthorizationRepository repository, Collection<UUID> activityIds) {
    if (activityIds.isEmpty()) {
      return Map.of();
    }
    return repository.findAllByActivityIds(activityIds).stream()
        .filter(ActivityAuthorization::isResponsible)
        .collect(
            Collectors.groupingBy(
                ActivityAuthorization::getActivityId,
                Collectors.mapping(ActivityAuthorization::getStaff, Collectors.toList())));
  }

  private Optional<Staff> loggedInStaff() {
    try {
      return Optional.of(loggedInUserService.getLoggedInStaff());
    } catch (UserIsNotStaffException e) {
      return Optional.empty();
    }
  }

  private void requireGranted(boolean granted) {
    if (!granted) {
      throw new UserNotAuthorizedException();
    }
  }

  private void requireActivity(UUID activityId) {
    if (!activityRepository.existsById(activityId)) {
      throw new ActivityNotFoundException();
    }
  }

  private void requireDraft(UUID activityDraftId) {
    if (!activityDraftRepository.existsById(activityDraftId)) {
      throw new ActivityDraftNotFoundException();
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
