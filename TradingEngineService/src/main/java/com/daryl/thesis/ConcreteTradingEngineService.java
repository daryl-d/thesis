package com.daryl.thesis;

import com.daryl.thesis.RunTradingServiceDocument.RunTradingService;

public class ConcreteTradingEngineService implements
		TradingEngineServiceSkeletonInterface {

	public void runTradingService(
			RunTradingServiceDocument runTradingServiceDocument) {
		CassandraBackedTradingEngine cassandraBackedTradingEngine = new CassandraBackedTradingEngine();

		RunTradingService runTradingService = runTradingServiceDocument
				.getRunTradingService();
		String instrument = runTradingService.getInstrument();
		int date = runTradingService.getDate();
		int lowerTime = runTradingService.getLowerTime();
		int upperTime = runTradingService.getUpperTime();

		cassandraBackedTradingEngine.runEngine(instrument, date, lowerTime,
				upperTime);
	}
}
