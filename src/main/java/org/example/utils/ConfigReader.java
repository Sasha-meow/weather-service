package org.example.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Утилитарный класс для работы с конфигами проекта.
 * Предоставляет методы для чтения и доступа к свойствам из config.properties
 */
public class ConfigReader {
    private FileInputStream fileInputStream = null;
    private final Properties properties = new Properties();

    /**
     * Загружает данные из конфига config.properties проекта с проверкой на ошибки
     */
    public void load() throws IOException {
        try {
            fileInputStream = new FileInputStream("config.properties");
            properties.load(fileInputStream);
        } catch (IOException error) {
            System.out.println(error.getMessage());
        } finally {
            if (fileInputStream != null) {
                fileInputStream.close();
            }
        }
    }

    public Properties getProperties() {
        return properties;
    }
}
