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
 * Copyright (c) 2025. NittenApps
 */

package dev.nittenapps.stack.filter;

import dev.nittenapps.stack.util.SecurityUtils;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.slf4j.MDC;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.stream.Collectors;

/**
 * MDCFilter is a servlet filter that intercepts HTTP requests and enriches the Mapped Diagnostic Context (MDC)
 * with request-specific information such as URI, HTTP method, request body, and the username of the authenticated user.
 * This facilitates improved logging and tracking of application requests by associating these details
 * with a specific thread of execution.
 * <p>
 * This filter uses SecurityUtils to retrieve the username of the currently authenticated user.
 * <p>
 * Responsibilities of MDCFilter:
 * - Add request-specific data into the MDC for use in logging.
 * - Clear the MDC context to prevent data leakage between threads.
 * <p>
 * Key values added to MDC:
 * - REQUEST_URI: The URI of the incoming HTTP request.
 * - REQUEST_METHOD: The HTTP method of the request (e.g., GET, POST).
 * - REQUEST_BODY: The body payload (if any) of the HTTP request.
 * - REQUEST_ID: A fallback request identifier when the request is not an HTTPServletRequest.
 * - REQUEST_USER: The username of the currently authenticated user.
 * <p>
 * Note:
 * - Properly catches and ensures MDC is cleared even if exceptions occur.
 * - Should be used in conjunction with thread-safe logging practices.
 */
@Component
@RequiredArgsConstructor
public class MDCFilter implements Filter {
    private final SecurityUtils securityUtils;

    /**
     * Processes incoming requests, adds relevant information to the Mapped Diagnostic Context (MDC), and ensures
     * the MDC is cleared after the request processing completes. This method intercepts the request and enriches
     * the logging context with details such as the request URI, HTTP method, body content (if applicable),
     * and the username of the currently authenticated user.
     *
     * @param request  the incoming servlet request, potentially containing HTTP-specific details. If the request is an
     *                 instance of HttpServletRequest, additional information such as URI, HTTP method, and body can be
     *                 extracted.
     * @param response the outgoing servlet response, allowing the filter to modify or pass it along in the chain.
     * @param chain    the filter chain that allows this filter to pass the request and response to the next filter or
     *                 target in the process.
     * @throws IOException      if an I/O error occurs during request or response processing.
     * @throws ServletException if a servlet-specific error occurs during request or response processing.
     */
    @Override
    public void doFilter(@NonNull ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        try {
            if (request instanceof HttpServletRequest httpRequest) {
                MDC.put("REQUEST_URI", httpRequest.getRequestURI());
                MDC.put("REQUEST_METHOD", httpRequest.getMethod());
                MDC.put("REQUEST_BODY", httpRequest.getReader().lines()
                        .collect(Collectors.joining(System.lineSeparator())));
            } else {
                MDC.put("REQUEST_ID", request.getRequestId());
            }
            MDC.put("REQUEST_USER", securityUtils.getUsername());

            chain.doFilter(request, response);
        } finally {
            MDC.clear();
        }
    }
}
