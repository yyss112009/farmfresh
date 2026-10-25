package com.backend.security;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Very simple in-memory session store.
 *
 * On login we generate a random token and remember which user it belongs to.
 * Every request to a protected endpoint sends that token back in the
 * "Authorization" header, and controllers ask this store who it belongs to.
 *
 * This resets whenever the backend restarts (that's fine for a college
 * project). No JWT / Spring Security filter chain needed - keeps the auth
 * flow easy to read and explain.
 */
@Component
public class TokenStore {

    private final Map<String, AuthPrincipal> tokens = new ConcurrentHashMap<>();

    public String issueToken(AuthPrincipal principal) {
        String token = UUID.randomUUID().toString();
        tokens.put(token, principal);
        return token;
    }

    public AuthPrincipal resolve(String token) {
        if (token == null) return null;
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        return tokens.get(token);
    }

    public void revoke(String token) {
        if (token == null) return;
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        tokens.remove(token);
    }
}
