package fr.avenirsesr.portfolio.additionalskill.infrastructure.batch;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

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
      // Lancer le job
      jobLauncher.run(importROME4CompetenceJob, new JobParameters());
      System.out.println("Le job d'import des compétences du ROME 4.0 a été lancé avec succès !");
    } catch (Exception e) {
      System.err.println(
          "Erreur lors du lancement du job d'import des compétences du ROME 4.0 : "
              + e.getMessage());
    }
  }
}
