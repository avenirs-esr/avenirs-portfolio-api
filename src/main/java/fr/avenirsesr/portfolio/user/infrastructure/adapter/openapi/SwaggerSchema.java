package fr.avenirsesr.portfolio.user.infrastructure.adapter.openapi;

import fr.avenirsesr.portfolio.common.data.domain.model.enums.EUserCategory;
import fr.avenirsesr.portfolio.common.group.domain.model.enums.EGroupType;
import fr.avenirsesr.portfolio.common.institution.domain.model.enums.EInstitutionType;
import fr.avenirsesr.portfolio.common.security.accesscontrol.domain.model.enums.ERole;
import fr.avenirsesr.portfolio.user.domain.model.enums.EContextType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.media.StringSchema;
import java.util.Arrays;

public final class SwaggerSchema {
  private SwaggerSchema() {}

  public static final Schema<String> userContextSchema =
      new StringSchema()
          .name("EContextType")
          ._enum(Arrays.stream(EContextType.values()).map(Enum::name).toList())
          .description("Enum for context type");

  public static final Schema<String> userCategorySchema =
      new StringSchema()
          .name("EUserCategory")
          ._enum(Arrays.stream(EUserCategory.values()).map(Enum::name).toList())
          .description("Enum for user category");

  public static final Schema<String> roleSchema =
      new StringSchema()
          .name("ERole")
          ._enum(Arrays.stream(ERole.values()).map(Enum::name).toList())
          .description("Enum for user role");

  public static final Schema<String> institutionTypeSchema =
      new StringSchema()
          .name("EInstitutionType")
          ._enum(Arrays.stream(EInstitutionType.values()).map(Enum::name).toList())
          .description("Enum for institution type");

  public static final Schema<String> groupTypeSchema =
      new StringSchema()
          .name("EGroupType")
          ._enum(Arrays.stream(EGroupType.values()).map(Enum::name).toList())
          .description("Enum for group type");
}
