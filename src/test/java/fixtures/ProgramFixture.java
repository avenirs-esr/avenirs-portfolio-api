package fixtures;

import fr.avenirsesr.portfolio.api.domain.model.Institution;
import fr.avenirsesr.portfolio.api.domain.model.Program;
import fr.avenirsesr.portfolio.api.domain.model.enums.ELanguage;
import fr.avenirsesr.portfolio.api.domain.model.enums.EPortfolioType;
import java.util.Set;
import java.util.UUID;

public class ProgramFixture {

  private UUID id;
  private Institution institution;
  private String name;
  private boolean isAPC;
  private ELanguage language = ELanguage.FRENCH;

  private ProgramFixture() {
    this.id = UUID.randomUUID();
    this.institution = InstitutionFixture.create().toModel();
    this.name = "Default Program";
    this.isAPC = false;
  }

  public static ProgramFixture create() {
    return new ProgramFixture();
  }

  public static ProgramFixture createWithAPC() {
    return new ProgramFixture()
        .withAPC(true)
        .withInstitution(
            InstitutionFixture.create().withEnabledFields(Set.of(EPortfolioType.APC)).toModel());
  }

  public static ProgramFixture createWithoutAPC() {
    return new ProgramFixture()
        .withAPC(false)
        .withInstitution(
            InstitutionFixture.create()
                .withEnabledFields(Set.of(EPortfolioType.LIFE_PROJECT))
                .toModel());
  }

  public ProgramFixture withId(UUID id) {
    this.id = id;
    return this;
  }

  public ProgramFixture withInstitution(Institution institution) {
    this.institution = institution;
    return this;
  }

  public ProgramFixture withName(String name) {
    this.name = name;
    return this;
  }

  public ProgramFixture withAPC(boolean isAPC) {
    this.isAPC = isAPC;
    return this;
  }

  public ProgramFixture withLanguage(ELanguage language) {
    this.language = language;
    return this;
  }

  public Program toModel() {
    return Program.toDomain(id, institution, name, isAPC, language);
  }
}
