package fr.avenirsesr.portfolio.student.progress.declared.experience.domain.service;

import static fr.avenirsesr.portfolio.common.validation.domain.constraints.FieldMaxLengths.*;
import static fr.avenirsesr.portfolio.common.validation.domain.utils.FieldValidationUtils.*;

import fr.avenirsesr.portfolio.association.domain.data.AssociationSearchResultData;
import fr.avenirsesr.portfolio.association.domain.model.Association;
import fr.avenirsesr.portfolio.association.domain.model.EAssociationContextType;
import fr.avenirsesr.portfolio.association.domain.model.EAssociationType;
import fr.avenirsesr.portfolio.association.domain.port.input.AssociationService;
import fr.avenirsesr.portfolio.association.domain.service.AssociationSearchHelper;
import fr.avenirsesr.portfolio.common.data.domain.model.AvenirsBaseModel;
import fr.avenirsesr.portfolio.common.data.domain.model.PageCriteria;
import fr.avenirsesr.portfolio.common.data.domain.model.PagedResult;
import fr.avenirsesr.portfolio.common.security.domain.exception.UserNotAuthorizedException;
import fr.avenirsesr.portfolio.shared.domain.port.input.LoggedInUserService;
import fr.avenirsesr.portfolio.student.progress.declared.experience.domain.data.DeclaredExperienceAssociationsData;
import fr.avenirsesr.portfolio.student.progress.declared.experience.domain.exception.DeclaredExperienceNotFoundException;
import fr.avenirsesr.portfolio.student.progress.declared.experience.domain.model.DeclaredExperience;
import fr.avenirsesr.portfolio.student.progress.declared.experience.domain.model.enums.EExperienceType;
import fr.avenirsesr.portfolio.student.progress.declared.experience.domain.port.input.DeclaredExperienceService;
import fr.avenirsesr.portfolio.student.progress.declared.experience.domain.port.output.repository.DeclaredExperienceRepository;
import fr.avenirsesr.portfolio.trace.domain.data.TraceAssociationData;
import fr.avenirsesr.portfolio.trace.domain.exception.TraceNotFoundException;
import fr.avenirsesr.portfolio.trace.domain.model.Trace;
import fr.avenirsesr.portfolio.trace.domain.port.input.TraceService;
import fr.avenirsesr.portfolio.user.domain.model.Student;
import fr.avenirsesr.portfolio.user.domain.port.input.StudentService;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class DeclaredExperienceServiceImpl implements DeclaredExperienceService {

  private final LoggedInUserService loggedInUserService;
  private final AssociationService associationService;
  private final AssociationSearchHelper associationSearchHelper;
  private final TraceService traceService;
  private final DeclaredExperienceRepository experienceRepository;
  private final StudentService studentService;

  @Override
  public DeclaredExperience create(
      UUID studentId,
      String title,
      EExperienceType experienceType,
      String organization,
      String activitySector,
      String location,
      String description,
      String sourceOfInformation,
      String summary,
      String externalLink,
      LocalDate startDate,
      LocalDate endDate) {
    Student student = studentService.getStudentById(studentId);
    return create(
        student,
        title,
        experienceType,
        organization,
        activitySector,
        location,
        description,
        sourceOfInformation,
        summary,
        externalLink,
        startDate,
        endDate);
  }

  @Override
  public DeclaredExperience create(
      String title,
      EExperienceType experienceType,
      String organization,
      String activitySector,
      String location,
      String description,
      String sourceOfInformation,
      String summary,
      String externalLink,
      LocalDate startDate,
      LocalDate endDate) {
    return create(
        loggedInUserService.getLoggedInStudent(),
        title,
        experienceType,
        organization,
        activitySector,
        location,
        description,
        sourceOfInformation,
        summary,
        externalLink,
        startDate,
        endDate);
  }

  private DeclaredExperience create(
      Student student,
      String title,
      EExperienceType experienceType,
      String organization,
      String activitySector,
      String location,
      String description,
      String sourceOfInformation,
      String summary,
      String externalLink,
      LocalDate startDate,
      LocalDate endDate) {
    log.info("DeclaredExperience creation for {}", student);

    checkDeclaredExperienceDataValidity(
        title,
        organization,
        activitySector,
        location,
        description,
        sourceOfInformation,
        summary,
        externalLink,
        startDate,
        endDate);

    var experience =
        DeclaredExperience.create(
            student,
            title,
            experienceType,
            organization,
            activitySector,
            location,
            description,
            sourceOfInformation,
            summary,
            externalLink,
            startDate,
            endDate);

    experience = experienceRepository.save(experience);
    log.info("{} has been created", experience);
    return experience;
  }

  @Override
  public DeclaredExperience update(
      UUID experienceId,
      String title,
      EExperienceType experienceType,
      String organization,
      String activitySector,
      String location,
      String description,
      String sourceOfInformation,
      String summary,
      String externalLink,
      LocalDate startDate,
      LocalDate endDate) {
    Student student = loggedInUserService.getLoggedInStudent();
    log.info("Update experienceId {} by {}", experienceId, student);

    var experience =
        experienceRepository
            .findById(experienceId)
            .orElseThrow(DeclaredExperienceNotFoundException::new);
    if (!experience.getStudent().equals(student)) {
      throw new UserNotAuthorizedException();
    }
    checkDeclaredExperienceDataValidity(
        title,
        organization,
        activitySector,
        location,
        description,
        sourceOfInformation,
        summary,
        externalLink,
        startDate,
        endDate);

    experience.setTitle(title);
    experience.setExperienceType(experienceType);
    experience.setOrganization(organization);
    experience.setActivitySector(activitySector);
    experience.setLocation(location);
    experience.setDescription(description);
    experience.setSourceOfInformation(sourceOfInformation);
    experience.setSummary(summary);
    experience.setExternalLink(externalLink);
    experience.setStartDate(startDate);
    experience.setEndDate(endDate);
    experience = experienceRepository.save(experience);

    log.info("{} updated successfully", experience);
    return experience;
  }

  private void checkDeclaredExperienceDataValidity(
      String title,
      String organization,
      String activitySector,
      String location,
      String description,
      String sourceOfInformation,
      String summary,
      String externalLink,
      LocalDate startDate,
      LocalDate endDate) {
    requireNotBlankAndMaxLength("title", title, TITLE_LENGTH);
    requireNotBlankAndMaxLength("organization", organization, ORGANIZATION_LENGTH);
    validateOptionalTextMaxLength("activitySector", activitySector, ACTIVITY_SECTOR_LENGTH);
    validateOptionalTextMaxLength("location", location, LOCATION_LENGTH);
    validateOptionalTextMaxLength(
        "sourceOfInformation", sourceOfInformation, SOURCE_OF_INFORMATION_LENGTH);
    validateOptionalTextMaxLength("description", description, DESCRIPTION_LENGTH);
    validateOptionalTextMaxLength("summary", summary, SUMMARY_LENGTH);
    requireNotNull("startDate", startDate);
    validateDateOrder(startDate, endDate);
    validateUrl(externalLink);
  }

  @Override
  public void delete(List<UUID> experienceIds) {
    Student student = loggedInUserService.getLoggedInStudent();
    log.info("Deleting experienceIds {} by {}", experienceIds, student);

    var experiences = experienceRepository.findAllById(experienceIds);

    if (experiences.stream().anyMatch(experience -> !experience.getStudent().equals(student))) {
      throw new UserNotAuthorizedException();
    }

    if (!new HashSet<>(experiences.stream().map(AvenirsBaseModel::getId).toList())
        .containsAll(experienceIds)) {
      throw new DeclaredExperienceNotFoundException();
    }

    associationService.deleteAllOf(experienceIds, DeclaredExperience.class);

    experienceRepository.removeAllFromDatabase(experiences);
    log.info("ExperienceIds {} successfully deleted", experienceIds);
  }

  @Override
  public DeclaredExperience get(UUID experienceId) {
    Student student = loggedInUserService.getLoggedInStudent();
    log.info("Get experienceId {} by {}", experienceId, student);

    var experience =
        experienceRepository
            .findById(experienceId)
            .orElseThrow(DeclaredExperienceNotFoundException::new);

    if (!experience.getStudent().equals(student)) {
      throw new UserNotAuthorizedException();
    }
    return experience;
  }

  @Override
  public PagedResult<DeclaredExperience> getView(PageCriteria pageCriteria) {
    Student student = loggedInUserService.getLoggedInStudent();
    log.info("Get experience view by {}", student);

    return experienceRepository.findAllByStudent(student, pageCriteria);
  }

  @Override
  public List<DeclaredExperience> findAllByIds(List<UUID> experienceIds) {
    return experienceRepository.findAllById(experienceIds);
  }

  @Override
  public PagedResult<DeclaredExperience> search(String keyword, PageCriteria pageCriteria) {
    Student student = loggedInUserService.getLoggedInStudent();
    return experienceRepository.findAllByStudent(student, pageCriteria, keyword);
  }

  private TraceAssociationData traceAssociationMapper(Association association, List<Trace> traces) {
    return new TraceAssociationData(
        association.getId(),
        traces.stream()
            .filter(t -> t.getId().equals(association.getId1()))
            .findAny()
            .orElseThrow(TraceNotFoundException::new));
  }

  @Override
  public PagedResult<AssociationSearchResultData> searchDeclaredExperiencesForAssociation(
      UUID excludeAssociatedWithElementId,
      EAssociationContextType contextType,
      String keyword,
      PageCriteria pageCriteria) {
    var experiences = search(keyword, pageCriteria);

    if (contextType == null) {
      return associationSearchHelper.searchForAssociation(
          null,
          null,
          null,
          null,
          experiences,
          AvenirsBaseModel::getId,
          DeclaredExperience::getTitle,
          de -> de.getExperienceType().name(),
          de -> false);
    }

    EAssociationType associationType = getAssociationType(contextType);

    return associationSearchHelper.searchForAssociation(
        excludeAssociatedWithElementId,
        contextType.toClass(),
        associationType,
        associationType.idExtractorFor(DeclaredExperience.class),
        experiences,
        AvenirsBaseModel::getId,
        DeclaredExperience::getTitle,
        de -> de.getExperienceType().name(),
        de -> false);
  }

  @Override
  public DeclaredExperienceAssociationsData getAssociations(UUID experienceId) {
    var student = loggedInUserService.getLoggedInStudent();
    var experience =
        experienceRepository
            .findById(experienceId)
            .orElseThrow(DeclaredExperienceNotFoundException::new);
    if (!experience.getStudent().equals(student)) {
      throw new UserNotAuthorizedException();
    }

    var associations =
        associationService.getAllOf(
            experience.getId(),
            DeclaredExperience.class,
            EAssociationType.getAllBy(DeclaredExperience.class));
    var traceAssociations =
        associations.stream()
            .filter(a -> a.getAssociationType() == EAssociationType.TRACE_DECLARED_EXPERIENCE)
            .toList();
    var traces =
        traceService.findAllTracesById(associations.stream().map(Association::getId1).toList());

    return new DeclaredExperienceAssociationsData(
        traceAssociations.stream().map(a -> traceAssociationMapper(a, traces)).toList());
  }

  private EAssociationType getAssociationType(EAssociationContextType contextType) {
    return switch (contextType) {
      case TRACE -> EAssociationType.TRACE_DECLARED_EXPERIENCE;
      case DECLARED_ACTIVITY, DECLARED_SKILL, DECLARED_EXPERIENCE ->
          throw new UnsupportedOperationException();
    };
  }
}
