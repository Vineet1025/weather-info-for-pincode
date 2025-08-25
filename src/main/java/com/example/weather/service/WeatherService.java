package com.example.weather.service;

import com.example.weather.model.Pincode;
import com.example.weather.model.WeatherData;
import com.example.weather.repository.WeatherRepo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;


@Service
public class WeatherService {


    private final WeatherRepo weatherRepo;
    private final PincodeService pincodeService;
    private final RestTemplate restTemplate;


    @Value("${app.openweather.api-key}")
    private String apiKey;


    public WeatherService(WeatherRepo weatherRepo, PincodeService pincodeService, RestTemplate restTemplate) {
        this.weatherRepo = weatherRepo;
        this.pincodeService = pincodeService;
        this.restTemplate = restTemplate;
    }


    /**
     * Fetch weather for a pincode & date. If already stored for that date, return cached value.
     * @param pincode
     * @param date
     * @return
     */
    public WeatherData getWeather(String pincode, LocalDate date) {
        LocalDate targetDate = (date == null) ? LocalDate.now() : date;


        Optional<WeatherData> cached = weatherRepo.findByPincodeAndDate(pincode, targetDate);
        if (cached.isPresent()) return cached.get();


// Resolve pincode to lat/lon (cached in DB)
        Pincode pin = pincodeService.resolve(pincode);


// Fetch current weather from OpenWeather
        String url = "https://api.openweathermap.org/data/2.5/weather?lat=" + pin.getLatitude()
                + "&lon=" + pin.getLongitude() + "&appid=" + apiKey + "&units=metric";
        try {
            Map body = restTemplate.getForObject(url, Map.class);
            if (body == null) throw new IllegalStateException("Empty weather response");


            Map main = (Map) body.get("main");
            List weatherArr = (List) body.get("weather");
            Map wind = (Map) body.get("wind");


            double temp = main != null && main.get("temp") != null ? ((Number) main.get("temp")).doubleValue() : 0.0;
            int humidity = main != null && main.get("humidity") != null ? ((Number) main.get("humidity")).intValue() : 0;
            double windSpeed = wind != null && wind.get("speed") != null ? ((Number) wind.get("speed")).doubleValue() : 0.0;
            String condition = (weatherArr != null && !weatherArr.isEmpty()) ? (String) ((Map) weatherArr.get(0)).get("description") : "unknown";


            WeatherData saved = weatherRepo.save(new WeatherData(
                    null, pincode, targetDate, temp, humidity, windSpeed, condition
            ));
            return saved;
        } catch (RestClientException ex) {
            throw new IllegalArgumentException("Failed to fetch weather: " + ex.getMessage());
        }
    }
}