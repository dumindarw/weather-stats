package com.integrations.weather;

import com.integrations.weather.client.WeatherApiClient;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.test.context.TestPropertySource;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static wiremock.org.hamcrest.CoreMatchers.containsString;
import static wiremock.org.hamcrest.MatcherAssert.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EnableAutoConfiguration
@AutoConfigureWireMock(port = 0)
@TestPropertySource(properties = {
    "app.weather-api.base-url=http://localhost:${wiremock.server.port}",
    "app.weather-api.api-key=test-api-key",
})
class WeatherControllerTests {
  //
  @Autowired
  private WeatherApiClient weatherApiClient;

  @Test
  @DisplayName("Test external weather API request")
  void testExternalWeatherRequest() {

    stubFor(
        get(urlPathEqualTo("/history.json"))
            .withQueryParam("q", equalTo("Helsinki"))
            .withQueryParam("dt", equalTo("2025-07-22"))
            .withQueryParam("end_dt", equalTo("2025-07-28"))
            .withQueryParam("key", equalTo("test-api-key"))
            .willReturn(okJson("""
                {
                    "city": "Helsinki",
                    "averageTemperature": 20.49,
                    "hottestDay": "2025-07-24",
                    "coldestDay": "2025-07-28"
                }
                """)));

    String response = this.weatherApiClient.getHistoryByCity("Helsinki", "2025-07-22", "2025-07-28", "test-api-key");

    assertThat(response, containsString("averageTemperature"));

  }
}
