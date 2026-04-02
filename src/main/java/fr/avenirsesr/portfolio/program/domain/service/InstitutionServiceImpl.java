package fr.avenirsesr.portfolio.program.domain.service;

import fr.avenirsesr.portfolio.common.configuration.domain.model.InstitutionConfigurationElements;
import fr.avenirsesr.portfolio.program.domain.model.Institution;
import fr.avenirsesr.portfolio.program.domain.port.input.InstitutionService;
import fr.avenirsesr.portfolio.program.domain.port.output.client.InstitutionConfigClient;
import fr.avenirsesr.portfolio.program.domain.port.output.repository.InstitutionRepository;
import fr.avenirsesr.portfolio.shared.domain.port.input.LoggedInUserService;
import fr.avenirsesr.portfolio.student.progress.imported.domain.model.StudentProgress;
import fr.avenirsesr.portfolio.student.progress.imported.domain.port.input.StudentProgressService;
import fr.avenirsesr.portfolio.user.domain.model.Student;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
public class InstitutionServiceImpl implements InstitutionService {
  private final StudentProgressService studentProgressService;
  private final LoggedInUserService loggedInUserService;
  private final InstitutionConfigClient institutionConfigClient;
  private final InstitutionRepository institutionRepository;

  @Override
  public InstitutionConfigurationElements getInstitutionConfiguration() {
    Student student = loggedInUserService.getLoggedInStudent();

    List<StudentProgress> studentProgresses =
        studentProgressService.findAllStudentProgressesByStudent(student);

    List<InstitutionConfigurationElements> configs =
        studentProgresses.stream()
            .map(sp -> sp.getTrainingPath().getProgram().getInstitution().getId())
            .distinct()
            .map(institutionConfigClient::getInstitutionConfigElementsById)
            .toList();

    boolean apcEnabled = configs.stream().anyMatch(InstitutionConfigurationElements::apcEnabled);
    boolean lifeProjectEnabled =
        configs.stream().anyMatch(InstitutionConfigurationElements::lifeProjectEnabled);

    return new InstitutionConfigurationElements(apcEnabled, lifeProjectEnabled);
  }

  @Override
  public Institution createInstitution(UUID institutionId, String name) {
    return institutionRepository.save(Institution.create(institutionId, name));
  }

  @Override
  public Institution updateInstitution(UUID institutionId, String name) {
    var institution = institutionRepository.findById(institutionId).orElseThrow();
    institution.setName(name);
    return institutionRepository.save(institution);
  }
}
