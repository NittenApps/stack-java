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

package dev.nittenapps.stack.files.service;

import dev.nittenapps.stack.api.ApiResponse;
import dev.nittenapps.stack.api.ObjectBody;
import dev.nittenapps.stack.files.api.StorageService;
import dev.nittenapps.stack.files.util.EncryptionUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.coyote.BadRequestException;
import org.apache.tika.Tika;
import org.apache.tika.mime.MimeTypeException;
import org.apache.tika.mime.MimeTypes;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URI;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController("filesController")
@RequestMapping(value = "/files/v1", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Files", description = "Files storage operations")
@Slf4j
public class Controller {
    private final ApplicationContext context;

    private final StorageService storageService;

    public Controller(ApplicationContext context, @Qualifier("filesystemStorage") StorageService storageService) {
        this.context = context;
        this.storageService = storageService;
    }

    /**
     * Serves a file from the storage as a downloadable or inline resource based on the provided request URI.
     *
     * @param request The HTTP request object which contains the URI for identifying the file to be served.
     * @return A ResponseEntity containing the requested resource as a body and appropriate HTTP headers for content
     * type and content disposition. If the resource is not found, it returns a 404 Not Found response.
     * @throws IOException       If an I/O error occurs during file handling.
     * @throws MimeTypeException If there is an error determining the MIME type of the file.
     */
    @GetMapping("/**")
    @Operation(summary = "Serves a file from the storage as a downloadable or inline resource based on the provided "
            + "request URI.")
    public ResponseEntity<Resource> serveFile(@Parameter(hidden = true) HttpServletRequest request)
            throws IOException, MimeTypeException {
        String path = StringUtils.substringAfter(request.getRequestURI(), "/files/v1/");
        try {
            path = EncryptionUtils.decrypt(path);
        } catch (Exception ignored) {
        }

        StorageService storageService = this.storageService;
        if (StringUtils.contains(path, ":")) {
            storageService = context.getBean(StringUtils.substringBefore(path, ":") + "Service", StorageService.class);
            path = StringUtils.substringAfter(path, ":");
        }

        Resource resource = storageService.loadAsResource(path);
        if (resource == null) {
            return ResponseEntity.notFound().build();
        }

        String contentType;
        String filename;
        if (StringUtils.isNotBlank(resource.getFilename())) {
            filename = resource.getFilename();
            contentType = URLConnection.getFileNameMap().getContentTypeFor(filename);
        } else {
            Tika tika = new Tika();
            contentType = tika.detect(resource.getInputStream());
            filename = path + "." + MimeTypes.getDefaultMimeTypes().forName(contentType).getExtension();
        }
        log.debug("contentType: {}, filename: {}", contentType, filename);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, contentType)
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + filename + "\"")
                .body(resource);
    }

    /**
     * Handles the upload of a file, validating its content type against the provided filename.
     *
     * @param filename the name of the file being uploaded
     * @param file     the file to be uploaded
     * @return a ResponseEntity containing a map of metadata about the uploaded file, including the file name, file
     * extension, file path, and success status
     * @throws IOException if an error occurs while reading the file or storing it
     */
    @PostMapping
    @Operation(summary = "Handles the upload of a file, validating its content type against the provided filename.")
    @Parameters({
            @Parameter(name = "filename", required = true, description = "The name of the file being uploaded"),
            @Parameter(name = "file", required = true, description = "The file to be uploaded")
    })
    public ResponseEntity<ApiResponse<ObjectBody<Map<String, Object>>>> uploadFile(
            @RequestParam("filename") String filename,
            @RequestParam("file") MultipartFile file) throws IOException {
        // Validate the file extension corresponds to the content
        String filenameType = URLConnection.getFileNameMap().getContentTypeFor(filename);
        Tika tika = new Tika();
        String contentType = tika.detect(file.getInputStream());
        if (!StringUtils.equals(contentType, filenameType)) {
            log.warn("File content does not match the file name: {} != {}", contentType, filenameType);
            throw new BadRequestException("File content does not match the file name");
        }

        String newFilename = String.format("F_%s.%s", UUID.randomUUID(), FilenameUtils.getExtension(filename));
        String path = storageService.store(file, newFilename);
        Map<String, Object> result = new HashMap<>();
        result.put("stringValue", filename);
        result.put("codeValue", FilenameUtils.getExtension(path));
        result.put("textValue", path);
        return ResponseEntity.created(URI.create(path)).body(new ApiResponse<>(new ObjectBody<>(result), null));
    }
}
