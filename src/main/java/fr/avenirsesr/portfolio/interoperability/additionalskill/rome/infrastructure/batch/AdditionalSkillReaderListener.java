package fr.avenirsesr.portfolio.interoperability.additionalskill.rome.infrastructure.batch;

import fr.avenirsesr.portfolio.additionalskill.domain.model.AdditionalSkill;
import fr.avenirsesr.portfolio.additionalskill.domain.model.AdditionalSkillCategory;
import fr.avenirsesr.portfolio.additionalskill.domain.model.enums.EAdditionalSkillType;
import fr.avenirsesr.portfolio.additionalskill.domain.port.output.repository.AdditionalSkillCategoryRepository;
import java.sql.ResultSet;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("!test")
public class AdditionalSkillReaderListener implements StepExecutionListener {

  private final AdditionalSkillCategoryRepository additionalSkillCategoryRepository;
  private final JdbcPagingItemReader<AdditionalSkill> reader;
  private Map<UUID, AdditionalSkillCategory> additionalSkillCategoryCache;

  public AdditionalSkillReaderListener(
      AdditionalSkillCategoryRepository additionalSkillCategoryRepository,
      JdbcPagingItemReader<AdditionalSkill> reader) {
    this.additionalSkillCategoryRepository = additionalSkillCategoryRepository;
    this.reader = reader;
  }

  @Override
  public void beforeStep(StepExecution stepExecution) {
    additionalSkillCategoryCache =
        additionalSkillCategoryRepository.findAll().stream()
            .collect(Collectors.toMap(AdditionalSkillCategory::getId, Function.identity()));

    reader.setRowMapper(
        (ResultSet rs, int rowNum) -> {
          UUID categoryId = UUID.fromString(rs.getString("additional_skill_category_id"));
          AdditionalSkillCategory category = additionalSkillCategoryCache.get(categoryId);

          return AdditionalSkill.toDomain(
              UUID.fromString(rs.getString("id")),
              rs.getString("libelle"),
              rs.getString("external_id"),
              category,
              EAdditionalSkillType.valueOf(rs.getString("type")),
              rs.getTimestamp("created_at").toInstant(),
              rs.getTimestamp("updated_at").toInstant());
        });
  }

  @Override
  public ExitStatus afterStep(StepExecution stepExecution) {
    additionalSkillCategoryCache = null;
    return ExitStatus.COMPLETED;
  }
}
