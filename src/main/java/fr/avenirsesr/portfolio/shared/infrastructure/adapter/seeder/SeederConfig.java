package fr.avenirsesr.portfolio.shared.infrastructure.adapter.seeder;

public class SeederConfig {
  // Users
  private static final int USERS_NB = 100;
  public static final int USERS_NB_OF_STUDENT = (int) (0.8 * USERS_NB);
  public static final int USERS_NB_OF_TEACHER = (int) (0.15 * USERS_NB);
  public static final int USERS_NB_OF_BOTH = (int) (0.05 * USERS_NB);

  // Institutions
  private static final int INSTITUTIONS_NB = 5;
  public static final int INSTITUTIONS_NB_OF_APC = (int) (0.6 * INSTITUTIONS_NB);
  public static final int INSTITUTIONS_NB_OF_LIFE_PROJECT = (int) (0.2 * INSTITUTIONS_NB);
  public static final int INSTITUTIONS_NB_OF_BOTH = (int) (0.2 * INSTITUTIONS_NB);

  // Programs
  public static final int PROGRAM_BY_INSTITUTION = 3;
  public static final int PROGRAM_NB_APC = 2;

  // Traces
  public static final int TRACES_NB = 20;

  // Skill
  public static final int SKILL_BY_PROGRAM = 6;
  public static final int SKILL_LEVEL_BY_SKILL = 3;

  // Training paths
  public static final int TRAINING_PATH_BY_PROGRAM = 3;

  // Cohorts
  public static final int COHORTS_NB = 500;
  public static final int COHORT_NB_USERS_MIN = 1;
  public static final int COHORT_NB_USERS_MAX = 50;

  // AMS
  public static final int AMS_NB = 200;
  public static final int NB_COHORTS_MIN_PER_AMS = 0;
  public static final int NB_COHORTS_MAX_PER_AMS = 8;

  public static final int NB_SKILL_LEVEL_MIN_PER_AMS = 1;
  public static final int NB_SKILL_LEVEL_MAX_PER_AMS = 4;

  public static final int NB_TRACES_MIN_PER_AMS = 0;
  public static final int NB_TRACES_MAX_PER_AMS = 8;

  // Student Additional Skills
  public static final int MIN_ADDITIONAL_SKILLS_PER_STUDENT = 1;
  public static final int MAX_ADDITIONAL_SKILLS_PER_STUDENT = 4;
}
