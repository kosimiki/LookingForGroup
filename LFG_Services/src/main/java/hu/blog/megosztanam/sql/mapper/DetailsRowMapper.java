package hu.blog.megosztanam.sql.mapper;

import hu.blog.megosztanam.model.UserDetails;
import hu.blog.megosztanam.model.shared.summoner.Server;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;


/**
 * Created by Miklós on 2017. 04. 20..
 */
public class DetailsRowMapper implements RowMapper<UserDetails> {

    @Override
    public UserDetails mapRow(ResultSet rs, int rowNum) throws SQLException {
        UserDetails userDetails = new UserDetails();
        userDetails.setSummonerId(rs.getInt("summoner_id"));
        userDetails.setUserId(rs.getInt("user_id"));
        userDetails.setServer(Server.valueOf(rs.getString("region")));
        return userDetails;
    }
}
