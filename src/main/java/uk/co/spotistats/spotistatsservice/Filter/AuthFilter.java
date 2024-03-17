package uk.co.spotistats.spotistatsservice.Filter;

import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.micrometer.common.lang.NonNullApi;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.hc.core5.http.ContentType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import uk.co.spotistats.spotistatsservice.Domain.Model.Error;

import java.io.IOException;
import java.time.Instant;
import java.util.Set;

@Component
@NonNullApi
public class AuthFilter extends OncePerRequestFilter {

    private final JWTVerifier jwtVerifier;
    private final ObjectMapper objectMapper;

    private static final Set<String> UNFILTERED_ENDPOINTS = Set.of("/login", "/authenticate/callback", "/token/refresh");

    public AuthFilter(JWTVerifier jwtVerifier, ObjectMapper objectMapper) {
        this.jwtVerifier = jwtVerifier;
        this.objectMapper = objectMapper;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        ContentCachingRequestWrapper wrappedRequest = new ContentCachingRequestWrapper(request);
        if (UNFILTERED_ENDPOINTS.contains(wrappedRequest.getRequestURI())) {
            filterChain.doFilter(wrappedRequest, response);
        } else {
            try {
                DecodedJWT decodedJWT = jwtVerifier.verify(wrappedRequest.getHeader("Authorization"));
                if (decodedJWT.getNotBeforeAsInstant().isBefore(Instant.now())){
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.setContentType(ContentType.APPLICATION_JSON.getMimeType());
                    response.getWriter().write(objectMapper.writeValueAsString(Error.jwtRefreshRequired()));
                    response.getWriter().close();
                }
                wrappedRequest.setAttribute("userId", decodedJWT.getSubject());
                filterChain.doFilter(wrappedRequest, response);
            } catch (JWTVerificationException jwtVerificationException){
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType(ContentType.APPLICATION_JSON.getMimeType());
                response.getWriter().write(objectMapper.writeValueAsString(Error.jwtVerificationException(jwtVerificationException)));
                response.getWriter().close();
            }
        }
    }
}
