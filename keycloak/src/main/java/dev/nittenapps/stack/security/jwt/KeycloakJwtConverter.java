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

package dev.nittenapps.stack.security.jwt;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
public class KeycloakJwtConverter implements Converter<Jwt, KeycloakJwt> {
    @Override
    public KeycloakJwt convert(@NonNull Jwt source) {
        log.trace("Converting {} to {}", source, KeycloakJwt.class);
        log.trace("Claims: {}", source.getClaims());
        Collection<GrantedAuthority> grantedAuthorities = extractAuthorities(source);
        var keycloakJwt = new KeycloakJwt(source, grantedAuthorities);
        keycloakJwt.setDetails(new KeycloakJwt.Details(source.getClaimAsString("name")));
        return keycloakJwt;
    }

    private Collection<GrantedAuthority> extractAuthorities(@NonNull Jwt source) {
        return Optional.ofNullable(source.getClaimAsMap("realm_access"))
                .map(realmAccess -> realmAccess.get("roles"))
                .filter(roles -> roles instanceof Collection<?>)
                .map(roles -> ((Collection<?>)roles).stream().map(role -> new SimpleGrantedAuthority("role_" + role))
                        .map(authority -> (GrantedAuthority)authority)
                        .collect(Collectors.toSet()))
                .orElse(Collections.emptySet());
    }
}
