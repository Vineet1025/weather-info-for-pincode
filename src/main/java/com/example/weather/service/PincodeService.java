package com.example.weather.service;

import com.example.weather.model.Pincode;
import com.example.weather.repository.PincodeRepo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;


import java.util.Map;


@Service
public class PincodeService {


    private final RestTemplate restTemplate;
    private final PincodeRepo pincodeRepo;


    @Value("${app.openweather.api-key}")
    private String apiKey;


    public PincodeService(RestTemplate restTemplate, PincodeRepo pincodeRepo) {
        this.restTemplate = restTemplate;
        this.pincodeRepo = pincodeRepo;
    }


    /**
     * Resolve Indian pincode to lat/lon using OpenWeather Geo ZIP API.
     * Caches the result in the database for reuse.
     * @param pincode
     * @return
     */
    public Pincode resolve(String pincode) {
        return pincodeRepo.findById(pincode).orElseGet(() -> {
            try {
                String url = "https://api.openweathermap.org/geo/1.0/zip?zip=" + pincode + ",IN&appid=" + apiKey;
                Map body = restTemplate.getForObject(url, Map.class);
                if (body == null || !body.containsKey("lat") || !body.containsKey("lon")) {
                    throw new IllegalArgumentException("Invalid response from geocoding API");
                }
                double lat = ((Number) body.get("lat")).doubleValue();
                double lon = ((Number) body.get("lon")).doubleValue();
                Pincode entity = new Pincode(pincode, lat, lon);
                return pincodeRepo.save(entity);
            } catch (RestClientException ex) {
                throw new IllegalArgumentException("Failed to geocode pincode " + pincode + ": " + ex.getMessage());
            }
        });
    }
}