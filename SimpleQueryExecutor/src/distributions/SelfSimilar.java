package distributions;
import java.security.SecureRandom;


public enum SelfSimilar {
	instance;
	private static final double H = 0.2;
	private static final SecureRandom random = new SecureRandom();

	private SelfSimilar() {

	}


	public int selfSimilar(int N) {
		return selfsimilar(N, H);
	}

	/**
	 * 
	 * The first h*N values get 1 - h of the distribution
	 */
	public int selfsimilar(int N, double h) {
		return ((int) (N * Math.pow(random.nextDouble(),
				(Math.log(h) / Math.log(1.0 - h)))))
				% N;
	}
}
