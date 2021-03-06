package com.daryl.thesis;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import com.sun.jersey.api.client.Client;

public enum ServiceEndpointUrls {
	instance;
	private Client client;
	private int index = 0;
	private List<String> endpoints;
	private static final String CATALINA_HOME = "CATALINA_HOME";
	private static final String ENGINE = "ENGINE";

	private ServiceEndpointUrls() {
		endpoints = new ArrayList<String>();

		client = new Client();
		try {
			File f = new File(System.getenv(CATALINA_HOME),  ENGINE);
			Scanner s = new Scanner(f);

			while (s.hasNext()) {
				endpoints.add(s.nextLine());
			}

			s.close();

			if (endpoints.size() == 0) {
				throw new Exception("No hosts found in $CATALINA_HOME/ENGINE");
			}

			for (String endpoint : endpoints) {
				System.err.println( "Endpoint: " + endpoint + " has been found" );
			}

		} catch (Exception ex) {
			System.err.println("Unable to read $CATALINA_HOME/ENGINE");
			ex.printStackTrace();

		}
	}

	public String getNextHost() {
		if (index >= endpoints.size()) {
			index = 0;
		}

		String host = endpoints.get(index);

		++index;
		System.err.println(host);
		return host;

	}

	public Client getClient() {
		return client;
	}
}
