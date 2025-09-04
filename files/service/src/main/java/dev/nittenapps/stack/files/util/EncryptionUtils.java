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

package dev.nittenapps.stack.files.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

@Slf4j
public class EncryptionUtils {
    private static final SecretKey SECRET_KEY;

    static {
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
            keyGenerator.init(128);
            SECRET_KEY = keyGenerator.generateKey();
        } catch (NoSuchAlgorithmException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    /**
     * Encrypts the provided string using AES encryption and encodes the result as a Base64 URL safe string without
     * padding.
     *
     * @param string the input string to encrypt; must not be null
     * @return the encrypted and encoded string; never null
     * @throws Exception if any error occurs during the encryption process
     */
    @NonNull
    public static String encrypt(@NonNull String string) throws Exception {
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, SECRET_KEY);
        byte[] encrypted = cipher.doFinal(string.getBytes());
        return Base64.getUrlEncoder().withoutPadding().encodeToString(encrypted);
    }

    /**
     * Decrypts the provided Base64 URL safe encoded string using AES decryption.
     *
     * @param string the encrypted and Base64 URL safe encoded string to be decrypted; must not be null
     * @return the decrypted string in its original form; never null
     * @throws Exception if any error occurs during the decryption process
     */
    @NonNull
    public static String decrypt(@NonNull String string) throws Exception {
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, SECRET_KEY);
        byte[] decrypted = cipher.doFinal(Base64.getUrlDecoder().decode(string));
        return new String(decrypted);
    }
}
