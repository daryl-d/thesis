package com.daryl.thesis;

import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.io.output.NullOutputStream;

import reader.CQLReader;
import record.Record;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;

import filter.FilterFactory;
import filter.InstrumentSpecificFilter;
import filter.MarkerRecordParser;
import filter.TradeEngine;
import filter.TradeFilterRemover;

public class CassandraBackedTradingEngine {
	private static final Logger s_logger = Logger
			.getLogger(CassandraBackedTradingEngine.class.getName());

	public void runEngine(String instrument, int date, int lowerTime,
			int upperTime) {
		Writer o = null;
		try {

			long start = System.currentTimeMillis();

			o = new OutputStreamWriter(new NullOutputStream());

			TradeFilterRemover t = new TradeFilterRemover(
					new InstrumentSpecificFilter<TradeEngine>(
							new TradeFilterRemover(
									new InstrumentSpecificFilter<MarkerRecordParser>(
											new CQLReader(instrument, date,
													lowerTime, upperTime),
											new MarkerRecordParser.Factory()),
									Record.Type.TRADE),
							new TradeEngine.Factory()), Record.Type.MARKER);
			// get the run time of the query
			long runTime = (System.currentTimeMillis() - start);
			t.close();
			// run the trading engine
			FilterFactory.write(FilterFactory.tradeEngine(t), o);

			// post the results to the time rest service
			ClientConfig config = new DefaultClientConfig();
			Client client = Client.create(config);
			WebResource service = client.resource(makeUrl(runTime));
			service.post(ClientResponse.class);
		} catch (Exception e) {
			s_logger.log(Level.SEVERE, "Could not perform trade processing", e);
		} finally {
		}
	}

	public static void main(String args[]) {
		CassandraBackedTradingEngine cassandraBackedTradingEngine = new CassandraBackedTradingEngine();
		cassandraBackedTradingEngine.runEngine("ABC", 20120831, 0, 86400);
	}

	private String makeUrl(long time) {
		return "http://localhost:8080/TimeService/rest/time/" + time;
	}

}
