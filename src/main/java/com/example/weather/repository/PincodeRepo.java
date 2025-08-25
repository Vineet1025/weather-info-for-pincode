package com.example.weather.repository;

import com.example.weather.model.Pincode;
import org.springframework.data.jpa.repository.JpaRepository;


public interface PincodeRepo extends JpaRepository<Pincode, String> {}
