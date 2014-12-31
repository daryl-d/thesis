package com.daryl.thesis;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/seed")
public class SeedProvider {

	@GET
	@Path("/get")
	@Produces(MediaType.TEXT_PLAIN)
	public String getSeed() {
		return SeedEnum.instance.getSeed();
	}

	@POST
	@Path("/set/{ip}")
	public Response setSeed(@PathParam("ip") String ip) {
		SeedEnum.instance.setSeed(ip);
		System.out.println(ip);
		return Response.ok().build();
	}

}
