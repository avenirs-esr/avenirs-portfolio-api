package fr.avenirsesr.portfolio.api.infrastructure.adapter.repository;

import fr.avenirsesr.portfolio.api.domain.model.AMS;
import fr.avenirsesr.portfolio.api.domain.port.output.repository.AMSRepository;
import fr.avenirsesr.portfolio.api.infrastructure.adapter.model.AMSEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class AMSDatabaseRepository implements AMSRepository {

    private final AMSJpaRepository jpaRepository;

    @Override
    public void save(AMS ams) {
        var entity = toEntity(ams);
        jpaRepository.save(entity);
    }

    @Override
    public void saveAll(List<AMS> amses) {
        var entities = amses.stream().map(AMSDatabaseRepository::toEntity).toList();
        jpaRepository.saveAll(entities);
    }

    public static AMSEntity toEntity(AMS ams) {
        return new AMSEntity(
                ams.getId(),
                UserDatabaseRepository.toEntity(ams.getUser()),
                ams.getSkillLevels().stream().map(SkillLevelDatabaseRepository::toEntity).toList()
        );
    }
}
