package query.variables;
import generators.ZipfGenerator;

import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;

public enum Securities {
	instance;
	private final String SECURITY = "SECURITY";
	private  final String CATALINA_HOME="CATALINA_HOME";
	private ZipfGenerator zipf = null;
	private ArrayList<String> securities;

	private Securities() {
		File f = new File( System.getenv(CATALINA_HOME) + "/" + SECURITY);
		securities = new ArrayList<String>();
		try{
			Scanner s = new Scanner(f);
			while(s.hasNext())
			{
				securities.add(s.nextLine());
			}
			s.close();
			zipf = new ZipfGenerator(securities.size(), 1.2);
		} catch(Exception ex)
		{
			System.err.println("$CATALINA_HOME/SECURITY not found!");
		}
		
	}

	public String getSecurity() {
		return securities.get(zipf.next() % securities.size());
	}
}
