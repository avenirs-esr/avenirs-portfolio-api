package fr.avenirsesr.portfolio.user.application.adapter.request;

import java.util.List;

public record UserProfileLookupRequest(List<String> eppns) {}
