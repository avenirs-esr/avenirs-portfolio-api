package fr.avenirsesr.portfolio.api.domain.model;

import java.util.List;

public record PagedResult<T>(List<T> content, long totalElements, int totalPages) {}
