package generators;
import java.util.Random;

public class ZipfGenerator {
	private Random rnd = new Random(System.currentTimeMillis());
	private int size;
	private double skew;
	private double bottom = 0;

	public ZipfGenerator(int size, double skew) {
		this.size = size;
		this.skew = skew;

		for (int i = 1; i < size; i++) {
			this.bottom += (1 / Math.pow(i, this.skew));
		}
	}

	// the next() method returns an random rank id.
	// The frequency of returned rank ids are follows Zipf distribution.
	public int next() {
		int rank;
		double frequency = 0;
		double dice;

		rank = rnd.nextInt(size);
		frequency = (1.0d / Math.pow(rank, this.skew)) / this.bottom;
		dice = rnd.nextDouble();

		while (!(dice < frequency)) {
			rank = rnd.nextInt(size);
			frequency = (1.0d / Math.pow(rank, this.skew)) / this.bottom;
			dice = rnd.nextDouble();
		}

		return rank;
	}

	// This method returns a probability that the given rank occurs.
	public double getProbability(int rank) {
		return (1.0d / Math.pow(rank, this.skew)) / this.bottom;
	}
	

//	public static void main(String[] args) {
//
//		ZipfGenerator zipf = new ZipfGenerator(86400,
//				0.8);
//		for (int i = 1; i <= 100; i++)
//			System.out.println(zipf.next());
//		RandomEngine mersenne = new MersenneTwister();
//		
//		for(Integer i : generateZipf(86400, 86400, 0.8))
//		{
//			System.out.println(i);
//		}
//		
//	ZipfGenerator zipf = new ZipfGenerator(86400, 1.1);
//	
//	for(int i = 0; i != 100; ++i)
//	{
//		System.out.println(zipf.next());
//	}
//	
//	
//	}
}