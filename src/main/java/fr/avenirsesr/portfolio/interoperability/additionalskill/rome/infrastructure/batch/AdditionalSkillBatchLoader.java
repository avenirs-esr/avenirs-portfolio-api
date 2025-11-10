package fr.avenirsesr.portfolio.interoperability.additionalskill.rome.infrastructure.batch;

import fr.avenirsesr.portfolio.additionalskill.domain.model.AdditionalSkill;
import fr.avenirsesr.portfolio.additionalskill.domain.model.AdditionalSkillCategory;
import fr.avenirsesr.portfolio.additionalskill.domain.model.enums.EAdditionalSkillType;
import fr.avenirsesr.portfolio.additionalskill.domain.port.output.OpenSearchIndex;
import fr.avenirsesr.portfolio.additionalskill.domain.port.output.repository.AdditionalSkillRepository;
import fr.avenirsesr.portfolio.interoperability.additionalskill.rome.domain.model.Competence;
import fr.avenirsesr.portfolio.interoperability.additionalskill.rome.domain.model.mapper.CompetenceMapper;
import fr.avenirsesr.portfolio.interoperability.additionalskill.rome.domain.port.input.RomeAdditionalSkillService;
import fr.avenirsesr.portfolio.interoperability.additionalskill.rome.domain.port.output.RomeAdditionalSkillApi;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import javax.sql.DataSource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.SkipListener;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.batch.core.job.flow.FlowExecutionStatus;
import org.springframework.batch.core.job.flow.JobExecutionDecider;
import org.springframework.batch.core.job.flow.support.SimpleFlow;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.Order;
import org.springframework.batch.item.database.support.PostgresPagingQueryProvider;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.PlatformTransactionManager;

@Slf4j
@Configuration
@Profile("!test")
@RequiredArgsConstructor
public class AdditionalSkillBatchLoader {
  private final RomeAdditionalSkillApi romeAdditionalSkillApi;
  private final RomeAdditionalSkillService romeAdditionalSkillService;
  private final AdditionalSkillRepository additionalSkillRepository;
  private final OpenSearchIndex openSearchIndex;
  private final JdbcTemplate jdbcTemplate;

  @Bean
  public Job importROME4SkillJob(
      JobRepository jobRepository,
      Flow importROME4SkillFlow,
      JobExecutionListener jobResultListener) {
    return new JobBuilder("importROME4SkillJob", jobRepository)
        .incrementer(new RunIdIncrementer())
        .start(importROME4SkillFlow)
        .end()
        .listener(jobResultListener)
        .build();
  }

  @Bean
  public Flow importROME4SkillFlow(
      JobExecutionDecider rome4FlowDecider, Flow dumpFlow, Flow apiRome4Flow) {
    return new FlowBuilder<SimpleFlow>("importROME4SkillFlow")
        .start(rome4FlowDecider)
        .on("DUMP")
        .to(dumpFlow)
        .from(rome4FlowDecider)
        .on("API_ROME4")
        .to(apiRome4Flow)
        .end();
  }

  @Bean
  public JobExecutionDecider rome4FlowDecider() {
    return (jobExecution, stepExecution) -> {
      int count = additionalSkillRepository.countAll(EAdditionalSkillType.ROME4);

      if (count == 0) {
        log.info(
            "No additional skills found, bootstrapping from SQL dump and skipping the rest"
                + " of the job.");
        return new FlowExecutionStatus("DUMP");
      }
      log.info("{} Additional skills found, continuing with sync job.", count);

      return new FlowExecutionStatus("API_ROME4");
    };
  }

  @Bean
  public Flow dumpFlow(
      Step importAdditionalSkillDumpStep,
      Step cleanAndCreateOpenSearchIndexStep,
      Step indexToOpenSearchStep) {
    return new FlowBuilder<SimpleFlow>("dumpFlow")
        .start(importAdditionalSkillDumpStep)
        .next(cleanAndCreateOpenSearchIndexStep)
        .next(indexToOpenSearchStep)
        .end();
  }

