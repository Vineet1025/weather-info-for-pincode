package com.example.weather.controller;

import com.example.weather.dto.WeatherRequest;
import com.example.weather.model.WeatherData;
import com.example.weather.service.WeatherService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.time.LocalDate;
import java.util.Map;


@RestController
@RequestMapping("/api/weather")
public class WeatherController {


    private final WeatherService weatherService;


    public WeatherController(WeatherService weatherService) {
        this.weatherService = weatherService;
    }


    @PostMapping
    public ResponseEntity<?> getWeather(@Valid @RequestBody WeatherRequest request) {
        WeatherData data = weatherService.getWeather(request.getPincode(), request.getDate());
        return ResponseEntity.ok(Map.of(
                "pincode", data.getPincode(),
                "date", data.getDate(),
                "tempC", data.getTemp(),
                "humidity", data.getHumidity(),
                "windSpeedMs", data.getWindSpeed(),
                "condition", data.getCondition()
        ));
    }


    // Simple health check
    @GetMapping("/health")
    public Map<String, Object> health() {
        return Map.of("status", "ok", "time", LocalDate.now());
    }
}