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

package dev.nittenapps.stack.files.api;

import org.springframework.core.io.Resource;
import org.springframework.lang.NonNull;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;

/**
 * Interface for a storage service that provides methods for file management, including storing, retrieving, and
 * deleting files. This service is intended to abstract the underlying storage mechanism.
 */
public interface StorageService {
    /**
     * Deletes all files and directories in the storage managed by the implementing service. This is intended to clear
     * all stored data, leaving the storage empty. Typically used for cleanup or resetting the storage state.
     * <p>
     * Implementations of this method should handle potential errors or exceptions that may arise due to file access
     * issues or permissions.
     */
    void deleteAll();

    /**
     * Deletes a file with the specified filename from the storage managed by the implementing service. If the file does
     * not exist, an appropriate action should be taken, such as throwing an exception or silently ignoring the request,
     * depending on the implementation.
     *
     * @param filename the name of the file to be deleted, must not be null
     */
    void delete(@NonNull String filename);

    /**
     * Initializes the storage service. This method is responsible for setting up the necessary resources,
     * configurations, or directory structures required for the storage system to operate. It should be called before
     * performing any operations with the storage service.
     * <p>
     * Implementations may use this method to create directories, verify configurations, or perform any other setup
     * operations. If the initialization fails, appropriate exceptions should be thrown to indicate an error in the
     * setup process.
     */
    void init();

    /**
     * Loads a file from the storage system based on the provided filename.
     *
     * @param filename the name of the file to load, must not be null
     * @return the Path to the file corresponding to the specified filename
     */
    Path load(@NonNull String filename);

    /**
     * Loads a file as a {@link Resource} from the storage system based on the provided filename.
     *
     * @param filename the name of the file to load, must not be null
     * @return the file as a Resource, or an appropriate representation indicating the file could not be found or loaded
     */
    Resource loadAsResource(@NonNull String filename);

    /**
     * Stores the given file with the specified filename in the storage system. This method allows saving a file,
     * typically by uploading, with a custom name provided by the user or the calling application.
     *
     * @param file the file to be stored, must not be null
     * @param filename the name to store the file as, may be null if the implementation allows default naming
     * @return the name or path of the stored file as a String
     */
    String store(@NonNull MultipartFile file, String filename);

    /**
     * Stores the specified file in the given directory with the provided filename. This method saves the uploaded file
     * into the storage system, organizing it into the specified directory. If a filename is provided, the stored file
     * will use that name; otherwise, additional logic may dictate naming conventions.
     *
     * @param file the file to be stored, must not be null
     * @param dir the directory in which to store the file, may be null or empty if the implementation allows default
     *            directories
     * @param filename the name to store the file as, may be null or empty if the implementation allows default naming
     *                 conventions
     * @return the name or full path of the stored file as a String
     */
    String store(@NonNull MultipartFile file, String dir, String filename);

    /**
     * Stores the given content as a file with the specified filename in the storage system. The content is saved to a
     * storage location managed by the class, and the stored file can later be retrieved or accessed using its filename.
     *
     * @param content the byte array representing the content to be stored, must not be null
     * @param filename the name to assign to the stored file*/
    String store(@NonNull byte[] content, @NonNull String filename);
}
