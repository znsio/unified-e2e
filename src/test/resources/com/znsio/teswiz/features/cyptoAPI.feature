@cryptoAPI @api
Feature: Crypto-Currency data change
# CONFIG=./configs/api_local_config.properties TAG=cryptoAPI PLATFORM=api ./gradlew run

  @priceChange
  Scenario Outline: Validate price change in last 24 hrs for  crypto currency <symbol>
    Given I send GET request for crypto <symbol>
    Then price change should be less than <maxPriceChange>
    Examples:
      | symbol    | maxPriceChange |
      | "LTCUSDT" | 75             |
      | "ETHUSDT" | 200            |
      | "BNBUSDT" | 55             |
      | "XRPUSDT" | 80             |

  @priceChangePercentage
  Scenario: Validate price change percentage for BTC
    Given I send GET request for crypto "BTCUSDT"
    Then price change percentage should be less than 20
