package log;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class Logger {
	private final String CATALINA_HOME = "CATALINA_HOME";
	private final String PATH = System.getenv(CATALINA_HOME)
			+ "/webapps/ROOT/log.txt";
	PrintWriter writer;

	public Logger() {
		try {
			writer = new PrintWriter(new BufferedWriter(new FileWriter(PATH,
					true)));
		} catch (Exception e) {
			System.err.println("Unable to log");
		}
	}

	public synchronized void writeToLog(long time) throws IOException {
		writer.println(time);
		writer.flush();
	}
	
	public void close()	{
		writer.close();
	}
}
