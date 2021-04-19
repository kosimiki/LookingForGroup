package hu.blog.megosztanam.filter;

import hu.blog.megosztanam.model.AuthenticatedUser;
import hu.blog.megosztanam.model.shared.User;
import hu.blog.megosztanam.service.IUserService;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;
import java.util.logging.Logger;

public class GoogleAuthFilter extends OncePerRequestFilter {

    private static final Logger LOGGER = Logger.getLogger(GoogleAuthFilter.class.getName());
    private final GoogleVerifier verifier;
    private final IUserService userService;

    public GoogleAuthFilter(GoogleVerifier verifier, IUserService userService) {
        this.verifier = verifier;
        this.userService = userService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        LOGGER.info(request.getMethod() + " " + request.getServletPath());
        Optional<User> user = verifier.parseToken(request.getHeader("Authorization"));
        if (user.isPresent()) {
            String email = user.get().getEmail();
            Optional<Integer> userIdByEmail = userService.getUserIdByEmail(email);
            if (userIdByEmail.isPresent()) {
                AuthenticatedUser userDetails = getUserDetails(email, userIdByEmail.get());
                UsernamePasswordAuthenticationToken
                        authentication = new UsernamePasswordAuthenticationToken(
                        userDetails, null,
                        userDetails.getAuthorities()
                );
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }

        filterChain.doFilter(request, response);
    }

    public static AuthenticatedUser getUserDetails(String email, int userId) {
        return new AuthenticatedUser(email, userId);
    }


}
