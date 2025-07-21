package fr.avenirsesr.portfolio.additionalskill.infrastructure.fixture;

import fr.avenirsesr.portfolio.additionalskill.domain.model.AdditionalSkill;
import fr.avenirsesr.portfolio.additionalskill.domain.model.AdditionalSkillsPaginated;
import fr.avenirsesr.portfolio.shared.domain.model.PageInfo;
import fr.avenirsesr.portfolio.shared.infrastructure.adapter.seeder.fake.FakerProvider;
import java.util.List;

public class AdditionalSkillPaginatedFixture {
  private static final FakerProvider faker = new FakerProvider();

  private List<AdditionalSkill> data;
  private PageInfo page;

  private AdditionalSkillPaginatedFixture() {
    this.data =
        AdditionalSkillFixture.create(faker.call().random().nextInt(1, 10)).stream()
            .map(AdditionalSkillFixture::toModel)
            .toList();
    int randomTotalElements = faker.call().random().nextInt(data.size(), data.size() + 5);
    this.page =
        new PageInfo(data.size(), randomTotalElements, randomTotalElements % data.size(), 0);
  }

  public static AdditionalSkillPaginatedFixture create() {
    return new AdditionalSkillPaginatedFixture();
  }

  public AdditionalSkillPaginatedFixture withData(List<AdditionalSkill> data) {
    this.data = data;
    return this;
  }

  public AdditionalSkillPaginatedFixture withPage(PageInfo page) {
    this.page = page;
    return this;
  }

  public AdditionalSkillsPaginated toModel() {
    return new AdditionalSkillsPaginated(data, page);
  }
}
