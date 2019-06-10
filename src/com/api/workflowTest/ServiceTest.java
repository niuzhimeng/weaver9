package com.api.workflowTest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@Path("/workflowTest/v1")

public class ServiceTest {

    @GET
    @Path("/test")
    @Produces("text/plain;charset=gbk")
    public String test() {
        return "{test:\"test\"}";

    }
}











