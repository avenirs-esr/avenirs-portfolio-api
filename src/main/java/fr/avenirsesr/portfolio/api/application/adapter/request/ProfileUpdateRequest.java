package fr.avenirsesr.portfolio.api.application.adapter.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProfileUpdateRequest {

  private String firstname;
  private String lastname;
  private String email;
  private String bio;
}
