package com.integrations.weather.controller;

import com.integrations.weather.service.WeatherStatsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CompletableFuture;

@Slf4j
@RestController
public class WeatherController {

  private final WeatherStatsService weatherStatsService;

  public WeatherController(WeatherStatsService weatherStatsService) {
    this.weatherStatsService = weatherStatsService;
  }

  @GetMapping(value = "/weather", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<String> getWeatherSummeryByCity(@RequestParam String city) {

    try {
      CompletableFuture<ResponseEntity<String>> weatherData = this.weatherStatsService.fetchWeatherData(city);

      return weatherData.join();
    }
    catch (InterruptedException ex) {

      log.error("CompletionException");
    }

    return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);

  }
}
