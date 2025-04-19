package com.brianzolilecchesi.drone.infrastructure.integration;

import com.brianzolilecchesi.drone.domain.dto.RainCellDTO;
import com.brianzolilecchesi.drone.domain.integration.WeatherGateway;

import java.util.Collections;
import java.util.List;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public class WeatherServiceRestClient implements WeatherGateway {
    private final RestTemplate restTemplate;
    private final String weatherApiUrl = "http://api.uspace.local/weather/weather";

    public WeatherServiceRestClient() {
        this.restTemplate = new RestTemplate();
    }

    public WeatherServiceRestClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public List<RainCellDTO> getWeather() {
        System.out.println("Call to weather API");
        ResponseEntity<RainCellDTO[]> response = restTemplate.exchange(
            weatherApiUrl + "/rain-cell",
            HttpMethod.GET,
            null,
            RainCellDTO[].class
        );
        return response.getBody() != null ? List.of(response.getBody()) : Collections.emptyList();
    }
}

