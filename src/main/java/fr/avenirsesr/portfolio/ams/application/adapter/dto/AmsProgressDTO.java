package fr.avenirsesr.portfolio.ams.application.adapter.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Random;

@Schema(
    name = "AMSProgress",
    requiredProperties = {"startedActivities", "totalActivities"})
public record AmsProgressDTO(int startedActivities, int totalActivities) {

  public static AmsProgressDTO createNotStartedMock() {
    return new AmsProgressDTO(0, 0);
  }

  public static AmsProgressDTO createInProgressMock() {
    Random random = new Random();
    int total = random.nextInt(8) + 1;
    int started = random.nextInt(total) + 1;

    return new AmsProgressDTO(started, total);
  }

  public static AmsProgressDTO createSubmittedOrCompletedMock() {
    Random random = new Random();
    int total = random.nextInt(8) + 1;

    return new AmsProgressDTO(total, total);
  }
}
