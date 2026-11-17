package fr.avenirsesr.portfolio.staff.activity.domain.data;

import java.util.List;
import java.util.UUID;

public record CandidateSearchCriteria(
    boolean national, List<UUID> institutionIds, List<UUID> groupIds) {}
