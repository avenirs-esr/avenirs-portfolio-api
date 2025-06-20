package fr.avenirsesr.portfolio.api.domain.model;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Random;

@Schema(requiredProperties = {"validated", "total", "percentage"})
public record AmsProgress(int validated, int total, int percentage) {
  
  public static AmsProgress createMock() {
    Random random = new Random();
    int total = random.nextInt(8) + 1;
    int validated = random.nextInt(total + 1);
    int percentage = (int) Math.round(((double) validated / total) * 100);
    
    return new AmsProgress(validated, total, percentage);
  }
}
