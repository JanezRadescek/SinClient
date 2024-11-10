package com.example.client.sin;

import com.example.client.dtos.Msg;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import java.util.List;

@Path("/sin")
@RegisterRestClient(configKey = "strategy-api")
public interface SinClient {

    @GET
    @Path("/active")
    List<Msg> getActiveTasks();
}
