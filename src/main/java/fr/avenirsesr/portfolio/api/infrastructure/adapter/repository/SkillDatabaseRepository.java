package fr.avenirsesr.portfolio.api.infrastructure.adapter.repository;

import fr.avenirsesr.portfolio.api.domain.model.Skill;
import fr.avenirsesr.portfolio.api.domain.port.output.repository.SkillRepository;
import fr.avenirsesr.portfolio.api.infrastructure.adapter.model.SkillEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class SkillDatabaseRepository implements SkillRepository {
    private final SkillJpaRepository jpaRepository;

    @Override
    public void save(Skill skill) {
        var entity = toEntity(skill);
        jpaRepository.save(entity);
    }

    @Override
    public void saveAll(List<Skill> skills) {
        var entities = skills.stream().map(SkillDatabaseRepository::toEntity).toList();
        jpaRepository.saveAll(entities);
    }

    public static SkillEntity toEntity(Skill skill) {
        return new SkillEntity(
                skill.getId(),
                skill.getName(),
                skill.getSkillLevels().stream().map(SkillLevelDatabaseRepository::toEntity).collect(Collectors.toSet())
        );
    }
}
