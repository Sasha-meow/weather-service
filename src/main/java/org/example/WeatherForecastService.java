package org.example;

import com.google.gson.JsonSyntaxException;
import org.example.model.QueryPojo;
import org.example.model.WeatherResponse;
import org.example.utils.ConfigReader;
import org.example.utils.JsonUtils;
import org.example.utils.SimpleHttpClient;

import java.io.IOException;
import java.util.Locale;
import java.util.Scanner;

/**
 * Основной сервис для работы с метеорологическими данными
 */
public class WeatherForecastService {
    private static final String[] headers = {"X-Yandex-Weather-Key", ""};
    private static final Scanner scanner = new Scanner(System.in);
    private static final QueryPojo query = new QueryPojo();
    private static WeatherResponse response;

    public static void main(String[] args) {
        loadConfig();
        getQueryParams();
        getWeatherForecast();
        scanner.close();
    }

    /**
     * Загружает ключ X-Yandex-Weather-Key из конфига проекта.
     * Выбрасывает ошибку, если ключ не найден (он является обязательным для доступа к API)
     */
    private static void loadConfig() {
        ConfigReader configReader = new ConfigReader();
        try {
            configReader.load();
            String key = configReader.getProperties().getProperty("key");
            if (key != null) {
                headers[1] = key;
            } else {
                throw new IOException("Ключ не найден!");
            }
        } catch (IOException error) {
            System.out.println("Возникла ошибка при чтении файла конфигураций: " + error.getMessage());
            System.exit(0);
        }
    }

    /**
     * Считывает query-параметры для запроса данных.
     * Предоставляет два варианта: 1 - прогноз погоды для Москвы, 2 - кастомный прогноз погоды на основе введенных пользователем координат
     */
    private static void getQueryParams() {
        System.out.println("Добро пожаловать в сервис метеорологических данных!");
        System.out.println("Если хотите узнать погоду в Москве, введите 1. Если хотите задать свои координаты - введите 2.");

        boolean isInputCorrect = false;

        while (!isInputCorrect) {
            if (scanner.hasNextInt()) {
                int action = scanner.nextInt();
                if (action == 1) {
                    isInputCorrect = true;
                    getLimit();
                } else if (action == 2) {
                    isInputCorrect = true;
                    getCoordinates();
                    getLimit();
                } else {
                    scanner.next();
                }
            } else {
                scanner.next();
            }
        }
    }

    /**
     * Считывает query-параметр limit для запроса данных
     * Проверяет, что параметр не отрицательный
     */
    private static void getLimit() {
        System.out.print("Введите лимит: ");
        boolean isInputCorrect = false;
        while (!isInputCorrect) {
            if (scanner.hasNextInt()) {
                int limit = scanner.nextInt();
                if (limit > 0) {
                    isInputCorrect = true;
                    query.setLimit(limit);
                } else {
                    scanner.next();
                }
            } else {
                scanner.next();
            }
        }
    }

    /**
     * Считывает query-параметры координат для запроса данных
     * Проверяет, что параметры заданы корректно (интервалы взяты из документации Яндекса)
     */
    private static void getCoordinates() {
        System.out.print("Введите широту: ");
        boolean isLatitudeCorrect = false;
        while (!isLatitudeCorrect) {
            if (scanner.hasNextDouble()) {
                double latitude = scanner.nextDouble();
                if (latitude >= -90.0 && latitude <= 90.0) {
                    isLatitudeCorrect = true;
                    query.setLatitude(latitude);
                } else {
                    scanner.next();
                }
            } else {
                scanner.next();
            }
        }

        System.out.print("Введите долготу: ");
        boolean isLongitudeCorrect = false;
        while (!isLongitudeCorrect) {
            if (scanner.hasNextDouble()) {
                double longitude = scanner.nextDouble();
                if (longitude >= -180.0 && longitude <= 180.0) {
                    isLongitudeCorrect = true;
                    query.setLongitude(longitude);
                } else {
                    scanner.next();
                }
            } else {
                scanner.next();
            }
        }
    }

    /**
     * Запрашивает информацию о погоде по заранее введенным данным, обрабатывая при этом исключения.
     * Выводит полученный body, температуру и среднюю температуру
     */
    private static void getWeatherForecast() {
        SimpleHttpClient httpClientService = new SimpleHttpClient();
        String url = String.format(Locale.US, "https://api.weather.yandex.ru/v2/forecast?lat=%f&lon=%f&limit=%d", query.getLatitude(), query.getLongitude(), query.getLimit());

        try {
            String jsonString = httpClientService.get(url, headers);
            System.out.println(jsonString);

            JsonUtils<WeatherResponse> jsonUtils = new JsonUtils<>();
            response = jsonUtils.convertToObject(jsonString, WeatherResponse.class);

            getTemperature();
            getAverageTemperature();
        } catch (IOException | InterruptedException | JsonSyntaxException error) {
            System.out.println("Произошла ошибка при запросе: " + error.getMessage());
        }
    }

    /**
     * Выводит температуру с проверкой ее присутствия в полученных данных
     */
    private static void getTemperature() {
        String currentTemp = response.getCurrentTemperature() != null ? response.getCurrentTemperature() : "Неизвестно";
        System.out.println("Температура: " + currentTemp + "°C");
    }

    /**
     * Высчитывает среднюю температуру с проверкой ее присутствия в полученных данных
     */
    private static void getAverageTemperature() {
        int averageTemperature = 0;
        // Проверка на случай, если данных в массиве пришло меньше запрошенного лимита (всякое бывает)
        int correctLength = Math.min(response.getForecastCount(), query.getLimit());
        int currentLength = correctLength;

        for (int i = 0; i < correctLength; i++) {
            Integer tempAvg = response.getAverageForecastTemperature(i);
            // Проверка на наличие средней температуры в текущем элементе: если температуры нет, конечное число элементов уменьшаем, в сумму элемент не прибавляем
            if (tempAvg != null) {
                averageTemperature += tempAvg;
            } else {
                currentLength--;
            }
        }

        // Проверка деления на нуль (всякое бывает)
        System.out.println("Средняя температура по прогнозу: " + (currentLength != 0 ? roundIntToOneDecimal(averageTemperature / currentLength) : "Неизвестно") + "°C");
    }

    /**
     * Приводит int число в double с одним знаком после запятой (пусть средняя погода будет точнее)
     */
    private static double roundIntToOneDecimal(int value) {
        return Math.round(((double) value) * 10.0) / 10.0;
    }
}
