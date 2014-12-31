package main;

import java.lang.management.ManagementFactory;
import java.math.BigInteger;
import java.util.Iterator;
import java.util.List;

public class Util {
	public static class Timer {
		private long	cpu;
		private long	real;
		private boolean	running;
		private long	user;

		public Timer() {
			real = System.nanoTime();
			user = ManagementFactory.getThreadMXBean()
					.getCurrentThreadUserTime();
			cpu = ManagementFactory.getThreadMXBean().getCurrentThreadCpuTime();
			running = true;
		}

		public void stop() {
			running = false;
			real = System.nanoTime() - real;
			user = ManagementFactory.getThreadMXBean()
					.getCurrentThreadUserTime() - user;
			cpu = ManagementFactory.getThreadMXBean().getCurrentThreadCpuTime()
					- cpu;
		}

		@Override
		public String toString() {
			if (running) {
				stop();
			}
			return String.format("real: %.2fs\nsystem: %.2fs\nuser: %.2fs\n",
					real / 1000000000.0, (cpu - user) / 1000000000.0,
					user / 1000000000.0);

		}
	}

	public static int compare(boolean b1, boolean b2) {
		return b1 != b2 ? b1 ? 1 : -1 : 0;
	}

	public static int compare(int a1, int a2) {
		return a1 >= a2 ? a1 == a2 ? 0 : 1 : -1;
	}

	public static <T extends Comparable<? super T>> int compare(Iterable<T> a1,
			Iterable<T> a2) {
		Iterator<T> it1 = a1.iterator();
		Iterator<T> it2 = a2.iterator();
		int tmp;
		while (it1.hasNext() && it2.hasNext()) {
			tmp = it1.next().compareTo(it2.next());
			if (tmp != 0) {
				return tmp;
			}
		}
		return compare(it1.hasNext(), it2.hasNext());
	}

	public static int compare(long a1, long a2) {
		return a1 >= a2 ? a1 == a2 ? 0 : 1 : -1;
	}

	public static <T extends Comparable<? super T>> int compare(T a1[], T a2[]) {
		int tmp = a1.length - a2.length;
		if (tmp != 0) {
			return tmp;
		}
		for (int i = 0; i < a1.length; i++) {
			tmp = a1[i].compareTo(a2[i]);
			if (tmp != 0) {
				return tmp;
			}
		}
		return 0;
	}

	public static String join(Iterable<? extends Object> l, String seperator) {
		final StringBuilder sb = new StringBuilder();
		boolean sep = false;
		for (final Object o : l) {
			if (sep) {
				sb.append(seperator);
			}
			sep = true;
			sb.append(o.toString());
		}
		return sb.toString();
	}

	public static int modulo(int dividend, int divisor) {
		return (dividend % divisor + divisor) % divisor;
	}

	public static long parseLongWithRounding(String s) {
		return new BigInteger(s).longValue();
	}

	public static <T extends Comparable<? super T>> void removeBefore(
			List<T> l, T t) {
		for (final Iterator<T> it = l.iterator(); it.hasNext();) {
			final T r = it.next();
			it.remove();
			if (r.equals(t)) {
				break;
			}
		}
	}

	public static <T> T returnAfterRunnable(T t, Runnable r) {
		r.run();
		return t;
	}
}
