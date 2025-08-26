package fr.avenirsesr.portfolio.additionalskill.infrastructure.batch;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class AdditionalSkillBatchScheduler {
  private final JobLauncher jobLauncher;
  private final Job importROME4CompetenceJob;

  public AdditionalSkillBatchScheduler(JobLauncher jobLauncher, Job importROME4CompetenceJob) {
    this.jobLauncher = jobLauncher;
    this.importROME4CompetenceJob = importROME4CompetenceJob;
  }

  @Scheduled(cron = "${additional.skill.batch.cron}")
  public void runJob() {
    try {
      JobParameters jobParameters =
          new JobParametersBuilder().addLong("time", System.currentTimeMillis()).toJobParameters();
      jobLauncher.run(importROME4CompetenceJob, jobParameters);
      log.info("The ROME 4.0 skills import job has been successfully launched !");
    } catch (Exception e) {
      log.error("Error when launching the ROME 4.0 skills import job : {}", e.getMessage());
    }
  }
}
