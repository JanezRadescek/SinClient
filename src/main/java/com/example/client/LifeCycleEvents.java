package com.example.client;

import io.quarkus.logging.Log;
import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;

@ApplicationScoped
public class LifeCycleEvents {

    @Inject
    SinClient sinClient;

    void onStart(@Observes StartupEvent ev) {
        Log.info("The application is starting...");
        sinClient.start();
    }


}
