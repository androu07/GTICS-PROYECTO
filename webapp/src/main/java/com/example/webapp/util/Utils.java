package com.example.webapp.util;

import java.util.Collection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

@Component
public class Utils {

    private final Logger log = LoggerFactory.getLogger(getClass());



    public String obtenerUsername(Authentication auth) {
        if (auth != null && auth.isAuthenticated()) {
            Object principal = auth.getPrincipal();
            if (principal instanceof User) {
                log.info("username: " + (((User) principal).getUsername()));
                return ((User) principal).getUsername();
            } else {
                return principal.toString();
            }
        }
        return null;
    }
}
