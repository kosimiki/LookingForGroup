package hu.blog.megosztanam.dependency;

import hu.blog.megosztanam.rest.ILFGService;

public interface BackendServiceDependency {

    ILFGService getLfgService();
}
