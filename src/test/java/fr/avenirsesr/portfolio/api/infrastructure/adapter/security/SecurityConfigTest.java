package fr.avenirsesr.portfolio.api.infrastructure.adapter.security;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(properties = "spring.profiles.active=test")
@AutoConfigureMockMvc
class SecurityConfigTest {

  @Value("${security.permit-all-paths}")
  private String permitAllPathsString;

  @TestConfiguration
  static class TestSecurityConfig {

    @Bean
    @Primary
    public HmacAuthenticationFilter testHmacFilter() throws ServletException, IOException {
      HmacAuthenticationFilter mockFilter = spy(new HmacAuthenticationFilter());

      doAnswer(
              (Answer<Void>)
                  invocation -> {
                    HttpServletRequest request = invocation.getArgument(0);
                    HttpServletResponse response = invocation.getArgument(1);
                    FilterChain chain = invocation.getArgument(2);

                    if (mockFilter.shouldNotFilter(request)) {
                      chain.doFilter(request, response);
                    } else {
                      throw new BadCredentialsException("Authentication required");
                    }
                    return null;
                  })
          .when(mockFilter)
          .doFilterInternal(
              any(HttpServletRequest.class),
              any(HttpServletResponse.class),
              any(FilterChain.class));

      return mockFilter;
    }
  }

  @Autowired private MockMvc mockMvc;

  @BeforeEach
  void setUp() {
    SecurityContextHolder.clearContext();
  }

  @Test
  void shouldRequireAuthenticationForProtectedEndpoints() {
    org.junit.jupiter.api.Assertions.assertThrows(
        org.springframework.security.authentication.BadCredentialsException.class,
        () ->
            mockMvc.perform(
                get("/api/some-protected-endpoint")
                    .header("X-Context-Kid", "TEST_KEY")
                    .header("X-Context-Signature", "test-signature")
                    .header(
                        "X-Signed-Context",
                        "{\"sub\":\"test-user\",\"exp\":\"2099-01-01T00:00:00Z\"}")));
  }

  @Test
  void shouldAllowAccessToAllPermitAllPaths() throws Exception {
    String[] paths = permitAllPathsString.split(",");
    for (String path : paths) {
      String cleanPath = path.trim().replace("/**", "");
      if (!cleanPath.isEmpty()) {
        try {
          if (cleanPath.contains("swagger-ui")) {
            mockMvc
                .perform(get(cleanPath))
                .andExpect(
                    result -> {
                      int status = result.getResponse().getStatus();
                      if (status != 200 && status != 302) {
                        throw new AssertionError(
                            "Status expected to be 200 or 302 but was: " + status);
                      }
                    });
          } else if (cleanPath.startsWith("/photo") || cleanPath.startsWith("/cover")) {
            mockMvc
                .perform(get(cleanPath))
                .andExpect(
                    result -> {
                      int status = result.getResponse().getStatus();
                      if (status != 200 && status != 404 && status != 500) {
                        throw new AssertionError(
                            "Status expected to be 200, 404 or 500 but was: " + status);
                      }
                    });
          } else {
            mockMvc.perform(get(cleanPath)).andExpect(status().isOk());
          }
        } catch (AssertionError e) {
          throw new AssertionError(
              "Test failed for path: " + cleanPath + " - " + e.getMessage(), e);
        }
      }
    }
  }
}
