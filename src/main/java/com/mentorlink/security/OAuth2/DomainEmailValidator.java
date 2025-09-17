package com.mentorlink.security.OAuth2;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class DomainEmailValidator {

    private final String allowedDomain; // e.g., college.edu (no @)

    public DomainEmailValidator(@Value("${app.oauth.allowed-domain:*}") String allowedDomain) {
        this.allowedDomain = allowedDomain;
    }

    public boolean isAllowed(String email) {
        if (allowedDomain.equals("*")) return true;
        if (email == null || !email.contains("@")) return false;
        String domain = email.substring(email.indexOf('@') + 1).toLowerCase();
        return domain.equalsIgnoreCase(allowedDomain);
    }
}
