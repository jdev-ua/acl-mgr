package ua.pp.jdev.permits.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {
	@Bean
	protected PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	protected SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		// Disable CSRF and frame options to unblock H2-console.
		// Should be blocked on PROD
		http.headers().frameOptions().disable();
		http.csrf().disable();

		return http.authorizeRequests()
				// Grant Administrators with permit to Users' Management section
				.antMatchers("/users/**").hasRole("ADMIN")
				// Grant Administrators and Editors with permit to create/edit/delete ACL's data
				.antMatchers("/acls/new", "/acls/**/edit").hasAnyRole("EDITOR", "ADMIN")
				// Grant any authorized user with permit to read ACL's data
				.antMatchers("/acls", "/acls/**").authenticated()
				// Make the rest of links available for anonymous users
				.anyRequest().permitAll().and()
				.formLogin().defaultSuccessUrl("/acls").and().build();
	}
}
