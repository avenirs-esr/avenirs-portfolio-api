package fr.avenirsesr.portfolio.api.application.adapter.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ErrorResponse {

  private String code;
  private String message;
}
