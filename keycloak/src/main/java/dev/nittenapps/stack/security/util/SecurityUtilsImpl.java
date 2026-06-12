/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * Copyright (c) 2024. NittenApps
 */

package dev.nittenapps.stack.security.util;

import dev.nittenapps.stack.security.jwt.KeycloakJwt;
import dev.nittenapps.stack.util.SecurityUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth2.core.oidc.StandardClaimNames;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

/**
 * Implementation of the SecurityUtils interface that provides utility methods for accessing security-related
 * information in the currently authenticated context. This implementation supports Keycloak JWT-based authentication
 * and username-password-based authentication.
 */
@Service
public class SecurityUtilsImpl implements SecurityUtils {
    /**
     * Retrieves the full name of the currently authenticated user based on the authentication type.
     * <p>
     * If the user is authenticated through Keycloak JWT, the full name is derived from the token attributes.
     * <p>
     * If the user is authenticated through a username-password authentication token, the username is returned.
     * <p>
     * For anonymous or unauthenticated users, a default "[ANONYMOUS]" string is returned.
     *
     * @return the full name of the authenticated user, or "[ANONYMOUS]" if the user is not authenticated
     */
    @Override
    public String getFullName() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication instanceof KeycloakJwt keycloakJwt) {
            return getFullName(keycloakJwt);
        } else if (authentication instanceof UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken) {
            return usernamePasswordAuthenticationToken.getName();
        }
        return "[ANONYMOUS]";
    }

    /**
     * Retrieves the claims associated with the currently authenticated user.
     * <p>
     * If the user is authenticated using a Keycloak JWT, this method extracts the claims from the KeycloakJwt. Details
     * object associated with the authentication token.
     * <p>
     * If the user is not authenticated or if the authentication token is not an instance of KeycloakJwt, an empty map
     * is returned.
     *
     * @return a map containing the claims of the authenticated user, or an empty map if the user is not authenticated
     * or the authentication token does not include claims.
     */
    @Override
    public Map<String, Object> getUserClaims() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication instanceof KeycloakJwt keycloakJwt) {
            return ((KeycloakJwt.Details)keycloakJwt.getDetails()).getClaims();
        }
        return Collections.emptyMap();
    }

    /**
     * Retrieves the authenticated user's details based on the current security context.
     * <p>
     * If the user is authenticated using a Keycloak JWT, this method constructs and returns a User object with the
     * username, a protected password placeholder, and the user's authorities derived from the JWT.
     * <p>
     * If the user is authenticated using a UsernamePasswordAuthenticationToken, this method returns the User object
     * stored in the authentication's details.
     * <p>
     * If the user is not authenticated or the authentication type is unsupported, this method returns null.
     *
     * @return an instance of {@code User} containing the authenticated user's details, or {@code null} if no
     * authenticated user is found or the authentication type is unsupported.
     */
    @Override
    public User getUserDetails() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication instanceof KeycloakJwt keycloakJwt) {
            return Optional.of(keycloakJwt)
                    .map(jwt -> new User(getUsername(jwt), "[PROTECTED]", jwt.getAuthorities()))
                    .orElse(null);
        } else if (authentication instanceof UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken) {
            return (User)usernamePasswordAuthenticationToken.getDetails();
        }
        return null;
    }

    /**
     * Retrieves the username of the currently authenticated user.
     * <p>
     * If the user is authenticated using a Keycloak JWT, the username is extracted from the token's attributes.
     * <p>
     * If the user is authenticated using a UsernamePasswordAuthenticationToken, the username is derived from the
     * authentication token's name.
     * <p>
     * If no authentication is present or the authentication type is unsupported, the method returns "[ANONYMOUS]".
     *
     * @return the username of the authenticated user, or "[ANONYMOUS]" if the user is not authenticated.
     */
    @Override
    public String getUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication instanceof KeycloakJwt keycloakJwt) {
            return getUsername(keycloakJwt);
        } else if (authentication instanceof UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken) {
            return usernamePasswordAuthenticationToken.getName();
        }
        return "[ANONYMOUS]";
    }

    /**
     * Extracts the full name of the user from the provided Keycloak JWT token.
     * <p>
     * The method retrieves the token attributes from the KeycloakJwt instance, extracts the name claim
     * (StandardClaimNames.NAME), and validates that it is not blank. If the name claim is not present or blank,
     * the method returns null.
     *
     * @param keycloakJwt the Keycloak JWT token, which provides authentication and user details, may be null in which
     *                    case null is returned.
     * @return the full name of the user if present and not blank in the token attributes, otherwise null.
     */
    private String getFullName(KeycloakJwt keycloakJwt) {
        return Optional.ofNullable(keycloakJwt)
                .map(JwtAuthenticationToken::getTokenAttributes)
                .map(attributes -> MapUtils.getString(attributes, StandardClaimNames.NAME))
                .filter(StringUtils::isNotBlank)
                .orElse(null);
    }

    /**
     * Retrieves the username from the provided KeycloakJwt object.
     *
     * @param keycloakJwt the KeycloakJwt object that contains the token attributes
     * @return the username if present and non-blank; null otherwise
     */
    private String getUsername(KeycloakJwt keycloakJwt) {
        return Optional.ofNullable(keycloakJwt)
                .map(JwtAuthenticationToken::getTokenAttributes)
                .map(attributes -> MapUtils.getString(attributes, StandardClaimNames.PREFERRED_USERNAME))
                .filter(StringUtils::isNotBlank)
                .orElse(null);
    }
}
