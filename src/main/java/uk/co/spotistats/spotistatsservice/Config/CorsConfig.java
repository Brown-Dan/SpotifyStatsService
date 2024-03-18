package uk.co.spotistats.spotistatsservice.Config;

import io.micrometer.common.lang.NonNullApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


@NonNullApi
@Configuration
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CorsConfig {
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOrigins("*") // Allow requests from any origin
                        .allowedHeaders("*")
                        .allowedMethods("*"); // Allow all HTTP methods
            }
        };
    }
}


