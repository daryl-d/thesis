package com.thesis.daryl;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

public class PostData {
	public static void main(String args[]) {
		Client client = Client.create();
		WebResource webResource = client.resource("http://localhost:8080/TimeService");
		System.out.println(webResource.path("rest").path("time").path("124#elems").post(ClientResponse.class).getStatus());
	}
}
