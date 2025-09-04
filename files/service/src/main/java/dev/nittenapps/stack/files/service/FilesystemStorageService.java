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

import dev.nittenapps.stack.files.api.StorageService;
import dev.nittenapps.stack.files.exception.StorageException;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.*;
import java.util.UUID;

/**
 * A service implementation that provides filesystem-based storage functionality for managing files. It enables storing,
 * retrieving, and deleting files in the specified root directory.
 * <p>
 * This service relies on the `stack.files-root` configuration property to determine the root directory for file
 * storage. The directory is initialized during the bean lifecycle and ensures subdirectory creation as needed.
 * <p>
 * Key functionalities include:
 * - Storing files from {@link MultipartFile}, byte content, and optional subdirectories.
 * - Loading files and providing them as {@link Resource}.
 * - Deleting specific files or all files within the storage location.
 * <p>
 * Methods throw {@link StorageException} for errors encountered during storage operations. The service uses strict
 * validation to avoid storing files outside the root location.
 * <p>
 * This service logs relevant information at initialization and during file operations using the SLF4J logging
 * framework.
 * <p>
 * This class is annotated with {@link Service} and must be managed within a Spring container for dependency injection
 * and lifecycle management.
 */
@Service("filesystemStorage")
@Slf4j
public class FilesystemStorageService implements StorageService {
    private final Path rootLocation;

    protected FilesystemStorageService(@Value("${stack.files-root}") String rootDir) {
        log.info("Initializing filesystem storage service in root directory: {}", rootDir);
        this.rootLocation = Paths.get(rootDir);
    }

    @Override
    public void deleteAll() {
        FileSystemUtils.deleteRecursively(rootLocation.toFile());
    }

    @Override
    public void delete(@NonNull String filename) {
        try {
            Path file = load(filename);
            Files.delete(file);
        } catch (IOException e) {
            throw new StorageException("Could not delete file: " + filename, e);
        }
    }

    @Override
    @PostConstruct
    public void init() {
        try {
            Files.createDirectories(rootLocation);
        } catch (IOException e) {
            throw new StorageException("Could not initialize filesystem storage service", e);
        }
    }

    @Override
    public Path load(@NonNull String filename) {
        return rootLocation.resolve(filename);
    }

    @Override
    public Resource loadAsResource(@NonNull String filename) {
        try {
            Path file = load(filename);
            Resource resource = new UrlResource(file.toUri());
            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new StorageException("Could not read file: " + filename);
            }
        } catch (MalformedURLException e) {
            throw new StorageException("Could not read file: " + filename, e);
        }
    }

    @Override
    public String store(@NonNull MultipartFile file, String filename) {
        return store(file, null, filename);
    }

    @Override
    public String store(@NonNull MultipartFile file, String dir, String filename) {
        try {
            if (file.isEmpty()) {
                throw new StorageException("Could not store empty file");
            }
            if (StringUtils.isBlank(filename) && StringUtils.isNotBlank(file.getOriginalFilename())) {
                filename = String.format("%s.%s", UUID.randomUUID(),
                        FilenameUtils.getExtension(file.getOriginalFilename()));
            }
            String path = StringUtils.isBlank(dir) ?  "" : dir;
            if (StringUtils.isNotBlank(path)) {
                Files.createDirectories(rootLocation.resolve(path).toAbsolutePath());
            }
            Path destination = rootLocation.resolve(path).resolve(filename).normalize().toAbsolutePath();
            try  (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, destination, StandardCopyOption.REPLACE_EXISTING);
                return path + "/" + filename;
            }
        } catch (IOException e) {
            throw new StorageException("Could not store file: " + filename, e);
        }
    }

    @Override
    public String store(@NonNull byte[] content, @NonNull String filename) {
        try {
            Path destination = rootLocation.resolve(filename).normalize().toAbsolutePath();
            if (!destination.toAbsolutePath().startsWith(rootLocation.toAbsolutePath())) {
                throw new StorageException("Cannot store file outside root location");
            }
            Files.write(destination, content, StandardOpenOption.CREATE, StandardOpenOption.WRITE);
            return filename;
        } catch (IOException e) {
            throw new StorageException("Could not store file: " + filename, e);
        }
    }
}
