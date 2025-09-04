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

package dev.nittenapps.stack.spring.boot.autoconfigure;

import dev.nittenapps.stack.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Configuration
@ConditionalOnWebApplication
@EnableJpaAuditing
@RequiredArgsConstructor
@Slf4j
public class AutoConfiguration {
    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection") private final SecurityUtils securityUtils;

    @Configuration
    @ConditionalOnClass(dev.nittenapps.stack.activity.service.Controller.class)
    @ComponentScan(basePackages = "dev.nittenapps.stack.activity.service")
    protected static class ActivityControllerConfiguration {
    }
}
