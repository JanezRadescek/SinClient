package com.example.client.sin;

import com.example.client.CustomExcpetion;
import com.example.client.dtos.Msg;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.logging.Log;
import jakarta.websocket.ClientEndpoint;
import jakarta.websocket.ContainerProvider;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.WebSocketContainer;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.net.URI;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

@ClientEndpoint
public class SinSocket {

    @ConfigProperty(name = "connect.timeout", defaultValue = "5")
    int connectTimeout;

    @ConfigProperty(name = "sin.url")
    String url;


    private Session session;
    private ObjectMapper objectMapper = new ObjectMapper();
    private CountDownLatch resultLatch;
    private CountDownLatch sessionLatch;
    private BlockingQueue<Msg> messageQueue = new LinkedBlockingQueue<>();

    public void sendMessage(Msg msg) throws InterruptedException, JsonProcessingException {
        String message = objectMapper.writeValueAsString(msg);
        sendMessage(message);
    }

    public Msg waitForMsg(int resultTimeout) throws InterruptedException {
        resultLatch = new CountDownLatch(1);
        Msg resultMsg = messageQueue.poll();
        if (resultMsg == null) {
            if (resultLatch.await(resultTimeout, TimeUnit.SECONDS)) {
                resultMsg = messageQueue.poll();
            } else {
                throw new CustomExcpetion("Timeout waiting for response from server");
            }
        }

        return resultMsg;
    }

    private void connect(String uri) {
        try {
            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            container.connectToServer(this, new URI(uri));
        } catch (Exception e) {
            Log.error("Error connecting to WebSocket", e);
        }
    }

    @OnOpen
    void onOpen(Session session) {
        this.session = session;
        Log.info("WebSocket connection opened");
        if (sessionLatch != null) {
            sessionLatch.countDown();
        }
    }

    @OnMessage
    void onMessage(String message) {
        Log.info("Received message: " + message);
        try {
            Msg msg = objectMapper.readValue(message, Msg.class);
            messageQueue.add(msg);
            if (resultLatch != null) {
                resultLatch.countDown();
            }
        } catch (JsonProcessingException e) {
            Log.error("Error parsing message: " + message, e);
        }
    }

    private void sendMessage(String message) throws InterruptedException {
        if (session == null || !session.isOpen()) {
            sessionLatch = new CountDownLatch(1);
            connect(url);
            if (!sessionLatch.await(connectTimeout, TimeUnit.SECONDS)) {
                throw new CustomExcpetion("Timeout connecting to WebSocket");
            }
        }
        session.getAsyncRemote().sendText(message);
    }
}
