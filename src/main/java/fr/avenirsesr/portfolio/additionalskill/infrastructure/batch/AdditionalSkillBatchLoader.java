package fr.avenirsesr.portfolio.additionalskill.infrastructure.batch;

import fr.avenirsesr.portfolio.additionalskill.domain.model.AdditionalSkill;
import fr.avenirsesr.portfolio.additionalskill.domain.port.input.RomeAdditionalSkillService;
import fr.avenirsesr.portfolio.additionalskill.domain.port.output.RomeAdditionalSkillApi;
import fr.avenirsesr.portfolio.additionalskill.infrastructure.adapter.mapper.AdditionalSkillMapper;
import fr.avenirsesr.portfolio.additionalskill.infrastructure.adapter.model.Competence;
import fr.avenirsesr.portfolio.shared.domain.port.output.utils.UuidGenerator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;
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
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.batch.core.job.flow.support.SimpleFlow;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.transaction.PlatformTransactionManager;

@Slf4j
@Configuration
@Profile("!test")
@RequiredArgsConstructor
public class AdditionalSkillBatchLoader {
  private final UuidGenerator uuidGenerator;
  private final RomeAdditionalSkillApi romeAdditionalSkillApi;
  private final RomeAdditionalSkillService romeAdditionalSkillService;

  @Bean
  public Job importROME4SkillJob(JobRepository jobRepository, Flow importROME4SkillFlow) {
    return new JobBuilder("importROME4SkillJob", jobRepository)
        .incrementer(new RunIdIncrementer())
        .start(importROME4SkillFlow)
        .end()
        .listener(jobResultListener())
        .build();
  }

  @Bean
  public Flow importROME4SkillFlow(
      Step checkROME4VersionUpdateStep, Step cleanROME4SkillStep, Step importROME4SkillStep) {
    return new FlowBuilder<SimpleFlow>("importROME4SkillFlow")
        .start(checkROME4VersionUpdateStep)
        .on("NOOP")
        .end()
        .from(checkROME4VersionUpdateStep)
        .on("*")
        .to(cleanROME4SkillStep)
        .next(importROME4SkillStep)
        .build();
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
  public Step cleanROME4SkillStep(
      JobRepository jobRepository, PlatformTransactionManager transactionManager) {
    return new StepBuilder("cleanROME4SkillStep", jobRepository)
        .tasklet(
            (contribution, chunkContext) -> {
              romeAdditionalSkillService.cleanAndCreateAdditionalSkillIndex();
              return RepeatStatus.FINISHED;
            },
            transactionManager)
        .build();
  }

  @Bean
  public Step importROME4SkillStep(
      JobRepository jobRepository, PlatformTransactionManager transactionManager) {
    return new StepBuilder("importROME4SkillStep", jobRepository)
        .<Competence, AdditionalSkill>chunk(100, transactionManager)
        .reader(itemReader())
        .processor(itemProcessor())
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
  public ItemProcessor<Competence, AdditionalSkill> itemProcessor() {
    return competence -> AdditionalSkillMapper.createToDomain(competence, uuidGenerator);
  }

  @Bean
  public ItemWriter<AdditionalSkill> itemWriter() {
    return additionalSkills -> {
      List<AdditionalSkill> additionalSkillList = new ArrayList<>(additionalSkills.getItems());
      romeAdditionalSkillService.synchronizeAndIndexAdditionalSkills(additionalSkillList);
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
}