  @Bean
  public Flow apiRome4Flow(
      Step checkROME4VersionUpdateStep,
      Step importROME4SkillStep,
      Step cleanAndCreateOpenSearchIndexStep,
      Step indexToOpenSearchStep) {
    return new FlowBuilder<SimpleFlow>("apiRome4Flow")
        .start(checkROME4VersionUpdateStep)
        .on("NOOP")
        .end()
        .from(checkROME4VersionUpdateStep)
        .on("*")
        .to(importROME4SkillStep)
        .from(importROME4SkillStep)
        .on("*")
        .to(cleanAndCreateOpenSearchIndexStep)
        .from(cleanAndCreateOpenSearchIndexStep)
        .on("*")
        .to(indexToOpenSearchStep)
        .end();
  }

  @Bean
  public Step importAdditionalSkillDumpStep(
      JobRepository jobRepository, PlatformTransactionManager transactionManager) {
    return new StepBuilder("importAdditionalSkillDumpStep", jobRepository)
        .tasklet(
            (contribution, chunkContext) -> {
              bootstrapAdditionalSkillsFromSqlDump();
              return RepeatStatus.FINISHED;
            },
            transactionManager)
        .build();
  }

  @Bean
  public Step cleanAndCreateOpenSearchIndexStep(
      JobRepository jobRepository, PlatformTransactionManager transactionManager) {
    return new StepBuilder("cleanAndCreateOpenSearchIndexStep", jobRepository)
        .tasklet(
            (contribution, chunkContext) -> {
              openSearchIndex.cleanAndCreateAdditionalSkillIndex();
              return RepeatStatus.FINISHED;
            },
            transactionManager)
        .build();
  }

  @Bean
  public Step indexToOpenSearchStep(
      JobRepository jobRepository,
      PlatformTransactionManager transactionManager,
      JdbcPagingItemReader<AdditionalSkill> additionalSkillReader,
      ItemWriter<AdditionalSkill> openSearchWriter,
      AdditionalSkillReaderListener additionalSkillReaderListener) {
    return new StepBuilder("indexToOpenSearchStep", jobRepository)
        .<AdditionalSkill, AdditionalSkill>chunk(500, transactionManager)
        .reader(additionalSkillReader)
        .writer(openSearchWriter)
        .listener(additionalSkillReaderListener)
        .build();
  }

  @Bean
  public JdbcPagingItemReader<AdditionalSkill> additionalSkillReader(DataSource dataSource) {
    JdbcPagingItemReader<AdditionalSkill> reader = new JdbcPagingItemReader<>();

    reader.setDataSource(dataSource);
    reader.setFetchSize(500);

    PostgresPagingQueryProvider queryProvider = new PostgresPagingQueryProvider();
    queryProvider.setSelectClause("*");
    queryProvider.setFromClause("FROM additional_skill");
    queryProvider.setSortKeys(Map.of("id", Order.ASCENDING));

    reader.setQueryProvider(queryProvider);

    return reader;
  }

  @Bean
  public ItemWriter<AdditionalSkill> openSearchWriter() {
    return items -> {
      List<AdditionalSkill> copy = new ArrayList<>(items.getItems());
      openSearchIndex.indexAll(copy);
    };
  }

  @Bean
  public Step checkROME4VersionUpdateStep(
      JobRepository jobRepository, PlatformTransactionManager transactionManager) {
    return new StepBuilder("checkROME4VersionUpdateStep", jobRepository)
        .tasklet(
            (contribution, chunkContext) -> {
              boolean isNewVersion = romeAdditionalSkillService.checkRomeVersionUpdated();

              if (!isNewVersion) {
                log.info(
                    "checkROME4VersionUpdateStep (NOOP) because there are no updates to ROME 4.0");
                contribution.setExitStatus(ExitStatus.NOOP);
              } else {
                log.info(
                    "checkROME4VersionUpdateStep (COMPLETED) because there are updates to ROME"
                        + " 4.0");
              }

              return RepeatStatus.FINISHED;
            },
            transactionManager)
        .build();
  }

  @Bean
  public Step importROME4SkillStep(
      JobRepository jobRepository, PlatformTransactionManager transactionManager) {
    var categories = new ArrayList<AdditionalSkillCategory>();
    return new StepBuilder("importROME4SkillStep", jobRepository)
        .<Competence, AdditionalSkill>chunk(100, transactionManager)
        .reader(itemReader())
        .processor(itemProcessor(categories))
        .writer(itemWriter())
        .faultTolerant()
        .skipPolicy(
            (throwable, skipCount) -> {
              log.error("Error while importing skills", throwable);
              return throwable instanceof RuntimeException;
            })
        .listener(skipListener())
        .build();
  }

