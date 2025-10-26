package org.example.utils;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

/**
 * Утилитарный класс для работы с API.
 * Предоставляет метод инициации GET-запроса (сделан только один, дабы в проекте требуется только он, в дальнейшем класс можно расширить)
 */
public class SimpleHttpClient {
    private final int DURATION = 10;
    private final HttpClient httpClient;

    public SimpleHttpClient() {
        this.httpClient = HttpClient.newBuilder().version(HttpClient.Version.HTTP_2).connectTimeout(Duration.ofSeconds(DURATION)).build();
    }

    /**
     * Инициирует GET-запрос по переданному url и с переданными хедерами
     * (в дальнейшем параметры можно расширять)
     */
    public String get(String url, String[] headers) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).headers(headers).timeout(Duration.ofSeconds(DURATION)).build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new IOException("Ошибка при запросе: " + response.statusCode() + " " + response.body());
        }

        return response.body();
    }
}
