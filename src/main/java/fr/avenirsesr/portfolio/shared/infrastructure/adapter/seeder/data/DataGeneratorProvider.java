package fr.avenirsesr.portfolio.shared.infrastructure.adapter.seeder.data;

import fr.avenirsesr.portfolio.shared.domain.model.enums.ELanguage;
import fr.avenirsesr.portfolio.shared.domain.port.output.seeder.DataGeneratorInterface;
import java.util.HashMap;
import java.util.Optional;
import java.util.Random;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class DataGeneratorProvider<T extends DataGeneratorInterface>
    implements ApplicationContextAware {
  private static ApplicationContext context;
  private static final HashMap<String, Integer> seedCounts = new HashMap<>();
  private String globalSeed;
  private Class<?> generatorClass;

  @Override
  public void setApplicationContext(ApplicationContext applicationContext) {
    context = applicationContext;
  }

  public DataGeneratorProvider<T> init(
      Class<?> classNameForSeed, Class<? extends DataGeneratorInterface> generatorInterface) {
    globalSeed = classNameForSeed.getSimpleName();
    this.generatorClass = generatorInterface;
    return this;
  }

  @SuppressWarnings("unchecked")
  public T with(String seed, ELanguage language) {
    var key = String.join(globalSeed, "-", seed);
    int count = Optional.ofNullable(seedCounts.get(key)).orElse(0);
    count += 1;
    seedCounts.put(key, count);
    try {
      T generator = (T) context.getBean(generatorClass);
      generator.setRandom(new Random((long) key.hashCode() * count));
      generator.setLanguage(language);

      return generator;

    } catch (Exception e) {
      throw new RuntimeException("Failed to instantiate DataGenerator", e);
    }
  }

  public T with(String seed) {
    return with(seed, ELanguage.FALLBACK);
  }
}
