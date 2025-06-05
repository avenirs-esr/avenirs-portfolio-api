package fr.avenirsesr.portfolio.api.infrastructure.adapter.model;

import lombok.Data;

import java.time.Instant;
import java.util.UUID;


@Data
public class UserPayload {
    private UUID id;
    private Instant iat;
    private Instant exp;
}
