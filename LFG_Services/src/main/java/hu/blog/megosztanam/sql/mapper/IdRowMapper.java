package hu.blog.megosztanam.sql.mapper;

import hu.blog.megosztanam.model.UserIds;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;


/**
 * Created by Miklós on 2017. 04. 20..
 */
public class IdRowMapper implements RowMapper<UserIds> {

    @Override
    public UserIds mapRow(ResultSet rs, int rowNum) throws SQLException {
        UserIds userIds = new UserIds();
        userIds.setSummonerId(rs.getInt("summoner_id"));
        userIds.setUserId(rs.getInt("user_id"));
        return userIds;
    }
}
