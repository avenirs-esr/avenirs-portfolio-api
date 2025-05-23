package fr.avenirsesr.portfolio.api.domain.service;

import fr.avenirsesr.portfolio.api.domain.model.ProgramProgress;
import fr.avenirsesr.portfolio.api.domain.model.Skill;
import fr.avenirsesr.portfolio.api.domain.model.Student;
import fr.avenirsesr.portfolio.api.domain.port.input.ProgramProgressService;
import fr.avenirsesr.portfolio.api.domain.port.output.repository.ProgramProgressRepository;
import java.util.*;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@AllArgsConstructor
@Service
public class ProgramProgressServiceImpl implements ProgramProgressService {
  private static final int MAX_SKILLS = 6;
  private final ProgramProgressRepository programProgressRepository;

  private static Map<ProgramProgress, Set<Skill>> cleanProgramProgressList(
      List<ProgramProgress> programProgressList) {
    int skillLimit = !programProgressList.isEmpty() ? MAX_SKILLS / programProgressList.size() : 0;

    return programProgressList.stream()
        .sorted(Comparator.comparing(p -> p.getProgram().getName()))
        .collect(
            Collectors.toMap(
                programProgress -> programProgress,
                programProgress ->
                    programProgress.getSkills().stream()
                        .sorted(Comparator.comparing(Skill::getName))
                        .limit(skillLimit)
                        .collect(Collectors.toCollection(LinkedHashSet::new)),
                (existing, replacement) -> existing,
                LinkedHashMap::new));
  }

  @Override
  public Map<ProgramProgress, Set<Skill>> getSkillsOverview(Student student) {
    return cleanProgramProgressList(programProgressRepository.findAllByStudent(student));
  }

  @Override
  public boolean isStudentFollowingAPCProgram(Student student) {
    var programProgress = programProgressRepository.findAllAPCByStudent(student);
    return !programProgress.isEmpty();
  }
}
