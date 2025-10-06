package com.parkingapp.backendapi.infrastructure.security.jwt;

import com.parkingapp.backendapi.infrastructure.security.service.UserDetailsServiceImpl;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Filter is responsible for authenticating a user
 *
 * <ul>
 *   <li>- extract JWT token from Authorization header
 *   <li>- Validate JWT token
 *   <li>- set user's details in the security context
 * </ul>
 */
@Component
@Slf4j
@AllArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private JwtTokenProvider jwtTokenProvider;
  private UserDetailsServiceImpl userDetailsService;

  @Override
  protected void doFilterInternal(
      @NonNull HttpServletRequest request,
      @NonNull HttpServletResponse response,
      @NonNull FilterChain filterChain)
      throws ServletException, IOException {

    final String authHeader = request.getHeader("Authorization");
    final String jwt;
    final String userEmail;

    if (authHeader == null || !authHeader.startsWith("Bearer ")) {
      filterChain.doFilter(request, response);
      return;
    }

    jwt = authHeader.substring(7); // Extract the token itself, `Bearer ` is 7 chars

    // token validation
    try {
      Claims claims = jwtTokenProvider.validateTokenAndGetClaims(jwt);

      userEmail = jwtTokenProvider.extractUsername(claims);

      // check if user is not already authenticated
      if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
        UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);

        if (jwtTokenProvider.isTokenValid(claims, userDetails)) {
          UsernamePasswordAuthenticationToken authToken =
              new UsernamePasswordAuthenticationToken(
                  userDetails, null, userDetails.getAuthorities());

          authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

          SecurityContextHolder.getContext().setAuthentication(authToken);
        }
      }

    } catch (Exception e) { // todo: handle specific jwt errors instead of generic
      log.warn("Invalid JWT token detected: {}", e.getMessage(), e);
    }
    filterChain.doFilter(request, response);
  }
}
