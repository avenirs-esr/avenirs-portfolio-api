package fr.avenirsesr.portfolio.api.infrastructure.adapter.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.avenirsesr.portfolio.api.application.adapter.response.ErrorResponse;
import fr.avenirsesr.portfolio.api.domain.exception.UserNotAuthorizedException;
import fr.avenirsesr.portfolio.api.domain.model.enums.EErrorCode;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");

        ErrorResponse error = new ErrorResponse(EErrorCode.USER_NOT_AUTHORIZED.getCode(), EErrorCode.USER_NOT_AUTHORIZED.getMessage());
        response.getWriter().write(objectMapper.writeValueAsString(error));
    }
}

