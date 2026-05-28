package fr.avenirsesr.portfolio.user.infrastructure.adapter.seeder;

import com.fasterxml.jackson.core.type.TypeReference;
import fr.avenirsesr.portfolio.common.seeder.domain.model.enums.ESeedMode;
import fr.avenirsesr.portfolio.common.utils.FileReader;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.model.UserEntity;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.model.UserPrincipalEntity;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.repository.UserJpaRepository;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.repository.UserPrincipalJpaRepository;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.seeder.data.UserPrincipalCreationData;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserPrincipalSeeder {

  private static final String TABLE_NAME = "user-principal";
  private static final String PATH_FILE = "seeder/user-principal.json";

  private final FileReader fileReader;
  private final UserJpaRepository userJpaRepository;
  private final UserPrincipalJpaRepository userPrincipalJpaRepository;

  public String tableName() {
    return TABLE_NAME;
  }

  @Transactional
  public int seedAlone(ESeedMode mode) {
    log.info("Seeding {} with mode {}...", TABLE_NAME, mode);

    Map<UUID, String> eppnByUserId = readEppnByUserId();

    int processed =
        userJpaRepository.findAll().stream()
            .mapToInt(user -> seedForUser(user, eppnByUserId, mode))
            .sum();

    log.info("✔ {} {} rows processed", processed, TABLE_NAME);
    return processed;
  }

  private Map<UUID, String> readEppnByUserId() {
    return fileReader
        .readJSON(PATH_FILE, new TypeReference<List<UserPrincipalCreationData>>() {})
        .stream()
        .collect(
            Collectors.toMap(UserPrincipalCreationData::userId, UserPrincipalCreationData::eppn));
  }

  private int seedForUser(UserEntity user, Map<UUID, String> eppnByUserId, ESeedMode mode) {
    String eppn = eppnByUserId.get(user.getId());

    if (eppn == null || eppn.isBlank()) {
      log.warn("Skipping user_principal seed: no eppn found for user {}", user.getId());
      return 0;
    }

    var existing = userPrincipalJpaRepository.findByUserId(user.getId());

    if (existing.isPresent() && mode == ESeedMode.INSERT_ONLY) {
      return 0;
    }

    var now = Instant.now();

    existing.ifPresentOrElse(entity -> overwrite(entity, eppn, now), () -> create(user, eppn, now));

    return 1;
  }

  private void overwrite(UserPrincipalEntity entity, String eppn, Instant now) {
    entity.setEppn(eppn);
    entity.setUpdatedAt(now);
    userPrincipalJpaRepository.save(entity);
  }

  private void create(UserEntity user, String eppn, Instant now) {
    userPrincipalJpaRepository.save(
        UserPrincipalEntity.of(UUID.randomUUID(), user, eppn, now, now));
  }
}
