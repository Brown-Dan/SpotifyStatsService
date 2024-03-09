package uk.co.spotistats.spotistatsservice.Config;

import org.springframework.context.annotation.Configuration;
import uk.co.autotrader.traverson.Traverson;
import org.springframework.context.annotation.Bean;
import uk.co.autotrader.traverson.http.ApacheHttpTraversonClientAdapter;

@Configuration
public class TraversonConfiguration {

    @Bean
    public Traverson getTraverson() {
        return new Traverson(new ApacheHttpTraversonClientAdapter());
    }
}
