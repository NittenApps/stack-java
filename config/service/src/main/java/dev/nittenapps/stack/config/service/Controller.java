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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/config/v1", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@Tag(name = "Config", description = "Configuration operations")
public class Controller {
    private final ConfigService configService;

    @PostMapping("/catalog-values")
    @Operation(summary = "Saves a CatalogValue")
    public ResponseEntity<ApiResponse<ObjectBody<CatalogValueDto>>> saveCatalogValue(
            @RequestBody CatalogValueDto catalogValueDto) {
        configService.saveCatalogValue(catalogValueDto);
        return ResponseEntity.ok(new ApiResponse<>(new ObjectBody<>(null), null));
    }

    @GetMapping("/catalog-values/{catalogCode}")
    @Operation(summary = "Retrieves the catalog values")
    public ResponseEntity<ApiResponse<ListBody<CatalogValueDto>>> catalogValues(
            @PathVariable("catalogCode") String catalogCode,
            @RequestParam MultiValueMap<String, String> params) {
        return ResponseEntity.ok(new ApiResponse<>(new ListBody<>(configService.getCatalogValues(catalogCode, params)),
                null));
    }

    @GetMapping("/catalogs")
    @Operation(summary = "Retrieves the list of active catalogs")
    public ResponseEntity<ApiResponse<ListBody<CatalogListDto>>> catalogs() {
        return ResponseEntity.ok(new ApiResponse<>(new ListBody<>(configService.getCatalogs()), null));
    }

    @GetMapping("/parameters/{paramCode}")
    @Operation(summary = "Retrieves the parameter value")
    public ResponseEntity<ApiResponse<ObjectBody<AttributeValueDto>>> parameter(@PathVariable String paramCode) {
        AttributeValue value = configService.getParameterValue("__DEFAULT__", paramCode);
        return ResponseEntity.ok(new ApiResponse<>(new ObjectBody<>(new AttributeValueDto(null, value.getCodeValue(),
                value.getStringValue(), value.getNumberValue(), value.getDateValue(), value.getBooleanValue(),
                value.getTextValue(), null)), null));
    }
}
