package com.thesis.daryl;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;



@Path("time")
public class TimeKeeper {

	@POST
	@Path("{id}")
	public Response put(@PathParam("id") String point)
	{
		SaveData.instance.addPoint(point);
		return Response.ok().build();
	}
}
