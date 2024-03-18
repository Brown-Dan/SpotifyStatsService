package uk.co.spotistats.spotistatsservice.Config;


import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JwtVerifierConfig {

    @Bean
    public JWTVerifier getJwtVerifier(){
        Algorithm algorithm = Algorithm.HMAC256(System.getenv("SECRET"));
        return JWT.require(algorithm)
                .withIssuer(System.getenv("SECRET"))
                .build();
    }

    @Bean
    public Algorithm getAlgorithm(){
        return Algorithm.HMAC256(System.getenv("SECRET"));
    }
}
