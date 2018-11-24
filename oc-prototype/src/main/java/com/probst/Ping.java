package com.probst;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Path("/ping")
public class Ping {
	
	@Path("/hello")
	@GET
	public String hello() {
		return "hello";
	}

}
