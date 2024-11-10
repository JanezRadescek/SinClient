package com.example.client;

import com.example.client.sin.SinService;
import io.quarkus.logging.Log;
import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;

@ApplicationScoped
public class LifeCycleEvents {

    @Inject
    SinService sinService;

    void onStart(@Observes StartupEvent ev) {
        Log.info("The application is starting...");
        sinService.start();
    }


}
