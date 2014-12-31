package query.variables;
import java.security.SecureRandom;

import query.variables.utility.Pair;


public enum Times {
instance;
	private static final int BASE_TIME=36000;
	private static final int MAX_INTERVAL_LENGTH = 18000;
	private static final SecureRandom random = new SecureRandom();
	
	private Times() {}
	
	
	public Pair getTime(int interval)
	{	
		// min interval is 10 seconds
		int x = 10 + interval;
		int start = BASE_TIME + random.nextInt(Math.max(MAX_INTERVAL_LENGTH - x, 1));
		return new Pair(start, start + x);
	}
	
	
}
