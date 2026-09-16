package fr.avenirsesr.portfolio.student.association.domain.service;

import fr.avenirsesr.portfolio.common.data.domain.model.AvenirsBaseModel;
import fr.avenirsesr.portfolio.student.activity.domain.data.DeclaredActivityAssociationData;
import fr.avenirsesr.portfolio.student.activity.domain.exception.DeclaredActivityNotFoundException;
import fr.avenirsesr.portfolio.student.activity.domain.model.DeclaredActivity;
import fr.avenirsesr.portfolio.student.activity.domain.port.input.DeclaredActivityService;
import fr.avenirsesr.portfolio.student.association.domain.data.AssociatedElementsData;
import fr.avenirsesr.portfolio.student.association.domain.model.Association;
import fr.avenirsesr.portfolio.student.association.domain.model.EAssociationType;
import fr.avenirsesr.portfolio.student.association.domain.port.input.AssociatedElementsService;
import fr.avenirsesr.portfolio.student.association.domain.port.input.AssociationService;
import fr.avenirsesr.portfolio.student.experience.domain.data.DeclaredExperienceAssociationData;
import fr.avenirsesr.portfolio.student.experience.domain.exception.DeclaredExperienceNotFoundException;
import fr.avenirsesr.portfolio.student.experience.domain.model.DeclaredExperience;
import fr.avenirsesr.portfolio.student.experience.domain.port.input.DeclaredExperienceService;
import fr.avenirsesr.portfolio.student.skill.domain.data.DeclaredSkillAssociationData;
import fr.avenirsesr.portfolio.student.skill.domain.exception.DeclaredSkillProgressNotFoundException;
import fr.avenirsesr.portfolio.student.skill.domain.model.DeclaredSkillProgress;
import fr.avenirsesr.portfolio.student.skill.domain.port.input.DeclaredSkillProgressService;
import fr.avenirsesr.portfolio.student.trace.domain.data.TraceAssociationData;
import fr.avenirsesr.portfolio.student.trace.domain.exception.TraceNotFoundException;
import fr.avenirsesr.portfolio.student.trace.domain.model.Trace;
import fr.avenirsesr.portfolio.student.trace.domain.port.input.TraceService;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AssociatedElementsServiceImpl implements AssociatedElementsService {
  private final AssociationService associationService;
  private final TraceService traceService;
  private final DeclaredActivityService declaredActivityService;
  private final DeclaredSkillProgressService declaredSkillProgressService;
  private final DeclaredExperienceService declaredExperienceService;

  @Override
  public AssociatedElementsData getAllAssociatedElementsOf(UUID id, Class<?> clazz) {
    return getAllAssociatedElementsOf(id, clazz, false);
  }

  @Override
  public AssociatedElementsData getAllAssociatedElementsOf(
      UUID id, Class<?> clazz, boolean onlyNotCompletedActivities) {
    Map<Class<?>, List<Association>> associationsByAssociatedClass =
        associationService.getAllOf(id, clazz, EAssociationType.getAllBy(clazz)).stream()
            .collect(
                Collectors.groupingBy(
                    association -> association.getAssociationType().associatedKeyOf(clazz)));

    return new AssociatedElementsData(
        toTraceAssociations(associationsOf(associationsByAssociatedClass, Trace.class), clazz),
        toDeclaredActivityAssociations(
            associationsOf(associationsByAssociatedClass, DeclaredActivity.class),
            clazz,
            onlyNotCompletedActivities),
        toDeclaredSkillAssociations(
            associationsOf(associationsByAssociatedClass, DeclaredSkillProgress.class), clazz),
        toDeclaredExperienceAssociations(
            associationsOf(associationsByAssociatedClass, DeclaredExperience.class), clazz));
  }

  private List<Association> associationsOf(
      Map<Class<?>, List<Association>> associationsByAssociatedClass, Class<?> associatedClass) {
    return associationsByAssociatedClass.getOrDefault(associatedClass, List.of());
  }

  private List<TraceAssociationData> toTraceAssociations(
      List<Association> associations, Class<?> clazz) {
    if (associations.isEmpty()) {
      return List.of();
    }

    var traces = traceService.findAllTracesById(associatedIdsOf(associations, clazz));

    return associations.stream()
        .map(
            association ->
                new TraceAssociationData(
                    association.getId(),
                    associatedElementOf(traces, association, clazz, TraceNotFoundException::new)))
        .toList();
  }

  private List<DeclaredActivityAssociationData> toDeclaredActivityAssociations(
      List<Association> associations, Class<?> clazz, boolean onlyNotCompletedActivities) {
    if (associations.isEmpty()) {
      return List.of();
    }

    var ids = associatedIdsOf(associations, clazz);
    var declaredActivities =
        onlyNotCompletedActivities
            ? declaredActivityService.findAllNotCompletedActivitiesByIds(ids)
            : declaredActivityService.findAllDeclaredActivitiesByIds(ids);
    var statuses = declaredActivityService.getDeclaredActivityStatus(declaredActivities);

    return associations.stream()
        .map(
            association -> {
              var declaredActivity =
                  associatedElementOf(
                      declaredActivities,
                      association,
                      clazz,
                      DeclaredActivityNotFoundException::new);

              return new DeclaredActivityAssociationData(
                  association.getId(), declaredActivity, statuses.get(declaredActivity));
            })
        .toList();
  }

  private List<DeclaredSkillAssociationData> toDeclaredSkillAssociations(
      List<Association> associations, Class<?> clazz) {
    if (associations.isEmpty()) {
      return List.of();
    }

    var declaredSkillProgresses =
        declaredSkillProgressService.findAllDeclaredSkillProgressesByIds(
            associatedIdsOf(associations, clazz));

    return associations.stream()
        .map(
            association ->
                new DeclaredSkillAssociationData(
                    association.getId(),
                    associatedElementOf(
                        declaredSkillProgresses,
                        association,
                        clazz,
                        DeclaredSkillProgressNotFoundException::new)))
        .toList();
  }

  private List<DeclaredExperienceAssociationData> toDeclaredExperienceAssociations(
      List<Association> associations, Class<?> clazz) {
    if (associations.isEmpty()) {
      return List.of();
    }

    var declaredExperiences =
        declaredExperienceService.findAllByIds(associatedIdsOf(associations, clazz));

    return associations.stream()
        .map(
            association ->
                new DeclaredExperienceAssociationData(
                    association.getId(),
                    associatedElementOf(
                        declaredExperiences,
                        association,
                        clazz,
                        DeclaredExperienceNotFoundException::new)))
        .toList();
  }

  private List<UUID> associatedIdsOf(List<Association> associations, Class<?> clazz) {
    return associations.stream().map(association -> associatedIdOf(association, clazz)).toList();
  }

  private UUID associatedIdOf(Association association, Class<?> clazz) {
    return association.getAssociationType().associatedIdExtractorFor(clazz).apply(association);
  }

  private <T extends AvenirsBaseModel> T associatedElementOf(
      List<T> elements,
      Association association,
      Class<?> clazz,
      Supplier<RuntimeException> notFoundException) {
    var associatedId = associatedIdOf(association, clazz);

    return elements.stream()
        .filter(element -> element.getId().equals(associatedId))
        .findAny()
        .orElseThrow(notFoundException);
  }
}
