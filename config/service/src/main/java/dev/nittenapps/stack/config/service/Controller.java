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

package dev.nittenapps.stack.config.service;

import dev.nittenapps.stack.api.ApiResponse;
import dev.nittenapps.stack.api.ListBody;
import dev.nittenapps.stack.api.ObjectBody;
import dev.nittenapps.stack.config.dto.CatalogListDto;
import dev.nittenapps.stack.config.dto.CatalogValueDto;
import dev.nittenapps.stack.config.mapper.CatalogValueMapper;
import dev.nittenapps.stack.data.domain.AbstractAttributeValue;
import dev.nittenapps.stack.data.dto.AttributeValueDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

/**
 * The Controller class provides endpoints for managing configuration-related resources, such as catalogs, catalog
 * values, and configuration parameters. It handles HTTP requests and responses, offering operations to create,
 * retrieve, and manage these resources.
 */
@RestController
@RequestMapping(value = "/config/v1", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@Tag(name = "Config", description = "Configuration operations")
public class Controller {
    private final CatalogValueMapper catalogValueMapper;

    private final ConfigService configService;

    /**
     * Saves a catalog value to the system.
     *
     * @param catalogValueDto the data transfer object containing the catalog value information to be saved
     * @return a {@code ResponseEntity} containing an {@code ApiResponse} with an {@code ObjectBody} wrapping a null
     * value, indicating the operation completed successfully
     */
    @PostMapping("/catalog-values")
    @Operation(summary = "Saves a CatalogValue",
               description = "Creates or updates a catalog value in the system.",
               parameters = @Parameter(name = "catalogValueDto",
                                       description = "The catalog value data transfer object."))
    public ResponseEntity<ApiResponse<ObjectBody<CatalogValueDto>>> saveCatalogValue(
            @RequestBody CatalogValueDto catalogValueDto) {
        configService.saveCatalogValue(catalogValueDto);
        return ResponseEntity.ok(new ApiResponse<>(new ObjectBody<>(null), null));
    }

    /**
     * Retrieves the catalog values for a specified catalog code and optional query parameters.
     *
     * @param catalogCode the unique identifier of the catalog from which to retrieve values
     * @param params      a map of optional query parameters that can be used to filter or modify the catalog value
     *                    retrieval
     * @return a {@code ResponseEntity} containing an {@code ApiResponse} with a {@code ListBody} that wraps a list
     * of {@code CatalogValueDto} objects representing the catalog values
     */
    @GetMapping("/catalog-values/{catalogCode}")
    @Operation(summary = "Retrieves the catalog values",
               description = "Retrieves the catalog values for a specified catalog code and optional query parameters.",
               parameters = {
                       @Parameter(name = "catalogCode",
                                  description = "The unique code of the catalog from which to retrieve values."),
               })
    public ResponseEntity<ApiResponse<ListBody<CatalogValueDto>>> catalogValues(
            @PathVariable String catalogCode,
            @RequestParam MultiValueMap<String, String> params) {
        return ResponseEntity.ok(new ApiResponse<>(new ListBody<>(configService.getCatalogValues(catalogCode, params)),
                null));
    }

    /**
     * Retrieves a specific catalog value by the given catalog code and code.
     *
     * @param catalogCode the unique identifier of the catalog to which the value belongs
     * @param code        the unique identifier of the catalog value to be retrieved
     * @return a {@code ResponseEntity} containing an {@code ApiResponse} with an {@code ObjectBody}
     * that wraps the {@code CatalogValueDto} retrieved, or {@code null} if not found
     */
    @GetMapping("/catalog-values/{catalogCode}/{code}")
    @Operation(summary = "Retrieves one catalog value",
               description = "Retrieves a specific catalog value by the given catalog code and code.",
               parameters = {
                       @Parameter(name = "catalogCode",
                                  description = "The unique identifier of the catalog to which the value belongs."),
                       @Parameter(name = "code",
                                  description = "The unique identifier of the catalog value to be retrieved.")
               })
    public ResponseEntity<ApiResponse<ObjectBody<CatalogValueDto>>> catalogValue(@PathVariable String catalogCode,
                                                                                 @PathVariable String code) {
        return ResponseEntity.ok(new ApiResponse<>(new ObjectBody<>(catalogValueMapper.toFullDto(configService
                .getCatalogValue(catalogCode, code).orElse(null))), null));
    }

    /**
     * Retrieves the list of active catalogs.
     *
     * @return a {@code ResponseEntity} containing an {@code ApiResponse} with a {@code ListBody} that wraps a list
     * of {@code CatalogListDto} objects representing the active catalogs in the system
     */
    @GetMapping("/catalogs")
    @Operation(summary = "Retrieves the list of active catalogs",
               description = "Retrieves the list of active catalogs.")
    public ResponseEntity<ApiResponse<ListBody<CatalogListDto>>> catalogs() {
        return ResponseEntity.ok(new ApiResponse<>(new ListBody<>(configService.getCatalogs()), null));
    }

    /**
     * Retrieves the parameter value associated with the specified parameter code.
     *
     * @param paramCode the unique identifier of the parameter whose value is to be retrieved
     * @return a {@code ResponseEntity} containing an {@code ApiResponse} with an {@code ObjectBody} wrapping
     * an {@code AttributeValueDto} object that represents the retrieved parameter value
     */
    @GetMapping("/parameters/{paramCode}")
    @Operation(summary = "Retrieves the parameter value",
               description = "Retrieves the parameter value associated with the specified parameter code.",
               parameters = {
                       @Parameter(name = "paramCode",
                                  description = "The unique identifier of the parameter whose value is to be "
                                          + "retrieved.")
               })
    public ResponseEntity<ApiResponse<ObjectBody<AttributeValueDto>>> parameter(@PathVariable String paramCode) {
        AbstractAttributeValue value = configService.getParameterValue("__DEFAULT__", paramCode);
        return ResponseEntity.ok(new ApiResponse<>(new ObjectBody<>(new AttributeValueDto(null, value.getCodeValue(),
                value.getStringValue(), value.getNumberValue(), value.getDateValue(), value.getBooleanValue(),
                value.getTextValue(), null)), null));
    }
}
