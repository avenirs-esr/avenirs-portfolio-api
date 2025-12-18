package fr.avenirsesr.portfolio.program.domain.service;

import fr.avenirsesr.portfolio.common.configuration.domain.model.InstitutionConfigurationElements;
import fr.avenirsesr.portfolio.program.domain.port.input.InstitutionService;
import fr.avenirsesr.portfolio.program.domain.port.output.client.InstitutionConfigClient;
import fr.avenirsesr.portfolio.shared.domain.port.input.LoggedInUserService;
import fr.avenirsesr.portfolio.student.progress.imported.domain.model.StudentProgress;
import fr.avenirsesr.portfolio.student.progress.imported.domain.port.output.repository.StudentProgressRepository;
import fr.avenirsesr.portfolio.user.domain.model.Student;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
public class InstitutionServiceImpl implements InstitutionService {
  private final StudentProgressRepository studentProgressRepository;
  private final LoggedInUserService loggedInUserService;
  private final InstitutionConfigClient institutionConfigClient;

  @Override
  public InstitutionConfigurationElements getInstitutionConfiguration() {
    Student student = loggedInUserService.getLoggedInStudent();

    List<StudentProgress> studentProgresses = studentProgressRepository.findAllByStudent(student);

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
}
