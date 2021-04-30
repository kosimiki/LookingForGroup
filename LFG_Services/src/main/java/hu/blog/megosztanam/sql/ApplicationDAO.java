package hu.blog.megosztanam.sql;

import hu.blog.megosztanam.model.shared.Role;
import hu.blog.megosztanam.model.shared.post.PostApplyResponse;
import hu.blog.megosztanam.sql.extractor.ApplicationExtractor;
import hu.blog.megosztanam.sql.model.Application;
import hu.blog.megosztanam.sql.model.RoleObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.*;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by Miklós on 2017. 05. 07..
 */
@Repository
public class ApplicationDAO {

    private static final String INSERT = "INSERT INTO applications (user_id, post_id) values (:userId, :postId)";

    private static final String QUERY = "SELECT u.summoner_id, u.user_id, u.region, a.post_id, p.role, a.accepted, a.date_of_application, l.*\n" +
            "FROM applications a\n" +
            "JOIN positions p on a.id = p.application_id " +
            "JOIN posts l ON a.post_id = l.id \n" +
            "JOIN users u ON a.user_id = u.user_id\n" +
            "WHERE l.user_id = :userId " +
            "ORDER BY a.date_of_application DESC";

    private static final String APPLICATIONS_OF_USER = "SELECT u.summoner_id, u.user_id, u.region, a.post_id, p.role, a.date_of_application, a.accepted, l.*\n" +
            "FROM applications a\n" +
            "JOIN positions p on a.id = p.application_id " +
            "JOIN posts l ON a.post_id = l.id \n" +
            "JOIN users u ON a.user_id = u.user_id\n" +
            "WHERE a.user_id = :userId " +
            "ORDER BY a.date_of_application DESC";
    private static final String INSERT_ROLE = "INSERT INTO positions (application_id, role) VALUES (:applicationId, :role)";


    private final ApplicationExtractor extractor;
    private final NamedParameterJdbcTemplate template;


    @Autowired
    public ApplicationDAO(ApplicationExtractor extractor, JdbcTemplate simpleTemplate) {
        this.extractor = extractor;
        this.template = new NamedParameterJdbcTemplate(simpleTemplate);
    }

    @Transactional
    public void saveApplication(Integer userId, Integer postId, List<Role> roles) {
        Application application = new Application(userId, postId);
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        template.update(INSERT, new BeanPropertySqlParameterSource(application), keyHolder);
        int applicationId = (int) keyHolder.getKeys().get("id");

        SqlParameterSource[] roleParameters = SqlParameterSourceUtils
                .createBatch(roles.stream()
                        .map(role -> new RoleObject(applicationId, role.name()))
                        .toArray());
        template.batchUpdate(INSERT_ROLE, roleParameters);
    }

    public List<PostApplyResponse> getApplicationsByPostOwner(Integer userId) {
        return this.template.query(QUERY, new MapSqlParameterSource("userId", userId), extractor);
    }

    public List<PostApplyResponse> getApplicationsByApplicant(Integer userId) {
        return this.template.query(APPLICATIONS_OF_USER, new MapSqlParameterSource("userId", userId), extractor);
    }

    @Transactional
    public void deleteApplicationsOfUser(Integer userId) {
        MapSqlParameterSource idParameter = new MapSqlParameterSource("userId", userId);
        List<Integer> applicationIds = template.queryForList("SELECT id FROM applications " +
                "WHERE user_id = :userId", idParameter, Integer.class);
        if (!applicationIds.isEmpty()) {
            template.update("DELETE FROM positions WHERE application_id in (:ids)",
                    new MapSqlParameterSource("ids", applicationIds));
        }
        this.template.update("DELETE FROM applications WHERE user_id = :userId",
                idParameter);
    }

    @Transactional
    public void deleteApplications(Integer postId) {
        MapSqlParameterSource parameters = new MapSqlParameterSource("postId", postId);
        List<Integer> applicationIds = template.queryForList("SELECT id FROM applications " +
                "WHERE post_id = :postId", parameters, Integer.class);
        if (!applicationIds.isEmpty()) {
            template.update("DELETE FROM positions WHERE application_id in (:ids)",
                    new MapSqlParameterSource("ids", applicationIds));
        }

        template.update("DELETE FROM applications WHERE post_id = :postId",
                parameters);
    }

    @Transactional
    public void deleteApplication(Integer postId, Integer applicantUserId) {
        MapSqlParameterSource parameters = new MapSqlParameterSource("applicantUserId", applicantUserId);
        parameters.addValue("postId", postId);
        int applicationId = template.queryForObject("SELECT id FROM applications " +
                "WHERE user_id = :applicantUserId and post_id = :postId", parameters, Integer.class);
        template.update("DELETE FROM positions WHERE application_id = :id",
                new MapSqlParameterSource("id", applicationId));
        template.update("DELETE FROM applications WHERE user_id = :applicantUserId and post_id = :postId",
                parameters);
    }

    public void acceptApplication(Integer postId, Integer applicantUserId) {
        MapSqlParameterSource parameters = new MapSqlParameterSource("applicantUserId", applicantUserId);
        parameters.addValue("postId", postId);
        template.update("UPDATE applications set accepted = true WHERE user_id = :applicantUserId and post_id = :postId",
                parameters);
    }
}
