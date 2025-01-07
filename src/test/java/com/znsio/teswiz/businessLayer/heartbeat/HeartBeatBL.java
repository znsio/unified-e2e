package com.znsio.teswiz.businessLayer.heartbeat;

import com.znsio.teswiz.context.TestExecutionContext;
import com.znsio.teswiz.entities.SAMPLE_TEST_CONTEXT;
import com.znsio.teswiz.exceptions.InvalidTestDataException;
import com.znsio.teswiz.runner.Runner;
import com.znsio.teswiz.tools.HeartBeat;
import com.znsio.teswiz.tools.ReportPortalLogger;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;

import static com.znsio.teswiz.tools.Wait.waitFor;
import static org.assertj.core.api.Assertions.assertThat;

public class HeartBeatBL {
    private static final Logger LOGGER = LogManager.getLogger(HeartBeatBL.class.getName());
    private final TestExecutionContext context;

    public HeartBeatBL() {
        long threadId = Thread.currentThread().getId();
        this.context = Runner.getTestExecutionContext(threadId);
    }

    public HeartBeatBL startHeatBeat(String userPersona) {
        LOGGER.info("Active thread count: " + Thread.activeCount());
        HashMap<String, HeartBeat> heartbeatMap = getHeartbeatHashMap();
        if (null != heartbeatMap.get(userPersona.toLowerCase())) {
            throw new InvalidTestDataException(String.format("HeartBeat already started for userPersona: '%s'", userPersona));
        }
        HeartBeat currentUserHeartBeat = new HeartBeat(userPersona, "https://api.open-meteo.com/v1/forecast", 5);
        heartbeatMap.put(userPersona.toLowerCase(), currentUserHeartBeat);
        Thread heartbeatThread = new Thread(currentUserHeartBeat);
        heartbeatThread.start();
        String message = String.format("HeartBeat started for '%s' with initial heartBeat count: '%d'", userPersona, currentUserHeartBeat.getHeartBeatCounter());
        ReportPortalLogger.logInfoMessage(message);
        waitFor(10);
        message = String.format("HeartBeat after 10 seconds: '%s' with initial heartBeat count: '%d'", userPersona, currentUserHeartBeat.getHeartBeatCounter());
        ReportPortalLogger.logInfoMessage(message);
        LOGGER.info("Active thread count: " + Thread.activeCount());
        return this;
    }

    private HashMap<String, HeartBeat> getHeartbeatHashMap() {
        HashMap<String, HeartBeat> heartbeatMap = null;
        if (null == context.getTestState(SAMPLE_TEST_CONTEXT.HEARTBEAT_MAP)) {
            LOGGER.info("Initialising HeartBeat hashmap");
            heartbeatMap = new HashMap<>();
            context.addTestState(SAMPLE_TEST_CONTEXT.HEARTBEAT_MAP, heartbeatMap);
        } else {
            heartbeatMap = (HashMap<String, HeartBeat>) context.getTestState(SAMPLE_TEST_CONTEXT.HEARTBEAT_MAP);
            LOGGER.info(String.format("HeartBeat hashmap already initialised and has '%d' heartbeats registered", heartbeatMap.values().size()));
        }
        return heartbeatMap;
    }

    public HeartBeatBL seeHeartBeatFor(String userPersona) {
        LOGGER.info("Active thread count: " + Thread.activeCount());
        HashMap<String, HeartBeat> heartbeatMap = getHeartbeatHashMap();
        HeartBeat heartBeat = heartbeatMap.get(userPersona.toLowerCase());
        if (null == heartBeat) {
            throw new InvalidTestDataException(String.format("HeartBeat not running for userPersona: '%s'", userPersona));
        }
        int heartBeatCounter = heartBeat.getHeartBeatCounter();
        LOGGER.info(String.format("userPersona: '%s': HeartBeat counter '%d'", userPersona, heartBeatCounter));
        assertThat(heartBeatCounter).as("HeartBeat counter is less than expected").isGreaterThan(2);
        LOGGER.info("Active thread count: " + Thread.activeCount());
        return this;
    }
}
