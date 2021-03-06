package distributions;
import generators.ZipfGenerator;

public enum ZipfDistribution {
	instance;
	private final int MAX_INTERVAL_LENGTH_IN_SECONDS = 17990;
	private ZipfGenerator zipf = new ZipfGenerator(
			MAX_INTERVAL_LENGTH_IN_SECONDS, 1.2);

	private ZipfDistribution() {

	}

	public int getNext() {
		return zipf.next();
	}
}
