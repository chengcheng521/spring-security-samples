package org.javaboy.formlogin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.ObjectPostProcessor;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configurers.RememberMeConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenBasedRememberMeServices;
import org.springframework.security.web.authentication.session.ChangeSessionIdAuthenticationStrategy;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.sql.DataSource;

/**
 * @作者 江南一点雨
 * @公众号 江南一点雨
 * @微信号 a_java_boy
 * @GitHub https://github.com/lenve
 * @博客 http://wangsong.blog.csdn.net
 * @网站 http://www.javaboy.org
 */
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    @Bean
    protected UserDetailsService userDetailsService() {

        InMemoryUserDetailsManager manager = new InMemoryUserDetailsManager();
        manager.createUser(User.withUsername("javaboy").password("{bcrypt}$2a$10$Sb1gAUH4wwazfNiqflKZve4Ubh.spJcxgHG8Cp29DeGya5zsHENqi").roles("admin").build());
        manager.createUser(User.withUsername("sang").password("{noop}123").roles("admin").build());
        manager.createUser(User.withUsername("江南一点雨").password("{MD5}{Wucj/L8wMTMzFi3oBKWsETNeXbMFaHZW9vCK9mahMHc=}4d43db282b36d7f0421498fdc693f2a2").roles("user").build());
        return manager;
    }

    @Autowired
    DataSource dataSource;
    @Bean
    JdbcTokenRepositoryImpl jdbcTokenRepository() {
        JdbcTokenRepositoryImpl jdbcTokenRepository = new JdbcTokenRepositoryImpl();
        jdbcTokenRepository.setDataSource(dataSource);
        return jdbcTokenRepository;
    }

    @Bean
    PersistentTokenBasedRememberMeServices persistentTokenBasedRememberMeServices() {
        return new PersistentTokenBasedRememberMeServices("javaboy123", userDetailsService(), jdbcTokenRepository());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/rememberme").rememberMe()
                .antMatchers("/admin").fullyAuthenticated()
                .anyRequest().authenticated()
                .and()
                .formLogin()
                .permitAll()
                .and()
                .rememberMe()
                .key("javaboy")
//                .tokenRepository(jdbcTokenRepository())
//                .and()
                .withObjectPostProcessor(new ObjectPostProcessor<RememberMeConfigurer>() {
                    @Override
                    public <O extends RememberMeConfigurer> O postProcess(O object) {
//                        object.setPublishAuthorizationSuccess(true);
//                        object.setAlwaysCreateSession(false);
                        object.rememberMeServices(persistentTokenBasedRememberMeServices());
                        return object;
                    }
                })
                .and()
                .csrf().disable();
    }
}
