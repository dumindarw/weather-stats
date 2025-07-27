package com.integrations.weather.client;

import com.integrations.weather.config.FeignClientConfiguration;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "weather-api", url = "${app.weather-api.base-url}", fallback = WeatherApiFallback.class, configuration = FeignClientConfiguration.class)
//@CacheConfig(cacheNames = { "weather-stats" })
public interface WeatherApiClient {

  @GetMapping(value = "/history.json")
  @Cacheable(value = "history_cache", key = "#q + '_' + #dt + '_' + #end_dt")
  String getHistoryByCity(
      @RequestParam("q") String city,
      @RequestParam("dt") String fromDate,
      @RequestParam("end_dt") String toDate,
      @RequestParam("key") String apiKey);
}
