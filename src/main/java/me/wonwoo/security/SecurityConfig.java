package me.wonwoo.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.vote.AffirmativeBased;
import org.springframework.security.access.vote.RoleVoter;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.access.intercept.FilterInvocationSecurityMetadataSource;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;

import java.util.ArrayList;
import java.util.List;


@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

  @Autowired
  private AuthenticationManager authenticationManager;

  @Autowired
  private UserDetailsService authenticationService;

  @Autowired
  private FilterInvocationSecurityMetadataSource filterInvocationSecurityMetadataSource;

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http
      .authorizeRequests()
      .antMatchers("/").permitAll()
      .and()
      .exceptionHandling().accessDeniedPage("/denied")
      .and()
      .formLogin()
      .loginPage("/login")
      .defaultSuccessUrl("/home")
      .and()
      .logout()
      .logoutSuccessUrl("/login")
      .and()
      .addFilter(filterSecurityInterceptor());
  }

  @Override
  protected void configure(AuthenticationManagerBuilder auth) throws Exception {
    auth.userDetailsService(authenticationService);

  }

  @Bean
  public FilterSecurityInterceptor filterSecurityInterceptor() {
    FilterSecurityInterceptor filterSecurityInterceptor = new FilterSecurityInterceptor();
    filterSecurityInterceptor.setAuthenticationManager(authenticationManager);
    filterSecurityInterceptor.setSecurityMetadataSource(filterInvocationSecurityMetadataSource);
    filterSecurityInterceptor.setAccessDecisionManager(affirmativeBased());
    return filterSecurityInterceptor;
  }

  @Bean
  public AffirmativeBased affirmativeBased() {
    List<AccessDecisionVoter<? extends Object>> accessDecisionVoters = new ArrayList<>();
    accessDecisionVoters.add(roleVoter());
    AffirmativeBased affirmativeBased = new AffirmativeBased(accessDecisionVoters);
    return affirmativeBased;
  }

  @Bean
  public RoleVoter roleVoter() {
    RoleVoter roleVoter = new RoleVoter();
    roleVoter.setRolePrefix("ROLE_");
    return roleVoter;
  }
}