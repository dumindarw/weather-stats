package com.integrations.weather.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WeatherSummery {

  private String city;

  private double averageTemperature;

  private LocalDate hottestDay;

  private LocalDate coldestDay;

}
