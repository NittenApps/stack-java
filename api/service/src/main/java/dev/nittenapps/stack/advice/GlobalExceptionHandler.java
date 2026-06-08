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

package dev.nittenapps.stack.advice;

import dev.nittenapps.stack.api.ApiException;
import jakarta.persistence.OptimisticLockException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.time.Instant;
import java.util.NoSuchElementException;

/**
 * GlobalExceptionHandler is a centralized exception handling class that handles multiple types of exceptions in the
 * application. It extends {@link ResponseEntityExceptionHandler} to leverage default Spring's exception handling
 * mechanism, and uses the {@link RestControllerAdvice} annotation to allow cross-controller exception handling.
 * <p>
 * The class provides custom handling for specific exceptions and returns appropriate
 * HTTP response entities with proper status codes and payloads for API consumers.
 * <p>
 * Exception handlers in this class cater to the following types of exceptions:
 * <ul>
 * <li>{@link ApiException}: Custom exception handling to return meaningful error responses.</li>
 * <li>{@link AuthorizationDeniedException}: Returns HTTP 403 FORBIDDEN for unauthorized access cases.</li>
 * <li>{@link InvocationTargetException}: Handles invocation-related issues and translates the cause
 * into a proper response.</li>
 * <li>{@link NoSuchElementException}: Returns HTTP 404 NOT FOUND for requests targeting non-existent resources.</li>
 * <li>{@link OptimisticLockException}: Returns HTTP 409 CONFLICT for optimistic locking conflicts.</li>
 * <li>{@link RuntimeException}: General runtime exception handling to ensure robust error processing.</li>
 * </ul>
 * <p>
 * Each exception handler logs the error details where applicable, and formats an error
 * response body using the {@link ErrorResponse} utility class.
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
    /**
     * Handles exceptions of type ApiException and returns a structured error response.
     *
     * @param ex      the ApiException instance caught during processing
     * @param request the current web request instance
     * @return a ResponseEntity containing an ErrorResponse object with detailed error information
     */
    @ExceptionHandler(ApiException.class)
    ResponseEntity<ErrorResponse> handleApiException(ApiException ex, @NonNull WebRequest request) {
        log.error(ex.getMessage(), ex);
        return ResponseEntity.internalServerError()
                .body(ErrorResponse.builder(ex, HttpStatus.INTERNAL_SERVER_ERROR, "Error")
                        .type(URI.create("http://localhost:8080/errors/internal-error"))
                        .title("Internal Error")
                        .instance(URI.create(request.getContextPath()))
                        .detailMessageCode(ex.getApiMessage().getCode())
                        .detail(ex.getApiMessage().getMessage())
                        .property("timestamp", Instant.now())
                        .build());
    }

    /**
     * Handles exceptions of type AuthorizationDeniedException. This method is triggered when such an exception is
     * thrown, returning a response entity with the appropriate HTTP status code.
     *
     * @param ignored the AuthorizationDeniedException instance that was thrown
     * @return a ResponseEntity object with a FORBIDDEN (403) HTTP status code and no body
     */
    @ExceptionHandler(AuthorizationDeniedException.class)
    ResponseEntity<ErrorResponse> handleAuthorizationDeniedException(AuthorizationDeniedException ignored) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    /**
     * Handles InvocationTargetException thrown during method invocation and generates a meaningful response. Logs the
     * root cause of the exception and delegates handling based on the type of the underlying cause.
     *
     * @param ex      the InvocationTargetException thrown during method invocation
     * @param request the current web request during which the exception occurred
     * @return a ResponseEntity containing an ErrorResponse with details of the error, and an appropriate HTTP status
     * code
     */
    @ExceptionHandler(InvocationTargetException.class)
    ResponseEntity<ErrorResponse> handleInvocationTargetException(@NonNull InvocationTargetException ex,
                                                                  @NonNull WebRequest request) {
        Throwable cause = ex.getCause();
        log.error(cause.getMessage(), cause);
        return switch (cause) {
            case ApiException apiException -> handleRuntimeException(apiException, request);
            case OptimisticLockException optimisticLockException ->
                    handleOptimisticLockException(optimisticLockException, request);
            case RuntimeException runtimeException -> handleRuntimeException(runtimeException, request);
            default -> ResponseEntity.internalServerError()
                    .body(ErrorResponse.builder(ex, HttpStatus.INTERNAL_SERVER_ERROR, "Error")
                            .type(URI.create("http://localhost:8080/errors/internal-error"))
                            .title("Internal Error")
                            .instance(URI.create(request.getContextPath()))
                            .detailMessageCode("ERR-000")
                            .detail(cause.getMessage())
                            .property("timestamp", Instant.now())
                            .build());
        };
    }

    /**
     * Handles the {@code NoSuchElementException} exception by returning a response with HTTP status {@code NOT_FOUND}.
     *
     * @param ignored the exception object that was thrown and is being handled
     * @return a {@code ResponseEntity} with HTTP status {@code NOT_FOUND} and no body
     */
    @ExceptionHandler(NoSuchElementException.class)
    ResponseEntity<ErrorResponse> handleNoSuchElementException(NoSuchElementException ex) {
        log.debug(ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    /**
     * Handles {@code OptimisticLockException} and returns a structured error response with HTTP status {@code CONFLICT}.
     * This method is triggered when an optimistic locking conflict occurs, typically when one user attempts to update
     * a record that has been modified by another user.
     *
     * @param ex      the OptimisticLockException that was thrown during processing
     * @param request the current web request in which the exception occurred
     * @return a ResponseEntity containing an ErrorResponse object with detailed error information,
     * including the conflict details and an appropriate HTTP status code
     */
    @ExceptionHandler(OptimisticLockException.class)
    ResponseEntity<ErrorResponse> handleOptimisticLockException(@NonNull OptimisticLockException ex,
                                                                @NonNull WebRequest request) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ErrorResponse.builder(ex, HttpStatus.CONFLICT, "Error")
                        .type(URI.create("http://localhost:8080/errors/conflict"))
                        .title("Conflict")
                        .instance(URI.create(request.getContextPath()))
                        .detailMessageCode("DB-000")
                        .detail("Registro modificado por otro usuario, actualiza la información e intenta nuevamente.")
                        .property("timestamp", Instant.now())
                        .build());
    }

    /**
     * Handles RuntimeException and generates an appropriate error response.
     *
     * @param ex      the RuntimeException that was thrown
     * @param request the current web request that triggered the exception
     * @return a ResponseEntity containing an ErrorResponse with detailed error information
     */
    @ExceptionHandler(RuntimeException.class)
    ResponseEntity<ErrorResponse> handleRuntimeException(RuntimeException ex, @NonNull WebRequest request) {
        log.error(ex.getMessage(), ex);
        return ResponseEntity.internalServerError()
                .body(ErrorResponse.builder(ex, HttpStatus.INTERNAL_SERVER_ERROR, "Error")
                        .type(URI.create("http://localhost:8080/errors/internal-error"))
                        .title("Internal Error")
                        .instance(URI.create(request.getContextPath()))
                        .property("timestamp", Instant.now())
                        .build());
    }
}
