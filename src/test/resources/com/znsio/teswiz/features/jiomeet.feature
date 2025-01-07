@jiomeet @prod @inMeeting
Feature: In a meeting scenarios

#  CONFIG=./configs/jio/jiomeet_local_config.properties TAG="@test" PLATFORM=android ./gradlew run
#  CONFIG=./configs/jio/jiomeet_local_config.properties TAG="@jiomeet and @single-user" PLATFORM=android ./gradlew run
#  CONFIG=./configs/jio/jiomeet_local_config.properties TAG="@jiomeet and @single-user" PLATFORM=web ./gradlew run
#  IS_VISUAL=false CONFIG=./configs/jio/jiomeet_local_config.properties TAG="@jiomeet and @single-user" PLATFORM=electron ./gradlew run
#  IS_VISUAL=false CONFIG=./configs/jio/jiomeet_pcloudy_config.properties TAG="@jiomeet and @test" CLOUD_USERNAME=$PCLOUDY_CLOUD_USERNAME CLOUD_KEY=$PCLOUDY_CLOUD_KEY PLATFORM=android ./gradlew run
  @android @web @single-user @electron @test
  Scenario: User should be able to change the mic settings
    Given I sign in as a registered "Host"
    And I start an instant meeting
    When I Unmute myself
    Then I should be able to Mute myself

#  CONFIG=./configs/jio/jiomeet_local_config.properties TAG="@jiomeet and @multiuser-android-web and @single-app" ./gradlew run
  @multiuser-android-web @single-app
  Scenario: Guest (on Web) and host (on Android) can chat in a meeting
    Given "Host" logs-in and starts an instant meeting on "android"
    And "Guest" joins the meeting from "web"
    Then "Host" should be able to get to chat window
    When "Host" sends "Hey" chat message
    Then "Guest" should see the chat message on its chat window

#  CONFIG=./configs/jio/jiomeet_local_config.properties TAG="@jiomeet and @multiuser-android and @single-app" ./gradlew run
  @multiuser-android @single-app
  Scenario: Guest (on android) and host (on Android) can chat in a meeting
    Given "Host" logs-in and starts an instant meeting on "android"
    And "Guest" joins the meeting from "android"
    Then "Host" should be able to get to chat window
    When "Host" sends "Hey" chat message
    Then "Guest" should see the chat message on its chat window

#  CONFIG=./configs/jio/jiomeet_local_config.properties TAG="@jiomeet and @multiuser-android and @multi-app" ./gradlew run
  @multiuser-android @multi-app
  Scenario: Guest (on older-app-verion on android) and host (on Android) can chat in a meeting
    Given "Host" logs-in and starts an instant meeting in "jiomeetLatest" on "android"
    And "Guest" joins the meeting from "jiomeetOldVersion" on "android"
    Then "Host" should be able to get to chat window
    When "Host" sends "Hey" chat message
    Then "Guest" should see the chat message on its chat window

#  CONFIG=./configs/jio/jiomeet_local_config.properties TAG="@jiomeet and @multiuser-android-web and @multi-app" ./gradlew run
  @multiuser-android-web @multi-app @chat
  Scenario: Guest-1 (on older-app-version on android), Guest-2 (on chrome) and host (on Android) can chat in a meeting
    Given "Host" logs-in and starts an instant meeting in "jiomeetLatest" on "android"
    And "Guest-1" joins the meeting from "jiomeetOldVersion" on "android"
    And "Guest-2" joins the meeting from "jiomeet-chrome" on "web"
    Then "Host" should be able to get to chat window
    When "Host" sends "Hey" chat message
    Then "Guest-2" should see the chat message on its chat window

#  CONFIG=./configs/jio/jiomeet_local_config.properties TAG="@jiomeet and  @selectNotificationTest" PLATFORM=android ./gradlew run
  @selectNotificationTest @android
  Scenario: User should be able to view and open the JioMeet Meeting Notification
    Given "Host" logs-in and starts an instant meeting in "jiomeetLatest" on "android"
    When I open the JioMeet meeting notification from notification bar
    Then I should be able to go back to Meeting
