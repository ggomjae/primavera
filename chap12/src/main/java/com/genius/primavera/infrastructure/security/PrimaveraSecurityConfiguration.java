package com.genius.primavera.infrastructure.security;

import com.genius.primavera.infrastructure.filter.PrimaveraFilter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import java.util.List;

import javax.servlet.Filter;

@Slf4j
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true, jsr250Enabled = true)
public class PrimaveraSecurityConfiguration extends WebSecurityConfigurerAdapter {

	private AuthenticationSuccessHandler successHandler = (request, response, authentication) -> log.info("success : " + request.getContextPath());
	private AuthenticationFailureHandler failureHandler = (request, response, authentication) -> log.info("failure : " + request.getContextPath());

	private final Filter ssoFilter;
	private final PrimaveraUserDetailsService primaveraUserDetailsService;

	public PrimaveraSecurityConfiguration(Filter ssoFilter, PrimaveraUserDetailsService primaveraUserDetailsService) {
		this.ssoFilter = ssoFilter;
		this.primaveraUserDetailsService = primaveraUserDetailsService;
	}

	@Override
	protected void configure(final AuthenticationManagerBuilder auth) throws Exception {
		var encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
		var genius = new User("Genius", "{bcrypt}$2a$10$7UEHLpn1r4gZY2qxiZFJ5.7wa3Hdz8IXgxUtFogy0Ac10fh7TG4V.", List.of(new SimpleGrantedAuthority("USER")));
		var marcus = new User("Marcus Tullius Cicero", "{bcrypt}$2a$10$7UEHLpn1r4gZY2qxiZFJ5.7wa3Hdz8IXgxUtFogy0Ac10fh7TG4V.", List.of(new SimpleGrantedAuthority("USER")));
		var julius = new User("Julius Caesar", "{bcrypt}$2a$10$7UEHLpn1r4gZY2qxiZFJ5.7wa3Hdz8IXgxUtFogy0Ac10fh7TG4V.", List.of(new SimpleGrantedAuthority("USER")));
		auth.inMemoryAuthentication().withUser(genius).withUser(marcus).withUser(julius);
		auth.authenticationProvider(authenticationProvider());
	}

	@Override
	public void configure(WebSecurity webSecurity) throws Exception {
		webSecurity.ignoring().antMatchers(HttpMethod.GET, "/resources/**", "/bower_components/**", "/dist/**", "/plugins/**", "/favicon.ico");
	}

	@Override
	protected void configure(final HttpSecurity http) throws Exception {
		http
				.csrf().disable()
				.authorizeRequests()
				.antMatchers("/auth/**", "/login/**", "/error").permitAll()
				.anyRequest().authenticated()
				.and()
				.addFilterBefore(new PrimaveraFilter(), UsernamePasswordAuthenticationFilter.class)
				.addFilterBefore(ssoFilter, BasicAuthenticationFilter.class)
				.formLogin()
				.usernameParameter("email")
				.passwordParameter("password")
				.loginPage("/login")
				.loginProcessingUrl("/signin")
				.successHandler(successHandler)
				.defaultSuccessUrl("/index", true)
				.failureHandler(failureHandler)
				.failureUrl("/login?error=true")
				.and()
				.logout()
				.logoutUrl("/signout")
				.deleteCookies("JSESSIONID");
	}

	@Bean
	public DaoAuthenticationProvider authenticationProvider() {
		var authProvider = new DaoAuthenticationProvider();
		authProvider.setUserDetailsService(primaveraUserDetailsService);
		return authProvider;
	}
}