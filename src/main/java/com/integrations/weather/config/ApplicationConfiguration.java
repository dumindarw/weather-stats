package com.integrations.weather.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.text.SimpleDateFormat;
import java.time.Duration;

@Configuration
@Slf4j
@EnableCaching
public class ApplicationConfiguration {

  @Bean
  @Primary
  public ObjectMapper objectMapper() {

    SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");

    return new ObjectMapper()
        .registerModule(new JavaTimeModule())
        .setDateFormat(outputFormat)
        .setSerializationInclusion(JsonInclude.Include.NON_NULL);
  }

  @Bean
  public TaskExecutor taskExecutor() {
    ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
    threadPoolTaskExecutor.setCorePoolSize(2);
    threadPoolTaskExecutor.setMaxPoolSize(6);
    threadPoolTaskExecutor.setQueueCapacity(5);
    threadPoolTaskExecutor.setThreadNamePrefix("WeatherStatsAsyncThread-");
    threadPoolTaskExecutor.setRejectedExecutionHandler(
        (r, executor1) -> log.warn("Task rejected, thread pool is full and queue is also full"));
    threadPoolTaskExecutor.initialize();
    return threadPoolTaskExecutor;
  }

  @Bean
  public CacheManager cacheManager() {
    CaffeineCacheManager cacheManager = new CaffeineCacheManager("history_cache");
    cacheManager.setCaffeine(this.caffeineCacheBuilder());
    return cacheManager;
  }

  Caffeine<Object, Object> caffeineCacheBuilder() {
    return Caffeine.newBuilder()
        .initialCapacity(100)
        .maximumSize(500)
        .expireAfterWrite(Duration.ofMinutes(5));
  }
}
