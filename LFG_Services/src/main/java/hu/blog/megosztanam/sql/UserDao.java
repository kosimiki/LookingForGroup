package hu.blog.megosztanam.sql;

import hu.blog.megosztanam.model.UserDetails;
import hu.blog.megosztanam.model.shared.summoner.Server;
import hu.blog.megosztanam.sql.mapper.DetailsRowMapper;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import java.util.Optional;


/**
 * Created by Miklós on 2016. 12. 10..
 */
@Repository
public class UserDao {

    private final NamedParameterJdbcTemplate template;
    private final SimpleJdbcInsert insert;

    public UserDao(JdbcTemplate simpleTemplate) {
        this.insert = new SimpleJdbcInsert(simpleTemplate).withTableName("users").usingGeneratedKeyColumns("user_id");
        this.template = new NamedParameterJdbcTemplate(simpleTemplate);
    }

    private static final String EXISTING_USER =
            "SELECT COUNT(*) FROM users WHERE google_id = :google_id";

    private static final String SUMMONER_ID =
            "SELECT user_id, summoner_id, region FROM users WHERE google_id = :google_id";

    private static final String QUERY_SUMMONER =
            "SELECT user_id, summoner_id, region FROM users WHERE user_id = :userId";

    private static final String SAVE_TOKEN = "UPDATE users SET firebase_id = :token WHERE user_id = :userId";

    public boolean isRegisteredUser(String googleId) {
        return template.queryForObject(EXISTING_USER, new MapSqlParameterSource("google_id", googleId), Boolean.class);
    }

    public UserDetails getSummonerId(String googleId) {
        return template.queryForObject(SUMMONER_ID, new MapSqlParameterSource("google_id", googleId), new DetailsRowMapper());
    }

    public UserDetails getSummoner(Integer userId) {
        return template.queryForObject(QUERY_SUMMONER, new MapSqlParameterSource("userId", userId), new DetailsRowMapper());
    }

    public String getFirebaseId(Integer userId) {
        String firebaseId = template.queryForObject("SELECT firebase_id FROM users WHERE user_id = :userId", new MapSqlParameterSource("userId", userId), String.class);
        return firebaseId != null ? firebaseId.replaceAll(" ", "") : null;
    }

    public void saveMessagingToken(Integer userId, String token) {
        SqlParameterSource parameterSource = new MapSqlParameterSource()
                .addValue("token", token)
                .addValue("userId", userId);
        template.update(SAVE_TOKEN, parameterSource);
    }

    public int saveUser(String googleId, String summonerId, Server server) {
        SqlParameterSource parameterSource = new MapSqlParameterSource()
                .addValue("google_id", googleId)
                .addValue("summoner_id", summonerId)
                .addValue("region", server.getValue());
        return insert.executeAndReturnKey(parameterSource).intValue();
    }


    public UserDetails getSummonerId(Integer userId) {
        return template.queryForObject("SELECT user_id, summoner_id, region FROM users WHERE user_id = :userId",
                new MapSqlParameterSource("userId", userId), new DetailsRowMapper());

    }

    public Optional<Integer> getUserByGoogleId(String googleId) {
        try {
            Integer userId = template.queryForObject("SELECT user_id FROM users where google_id = :googleId",
                    new MapSqlParameterSource("googleId", googleId), Integer.class);
            return Optional.of(userId);
        } catch (EmptyResultDataAccessException ex) {
            return Optional.empty();
        }
    }
}
