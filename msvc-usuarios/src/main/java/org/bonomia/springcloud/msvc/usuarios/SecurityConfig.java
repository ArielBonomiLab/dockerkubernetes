package org.bonomia.springcloud.msvc.usuarios;

import static org.springframework.security.config.Customizer.withDefaults;

import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
@EnableWebSecurity
public class SecurityConfig {
	
	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		/*
		 * http.authorizeRequests() .antMatchers("/authorized").permitAll()
		 * .antMatchers(HttpMethod.GET, "/").hasAnyAuthority("SCOPE_read","SCOPE_write")
		 * .antMatchers(HttpMethod.POST, "/").hasAuthority("SCOPE_wirte")
		 * .antMatchers(HttpMethod.PUT, "/{id}").hasAuthority("SCOPE_wirte")
		 * .antMatchers(HttpMethod.DELETE, "/{id}").hasAuthority("SCOPE_wirte")
		 * .anyRequest().authenticated() .and()
		 * .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
		 * .and() .oauth2Login(oauth2Login ->
		 * oauth2Login.loginPage("/ouath/authorization/msvc-usuarios-client"))
		 * .oauth2Client(withDefaults()) .oauth2ResourceServer().jwt();
		 */
        http.authorizeRequests(requests -> requests
                .antMatchers("/authorized").permitAll()
                .antMatchers(HttpMethod.GET, "/").hasAnyAuthority("SCOPE_read", "SCOPE_write")
                .antMatchers(HttpMethod.POST, "/").hasAuthority("SCOPE_wirte")
                .antMatchers(HttpMethod.PUT, "/{id}").hasAuthority("SCOPE_wirte")
                .antMatchers(HttpMethod.DELETE, "/{id}").hasAuthority("SCOPE_wirte")
                .anyRequest().authenticated())
                .sessionManagement(management -> management.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .oauth2Login(oauth2Login -> oauth2Login.loginPage("/ouath/authorization/msvc-usuarios-client"))
                .oauth2Client(withDefaults())
                .oauth2ResourceServer(server -> server.jwt());
		return http.build();
	}
	
	
}
