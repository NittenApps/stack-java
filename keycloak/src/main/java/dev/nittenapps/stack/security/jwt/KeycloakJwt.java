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

import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.io.Serial;
import java.io.Serializable;
import java.util.Collection;
import java.util.Map;

@Getter @Setter
@ToString(callSuper = true)
public class KeycloakJwt extends JwtAuthenticationToken {
    @Serial private static final long serialVersionUID = -851442547430174234L;

    public KeycloakJwt(Jwt jwt, Collection<? extends GrantedAuthority> authorities) {
        super(jwt, authorities);
    }

    @Getter
    @NoArgsConstructor @AllArgsConstructor
    @ToString
    public static class Details implements Serializable {
        @Serial private static final long serialVersionUID = -730342037299382458L;

        private String name;
        Map<String, Object> claims;
    }
}
