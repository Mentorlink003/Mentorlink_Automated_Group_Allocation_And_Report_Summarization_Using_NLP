package com.mentorlink.security.OAuth2;

import com.mentorlink.modules.users.UserRepository;
import com.mentorlink.modules.users.entity.User;
import com.mentorlink.common.debug.AgentDebugLog;
import com.mentorlink.security.OAuth2.DomainEmailValidator;
import com.mentorlink.security.jwt.JwtTokenProvider;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    @Autowired
    private JwtTokenProvider jwt;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DomainEmailValidator domainEmailValidator;

    // Break SecurityConfig <-> OAuth2SuccessHandler cycle by not depending on PasswordEncoder bean from SecurityConfig.
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    // #region agent log
    @PostConstruct
    void __agentInit() {
        AgentDebugLog.log("99a5a7", "pre-fix", "H1", "OAuth2SuccessHandler.java:__agentInit",
                "OAuth2SuccessHandler initialized", "{\"hasJwt\":true,\"hasUserRepo\":true,\"hasDomainValidator\":true,\"passwordEncoder\":\"localBCrypt\"}");
    }
    // #endregion

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication)
            throws IOException, ServletException {

        DefaultOAuth2User oauthUser = (DefaultOAuth2User) authentication.getPrincipal();
        Map<String, Object> attributes = oauthUser.getAttributes();
        String email = (String) attributes.get("email");

        // Ensure we have an email from the provider
        if (email == null || email.isBlank()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"Email not provided by OAuth2 provider. Ensure 'user:email' scope is configured.\"}");
            return;
        }

        // Check if email domain is allowed
        if (!domainEmailValidator.isAllowed(email)) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"Email domain is not allowed\"}");
            return;
        }

        // Find or create local User linked to this GitHub account
        User user = userRepository.findByEmail(email)
                .orElseGet(() -> createOAuthUser(email, attributes));

        // Build ROLE_ authorities list for JWT
        List<String> roles = (user.getRoles() == null || user.getRoles().isEmpty())
                ? List.of("ROLE_STUDENT")
                : user.getRoles().stream()
                    .map(role -> role.startsWith("ROLE_") ? role : "ROLE_" + role)
                    .toList();

        String token = jwt.generate(email, roles);

        response.setContentType("application/json");
        response.getWriter().write("{\"token\": \"" + token + "\"}");
    }

    /**
     * Create a new local User for an OAuth2-authenticated user (default STUDENT; Admin/Faculty set via Excel upload).
     */
    private User createOAuthUser(String email, Map<String, Object> attributes) {
        Object nameAttr = attributes.get("name");
        Object loginAttr = attributes.get("login");

        String fullName = nameAttr != null && !nameAttr.toString().isBlank()
                ? nameAttr.toString()
                : (loginAttr != null ? loginAttr.toString() : email);

        // Random, unusable password – login is always via OAuth2 (no local login)
        String randomPassword = "oauth2-user-" + UUID.randomUUID();

        User user = new User();
        user.setEmail(email);
        user.setFullName(fullName);
        user.setPassword(passwordEncoder.encode(randomPassword));

        user.getRoles().add("STUDENT");

        return userRepository.save(user);
    }
}
