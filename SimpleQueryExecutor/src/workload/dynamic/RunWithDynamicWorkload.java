package workload.dynamic;
import query.variables.Dates;
import query.variables.Securities;
import query.variables.Times;
import query.variables.utility.Pair;
import utility.Counter;

import com.daryl.thesis.InvokeTradingEngine;

import distributions.PoissonDistribution;
import distributions.ZipfDistribution;

public class RunWithDynamicWorkload {

	public static void main(String args[]) throws InterruptedException {
		long interval = 5000;

		while (Counter.instance.get() != 100) {
			long start = System.currentTimeMillis();
			Thread t = new Thread(new Runnable() {
				@Override
				public void run() {
					int poisson = PoissonDistribution.instance.getNext();

					for (int j = 0; j != poisson; ++j) {

						Pair p = Times.instance
								.getTime(ZipfDistribution.instance.getNext());
						InvokeTradingEngine.call(
								Securities.instance.getSecurity()
										+ Counter.instance.get(),
								Dates.instance.getNextDate(),
								String.valueOf(p.getX()), String.valueOf(p.getY()));
					}
				}
			});
			t.start();
			Counter.instance.increment();
			long end = interval - (System.currentTimeMillis() - start);
			if (end > 0)
				Thread.sleep(end);
		}
	}
}
