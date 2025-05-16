package fr.avenirsesr.portfolio.api.application.adapter.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProfileDTO {

    private String firstname;
    private String lastname;
    private String bio;
    private String profilePictureBase64;
    private String coverPictureBase64;

}
