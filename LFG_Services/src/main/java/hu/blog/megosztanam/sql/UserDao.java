package hu.blog.megosztanam.sql;

import hu.blog.megosztanam.model.UserIds;
import hu.blog.megosztanam.sql.mapper.IdRowMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;


/**
 * Created by Miklós on 2016. 12. 10..
 */
@Repository
public class UserDao {

    @Autowired
    private NamedParameterJdbcTemplate template;

    private static final String EXISTING_USER =
            "SELECT COUNT(*) FROM users WHERE email_address = :email_address";

    private static final String SUMMONER_ID =
            "SELECT user_id, summoner_id FROM users WHERE email_address = :email_address";
    private static final String INSERT_USER =
            "INSERT INTO users (email_address, summoner_id)" +
                    "VALUES (:email_address, :summoner_id)";

    public boolean isRegisteredUser(String email){
        return template.queryForObject(EXISTING_USER, new MapSqlParameterSource("email_address", email), Boolean.class);
    }

    public UserIds getSummonerId(String email){
        return template.queryForObject(SUMMONER_ID, new MapSqlParameterSource("email_address", email), new IdRowMapper());
    }

    public int saveUser(String email, Integer summonerId){
        SqlParameterSource parameterSource = new MapSqlParameterSource()
                .addValue("email_address", email)
                .addValue("summoner_id", summonerId);
        return template.update(INSERT_USER,parameterSource);
    }


}
