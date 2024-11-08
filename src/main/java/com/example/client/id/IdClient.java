package com.example.client.id;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import java.util.UUID;

@Path("/id")
@RegisterRestClient(configKey = "id-api")
public interface IdClient {

    @GET
    UUID getClientId();
}
