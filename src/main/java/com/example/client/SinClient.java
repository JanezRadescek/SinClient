package com.example.client;

import com.example.client.dtos.Msg;
import com.example.client.dtos.MsgType;
import com.example.client.dtos.Task;
import com.example.client.id.IdClient;
import com.example.client.strategy.Strategy;
import com.example.client.strategy.StrategyClient;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.util.UUID;

@ApplicationScoped
public class SinClient {

    @Inject
    SinSocket sinSocket;

    @RestClient
    StrategyClient strategyClient;

    @RestClient
    IdClient idClient;

    @ConfigProperty(name = "result.timeout", defaultValue = "60")
    int resultTimeout;

    UUID clientId;

    public void start() {

        clientId = idClient.getClientId();

        var strategy = strategyClient.getFairStrategy();
        strategy = Strategy.FINISH;
        Log.info("Received Fair strategy: " + strategy);

        while (true) {
            calculateSin(strategy);
        }
    }

    private void calculateSin(Strategy strategy) {
        var msg = createMsg();
        Log.info("Sending msg: " + msg);
        try {
            sinSocket.sendMessage(msg);

            Msg resultMsg = switch (strategy) {
                case FINISH -> finishStrategy(msg.id());
                case ANNOYING -> AnnoyingStrategy(msg.id());
                case RANDOM -> randomStrategy(msg.id());
            };

            if (resultMsg != null) {
                Log.info("Received RESULT message: " + resultMsg);
            } else {
                Log.warn("Did not receive RESULT message within the timeout period");
            }
        } catch (InterruptedException e) {
            Log.error("Interrupted while waiting for RESULT message", e);
        } catch (JsonProcessingException e) {
            Log.error("Error while sending message", e);
        }
    }

    private Msg finishStrategy(String id) throws InterruptedException {
        while (true) {
            Msg resultMsg = sinSocket.waitForMsg(resultTimeout);
            if (resultMsg != null && resultMsg.id().equals(id)) {
                return resultMsg;
            }
        }
    }

    private Msg AnnoyingStrategy(String id) throws InterruptedException, JsonProcessingException {
        sinSocket.sendMessage(new Msg(MsgType.CANCEL, id, null, null));
        while (true) {
            Msg resultMsg = sinSocket.waitForMsg(resultTimeout);
            if (resultMsg != null && resultMsg.id().equals(id)) {
                return resultMsg;
            }
        }
    }

    private Msg randomStrategy(String id) {
        return null;
    }

    private Msg createMsg() {
        double x = 3.14;
        int n = 10;
        return new Msg(MsgType.NEW_TASK, clientId.toString() + "::" + UUID.randomUUID(), new Task(x, 0, 0, 0, n), null);
    }
}
