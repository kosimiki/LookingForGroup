package hu.blog.megosztanam.filter;

import hu.blog.megosztanam.service.IUserService;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.Optional;

@Configuration
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    private final GoogleVerifier verifier;
    private final IUserService userService;

    public SecurityConfiguration(GoogleVerifier verifier, IUserService userService) {
        this.verifier = verifier;
        this.userService = userService;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.cors().and().csrf().disable();
        http.authorizeRequests()
                .antMatchers( "/login", "/lol/**").permitAll()
                .anyRequest()
                .authenticated();

        http.addFilterBefore(new GoogleAuthFilter(verifier, userService), UsernamePasswordAuthenticationFilter.class);

    }


}
