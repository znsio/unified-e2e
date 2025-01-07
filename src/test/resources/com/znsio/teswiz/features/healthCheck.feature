@api @healthcheck
Feature: Health check feature

    # CONFIG=./configs/api_local_config.properties TAG=healthcheck PLATFORM=api ./gradlew run
  Scenario: Print username and current dir
    Given I print my username
    Then I see my current directory

  Scenario: Print current platform
    Given I print the current platform
