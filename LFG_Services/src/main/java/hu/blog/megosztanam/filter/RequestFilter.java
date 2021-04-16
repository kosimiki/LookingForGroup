package hu.blog.megosztanam.filter;

import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.logging.Logger;

@Component
public class RequestFilter implements Filter {

    private static final Logger LOGGER = Logger.getLogger(RequestFilter.class.getName());

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws ServletException, IOException {
        HttpServletRequest req = (HttpServletRequest) request;
        String userId = req.getHeader("userId");
        LOGGER.info("userId=" + userId + " - " + req.getMethod() + " " + req.getRequestURI());
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {

    }

}