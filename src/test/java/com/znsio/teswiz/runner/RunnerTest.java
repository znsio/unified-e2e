package com.znsio.teswiz.runner;

import org.assertj.core.api.Assertions;
import org.testng.annotations.Test;

class RunnerTest {

    // running passing test suite - failedScenarios==0, exit 0
    @Test
    void getStatusForPassingSuite_NoFailedScenarios_SomePassedScenarios() {
        byte status = Runner.getStatus(false, 10, 10, 100, 0);
        Assertions.assertThat(status).isEqualTo((byte) 0);
    }

    @Test
    void getStatusForPassingSuiteWith_FailedScenarios_SomePassedScenarios() {
        byte status = Runner.getStatus(false, 10, 10, 100, 10);
        Assertions.assertThat(status).isEqualTo((byte) 1);
    }

    @Test
    void getStatusForPassingSuite_InCorrectNumberOfFailedScenarios_SomePassedScenarios() {
        byte status = Runner.getStatus(false, 10, 10, 100, 3);
        Assertions.assertThat(status).isEqualTo((byte) 1);
    }

    @Test
    void getStatusForPassingSuiteNo_FailedScenarios_NoPassedScenarios() {
        byte status = Runner.getStatus(false, 10, 10, 0, 0);
        Assertions.assertThat(status).isEqualTo((byte) 0);
    }

    @Test
    void getStatusForPassingSuiteWith_FailedScenarios_NoPassedScenarios() {
        byte status = Runner.getStatus(false, 10, 10, 0, 10);
        Assertions.assertThat(status).isEqualTo((byte) 1);
    }

    @Test
    void getStatusForPassingSuite_InCorrectNumberOfFailedScenarios_NoPassedScenarios() {
        byte status = Runner.getStatus(false, 10, 10, 0, 3);
        Assertions.assertThat(status).isEqualTo((byte) 1);
    }

    // running failing test suite - passedScenarios==0, exit 0
    @Test
    void getStatusForFailingSuite_NoPassedScenarios_SomeFailedScenarios() {
        byte status = Runner.getStatus(true, 10, 10, 0, 100);
        Assertions.assertThat(status).isEqualTo((byte) 0);
    }

    @Test
    void getStatusForFailingSuiteWith_PassedScenarios_SomeFailedScenarios() {
        byte status = Runner.getStatus(true, 10, 10, 10, 100);
        Assertions.assertThat(status).isEqualTo((byte) 1);
    }

    @Test
    void getStatusForFailingSuite_InCorrectNumberOfPassedScenarios_SomeFailedScenarios() {
        byte status = Runner.getStatus(true, 10, 10, 4, 100);
        Assertions.assertThat(status).isEqualTo((byte) 1);
    }

    @Test
    void getStatusForFailingSuiteNo_PassedScenarios_NoFailedScenarios() {
        byte status = Runner.getStatus(true, 10, 10, 0, 0);
        Assertions.assertThat(status).isEqualTo((byte) 0);
    }

    @Test
    void getStatusForFailingSuiteWith_PassedScenarios_NoFailedScenarios() {
        byte status = Runner.getStatus(true, 10, 10, 10, 0);
        Assertions.assertThat(status).isEqualTo((byte) 1);
    }

    @Test
    void getStatusForFailingSuite_InCorrectNumberOfPassedScenarios_NoFailedScenarios() {
        byte status = Runner.getStatus(true, 10, 10, 3, 0);
        Assertions.assertThat(status).isEqualTo((byte) 1);
    }

}
