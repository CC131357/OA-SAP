package com.api.workflow.sgc.customs;

import com.alibaba.fastjson.JSON;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

@Path("/workflow/customs")
public class CustomsService {
    @GET
    @Path("/createApply")
    @Produces(MediaType.TEXT_PLAIN)
    public String createApply(@Context HttpServletRequest request, @Context HttpServletResponse response) {
        return "pai";
    }
}
