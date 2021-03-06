package com.daryl.thesis;

import com.sun.jersey.api.client.AsyncWebResource;

public class InvokeTradingEngine {
	private static final String SOAP_REQUEST_MESSAGE_TEMPLATE = "<?xml version=\"1.0\" encoding=\"utf-8\"?><soap:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\"><soap:Body><runTradingService xmlns=\"http://thesis.daryl.com\"><instrument>%s</instrument><date>%s</date><lowerTime>%s</lowerTime><upperTime>%s</upperTime></runTradingService></soap:Body></soap:Envelope>";

	public InvokeTradingEngine() {

	}

	public static void call(String instrument, String date, String lowerTime,
			String upperTime) {
		AsyncWebResource async = ServiceEndpointUrls.instance.getClient()
				.asyncResource(
						"http://" + ServiceEndpointUrls.instance.getNextHost()
								+ ":8080/axis2/services/TradingEngineService");
		String soapRequestMessage = String.format(
				SOAP_REQUEST_MESSAGE_TEMPLATE, instrument, date, lowerTime,
				upperTime);
		try {
			async.getRequestBuilder().post(soapRequestMessage).get();
			return;
		} catch (Exception ex) {
			 System.out.println("Not waiting for get to finish !");
			return;
		}

	}

	public static void main(String args[]) {
		InvokeTradingEngine.call("ABC", String.valueOf(20120831),
				String.valueOf(0), String.valueOf(86400));

	}
}
