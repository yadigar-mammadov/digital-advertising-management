package com.digitaladvertisingmanagement.auth.infrastructure;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.digitaladvertisingmanagement.auth.application.security.AuthenticatedUser;
import com.digitaladvertisingmanagement.auth.domain.model.User;
import com.digitaladvertisingmanagement.auth.domain.model.UserStatus;
import com.digitaladvertisingmanagement.auth.domain.repository.UserRepository;
import com.digitaladvertisingmanagement.auth.infrastructure.security.JwtAuthenticationFilter;
import com.digitaladvertisingmanagement.auth.infrastructure.security.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@ExtendWith(MockitoExtension.class)
public class JwtAuthenticationFilterTest {
  @Mock private JwtService jwtService;

  @Mock private UserRepository userRepository;

  @Mock private HttpServletRequest request;

  @Mock private HttpServletResponse response;

  @Mock private FilterChain filterChain;

  @Mock private AuthenticatedUser authenticatedUser;

  @InjectMocks private JwtAuthenticationFilter jwtAuthenticationFilter;

  private final Long userId = 1L;

  @BeforeEach
  void setUp() {
    SecurityContextHolder.clearContext();
  }

  @AfterEach
  void tearDown() {
    SecurityContextHolder.clearContext();
  }

  @Test
  void shouldAuthenticateUserWhenTokenIsValid() throws Exception {
    String token = "valid-token";

    User user = new User(userId, "test@example.com", "password-hash", UserStatus.ACTIVE);

    when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
    when(jwtService.isValid(token)).thenReturn(true);
    when(jwtService.extractAuthenticatedUser(token)).thenReturn(authenticatedUser);
    when(authenticatedUser.id()).thenReturn(userId);
    when(userRepository.findById(userId)).thenReturn(Optional.of(user));

    jwtAuthenticationFilter.doFilter(request, response, filterChain);

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    assertNotNull(authentication);
    assertTrue(authentication.isAuthenticated());
    assertSame(authenticatedUser, authentication.getPrincipal());

    verify(jwtService).isValid(token);
    verify(jwtService).extractAuthenticatedUser(token);
    verify(filterChain).doFilter(request, response);
  }

  @Test
  void shouldNotAuthenticateWhenUserIsDisabled() throws Exception {
    String token = "valid-token";

    User user = new User(userId, "test@example.com", "password-hash", UserStatus.DISABLED);

    when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
    when(jwtService.isValid(token)).thenReturn(true);
    when(jwtService.extractAuthenticatedUser(token)).thenReturn(authenticatedUser);
    when(authenticatedUser.id()).thenReturn(userId);
    when(userRepository.findById(userId)).thenReturn(Optional.of(user));

    jwtAuthenticationFilter.doFilter(request, response, filterChain);

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    assertNull(authentication);

    verify(userRepository).findById(userId);
    verify(filterChain).doFilter(request, response);
  }

  @Test
  void shouldNotAuthenticateWhenAuthorizationHeaderIsMissing() throws Exception {
    when(request.getHeader("Authorization")).thenReturn(null);

    jwtAuthenticationFilter.doFilter(request, response, filterChain);

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    assertNull(authentication);

    verifyNoInteractions(jwtService);
    verify(filterChain).doFilter(request, response);
  }

  @Test
  void shouldNotAuthenticateWhenAuthorizationHeaderIsNotBearer() throws Exception {
    when(request.getHeader("Authorization")).thenReturn("Basic some-token");

    jwtAuthenticationFilter.doFilter(request, response, filterChain);

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    assertNull(authentication);

    verifyNoInteractions(jwtService);
    verify(filterChain).doFilter(request, response);
  }

  @Test
  void shouldNotAuthenticateWhenTokenIsInvalid() throws Exception {
    String token = "invalid-token";

    when(request.getHeader("Authorization")).thenReturn("Bearer " + token);

    when(jwtService.isValid(token)).thenReturn(false);

    jwtAuthenticationFilter.doFilter(request, response, filterChain);

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    assertNull(authentication);

    verify(jwtService).isValid(token);
    verify(userRepository, never()).findById(any());
    verify(jwtService, never()).extractAuthenticatedUser(anyString());

    verify(filterChain).doFilter(request, response);
  }
}
