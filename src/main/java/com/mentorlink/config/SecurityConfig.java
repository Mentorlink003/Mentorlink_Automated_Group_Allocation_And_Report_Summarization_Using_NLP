package com.mentorlink.config;

import com.mentorlink.common.debug.AgentDebugLog;
import com.mentorlink.security.jwt.JwtAuthFilter;
import com.mentorlink.security.OAuth2.OAuth2SuccessHandler;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    @Autowired
    private JwtAuthFilter jwtAuthFilter;

    @Autowired
    private UserDetailsService customUserDetailsService;

    @Autowired
    private OAuth2SuccessHandler oAuth2SuccessHandler;

    // #region agent log
    @PostConstruct
    void __agentInit() {
        AgentDebugLog.log("99a5a7", "pre-fix", "H1", "SecurityConfig.java:__agentInit",
                "SecurityConfig initialized", "{\"hasJwtFilter\":true,\"hasUserDetailsService\":true,\"hasOAuth2SuccessHandler\":true}");
    }
    // #endregion

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                // Disable CSRF (since using JWT)
                .csrf(AbstractHttpConfigurer::disable)

                // Authorization rules
                .authorizeHttpRequests(auth -> auth

                        // 🔓 Public endpoints
                        .requestMatchers(
                                "/api/auth/**",
                                "/oauth2/**",
                                "/login/**",
                                "/actuator/**"
                        ).permitAll()

                        // 👤 User endpoints
                        .requestMatchers("/api/users/me").authenticated()
                        .requestMatchers("/api/users/**").hasRole("ADMIN")

                        // 👨‍🏫 Faculty endpoints (list is for students too)
                        .requestMatchers("/api/faculty/list").authenticated()
                        .requestMatchers("/api/faculty/**").hasRole("FACULTY")

                        // 👑 Admin endpoints
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")

                        // 📁 Projects & Groups
                        .requestMatchers("/api/projects/**").authenticated()
                        .requestMatchers("/api/groups/**").authenticated()

                        // 📅 Deadlines (read for all)
                        .requestMatchers("/api/deadlines").authenticated()

                        // 📄 Submissions
                        .requestMatchers("/api/submissions/**").authenticated()

                        // 📢 Notifications & Chat
                        .requestMatchers("/api/notifications/**").authenticated()
                        .requestMatchers("/ws/**").permitAll()

                        // Everything else requires authentication
                        .anyRequest().authenticated()
                )

                // 🔥 Enable OAuth2 Login (GitHub)
                .oauth2Login(oauth -> oauth
                        .successHandler(oAuth2SuccessHandler)
                )

                // Stateless session (VERY IMPORTANT)
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // DAO authentication provider (for normal login)
                .authenticationProvider(authenticationProvider())

                // Add JWT filter before UsernamePasswordAuthenticationFilter
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // DAO Authentication Provider (Email + Password login)
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(customUserDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    // Authentication Manager
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    // Password Encoder
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
