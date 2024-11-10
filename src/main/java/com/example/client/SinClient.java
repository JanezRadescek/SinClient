package com.example.client;

import com.example.client.dtos.Msg;
import com.example.client.dtos.MsgType;
import com.example.client.dtos.Task;
import com.example.client.id.IdClient;
import com.example.client.strategy.StrategyClient;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.util.Random;
import java.util.UUID;

@ApplicationScoped
public class SinClient {

    @Inject
    SinSocket sinSocket;

    @RestClient
    StrategyClient strategyClient;

    @RestClient
    IdClient idClient;

    @ConfigProperty(name = "msg.timeout", defaultValue = "5")
    int nextMsgTimeout;

    @ConfigProperty(name = "result.timeout", defaultValue = "10") // max 20 steps * 300ms = 6s. 4s extra
    int resultTimeout;

    Random random = new Random();

    UUID clientId;

    public void start() {

        clientId = getClientId();

        while (true) {
            try {
                calculateSin();
            } catch (Exception e) {
                Log.error("Error while calculating sin, restarting", e);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ex) {
                    Log.error("Interrupted while waiting for restart", ex);
                }
            }

        }
    }

    private UUID getClientId() {
        var sleepTime = 500;
        while (true) {
            try {
                return idClient.getClientId();
            } catch (Exception e) {
                Log.errorf("Sin server not responding. Sleeping for '%d'. Reason: %s", sleepTime, e.getMessage());
                try {
                    Thread.sleep(sleepTime);
                    sleepTime *= 2;
                } catch (InterruptedException interruptedException) {
                    Log.error("Interrupted while waiting for server", interruptedException);
                }
            }
        }
    }

    private void calculateSin() {

        var strategy = strategyClient.getFairStrategy();
        Log.info("Received fair strategy: " + strategy);

        var msg = createInitialMsg();
        Log.info("Sending msg: " + msg);
        try {
            sinSocket.sendMessage(msg);

            switch (strategy) {
                case FINISH -> finishStrategy(msg.id());
                case ANNOYING -> annoyingStrategy(msg.id());
                case IMPATIENT -> randomStrategy(msg.id());
            }
        } catch (InterruptedException e) {
            Log.error("Interrupted while waiting for RESULT message", e);
        } catch (JsonProcessingException e) {
            Log.error("Error while sending message", e);
        }
    }

    private void finishStrategy(String id) throws InterruptedException {
        while (true) {
            var finished = handleResponse(id, System.currentTimeMillis());
            if (finished) {
                return;
            }
        }
    }

    private void annoyingStrategy(String id) throws InterruptedException, JsonProcessingException {
        sinSocket.sendMessage(new Msg(MsgType.CANCEL, id, null, null));
        while (true) {
            var finished = handleResponse(id, System.currentTimeMillis());
            if (finished) {
                return;
            }
        }
    }

    private void randomStrategy(String id) throws InterruptedException, JsonProcessingException {
        var canceled = false;
        while (true) {
            var finished = handleResponse(id, System.currentTimeMillis());
            if (finished) {
                return;
            }
            if (!canceled && random.nextDouble() > 0.59) {
                Log.info("Canceling task");
                sinSocket.sendMessage(new Msg(MsgType.CANCEL, id, null, null));
                canceled = true;
            }
        }
    }

    private boolean handleResponse(String id, long start) throws InterruptedException {
        if (System.currentTimeMillis() - start >= resultTimeout) {
            throw new RuntimeException("Timeout waiting for result.");
        }
        Msg msg = sinSocket.waitForMsg(nextMsgTimeout);
        if (msg == null || !msg.id().equals(id)) {
            return false;
        }
        if (msg.error() != null) {
            Log.info("Error in task");
            return true;
        }
        switch (msg.type()) {
            case RESULT -> {
                Log.infof("sin(%f, %d) = %f", msg.task().input(), msg.task().required_steps(), msg.task().output());
                return true;
            }
            case PARTIAL -> {
                return false;
            }
            case CANCELED -> {
                Log.info("Task canceled");
                return true;
            }
            default -> {
                return false;
            }
        }
    }

    private Msg createInitialMsg() {
        double x = random.nextDouble() * 10;
        int n = random.nextInt(-1, 20);
        return new Msg(MsgType.NEW_TASK, clientId.toString() + "::" + UUID.randomUUID(), new Task(x, 0, 0, 0, n), null);
    }
}
