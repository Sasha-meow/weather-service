package org.example.model;

/**
 * Модель квери-параметров запроса погоды
 * По дефолту координаты Москвы
 */
public class QueryPojo {
    private double latitude = 55.75;
    private double longitude = 37.62;
    private int limit = 3;

    public QueryPojo() {
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }
}
