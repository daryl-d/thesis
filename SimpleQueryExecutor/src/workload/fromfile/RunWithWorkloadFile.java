package workload.fromfile;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.daryl.thesis.InvokeTradingEngine;

public class RunWithWorkloadFile {

	public static void main(String args[]) throws FileNotFoundException {
		if (args.length != 1) {
			System.err.println("Error: need to enter workload file !");
			System.exit(1);
		}

		final ScheduledExecutorService ex = Executors
				.newScheduledThreadPool(1000);
		final Scanner s = new Scanner(new File(args[0]));
		Runnable r = new Runnable() {
			@Override
			public void run() {

				if (!s.hasNext()) {
					s.close();
					System.out.println("done");
					ex.shutdown();
					System.exit(0);
				} else {
					int queries = Integer.parseInt(s.nextLine());
					ArrayList<String[]> q = new ArrayList<String[]>();
					for (int i = 0; i != queries; ++i) {
						q.add(s.nextLine().split(","));
					}

					for (String[] qu : q) {
						InvokeTradingEngine.call(qu[0], qu[1], qu[2], qu[3]);
					}
				}
			}
		};

		ex.scheduleAtFixedRate(r, 0, 5000, TimeUnit.MILLISECONDS);
	}
}
