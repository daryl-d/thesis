package filter;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import record.Record;

public class TradeGrapherFilter {
	public class GraphData {
		public final double[]	asks;
		public final double[]	bids;
		public final Date[]		dates;
		public final Date		end;
		public final String		instrument;
		public final double[]	marketDepths;
		public final double[]	prices;
		public final Date		start;
		public final double[]	volumes;

		public GraphData(String instrument, Date[] dates,
				double[] marketDepths, double[] volumes, double[] prices,
				double[] asks, double[] bids, Date start, Date end) {
			this.instrument = instrument;
			this.dates = dates;
			this.marketDepths = marketDepths;
			this.volumes = volumes;
			this.prices = prices;
			this.asks = asks;
			this.bids = bids;
			this.start = start;
			this.end = end;
		}

	}

	public class Sample {
		public double	ask;
		public double	bid;
		public Date		date;
		public double	marketDepth;
		public double	price;
		public double	volume;

		public Sample(Date d, double md, double vol, double p, double b,
				double a) {
			date = d;
			marketDepth = md;
			volume = vol;
			price = p;
			ask = a;
			bid = b;
		}

		public boolean datesEqual(Sample t) {
			if (date.equals(t.date)) {
				return true;
			}
			return false;
		}
	}

	public class Sampler {
		private int					divisor		= 1;
		private final String		instrument;
		private Sample				last;
		ArrayList<Sample>			records		= new ArrayList<Sample>();
		private int					skipCount	= 0;
		private final TradeEngine	te;

		public Sampler(String instrument, TradeEngine te) {
			this.instrument = instrument;
			this.te = te;
		}

		public void add(Record r) {
			last = new Sample(r.time, te.getMarketDepth(), te.getVolume(),
					te.getPrice(), te.getBid(), te.getAsk());

			if (contains(last)) {
				records.remove(records.size() - 1);
				records.add(last);
			} else {
				if (skipCount % divisor == 0) {
					records.add(last);
					if (records.size() > 20000) {
						boolean unlucky = false;
						for (Iterator<Sample> it = records.iterator(); it
								.hasNext();) {
							it.next();
							if (unlucky) {
								it.remove();
							}
							unlucky = !unlucky;
						}
						divisor *= 2;
					}
					skipCount = divisor;
				}
				skipCount--;
			}
		}

		public boolean contains(Sample t) {
			if (records.size() == 0) {
				return false;
			}
			return records.get(records.size() - 1).datesEqual(t);
		}

		public GraphData noSamplingV2() {
			records.add(last);
			int size = records.size();

			final double[] volumes = new double[size];
			final double[] marketDepth = new double[size];
			final double[] price = new double[size];
			final double[] asks = new double[size];
			final double[] bids = new double[size];
			final Date[] dates = new Date[size];
			Date start = records.get(0).date;
			Date end = records.get(records.size() - 1).date;

			long lastTime = 0;
			double lastVolume = 0;
			int prevVolume = 0;

			boolean priceWasNaN = true;
			int filled = 0;
			for (Sample s : records) {
				if (lastTime != 0) {
					lastVolume = lastVolume
							* Math.exp(0.000003 * (lastTime - s.date.getTime()))
							+ (s.volume - prevVolume);
				}

				prevVolume = (int) s.volume;
				lastTime = s.date.getTime();
				volumes[filled] = lastVolume;
				marketDepth[filled] = s.marketDepth;
				if (!Double.isNaN(s.price) && priceWasNaN) {
					Arrays.fill(price, 0, filled - 1, s.price);
					priceWasNaN = false;
				}
				price[filled] = s.price;
				asks[filled] = s.ask;
				bids[filled] = s.bid;
				dates[filled] = s.date;
				filled++;

			}

			return new GraphData(instrument, dates, marketDepth, volumes,
					price, asks, bids, start, end);
		}

		public GraphData sample(int wantedSamples) {
			// GraphData data = new GraphData();

			final Date first = records.get(0).date;
			final Date last = records.get(records.size() - 1).date;
			final Calendar firstc = Calendar.getInstance();
			final Calendar lastc = Calendar.getInstance();
			firstc.setTime(first);
			lastc.setTime(last);
			final int diff = (int) ((lastc.getTimeInMillis() - firstc
					.getTimeInMillis()) / 1000);
			final double[] volumes = new double[wantedSamples];
			final double[] marketDepth = new double[wantedSamples];
			final double[] price = new double[wantedSamples];
			final double[] asks = new double[wantedSamples];
			final double[] bids = new double[wantedSamples];
			final Date[] dates = new Date[wantedSamples];
			Date start = records.get(0).date;
			Date end = records.get(records.size() - 1).date;

			final Calendar rolling = Calendar.getInstance();
			final Calendar compare = Calendar.getInstance();

			final Iterator<Sample> it = records.iterator();
			Sample t = it.next();

			rolling.setTime(first);
			int prevVolume = 0;
			for (int i = 0; i < wantedSamples;) {
				compare.setTime(t.date);
				if (rolling.before(compare) || !it.hasNext()) {
					rolling.setTime(first);
					rolling.add(Calendar.SECOND, (i + 1) * diff / wantedSamples);
					volumes[i] = t.volume - prevVolume;
					prevVolume = (int) t.volume;
					price[i] = t.price;
					marketDepth[i] = t.marketDepth;
					dates[i] = t.date;
					asks[i] = t.ask;
					bids[i] = t.bid;
					i++;
				} else {
					t = it.next();
				}
			}
			return new GraphData(instrument, dates, marketDepth, volumes,
					price, asks, bids, start, end);
		}
	}

	private final HashMap<String, Sampler>				samplers	= new HashMap<String, Sampler>();

	private final InstrumentSpecificFilter<TradeEngine>	te;

	public TradeGrapherFilter(InstrumentSpecificFilter<TradeEngine> te) {
		this.te = te;
	}

	public GraphData[] getData() throws ParseException, IOException {
		while (true) {
			final Record e = te.get();
			if (e == null) {
				break;
			}
			Sampler s = samplers.get(e.instrument);
			if (s == null) {
				s = new Sampler(e.instrument, te.getForInstrument(e.instrument));
				samplers.put(e.instrument, s);
			}
			s.add(e);
		}
		te.close();
		GraphData data[] = new GraphData[samplers.size()];
		int i = 0;
		for (Sampler s : samplers.values()) {
			data[i++] = s.noSamplingV2();
		}
		return data;
	}
}
