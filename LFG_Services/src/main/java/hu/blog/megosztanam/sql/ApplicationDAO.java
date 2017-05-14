package hu.blog.megosztanam.sql;

import hu.blog.megosztanam.model.shared.Role;
import hu.blog.megosztanam.model.shared.post.PostApplyResponse;
import hu.blog.megosztanam.sql.extractor.ApplicationExtractor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Miklós on 2017. 05. 07..
 */
@Repository
public class ApplicationDAO {

    private NamedParameterJdbcTemplate template;

    private static final String INSERT = "INSERT INTO applications (user_id, post_id, role, date_of_application) values (:userId, :postId, :role, NOW())";

    private static final String QUERY = "SELECT u.summoner_id, u.user_id, u.region, a.post_id, a.role, a.date_of_application\n" +
            "FROM applications a\n" +
            "JOIN looking_for_member l ON a.post_id = l.id \n" +
            "JOIN users u ON a.user_id = u.user_id\n" +
            "WHERE l.user_id = :userId";

    @Autowired
    private ApplicationExtractor extractor;

    @Autowired
    public ApplicationDAO(JdbcTemplate simpleTemplate){
        this.template = new NamedParameterJdbcTemplate(simpleTemplate);
    }

    public int saveApplication(Integer userId, Integer postId, List<Role> roles){
        List<Application> applications = new ArrayList<>();
        roles.forEach(role -> applications.add(new Application(userId, postId, role.getValue())));

        return this.template.batchUpdate(INSERT, SqlParameterSourceUtils.createBatch(applications.toArray())).length;
    }

    public List<PostApplyResponse> getApplications(Integer userId){
        return this.template.query(QUERY, extractor);
    }


    class Application{
        private Integer userId;
        private Integer postId;
        private String role;

        public Application() {
        }

        public Application(Integer userId, Integer postId, String role) {
            this.userId = userId;
            this.postId = postId;
            this.role = role;
        }

        public Integer getUserId() {
            return userId;
        }

        public void setUserId(Integer userId) {
            this.userId = userId;
        }

        public Integer getPostId() {
            return postId;
        }

        public void setPostId(Integer postId) {
            this.postId = postId;
        }

        public String getRole() {
            return role;
        }

        public void setRole(String role) {
            this.role = role;
        }
    }
}
