package org.apache.camel.catalog.mcp;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/live")
public class LivenessResource {

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String hello() {
        return "Camel Catalog MCP is running";
    }
}
