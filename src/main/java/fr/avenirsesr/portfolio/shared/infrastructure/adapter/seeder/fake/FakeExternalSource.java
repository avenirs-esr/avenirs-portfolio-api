package fr.avenirsesr.portfolio.shared.infrastructure.adapter.seeder.fake;

public class FakeExternalSource {
  private static final FakerProvider faker = new FakerProvider();

  public static String generateExternalSourceId() {
    int externalIdType = faker.call().number().numberBetween(0, 3);
    return switch (externalIdType) {
      case 0 -> faker.call().internet().uuid();
      case 1 -> String.valueOf(faker.call().number().numberBetween(1, 999_999));
      case 2 -> faker.call().regexify("[A-Z]{3}[0-9]{3}");
      default -> throw new IllegalStateException("Unexpected value: " + externalIdType);
    };
  }
}
