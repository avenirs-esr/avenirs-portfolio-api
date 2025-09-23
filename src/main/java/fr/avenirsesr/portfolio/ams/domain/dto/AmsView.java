package fr.avenirsesr.portfolio.ams.domain.dto;

import fr.avenirsesr.portfolio.ams.domain.model.AMS;

public record AmsView(AMS ams, int skillLevelCount, int traceCount) {}
