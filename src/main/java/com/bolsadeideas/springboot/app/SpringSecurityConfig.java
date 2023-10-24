package com.bolsadeideas.springboot.app;

import org.springframework.security.core.userdetails.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import com.bolsadeideas.springboot.app.auth.handler.LoginSuccesHandler;

@EnableGlobalMethodSecurity(securedEnabled=true)
@Configuration
public class SpringSecurityConfig {
    
	@Autowired
	private	LoginSuccesHandler succesHandler;
	
    @Bean 
    public static BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService() throws Exception{
                
        InMemoryUserDetailsManager manager = new InMemoryUserDetailsManager();
        manager.createUser(User
                .withUsername("user")
                .password(passwordEncoder().encode("user"))
                .roles("USER")
                .build());
         manager.createUser(User
                    .withUsername("admin")
                    .password(passwordEncoder().encode("admin"))
                    .roles("ADMIN","USER")
                    .build());
        
        return manager;
    }
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests((authz) -> {
                try {
                    authz.requestMatchers("/", "/css/**", "/js/**", "/images/**", "/listar").permitAll()
//                            .requestMatchers("/uploads/**").hasAnyRole("USER")
//                            .requestMatchers("/ver/**").hasRole("USER")
//                            .requestMatchers("/factura/**").hasRole("ADMIN")
//                            .requestMatchers("/form/**").hasRole("ADMIN")
//                            .requestMatchers("/eliminar/**").hasRole("ADMIN")
                            .anyRequest().authenticated()
                            .and()
                            .formLogin(login -> login.permitAll().successHandler(succesHandler).loginPage("/login"))
                            .logout(logout -> logout.permitAll())
                    		.exceptionHandling(exception -> exception.accessDeniedPage("/error_403"));
 
                } catch (Exception e) {
                        e.printStackTrace();
                }
            });
 
        return http.build();
            
    }
}
    
