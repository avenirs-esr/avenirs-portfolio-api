package fr.avenirsesr.portfolio.shared.infrastructure.adapter.seeder;

public class SeederConfig {
  // Users
  private static final int USERS_NB = 50;
  public static final int USERS_NB_OF_STUDENT = (int) (0.8 * USERS_NB);
  public static final int USERS_NB_OF_TEACHER = (int) (0.15 * USERS_NB);
  public static final int USERS_NB_OF_BOTH = (int) (0.05 * USERS_NB);
  public static final int MAX_PROFILE_PHOTO_PER_USER = 1;
  public static final int MAX_COVER_PHOTO_PER_USER = 1;

  // Institutions
  private static final int INSTITUTIONS_NB = 4;
  public static final int INSTITUTIONS_NB_OF_APC = (int) (0.5 * INSTITUTIONS_NB);
  public static final int INSTITUTIONS_NB_OF_LIFE_PROJECT = (int) (0.25 * INSTITUTIONS_NB);
  public static final int INSTITUTIONS_NB_OF_BOTH = (int) (0.25 * INSTITUTIONS_NB);

  // Programs
  public static final int PROGRAM_BY_INSTITUTION = 2;
  public static final int PROGRAM_NB_APC = 1;

  // Traces
  public static final int TRACES_NB_MIN = 10;
  public static final int TRACES_NB_MAX = 20;
  public static final int MAX_ATTACHMENT_PER_TRACE = 3;

  // Skill
  public static final int SKILL_BY_PROGRAM = 6;
  public static final int SKILL_LEVEL_BY_SKILL = 3;

  // Training paths
  public static final int TRAINING_PATH_BY_PROGRAM = 3;

  // Cohorts
  public static final int COHORTS_NB = 50;
  public static final int COHORT_NB_USERS_MIN = 1;
  public static final int COHORT_NB_USERS_MAX = 25;

  // AMS
  public static final int AMS_NB = 80;
  public static final int NB_COHORTS_MIN_PER_AMS = 0;
  public static final int NB_COHORTS_MAX_PER_AMS = 8;

  public static final int NB_SKILL_LEVEL_MIN_PER_AMS = 1;
  public static final int NB_SKILL_LEVEL_MAX_PER_AMS = 4;

  public static final int NB_TRACES_MIN_PER_AMS = 0;
  public static final int NB_TRACES_MAX_PER_AMS = 3;

  // Student Additional Skills
  public static final int MIN_ADDITIONAL_SKILLS_PER_STUDENT = 1;
  public static final int MAX_ADDITIONAL_SKILLS_PER_STUDENT = 4;
}
