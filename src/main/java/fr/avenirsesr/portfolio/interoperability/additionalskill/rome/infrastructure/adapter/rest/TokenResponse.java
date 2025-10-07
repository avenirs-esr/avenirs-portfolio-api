package fr.avenirsesr.portfolio.interoperability.additionalskill.rome.infrastructure.adapter.rest;

import com.fasterxml.jackson.annotation.JsonProperty;

public record TokenResponse(
    @JsonProperty("access_token") String accessToken,
    String scope,
    @JsonProperty("token_type") String tokenType,
    @JsonProperty("expires_in") int expiresIn) {}
