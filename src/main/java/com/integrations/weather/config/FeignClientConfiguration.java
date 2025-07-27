package com.integrations.weather.config;

import com.integrations.weather.decorders.WetherApiFeignErrorDecoder;
import feign.codec.ErrorDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignClientConfiguration {

  @Bean
  public ErrorDecoder customFeignErrorDecoder() {
    return new WetherApiFeignErrorDecoder();
  }
}
