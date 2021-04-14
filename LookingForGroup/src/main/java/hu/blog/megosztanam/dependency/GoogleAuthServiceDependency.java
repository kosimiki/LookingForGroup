package hu.blog.megosztanam.dependency;

import hu.blog.megosztanam.login.GoogleAuthService;

public interface GoogleAuthServiceDependency {

    GoogleAuthService getAuthService();
}
