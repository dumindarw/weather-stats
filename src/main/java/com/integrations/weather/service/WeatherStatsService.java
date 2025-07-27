package com.integrations.weather.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.integrations.weather.client.WeatherApiClient;
import com.integrations.weather.dto.WeatherSummery;
import com.integrations.weather.model.Error400;
import com.integrations.weather.model.ForecastForecastday;
import com.integrations.weather.model.InlineResponse2002;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
public class WeatherStatsService {

  private final WeatherApiClient weatherApiClient;

  @Value("#{'${app.weather-api.api-key}'.split('')}")
  private char[] apiKey;

  private final ObjectMapper objectMapper;

  @Autowired
  CacheManager cacheManager;

  public WeatherStatsService(WeatherApiClient client, ObjectMapper mapper) {
    this.weatherApiClient = client;
    this.objectMapper = mapper;
  }

  @Async
  public CompletableFuture<ResponseEntity<String>> fetchWeatherData(String city) throws InterruptedException {

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    String toDate = LocalDate.now().format(formatter);
    String fromDate = LocalDate.now().minusDays(6).format(formatter);

    return CompletableFuture.supplyAsync(() -> {

              String response = this.weatherApiClient.getHistoryByCity(city, fromDate, toDate, new String(this.apiKey));

              InlineResponse2002 inlineResponse = null;
              try {
                inlineResponse = this.objectMapper.readValue(response, InlineResponse2002.class);

                return ResponseEntity.ok(
                    this.generateSummery(inlineResponse.getForecast().getForecastday(), inlineResponse.getLocation().getName()));

              }
              catch (JsonProcessingException e) {
                log.error("Error processing JSON response", e);
                throw new RuntimeException(e);
              }

            }
        )
        .exceptionally(ex -> {

          log.error("Exception status ", ex);

          Error400 error400 = new Error400();
          error400.setCode(400);
          error400.setMessage(ex.getMessage());

          try {
            return ResponseEntity.badRequest().body(this.objectMapper.writeValueAsString(error400));
          }
          catch (JsonProcessingException e) {
            log.error("Error processing JSON response", e);
            throw new RuntimeException(e);
          }
        });

  }

  private String generateSummery(List<ForecastForecastday> forecastdayList, String city) throws JsonProcessingException {

    WeatherSummery weatherSummery = new WeatherSummery();

    weatherSummery.setCity(city);

    var statistics = forecastdayList.stream().mapToDouble(forecastForecastday ->
        forecastForecastday.getDay().getAvgtempC().doubleValue()
    ).summaryStatistics();

    DecimalFormat df = new DecimalFormat("0.00");

    weatherSummery.setAverageTemperature(Double.parseDouble(df.format(statistics.getAverage())));

    log.info("City : {}", city);
    log.info("Average Temperature: {}", statistics.getAverage());

    forecastdayList.stream().filter(forecastForecastday -> statistics.getMin() == forecastForecastday.getDay().getAvgtempC().doubleValue())
        .findFirst()
        .ifPresent(forecastForecastday -> weatherSummery.setColdestDay(forecastForecastday.getDate()))
    ;

    forecastdayList.stream().filter(forecastForecastday -> statistics.getMax() == forecastForecastday.getDay().getAvgtempC().doubleValue())
        .findFirst()
        .ifPresent(forecastForecastday -> weatherSummery.setHottestDay(forecastForecastday.getDate()))
    ;

    return this.objectMapper.writeValueAsString(weatherSummery);

  }

}
