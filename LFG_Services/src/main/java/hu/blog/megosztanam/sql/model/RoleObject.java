package hu.blog.megosztanam.sql.model;

public class RoleObject {
    private Integer applicationId;
    private String role;

    public RoleObject(Integer applicationId, String role) {
        this.applicationId = applicationId;
        this.role = role;
    }

    public Integer getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(Integer applicationId) {
        this.applicationId = applicationId;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
