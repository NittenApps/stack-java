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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth2.core.oidc.StandardClaimNames;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service
public class SecurityUtilsImpl implements SecurityUtils {
    @Override
    public Map<String, Object> getUserClaims() {
        return ((KeycloakJwt.Details)SecurityContextHolder.getContext().getAuthentication().getDetails()).getClaims();
    }

    @Override
    public User getUserDetails() {
        return Optional.ofNullable((KeycloakJwt)SecurityContextHolder.getContext().getAuthentication())
                .map(jwt -> new User(getUsername(jwt), "[PROTECTED]", jwt.getAuthorities()))
                .orElse(null);
    }

    @Override
    public String getUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication instanceof JwtAuthenticationToken) {
            return getUsername((KeycloakJwt)authentication);
        }
            return "[ANONYMOUS]";
    }

    private String getUsername(KeycloakJwt keycloakJwt) {
        return Optional.ofNullable(keycloakJwt)
                .map(JwtAuthenticationToken::getTokenAttributes)
                .map(attributes -> MapUtils.getString(attributes, StandardClaimNames.PREFERRED_USERNAME))
                .filter(StringUtils::isNotBlank)
                .orElse(null);
    }
}
