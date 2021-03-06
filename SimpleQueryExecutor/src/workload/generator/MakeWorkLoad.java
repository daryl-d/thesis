package workload.generator;
import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;

import query.variables.Dates;
import query.variables.Securities;
import query.variables.Times;
import query.variables.utility.Pair;
import workload.generator.utility.Query;
import distributions.PoissonDistribution;
import distributions.ZipfDistribution;


public class MakeWorkLoad {
	public static void main(String args[]) throws Exception
	{
		if(args.length != 2)
		{
			System.err.println("duration of experiment in minutes and output file name");
			System.exit(1);
		}
		int minutes = Integer.parseInt(args[0]);
		String file = args[1];
		int intervals = 12 * minutes;
		
		File f = new File(file);
		FileOutputStream fs = new FileOutputStream(f);
		PrintWriter pw = new PrintWriter(fs);
		
		File fw = new File("numberPerMinute");
		FileOutputStream fos = new FileOutputStream(fw);
		PrintWriter pm = new PrintWriter(fos);
		
		int sum = 0;
		for(int i = 0; i != intervals; ++i)
		{
			int poisson = PoissonDistribution.instance.getNext();
			sum += poisson;
			pw.println(poisson);
			Query q = null;
			Pair p = null;
			for(int x = 0; x != poisson; ++x)
			{
				p = Times.instance.getTime(ZipfDistribution.instance.getNext());
				q = new Query(Securities.instance.getSecurity() + (i/12), Dates.instance.getNextDate(), String.valueOf(p.getX()), String.valueOf(p.getY()));
				pw.println(q);
			}
			
			if(i % 12 == 11 )
			{
				pm.println(sum);
				sum = 0;
			}
		}
		
		pw.close();
		fs.close();
		pm.close();
		fos.close();
		
	}
}
