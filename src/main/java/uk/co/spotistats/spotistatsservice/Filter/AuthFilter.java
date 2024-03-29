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
import org.apache.hc.core5.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger LOG = LoggerFactory.getLogger(AuthFilter.class);


    public AuthFilter(JWTVerifier jwtVerifier, ObjectMapper objectMapper) {
        this.jwtVerifier = jwtVerifier;
        this.objectMapper = objectMapper;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        ContentCachingRequestWrapper wrappedRequest = new ContentCachingRequestWrapper(request);
        LOG.info("Received request - {}", wrappedRequest.getHeader("Authorization"));
        response.setHeader("Access-Control-Allow-Origin", request.getHeader("Origin"));
        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE");
        response.setHeader("Access-Control-Max-Age", "3600");
        response.setHeader("Access-Control-Allow-Headers", "*");
        if (wrappedRequest.getMethod().equals("OPTIONS")) {
            response.setStatus(HttpStatus.SC_OK);
            filterChain.doFilter(wrappedRequest, response);
        } else {
            if (UNFILTERED_ENDPOINTS.contains(wrappedRequest.getRequestURI())) {
                filterChain.doFilter(wrappedRequest, response);
            } else {
                try {
                    DecodedJWT decodedJWT = jwtVerifier.verify(wrappedRequest.getHeader("Authorization"));
                    if (decodedJWT.getExpiresAtAsInstant().isBefore(Instant.now().plusSeconds(3600))) {
                        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                        response.setContentType(ContentType.APPLICATION_JSON.getMimeType());
                        response.getWriter().write(objectMapper.writeValueAsString(Error.jwtRefreshRequired()));
                        response.getWriter().close();
                    } else {
                        wrappedRequest.setAttribute("userId", decodedJWT.getSubject());
                        filterChain.doFilter(wrappedRequest, response);
                    }
                } catch (JWTVerificationException jwtVerificationException) {
                    LOG.info("Rejected request - {}", wrappedRequest.getHeader("Authorization"));
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.setContentType(ContentType.APPLICATION_JSON.getMimeType());
                    response.getWriter().write(objectMapper.writeValueAsString(Error.jwtVerificationException(jwtVerificationException)));
                    response.getWriter().close();
                }
            }
        }
    }
}
