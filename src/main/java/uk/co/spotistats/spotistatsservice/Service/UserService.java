package uk.co.spotistats.spotistatsservice.Service;

import org.springframework.stereotype.Service;
import uk.co.spotistats.spotistatsservice.Domain.Response.Error;
import uk.co.spotistats.spotistatsservice.Domain.UserAuthData;
import uk.co.spotistats.spotistatsservice.Repository.UserRepository;

import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Optional<Error> register(UserAuthData userAuthData) {
        Optional<UserAuthData> existingAuthData = userRepository.getAuthorizationDetailsByUsername(userAuthData.username());

        if (existingAuthData.isPresent()){
            return Optional.of(Error.authorizationDetailsPresent(userAuthData.username()));
        }
        userRepository.register(userAuthData);
        return Optional.empty();
    }
}