  @Bean
  @StepScope
  public ItemReader<Competence> itemReader() {
    return new ItemReader<>() {
      private Iterator<Competence> iterator;

      @Override
      public Competence read() {
        if (iterator == null) {
          try {
            List<Competence> data = romeAdditionalSkillApi.fetchAdditionalSkills();
            iterator = data.iterator();
          } catch (Exception e) {
            log.error("Error ROME4.0 API : {}", e.getMessage());
            iterator = Collections.emptyIterator();
          }
        }
        return iterator.hasNext() ? iterator.next() : null;
      }
    };
  }

  @Bean
  public ItemProcessor<Competence, AdditionalSkill> itemProcessor(
      ArrayList<AdditionalSkillCategory> categories) {
    return (Competence competence) ->
        AdditionalSkill.create(
            competence.getLibelle(),
            competence.getCode(),
            CompetenceMapper.toCategoryDomain(competence, categories),
            EAdditionalSkillType.ROME4);
  }

  @Bean
  public ItemWriter<AdditionalSkill> itemWriter() {
    return additionalSkills -> {
      List<AdditionalSkill> additionalSkillList = new ArrayList<>(additionalSkills.getItems());
      romeAdditionalSkillService.synchronizeAndSaveAdditionalSkills(additionalSkillList);
    };
  }

  @Bean
  public SkipListener<Competence, AdditionalSkill> skipListener() {
    return new SkipListener<>() {
      @Override
      public void onSkipInRead(Throwable t) {
        log.error("Skip in reading (API) : {}", t.getMessage());
      }

      @Override
      public void onSkipInProcess(Competence item, Throwable t) {
        log.error("Skip in processing for {} : {}", item, t.getMessage());
      }

      @Override
      public void onSkipInWrite(AdditionalSkill item, Throwable t) {
        log.error("Skip in writing for {} : {}", item, t.getMessage());
      }
    };
  }

  @Bean
  public JobExecutionListener jobResultListener() {
    return new JobExecutionListener() {
      @Override
      public void afterJob(JobExecution jobExecution) {
        Predicate<StepExecution> condition =
            stepExec ->
                stepExec.getReadCount() == 0
                    && stepExec.getWriteCount() == 0
                    && stepExec.getSkipCount() >= 0;

        List<StepExecution> filtered =
            jobExecution.getStepExecutions().stream()
                .filter(
                    stepExec ->
                        !"checkROME4VersionUpdateStep".equals(stepExec.getStepName())
                            || stepExec.getExitStatus().compareTo(ExitStatus.NOOP) != 0)
                .toList();

        boolean allSkippedOrEmpty = !filtered.isEmpty() && filtered.stream().allMatch(condition);

        if (allSkippedOrEmpty) {
          jobExecution.setStatus(BatchStatus.FAILED);
          jobExecution.setExitStatus(
              new ExitStatus("FAILED", "All steps skipped, no data processed"));
          log.error("Job completed as FAILED because all steps were skipped/empty.");
        }
      }
    };
  }

  public void bootstrapAdditionalSkillsFromSqlDump() {
    Resource additionalSkillsCategoriesResource =
        new ClassPathResource("/additional-skill/rome/rome_4_additional_skill_category.sql");
    Resource additionalSkillsResource =
        new ClassPathResource("/additional-skill/rome/rome_4_additional_skill.sql");
    Resource rome4versionResource =
        new ClassPathResource("/additional-skill/rome/rome_4_version.sql");

    try {
      String additionalSkillsCategoriesSql =
          new String(
              additionalSkillsCategoriesResource.getInputStream().readAllBytes(),
              StandardCharsets.UTF_8);
      String additionalSkillsSql =
          new String(
              additionalSkillsResource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
      String rome4versionSql =
          new String(rome4versionResource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);

      jdbcTemplate.execute(additionalSkillsCategoriesSql);
      jdbcTemplate.execute(additionalSkillsSql);
      jdbcTemplate.execute(rome4versionSql);

      log.info("additional skills bootstrap successfully executed.");
    } catch (IOException e) {
      log.error("An error occurred while bootstrapping ROME 4.0 version.", e);
      throw new RuntimeException(e);
    }
  }
}
