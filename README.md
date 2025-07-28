## Weather Summery using https://www.weatherapi.com/docs/ REST API

### External weather API format

```shell
[GET] https://api.weatherapi.com/v1/history.json?key=eb6d5b0db12844d8b5331916252707&q=London&dt=2025-07-20&end_dt=2025-07-27
```

### API to get weather summary

```shell
curl --location 'http://localhost:8080/weather?city=Helsinki'
```

### Example response

```json
{
  "city": "Helsinki",
  "averageTemperature": 20.49,
  "hottestDay": "2025-07-24",
  "coldestDay": "2025-07-28"
}
```