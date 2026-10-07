package fr.avenirsesr.portfolio.staff.activity.infrastructure.adapter.seeder.data;

import fr.avenirsesr.portfolio.staff.activity.domain.model.enums.EActivityThematic;
import java.util.List;

public record SystemActivityCreationData(
    Author author,
    String title,
    EActivityThematic thematic,
    String summary,
    String description,
    String recommendedCompletionContexts,
    boolean enableReflection,
    int traceAllowedAssociations,
    int feedbackAllowedIterations,
    List<String> links,
    String bannerFileName,
    String bannerMimeType,
    String attachmentFileName,
    String attachmentMimeType) {

  public record Author(String eppn, String firstName, String lastName, String email) {}
}
