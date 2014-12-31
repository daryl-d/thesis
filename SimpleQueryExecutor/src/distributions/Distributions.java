package distributions;


import java.util.Arrays;

import cern.jet.random.AbstractDistribution;
import cern.jet.random.Logarithmic;
import cern.jet.random.Normal;
import cern.jet.random.Poisson;
import cern.jet.random.Uniform;
import cern.jet.random.engine.MersenneTwister;
import cern.jet.random.engine.RandomEngine;
import edu.cornell.lassp.houle.RngPack.Ranmar;

public class Distributions{

	public Distributions() {
	}

	public static int[] generateZipf(int omega, int numRanks, double a){
		int[] values=new int[numRanks];
		RandomEngine m= new MersenneTwister();
		Arrays.fill(values,1);
		for(int i=2;i<=numRanks;i++){
			//double dist=(1/Math.pow(i,a));
			//System.out.print(dist+" , ");
			//values[i-1]=(new Double(dist)).intValue();
			values[i-2]=cern.jet.random.Distributions.nextZipfInt(a, m);
			//System.out.println(i + " " + values[i-1]);
		}
		int[] values2=new int[values.length];
		Arrays.sort(values);
		int max=values[values.length-1];
		System.out.println(max);
		for(int i=0;i<values.length;i++){
			double v=Math.max((values[i]*omega)/max, 1.0);
			values2[i]=(new Double(v)).intValue();
		}
		return values2;
	}
	
	public static int[] generateUniform(int min, int max, int numRanks){
		Uniform uniDist = new Uniform((double)min, (double)max, new Ranmar());
		int[] values=new int[numRanks];
		for(int i=0;i<numRanks;i++){
			values[i]=uniDist.nextInt();
		}
		return values;
	}
	
	public static int[] generatePoisson(double mean, int numRanks){
		AbstractDistribution dist= new Poisson(mean,new Ranmar());
		int[] values=new int[numRanks];
		for(int i=0;i<numRanks;i++){
			values[i]=dist.nextInt();
		}
		return values;
	}
	
	public static double[] generateWeibull(double alpha, double beta, int numRanks){
		RandomEngine rand=new MersenneTwister(3001);
		double[] values=new double[numRanks];
		for(int i=0;i<numRanks;i++){
			values[i]=cern.jet.random.Distributions.nextWeibull(alpha,beta,rand);
		}
		return values;
	}
	
	public static double[] generateLogarithmic(double lambda, int numRanks){
		double[] values=new double[numRanks];
		for(int i=0;i<numRanks;i++){
			values[i]=Logarithmic.staticNextDouble(lambda);
		}
		return values;
	}
	
	public static double[] generateNormal(double mean, double stddev, int numRanks){
		AbstractDistribution dist= new Normal(mean, stddev, new Ranmar());
		double[] values=new double[numRanks];
		for(int i=0; i<numRanks; i++){
			values[i]=dist.nextDouble();
		}
		return values;
	}
	
	public static void main(String args[])
	{
		for(Integer i: Distributions.generateZipf(86400, 100, 1.2))
		{
			System.out.println(i);
		}
		
	}
}