package com.fitfind.fitfind.security.filter;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.fitfind.fitfind.client.model.AuthorityStatus;
import com.fitfind.fitfind.security.auth.model.DecodedJwtData;
import com.fitfind.fitfind.security.auth.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

@Component
@AllArgsConstructor
@Slf4j
public class JwtOncePerRequestFilter extends OncePerRequestFilter {

    public static final String BEARER_PREFIX = "Bearer ";

    private final JwtService jwtService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (authHeader != null && authHeader.startsWith(BEARER_PREFIX)) {
            final String token = authHeader.replaceFirst(BEARER_PREFIX, "");

            DecodedJwtData decodedJwt;
            try {
                decodedJwt = jwtService.validateToken(token);
            } catch (JWTVerificationException | IllegalArgumentException e) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }

            List<AuthorityStatus> authorities = getAuthorities(decodedJwt);

            UsernamePasswordAuthenticationToken authentication =
                    UsernamePasswordAuthenticationToken.authenticated(decodedJwt.username(), null, authorities);

            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        filterChain.doFilter(request, response);
    }

    private List<AuthorityStatus> getAuthorities(DecodedJwtData decodedJWT) {
        if (decodedJWT == null || decodedJWT.authorities() == null) {
            return List.of();
        }

        return decodedJWT.authorities()
                .stream()
                .map(authStr -> {
                    try {
                        return AuthorityStatus.valueOf(authStr);
                    } catch (Exception e) {
                        log.debug("Ignoring invalid authority in JWT: {}", authStr);
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .toList();
    }
}
