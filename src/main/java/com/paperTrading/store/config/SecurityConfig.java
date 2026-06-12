package com.paperTrading.store.config;

import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.paperTrading.store.security.JwtFilter;

//import com.paperTrading.store.security.JwtFilter;

//import com.papertrading.security.CustomAuthenticationEntryPoint;
//import com.papertrading.security.JwtFilter;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtFilter jwtFilter;
//    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
        	.cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(csrf -> csrf.disable())
            .formLogin(form -> form.disable())
            .httpBasic(basic -> basic.disable())
            .authorizeHttpRequests(auth -> auth
            		
//            	    .requestMatchers("/paper/**").permitAll()
//            	    .requestMatchers("/store/**").permitAll()

            		
        		// Allow static frontend pages
            		// have to check and remove
        	    .requestMatchers("/static/profile/**").permitAll()	
        	    .requestMatchers(
        	            "/websocket.html",
        	            "/index.html",
        	            "/",
        	            "/css/**",
        	            "/js/**",
        	            "/images/**",
        	            "/static/**",
        	            "/payment.html",
        	            "login.html"
        	        ).permitAll()
                // Users endpoints
                .requestMatchers(
                		"/analytics/login-hit",
                    "/api/users/register/one",
                    "/api/users/register/two",
                    "/api/users/login",
                    "/api/users/send-otp",
                    "/api/users/verify-otp",
                    "/api/users/forget-password",
                    "/api/users/save",
                    "/ws/**",
                    "/api/holidays/**",
                    "/api/market/**",
                    "/create-order",
                    "/create-order",
                    "/api/users/login-google",
                    "/api/users/login-otp"
                ).permitAll()
                .requestMatchers("/api/users/logout").authenticated()

                // Kite endpoints
                .requestMatchers(
                		"/api/kite/**",
                        "/api/live-price/**",
                        "/api/live-prices",
                        "/api/access-token",
                        "/api/instruments/**",
                        "/getLiveprice/**",
                        "/api/orders/**",
                        "/api/funds/**"
                   
                ).permitAll()
            
                // Uncomment this if you want all other endpoints protected
                .anyRequest().authenticated()
            )
//            .exceptionHandling(ex -> ex
//                    .authenticationEntryPoint(customAuthenticationEntryPoint)
//                )
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

//        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
    
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.addAllowedOrigin("*"); // or specify "http://localhost:5500" etc.
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(false); // true only if you specify a specific origin
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

 // ✅ Add this to ignore static resources completely
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring()
            .requestMatchers("/profile/**", "/css/**", "/js/**", "/images/**");
    }
}
