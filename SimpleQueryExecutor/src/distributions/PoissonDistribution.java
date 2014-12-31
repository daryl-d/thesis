package distributions;
import java.util.Random;

import cern.jet.random.AbstractDistribution;
import cern.jet.random.Poisson;
import edu.cornell.lassp.houle.RngPack.Ranmar;


public enum PoissonDistribution {
	instance;
	
	AbstractDistribution poisson;
	Random r = new Random();
	
	private PoissonDistribution()
	{
		poisson = new Poisson(150, new Ranmar());
	}
	
	public int getNext()
	{
		return poisson.nextInt();
	}
}
