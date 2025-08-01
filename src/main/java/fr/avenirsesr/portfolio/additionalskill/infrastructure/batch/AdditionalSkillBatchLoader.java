package fr.avenirsesr.portfolio.additionalskill.infrastructure.batch;

import fr.avenirsesr.portfolio.additionalskill.domain.model.AdditionalSkill;
import fr.avenirsesr.portfolio.additionalskill.domain.port.input.RomeAdditionalSkillService;
import fr.avenirsesr.portfolio.additionalskill.domain.port.output.RomeAdditionalSkillApi;
import fr.avenirsesr.portfolio.additionalskill.infrastructure.adapter.mapper.AdditionalSkillMapper;
import fr.avenirsesr.portfolio.additionalskill.infrastructure.adapter.model.Competence;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
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
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@AllArgsConstructor
public class AdditionalSkillBatchLoader {
  private final RomeAdditionalSkillApi romeAdditionalSkillApi;
  private final RomeAdditionalSkillService romeAdditionalSkillService;

  @Bean
  public Job importROME4SkillJob(JobRepository jobRepository, Flow importROME4SkillFlow) {
    return new JobBuilder("importROME4SkillJob", jobRepository)
        .incrementer(new RunIdIncrementer())
        .start(importROME4SkillFlow)
        .end()
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
                System.out.println(
                    "checkROME4VersionUpdateStep (NOOP) because there are no updates to ROME 4.0");
                contribution.setExitStatus(ExitStatus.NOOP);
              } else {
                System.out.println(
                    "checkROME4VersionUpdateStep (COMPLETED) because there are updates to ROME 4.0");
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
        .build();
  }

  @Bean
  public ItemReader<Competence> itemReader() {
    return new ListItemReader<>(romeAdditionalSkillApi.fetchAdditionalSkills());
  }

  @Bean
  public ItemProcessor<Competence, AdditionalSkill> itemProcessor() {
    return AdditionalSkillMapper::createToDomain;
  }

  @Bean
  public ItemWriter<AdditionalSkill> itemWriter() {
    return additionalSkills -> {
      List<AdditionalSkill> additionalSkillList = new ArrayList<>(additionalSkills.getItems());
      romeAdditionalSkillService.synchronizeAndIndexAdditionalSkills(additionalSkillList);
    };
  }
}
