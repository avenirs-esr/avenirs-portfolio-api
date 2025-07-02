package fr.avenirsesr.portfolio.shared.infrastructure.context;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import fr.avenirsesr.portfolio.shared.domain.model.enums.ELanguage;
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
    when(request.getHeader("Accept-Language")).thenReturn("fr_FR");

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
    when(request.getHeader("Accept-Language")).thenReturn(null);

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
    when(request.getHeader("Accept-Language")).thenReturn("en");

    filter.doFilterInternal(request, response, filterChain);

    assertNull(RequestContext.get());
  }
}
