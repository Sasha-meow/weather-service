package org.example.model;

/**
 * Модель для представления данных о погоде.
 * Содержит поля из ответа на апи запроса погоды в Яндекс и методы для работы с ними
 */
public class WeatherResponse {
    private Fact fact;
    private Forecasts[] forecasts;

    public WeatherResponse() {
        this.forecasts = new Forecasts[0];
    }

    public WeatherResponse(Fact fact, Forecasts[] forecasts) {
        this.fact = fact;
        this.forecasts = forecasts != null ? forecasts.clone() : new Forecasts[0];
    }

    public String getCurrentTemperature() {
        return fact != null ? fact.getTemp() : null;
    }

    public Integer getAverageForecastTemperature(int dayIndex) {
        if (forecasts != null && dayIndex >= 0 && dayIndex < forecasts.length) {
            Forecasts forecast = forecasts[dayIndex];
            if (forecast != null && forecast.getParts() != null && forecast.getParts().getDay() != null) {
                return forecast.getParts().getDay().getTempAvg();
            }
        }
        return null;
    }

    public int getForecastCount() {
        return forecasts != null ? forecasts.length : 0;
    }

    public static class Fact {
        private String temp;

        public Fact() {
        }

        public Fact(String temp) {
            this.temp = temp;
        }

        public String getTemp() {
            return temp;
        }
    }

    public static class Forecasts {
        private Parts parts;

        public Forecasts() {
        }

        public Forecasts(Parts parts) {
            this.parts = parts;
        }

        public Parts getParts() {
            return parts;
        }
    }

    public static class Parts {
        private Day day;

        public Parts() {
        }

        public Parts(Day day) {
            this.day = day;
        }

        public Day getDay() {
            return day;
        }
    }

    public static class Day {
        private int temp_avg;

        public Day() {
        }

        public Day(int tempAvg) {
            this.temp_avg = tempAvg;
        }

        public int getTempAvg() {
            return temp_avg;
        }
    }
}