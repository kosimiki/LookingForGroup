package hu.blog.megosztanam.sql.model;

public class Application {
        private Integer userId;
        private Integer postId;

        public Application() {
        }

        public Application(Integer userId, Integer postId) {
            this.userId = userId;
            this.postId = postId;
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
    }