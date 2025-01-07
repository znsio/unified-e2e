package com.znsio.teswiz.runner;

import lombok.SneakyThrows;

import static com.znsio.teswiz.runner.Runner.NOT_SET;

public class PluginClI {
    private static PluginClI instance;

    private PluginClI() {
        instance = new PluginClI();
    }
    @SneakyThrows
    public static PluginClI getInstance() {
        return instance;
    }

    public boolean isCloudExecution() {
        return false;
    }

    public String getCloudName() {
        return NOT_SET;
    }
}
