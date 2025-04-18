package com.brianzolilecchesi.drone.infrastructure.integration;

import com.brianzolilecchesi.drone.domain.integration.GeoAwarenessGateway;
import com.brianzolilecchesi.drone.domain.dto.GeoZoneDTO;
import com.brianzolilecchesi.drone.domain.dto.SupportPointDTO;

import java.util.Collections;
import java.util.List;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;


public class GeoAwarenessRestClient implements GeoAwarenessGateway {

    private static final String geozone_api_url = "http://api.uspace.local/geo-awareness";
    private final RestTemplate restTemplate;

    public GeoAwarenessRestClient() {
        this.restTemplate = new RestTemplate();
    }
    
    public GeoAwarenessRestClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public List<GeoZoneDTO> getGeoZones() {
        ResponseEntity<List<GeoZoneDTO>> response = restTemplate.exchange(
            geozone_api_url + "/geozone",
            HttpMethod.GET,
            null,
            new ParameterizedTypeReference<List<GeoZoneDTO>>() {}
        );
        return response.getBody() != null ? response.getBody() : Collections.emptyList();
    }

    @Override
    public List<SupportPointDTO> getSupportPoints() {
        ResponseEntity<List<SupportPointDTO>> response = restTemplate.exchange(
            geozone_api_url + "/support-point",
            HttpMethod.GET,
            null,
            new ParameterizedTypeReference<List<SupportPointDTO>>() {}
        );
        return response.getBody() != null ? response.getBody() : Collections.emptyList();
    }
    
}
