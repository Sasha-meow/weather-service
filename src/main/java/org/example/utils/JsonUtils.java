package org.example.utils;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

/**
 * Утилитарный класс для работы с JSON данными.
 * Предоставляет методы для валидации и десериализации JSON-строки
 */
public class JsonUtils<T> {
    /**
     * Проверяет JSON-строку на корректность и возможность корректного парсинга
     */
    private boolean isValidJson(String jsonString, Class<T> type) {
        if (jsonString == null || jsonString.trim().isEmpty()) {
            return false;
        }

        try {
            new Gson().fromJson(jsonString, type);
            return true;
        } catch (JsonSyntaxException error) {
            return false;
        }
    }

    /**
     * Десериализует объект в переданный класс, проверяя предварительно на корректность
     */
    public T convertToObject(String jsonString, Class<T> type) throws JsonSyntaxException {
        if (isValidJson(jsonString, type)) {
            return new Gson().fromJson(jsonString, type);
        } else {
            throw new JsonSyntaxException("Некорректные JSON данные!");
        }
    }
}
