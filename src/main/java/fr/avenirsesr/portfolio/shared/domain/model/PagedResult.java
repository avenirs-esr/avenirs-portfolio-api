package fr.avenirsesr.portfolio.shared.domain.model;

import java.util.List;

public record PagedResult<T>(List<T> content, PageInfo pageInfo) {}
