package uk.co.spotistats.spotistatsservice.Repository;

import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;
import uk.co.spotistats.spotistatsservice.Domain.UserAuthData;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

import static uk.co.spotistats.generated.Tables.USER_AUTH_DATA;
import static uk.co.spotistats.spotistatsservice.Domain.UserAuthData.Builder.aUser;

@Repository
public class UserRepository {

    private final DSLContext db;

    public UserRepository(DSLContext db) {
        this.db = db;
    }

    public Optional<UserAuthData> getAuthorizationDetailsByUsername(String username) {
        uk.co.spotistats.generated.tables.pojos.UserAuthData userAuthDataEntity =
                db.selectFrom(USER_AUTH_DATA).where(USER_AUTH_DATA.USERNAME.eq(username))
                        .fetchOneInto(uk.co.spotistats.generated.tables.pojos.UserAuthData.class);

        return Optional.ofNullable(userAuthDataEntity).map(this::mapUserAuthDataEntityToUserAuthData);
    }

    public void register(UserAuthData userAuthData) {
        db.insertInto(USER_AUTH_DATA)
                .set(USER_AUTH_DATA.USERNAME, userAuthData.username())
                .set(USER_AUTH_DATA.LAST_UPDATED, LocalDateTime.now())
                .set(USER_AUTH_DATA.ACCESS_TOKEN, userAuthData.accessToken())
                .set(USER_AUTH_DATA.REFRESH_TOKEN, userAuthData.refreshToken())
                .execute();
    }

    public UserAuthData updateUserAuthData(UserAuthData userAuthData){
        return mapUserAuthDataEntityToUserAuthData(
                Objects.requireNonNull(db.update(USER_AUTH_DATA)
                        .set(USER_AUTH_DATA.LAST_UPDATED, LocalDateTime.now())
                        .set(USER_AUTH_DATA.ACCESS_TOKEN, userAuthData.accessToken())
                        .set(USER_AUTH_DATA.REFRESH_TOKEN, userAuthData.refreshToken())
                        .where(USER_AUTH_DATA.USERNAME.eq(userAuthData.username())).returning()
                        .fetchOneInto(uk.co.spotistats.generated.tables.pojos.UserAuthData.class)));
    }

    private UserAuthData mapUserAuthDataEntityToUserAuthData(uk.co.spotistats.generated.tables.pojos.UserAuthData userAuthDataEntity) {
        return aUser()
                .withUsername(userAuthDataEntity.getUsername())
                .withLastUpdated(userAuthDataEntity.getLastUpdated())
                .withRefreshToken(userAuthDataEntity.getRefreshToken())
                .withAccessToken(userAuthDataEntity.getAccessToken())
                .build();
    }
}
