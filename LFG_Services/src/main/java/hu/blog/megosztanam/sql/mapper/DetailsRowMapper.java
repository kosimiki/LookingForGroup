package hu.blog.megosztanam.sql.mapper;

import hu.blog.megosztanam.model.UserDetails;
import hu.blog.megosztanam.model.shared.summoner.Server;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;


/**
 * Created by Miklós on 2017. 04. 20..
 */
public class DetailsRowMapper implements RowMapper<UserDetails> {

    @Override
    public UserDetails mapRow(ResultSet rs, int rowNum) throws SQLException {
        UserDetails userDetails = new UserDetails();
        userDetails.setSummonerId(rs.getString("summoner_id"));
        userDetails.setUserId(rs.getInt("user_id"));
        Server server = Optional.ofNullable(rs.getString("region"))
                .map(r-> Server.valueOf(r.replaceAll(" ", "")))
                .orElseThrow(()-> new RuntimeException("lol server not found"));
        userDetails.setServer(server);
        return userDetails;
    }
}
