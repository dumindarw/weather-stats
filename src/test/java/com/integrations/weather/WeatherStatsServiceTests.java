package com.integrations.weather;

import com.integrations.weather.controller.WeatherController;
import com.integrations.weather.service.WeatherStatsService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@SpringBootTest
class WeatherStatsServiceTests {

  @Mock
  private WeatherStatsService weatherStatsService;

  @InjectMocks
  private WeatherController weatherController;

  @Test
  @DisplayName("Returns weather summary for valid city")
  void returnsWeatherSummaryForValidCity() throws Exception {
    String city = "London";
    String weatherSummeryJson = """
        {
            "city": "London",
            "averageTemperature": 18.97,
            "hottestDay": "2025-07-25",
            "coldestDay": "2025-07-24"
        }
        """;
    when(this.weatherStatsService.fetchWeatherData(city))
        .thenReturn(CompletableFuture.completedFuture(ResponseEntity.ok(weatherSummeryJson)));

    CompletableFuture<ResponseEntity<String>> result = this.weatherController.getWeatherSummeryByCity(city);
    assertThat(result.get().getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(result.get().getBody()).contains("averageTemperature");
  }

  @Test
  @DisplayName("Handles wrong city parameter gracefully")
  void handlesInvalidCityParameterGracefully() throws Exception {
    String city = "16161345566";
    when(this.weatherStatsService.fetchWeatherData(city))
        .thenReturn(CompletableFuture.completedFuture(ResponseEntity.badRequest().body("""
            {"code": 400, "message": "java.lang.IllegalArgumentException: Invalid request: {"error":{"code":1006,"message":"No matching location found."}}"}
            """)));

    CompletableFuture<ResponseEntity<String>> result = this.weatherController.getWeatherSummeryByCity(city);
    assertThat(result.get().getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    assertThat(result.get().getBody()).contains("No matching location found.");
  }

}
