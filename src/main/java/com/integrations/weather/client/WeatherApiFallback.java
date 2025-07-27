package com.integrations.weather.client;

import org.springframework.stereotype.Component;

@Component
public class WeatherApiFallback implements WeatherApiClient {

  @Override
  public String getHistoryByCity(String city, String fromDate, String toDate, String apiKey) {
    return "Service Unavailable. Please try again later.";
  }
}
