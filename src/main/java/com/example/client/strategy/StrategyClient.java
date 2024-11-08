package com.example.client.strategy;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@Path("/strategy")
@RegisterRestClient(configKey = "strategy-api")
public interface StrategyClient {

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Path("/hello")
    String hello();

    @GET
    @Path("/random")
    Strategy getRandomStrategy();

    @GET
    @Path("/fair")
    Strategy getFairStrategy();
}
