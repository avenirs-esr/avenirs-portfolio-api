package fr.avenirsesr.portfolio.security.infrastructure.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.avenirsesr.portfolio.shared.application.adapter.response.ErrorResponse;
import fr.avenirsesr.portfolio.shared.domain.model.enums.EErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

  private final ObjectMapper objectMapper = new ObjectMapper();

  @Override
  public void commence(
      HttpServletRequest request,
      HttpServletResponse response,
      AuthenticationException authException)
      throws IOException {
    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    response.setContentType("application/json");

    ErrorResponse error =
        new ErrorResponse(
            EErrorCode.USER_NOT_AUTHORIZED.name(), EErrorCode.USER_NOT_AUTHORIZED.getMessage());
    response.getWriter().write(objectMapper.writeValueAsString(error));
  }
}
