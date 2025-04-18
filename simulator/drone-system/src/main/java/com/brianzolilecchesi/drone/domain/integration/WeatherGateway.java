package com.brianzolilecchesi.drone.domain.integration;

import com.brianzolilecchesi.drone.domain.dto.RainCellDTO;
import java.util.List;

public interface WeatherGateway {
    
    List<RainCellDTO> getWeather();
}
