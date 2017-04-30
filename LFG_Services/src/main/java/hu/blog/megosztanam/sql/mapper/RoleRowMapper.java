package hu.blog.megosztanam.sql.mapper;


import hu.blog.megosztanam.model.shared.Role;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by Miklós on 2017. 04. 20..
 */
public class RoleRowMapper implements RowMapper<Role> {
    @Override
    public Role mapRow(ResultSet rs, int rowNum) throws SQLException {
        return Role.valueOf(rs.getString("role"));
    }
}
