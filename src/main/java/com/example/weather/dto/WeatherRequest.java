package com.example.weather.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class WeatherRequest {
    @NotBlank
    @Size(min = 6, max = 6, message = "Indian pincode must be 6 digits")
    @Pattern(regexp = "^[0-9]{6}$", message = "Pincode must be numeric")
    private String pincode;
    private LocalDate date;



}
