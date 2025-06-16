package fr.avenirsesr.portfolio.api.domain.service;

import fr.avenirsesr.portfolio.api.domain.model.ProgramProgress;
import fr.avenirsesr.portfolio.api.domain.model.Skill;
import fr.avenirsesr.portfolio.api.domain.model.SkillLevel;
import fr.avenirsesr.portfolio.api.domain.model.Student;
import fr.avenirsesr.portfolio.api.domain.model.enums.ELanguage;
import fr.avenirsesr.portfolio.api.domain.model.enums.ESkillLevelStatus;
import fr.avenirsesr.portfolio.api.domain.port.input.ProgramProgressService;
import fr.avenirsesr.portfolio.api.domain.port.output.repository.ProgramProgressRepository;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
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

  private static SkillLevel findCurrentSkillLevel(Set<SkillLevel> skillLevels) {
    if (skillLevels == null || skillLevels.isEmpty()) {
      return null;
    }
    return skillLevels.stream()
        .filter(sl -> sl.getStatus() == ESkillLevelStatus.UNDER_REVIEW)
        .findFirst()
        .orElseGet(
            () ->
                skillLevels.stream()
                    .filter(sl -> sl.getStatus() == ESkillLevelStatus.TO_BE_EVALUATED)
                    .findFirst()
                    .orElseGet(
                        () ->
                            skillLevels.stream()
                                .filter(sl -> sl.getStatus() == ESkillLevelStatus.NOT_STARTED)
                                .findFirst()
                                .orElse(null)));
  }

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
                        .map(
                            skill -> {
                              SkillLevel selectedSkillLevel =
                                  findCurrentSkillLevel(skill.getSkillLevels());
                              Set<SkillLevel> selectedSkillLevelSet =
                                  selectedSkillLevel != null
                                      ? Set.of(selectedSkillLevel)
                                      : Set.of();
                              return Skill.toDomain(
                                  skill.getId(),
                                  skill.getName(),
                                  selectedSkillLevelSet,
                                  skill.getLanguage());
                            })
                        .collect(Collectors.toCollection(LinkedHashSet::new)),
                (existing, replacement) -> existing,
                LinkedHashMap::new));
  }

  @Override
  public Map<ProgramProgress, Set<Skill>> getSkillsOverview(Student student, ELanguage language) {
    return cleanProgramProgressList(programProgressRepository.findAllByStudent(student, language));
  }

  @Override
  public boolean isStudentFollowingAPCProgram(Student student) {
    var programProgress = programProgressRepository.findAllAPCByStudent(student);
    return !programProgress.isEmpty();
  }

  @Override
  public List<ProgramProgress> getAllProgramProgress(Student student, ELanguage language) {
    return programProgressRepository.findAllWithoutSkillsByStudent(student, language).stream()
        .sorted(Comparator.comparing(p -> p.getProgram().getName()))
        .collect(Collectors.toList());
  }
}
