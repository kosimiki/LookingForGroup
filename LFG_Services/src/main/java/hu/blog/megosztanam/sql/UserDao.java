package hu.blog.megosztanam.sql;

import hu.blog.megosztanam.model.UserDetails;
import hu.blog.megosztanam.model.shared.summoner.Server;
import hu.blog.megosztanam.sql.mapper.DetailsRowMapper;
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
            "SELECT user_id, summoner_id, region FROM users WHERE email_address = :email_address";
    private static final String INSERT_USER =
            "INSERT INTO users (email_address, summoner_id, region)" +
                    "VALUES (:email_address, :summoner_id, :region)";

    public boolean isRegisteredUser(String email){
        return template.queryForObject(EXISTING_USER, new MapSqlParameterSource("email_address", email), Boolean.class);
    }

    public UserDetails getSummonerId(String email){
        return template.queryForObject(SUMMONER_ID, new MapSqlParameterSource("email_address", email), new DetailsRowMapper());
    }

    public int saveUser(String email, Integer summonerId, Server server){
        SqlParameterSource parameterSource = new MapSqlParameterSource()
                .addValue("email_address", email)
                .addValue("summoner_id", summonerId)
                .addValue("region", server.getValue());
        return template.update(INSERT_USER,parameterSource);
    }


}
