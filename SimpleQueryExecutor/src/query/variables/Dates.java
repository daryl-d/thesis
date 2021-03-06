package query.variables;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import distributions.SelfSimilar;



public enum Dates {
	instance;
	private final List<String> dates;
	private final String CATALINA_HOME = "CATALINA_HOME";
	private final String DATES = "DATES";

	private Dates() {
		dates = new ArrayList<String>();
		File f = new File(System.getenv(CATALINA_HOME) + "/" + DATES);

		try {
			Scanner s = new Scanner(f);

			while (s.hasNext()) {
				dates.add(s.nextLine());
			}

			s.close();

			if (dates.size() == 0) {
				throw new Exception("No dates in $CATALINA_HOME/DATES");

			}

		} catch (Exception ex) {
			ex.printStackTrace();
			System.exit(0);
		} finally {

		}
	}

	public String getNextDate() {
		return dates.get(SelfSimilar.instance.selfSimilar(dates.size()));
	}

}
