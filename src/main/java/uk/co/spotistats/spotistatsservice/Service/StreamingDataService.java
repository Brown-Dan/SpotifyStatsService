package uk.co.spotistats.spotistatsservice.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class StreamingDataService {

    private final UserService userService;

    private static final Logger LOG = LoggerFactory.getLogger(StreamingDataService.class);


    public StreamingDataService(UserService userService) {
        this.userService = userService;
    }

    public String tokenTesting(String username) {
        if (userService.hasAuthData(username)) {
            return userService.getAccessToken(username);
        }
        return null;
    }
}
