package uk.co.spotistats.spotistatsservice.Filter;

import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import io.micrometer.common.lang.NonNullApi;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;

import java.io.IOException;
import java.util.Set;

@Component
@NonNullApi
public class AuthFilter extends OncePerRequestFilter {

    private final JWTVerifier jwtVerifier;

    private static final Set<String> UNFILTERED_ENDPOINTS = Set.of("/spotify/login", "/spotify/authenticate/callback");

    public AuthFilter(JWTVerifier jwtVerifier) {
        this.jwtVerifier = jwtVerifier;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        ContentCachingRequestWrapper wrappedRequest = new ContentCachingRequestWrapper(request);
        if (UNFILTERED_ENDPOINTS.contains(wrappedRequest.getRequestURI())) {
            filterChain.doFilter(wrappedRequest, response);
        } else {
            try {
                DecodedJWT decodedJWT = jwtVerifier.verify(wrappedRequest.getHeader("Authorization"));
                wrappedRequest.setAttribute("username", decodedJWT.getSubject());
                filterChain.doFilter(wrappedRequest, response);
            } catch (JWTVerificationException jwtVerificationException){
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            }
        }
    }
}
