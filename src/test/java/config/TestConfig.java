package config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class TestConfig {
    private static final Properties PROPERTIES = new Properties();

    static {
        try (InputStream input = TestConfig.class.getClassLoader()
                .getResourceAsStream("config.properties")) {
            if (input == null) {
                throw new RuntimeException("Unable to find config.properties");
            }
            PROPERTIES.load(input);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load config.properties", e);
        }
    }

    public static String getProperty(String key) {
        return PROPERTIES.getProperty(key);
    }

    // Геттеры для конкретных свойств
    public static String getValidEmail() {
        return getProperty("valid.email");
    }

    public static String getValidPassword() {
        return getProperty("valid.password");
    }

    public static String getBaseUrl() {
        return getProperty("api.base.url");
    }
}
