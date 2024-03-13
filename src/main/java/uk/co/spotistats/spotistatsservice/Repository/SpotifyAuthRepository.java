package uk.co.spotistats.spotistatsservice.Repository;

import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;
import uk.co.spotistats.spotistatsservice.Domain.SpotifyAuth.SpotifyAuthData;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

import static uk.co.spotistats.generated.Tables.USER_AUTH_DATA;
import static uk.co.spotistats.spotistatsservice.Domain.SpotifyAuth.SpotifyAuthData.Builder.someSpotifyAuthData;

@Repository
public class SpotifyAuthRepository {

    private final DSLContext db;

    public SpotifyAuthRepository(DSLContext db) {
        this.db = db;
    }

    public Optional<SpotifyAuthData> getAuthorizationDetailsByUsername(String username) {
        uk.co.spotistats.generated.tables.pojos.UserAuthData userAuthDataEntity =
                db.selectFrom(USER_AUTH_DATA).where(USER_AUTH_DATA.USERNAME.eq(username))
                        .fetchOneInto(uk.co.spotistats.generated.tables.pojos.UserAuthData.class);

        return Optional.ofNullable(userAuthDataEntity).map(this::mapUserAuthDataEntityToUserAuthData);
    }

    public void insertSpotifyAuthData(SpotifyAuthData spotifyAuthData) {
        db.insertInto(USER_AUTH_DATA)
                .set(USER_AUTH_DATA.USERNAME, spotifyAuthData.username())
                .set(USER_AUTH_DATA.LAST_UPDATED, LocalDateTime.now())
                .set(USER_AUTH_DATA.ACCESS_TOKEN, spotifyAuthData.accessToken())
                .set(USER_AUTH_DATA.REFRESH_TOKEN, spotifyAuthData.refreshToken())
                .execute();
    }

    public SpotifyAuthData updateUserAuthData(SpotifyAuthData spotifyAuthData) {
        return mapUserAuthDataEntityToUserAuthData(
                Objects.requireNonNull(db.update(USER_AUTH_DATA)
                        .set(USER_AUTH_DATA.LAST_UPDATED, LocalDateTime.now())
                        .set(USER_AUTH_DATA.ACCESS_TOKEN, spotifyAuthData.accessToken())
                        .set(USER_AUTH_DATA.REFRESH_TOKEN, spotifyAuthData.refreshToken())
                        .where(USER_AUTH_DATA.USERNAME.eq(spotifyAuthData.username())).returning()
                        .fetchOneInto(uk.co.spotistats.generated.tables.pojos.UserAuthData.class)));
    }

    private SpotifyAuthData mapUserAuthDataEntityToUserAuthData(uk.co.spotistats.generated.tables.pojos.UserAuthData userAuthDataEntity) {
        return someSpotifyAuthData()
                .withUsername(userAuthDataEntity.getUsername())
                .withLastUpdated(userAuthDataEntity.getLastUpdated())
                .withRefreshToken(userAuthDataEntity.getRefreshToken())
                .withAccessToken(userAuthDataEntity.getAccessToken())
                .build();
    }
}
