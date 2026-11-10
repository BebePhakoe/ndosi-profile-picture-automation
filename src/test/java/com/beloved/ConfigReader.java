package com.beloved;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

/**
 * Reads the Ndosi login details without them ever being written in code.
 * 1. First it looks for environment variables (used by GitHub Actions).
 * 2. If they are not set, it reads config.properties in the project root (used on a laptop).
 */
public final class ConfigReader {

    private static final Path CONFIG_FILE = Path.of("config.properties");
    private static final Properties FILE_PROPERTIES = loadFile();

    private ConfigReader() {
    }

    public static String getEmail() {
        return get("NDOSI_EMAIL", "ndosi.email");
    }

    public static String getPassword() {
        return get("NDOSI_PASSWORD", "ndosi.password");
    }

    private static String get(String envName, String propertyKey) {
        String value = System.getenv(envName);
        if (value == null || value.isBlank()) {
            value = FILE_PROPERTIES.getProperty(propertyKey);
        }
        if (value == null || value.isBlank()) {
            throw new IllegalStateException("Missing login detail: set the environment variable "
                    + envName + " or add " + propertyKey + " to config.properties");
        }
        return value.trim();
    }

    private static Properties loadFile() {
        Properties properties = new Properties();
        if (Files.exists(CONFIG_FILE)) {
            try (InputStream input = Files.newInputStream(CONFIG_FILE)) {
                properties.load(input);
            } catch (IOException e) {
                throw new IllegalStateException("Could not read " + CONFIG_FILE.toAbsolutePath(), e);
            }
        }
        return properties;
    }
}
