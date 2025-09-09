package fr.avenirsesr.portfolio.shared.infrastructure.context;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import fr.avenirsesr.portfolio.shared.domain.model.enums.ELanguage;
import fr.avenirsesr.portfolio.testutils.BddLogger;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class RequestContextFilterTest {

  private final RequestContextFilter filter = new RequestContextFilter();

  private final HttpServletRequest request = mock(HttpServletRequest.class);
  private final HttpServletResponse response = mock(HttpServletResponse.class);
  private final FilterChain filterChain = mock(FilterChain.class);

  @BeforeEach
  void setup() {
    RequestContext.clear();
  }

  @AfterEach
  void cleanup() {
    RequestContext.clear();
  }

  @Test
  void shouldSetPreferredLanguageWhenHeaderPresent() throws Exception {
    BddLogger.given("a request");
    BddLogger.when("Accept-Language header is present");
    when(request.getHeader("Accept-Language")).thenReturn("fr-FR");

    BddLogger.then("it should set preferred language");
    doAnswer(
            invocation -> {
              RequestData contextData = RequestContext.get();
              assertNotNull(contextData);
              assertEquals(ELanguage.FRENCH, contextData.preferredLanguage());
              return null;
            })
        .when(filterChain)
        .doFilter(any(), any());

    filter.doFilterInternal(request, response, filterChain);

    verify(filterChain).doFilter(request, response);
  }

  @Test
  void shouldUseFallbackLanguageWhenHeaderMissing() throws Exception {
    BddLogger.given("a request");
    BddLogger.when("Accept-Language header is missing");
    when(request.getHeader("Accept-Language")).thenReturn(null);

    BddLogger.then("it should use fallback language");
    doAnswer(
            invocation -> {
              RequestData contextData = RequestContext.get();
              assertNotNull(contextData);
              assertEquals(ELanguage.FALLBACK, contextData.preferredLanguage());
              return null;
            })
        .when(filterChain)
        .doFilter(any(), any());

    filter.doFilterInternal(request, response, filterChain);

    verify(filterChain).doFilter(request, response);
  }

  @Test
  void shouldClearRequestContextAfterFilterChain() throws Exception {
    BddLogger.given("a request");
    when(request.getHeader("Accept-Language")).thenReturn("en");

    BddLogger.when("filter chain has ended");
    filter.doFilterInternal(request, response, filterChain);

    BddLogger.then("it should clear request context");
    assertNull(RequestContext.get());
  }
}
