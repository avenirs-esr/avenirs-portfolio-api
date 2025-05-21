package fr.avenirsesr.portfolio.api.domain.service;

import fr.avenirsesr.portfolio.api.domain.model.ProgramProgress;
import fr.avenirsesr.portfolio.api.domain.model.Skill;
import fr.avenirsesr.portfolio.api.domain.port.input.ProgramProgressService;
import fr.avenirsesr.portfolio.api.domain.port.output.repository.ProgramProgressRepository;

import java.util.*;
import java.util.stream.Collectors;

public class ProgramProgressServiceImpl implements ProgramProgressService {
  private final ProgramProgressRepository programProgressRepository;

  public ProgramProgressServiceImpl(ProgramProgressRepository programProgressRepository) {
    this.programProgressRepository = programProgressRepository;
  }

  private static List<ProgramProgress> cleanProgrammProgressList(
      List<ProgramProgress> programProgressList) {
    List<ProgramProgress> cleanedProgrammProgressList =
        programProgressList.stream()
            .sorted(Comparator.comparing(programProgress -> programProgress.getProgram().getName()))
            .limit(2)
            .toList();
    int skillsSizeLimit = getMaxSkillsPerProgram(cleanedProgrammProgressList);
    cleanedProgrammProgressList.forEach(
        programProgress -> {
          LinkedHashSet<Skill> linkedHashSet =
              programProgress.getSkills().stream()
                  .sorted(Comparator.comparing(Skill::getName))
                  .limit(skillsSizeLimit)
                  .collect(Collectors.toCollection(LinkedHashSet::new));
          programProgress.setSkills(linkedHashSet);
        });
    return cleanedProgrammProgressList;
  }

  private static int getMaxSkillsPerProgram(List<ProgramProgress> programProgressList) {
    if (programProgressList.size() == 2) {
      int skillsSizeFirstProgram = programProgressList.get(0).getSkills().size();
      int skillsSizeSecondProgram = programProgressList.get(1).getSkills().size();
      if (skillsSizeFirstProgram >= 3 && skillsSizeSecondProgram >= 3) {
        return 3;
      } else if (skillsSizeFirstProgram == 2 || skillsSizeSecondProgram == 2) {
        return 4;
      } else if (skillsSizeFirstProgram == 1 || skillsSizeSecondProgram == 1) {
        return 5;
      }
    }
    return 6;
  }

  @Override
  public List<ProgramProgress> getSkillsOverview(UUID userId) {
    return cleanProgrammProgressList(programProgressRepository.getSkillsOverview(userId));
  }
}
