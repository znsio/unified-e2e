@weatherAPI @api
Feature: Weather data based on location Coordinates
# CONFIG=./configs/api_local_config.properties TAG=weatherAPI PLATFORM=api ./gradlew run

  @temperature
  Scenario: Validate current temperature for location - New Delhi
    Given I send GET request with location coordinates
    Then temperature of that location should be in range 0 and 55 C

  @invalidDays
  Scenario: Verify we are unable to fetch temperature for more than 16 days
    Given I send GET request with location coordinates and invalid forecast days
    Then error message should be "Forecast days is invalid. Allowed range 0 to 16."

  @windSpeed
  Scenario Outline: Validate wind speed for <city>
    Given I send GET request with valid location coordinates <latitude> and <longitude>
    Then wind speed of that location should be in range 0 and 90 kmph
    Examples:
      | latitude    | longitude   | city    |
      | "18.987807" | "72.836447" | Mumbai  |
      | "28.651952" | "77.231495" | Delhi   |
      | "22.562627" | "88.363044" | Kolkata |
      | "13.084622" | "80.248357" | Chennai |
