package fr.avenirsesr.portfolio.security.infrastructure.configuration;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.avenirsesr.portfolio.shared.application.adapter.response.ErrorResponse;
import fr.avenirsesr.portfolio.shared.domain.model.enums.EErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.io.StringWriter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.AuthenticationException;

@ExtendWith(MockitoExtension.class)
class CustomAuthenticationEntryPointTest {

  @InjectMocks private CustomAuthenticationEntryPoint entryPoint;

  @Mock private HttpServletRequest request;

  @Mock private HttpServletResponse response;

  @Mock private AuthenticationException authException;

  @Spy private ObjectMapper objectMapper = new ObjectMapper();

  private StringWriter stringWriter;
  private PrintWriter printWriter;

  @BeforeEach
  void setUp() throws Exception {
    stringWriter = new StringWriter();
    printWriter = new PrintWriter(stringWriter);
    when(response.getWriter()).thenReturn(printWriter);
  }

  @Test
  void shouldReturnUnauthorizedWithErrorResponse() throws Exception {
    entryPoint.commence(request, response, authException);

    verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    verify(response).setContentType("application/json");

    ErrorResponse expectedError =
        new ErrorResponse(
            EErrorCode.USER_NOT_AUTHORIZED.name(), EErrorCode.USER_NOT_AUTHORIZED.getMessage());
    String expectedJson = objectMapper.writeValueAsString(expectedError);

    printWriter.flush();
    String actualJson = stringWriter.toString();

    verify(response).getWriter();
    assert (actualJson.equals(expectedJson));
  }
}
