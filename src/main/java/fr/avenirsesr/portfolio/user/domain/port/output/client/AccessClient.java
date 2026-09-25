package fr.avenirsesr.portfolio.user.domain.port.output.client;

import java.util.List;
import java.util.UUID;

public interface AccessClient {

  boolean staffHasAccess(String eppn, List<UUID> targetInstitutionIds, List<UUID> targetGroupIds);

  StudentAccessScope getStudentScope(String eppn);
}
