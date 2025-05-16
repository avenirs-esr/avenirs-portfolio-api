package fr.avenirsesr.portfolio.api.infrastructure.adapter.repository;

import fr.avenirsesr.portfolio.api.domain.model.ExternalUser;
import fr.avenirsesr.portfolio.api.domain.port.output.repository.ExternalUserRepository;
import fr.avenirsesr.portfolio.api.infrastructure.adapter.model.ExternalUserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ExternalUserDatabaseRepository implements ExternalUserRepository {
    private final ExternalUserJpaRepository jpaRepository;

    @Override
    public void save(ExternalUser externalUser) {
        var entity = toEntity(externalUser);
        jpaRepository.save(entity);
    }

    @Override
    public void saveAll(List<ExternalUser> externalUsers) {
        var entities = externalUsers.stream().map(ExternalUserDatabaseRepository::toEntity).toList();
        jpaRepository.saveAll(entities);
    }

    public static ExternalUserEntity toEntity(ExternalUser externalUser) {
        return new ExternalUserEntity(
                externalUser.getExternalId(),
                externalUser.getSource(),
                UserDatabaseRepository.toEntity(externalUser.getUser()),
                externalUser.getCategory(),
                externalUser.getEmail(),
                externalUser.getFirstName(),
                externalUser.getLastName()
        );
    }
}
