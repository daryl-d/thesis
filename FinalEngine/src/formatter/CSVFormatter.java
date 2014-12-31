package formatter;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.EnumSet;
import java.util.StringTokenizer;

import main.Util;
import record.Amend;
import record.CancelTrade;
import record.Delete;
import record.Enter;
import record.OffMarketTrade;
import record.Record;
import record.Record.Type;
import record.Trade;

public class CSVFormatter implements Formatter {

	public static String modString(String s) {
		final StringTokenizer st = new StringTokenizer(s);
		final String data[] = new String[2];
		int i = 0;

		while (i < 2 && st.hasMoreTokens()) {
			data[i] = st.nextToken();
			i++;
		}

		return data[0] + "," + data[1];
	}

	private final DateFormat df = new SimpleDateFormat("yyyyMMdd HH:mm:ss.SSS");

	private int oldDay;
	private int oldHour;
	private int oldMillisecond;
	private int oldMinute;
	private int oldMonth;
	private int oldSecond;
	private long oldTime;
	private int oldYear;

	@SuppressWarnings("deprecation")
	public Date fastDateParser(char[] buf) {

		Date d = new Date(parseInt(buf, 0, 4) - 1900, parseInt(buf, 4, 2),
				parseInt(buf, 6, 2), parseInt(buf, 9, 2), parseInt(buf, 12, 2),
				parseInt(buf, 15, 2));
		d.setTime(d.getTime() + parseInt(buf, 18, 3));
		return d;
	}

	@SuppressWarnings("deprecation")
	public Date fastDateParser2(char[] buf) {
		int year = parseInt(buf, 0, 4) - 1900;
		int month = parseInt(buf, 4, 2);
		int day = parseInt(buf, 6, 2);
		int hour = parseInt(buf, 9, 2);
		int minute = parseInt(buf, 12, 2);
		int second = parseInt(buf, 15, 2);
		int millisecond = parseInt(buf, 18, 3);
		if (year == oldYear && month == oldMonth && day == oldDay) {
			long time = oldTime + (hour - oldHour) * 3600000
					+ (minute - oldMinute) * 60000 + (second - oldSecond)
					* 1000 + (millisecond - oldMillisecond);
			return new Date(time);
		} else {
			oldYear = year;
			oldMonth = month;
			oldDay = day;
			oldHour = hour;
			oldMinute = minute;
			oldSecond = second;
			oldMillisecond = millisecond;
			Date d = new Date(year, month, day, hour, minute, second);
			d.setTime(d.getTime() + millisecond);
			oldTime = d.getTime();
			return d;
		}
	}

	@Override
	// OFFTR, TRADE, QUOTE, CANCEL_TRADE, ENTER, AMEND, DELETE, MARKER
	public String format(Record record) {
		final String common = record.instrument + ","
				+ CSVFormatter.modString(df.format(record.time)) + ","
				+ record.getType() + ",";
		String other = "";

		switch (record.getType()) {
		case OFFTR:
			final OffMarketTrade o = (OffMarketTrade) record;
			// Price,Volume,Undisclosed Volume,Value,Qualifiers,Trans ID,Bid
			// ID,Ask ID,Bid/Ask,Entry Time,Old Price,Old Volume,Buyer
			// Broker
			// ID,Seller Broker ID
			other = o.price + "," + o.volume + "," + "," + o.price * o.volume
					+ "," + o.getQualifiersString() + ",0," + o.bidId + ","
					+ o.askId + ",,,,," + o.bidBroker + "," + o.askBroker;
			break;
		case TRADE:
			final Trade t = (Trade) record;
			// Price,Volume,Undisclosed Volume,Value,Qualifiers,Trans ID,Bid
			// ID,Ask ID,Bid/Ask,Entry Time,Old Price,Old Volume,Buyer
			// Broker
			// ID,Seller Broker ID
			other = t.price + "," + t.volume + "," + "," + t.volume * t.price
					+ "," + t.getQualifiersString() + ",0," + t.bidId + ","
					+ t.askId + ",,,,," + t.bidBroker + "," + t.askBroker;
			break;
		case CANCEL_TRADE:
			// Price,Volume,Undisclosed Volume,Value,Qualifiers,Trans ID,Bid
			// ID,Ask ID,Bid/Ask,Entry Time,Old Price,Old Volume,Buyer
			// Broker
			// ID,Seller Broker ID
			final CancelTrade ct = (CancelTrade) record;
			other = ct.price + "," + ct.volume + ",," + ct.price * ct.volume
					+ "," + ct.getQualifiersString() + ",0,,,,,,,,";
			break;
		case ENTER:
			// Price,Volume,Undisclosed Volume,Value,Qualifiers,Trans ID,Bid
			// ID,Ask ID,Bid/Ask,Entry Time,Old Price,Old Volume,Buyer
			// Broker
			// ID,Seller Broker ID
			final Enter e = (Enter) record;
			String c;
			String id;
			String brokerId;
			if (e.bid) {
				c = "B";
				id = e.id + ",";
				brokerId = e.broker + ",";
			} else {
				c = "A";
				id = "," + e.id;
				brokerId = "," + e.broker;
			}
			other = e.price + "," + e.volume + ",,," + e.getQualifiersString()
					+ ",0," + id + "," + c + ",,,," + brokerId;
			break;
		case AMEND:
			// Price,Volume,Undisclosed Volume,Value,Qualifiers,Trans ID,Bid
			// ID,Ask ID,Bid/Ask,Entry Time,Old Price,Old Volume,Buyer
			// Broker
			// ID,Seller Broker ID
			final Amend a = (Amend) record;
			if (a.bid) {
				c = "B";
				id = a.id + ",";
				brokerId = a.broker + ",";
			} else {
				c = "A";
				id = "," + a.id;
				brokerId = "," + a.broker;
			}
			other = a.price + "," + a.volume + ",,," + a.getQualifiersString()
					+ ",0," + id + "," + c + ",,,," + brokerId;
			break;
		case DELETE:
			// Price,Volume,Undisclosed Volume,Value,Qualifiers,Trans ID,Bid
			// ID,Ask ID,Bid/Ask,Entry Time,Old Price,Old Volume,Buyer
			// Broker
			// ID,Seller Broker ID
			final Delete d = (Delete) record;
			if (d.bid) {
				c = "B";
				id = d.id + ",";
				brokerId = d.broker + ",";
			} else {
				c = "A";
				id = "," + d.id;
				brokerId = "," + d.broker;
			}
			other = ",,,," + d.getQualifiersString() + ",0," + id + "," + c
					+ ",,,," + brokerId;
			break;
		default:
			return "";

		}

		return common + other;

	}

	@Override
	public Record parse(String line) throws ParseException {
		// replace slows us down by 0.4 /* .replace("\"", "") */
		// final String[] data = line.split(",", -1);
		String data[] = new String[18];

		{
			char[] linebuf = line.toCharArray();
			int old = 0;
			int datacount = 0;
			int i;
			for (i = 0; i < linebuf.length; i++) {
				if (linebuf[i] == ',') {
					if (datacount == 1) {
						datacount++;
					} else {
						data[datacount++] = new String(linebuf, old, i - old);
						old = i + 1;
					}
				}
			}
			if (old != linebuf.length) {
				data[datacount] = new String(linebuf, old, i - old);
			} else {
				data[datacount] = "";
			}
		}
		final String instrument = data[0];
		final Date time = fastDateParser(data[2].toCharArray());
		final Record.Type type;
		try {
			type = Record.Type.valueOf(data[3]);

		} catch (IllegalArgumentException e) {
			return null;
		}
		EnumSet<Record.Qualifier> qualifiers = EnumSet
				.noneOf(Record.Qualifier.class);

		if (!data[8].equals("")) {
			final String tmp[] = data[8].split(" ");
			for (String element : tmp) {
				qualifiers.add(Record.Qualifier.valueOf(element));
			}
		}

		switch (type) {
		case CONTROL:
			return null;
		case ENTER:
		case AMEND:
		case DELETE: {
			final boolean bid = data[12].equals("B");
			float price = -1;
			int volume = -1;
			if (type != Record.Type.DELETE) {
				price = Float.parseFloat(data[4]);
				volume = Integer.parseInt(data[5]);
				if (!data[6].equals("")) {
					Integer.parseInt(data[6]);
				}
			}
			long id;
			int broker;
			if (bid) {
				id = Util.parseLongWithRounding(data[10]);
				if (!data[16].equals("")) {
					broker = Integer.parseInt(data[16]);
				} else {
					broker = 0;
				}
			} else {
				id = Util.parseLongWithRounding(data[11]);
				if (!data[17].equals("")) {
					broker = Integer.parseInt(data[17]);
				} else {
					broker = 0;
				}
			}
			if (type == Record.Type.ENTER) {
				return new Enter(instrument, time, qualifiers, bid, price,
						volume, id, broker);
			} else if (type == Record.Type.AMEND) {
				return new Amend(instrument, time, qualifiers, bid, price,
						volume, id, broker);
			} else {
				return new Delete(instrument, time, qualifiers, bid, id, broker);
			}
		}
		case OFFTR:
		case TRADE: {
			final float price = Float.parseFloat(data[4]);
			final int volume = Integer.parseInt(data[5]);
			final long bidId = Util.parseLongWithRounding(data[10]);
			final int bidBroker;

			if (!data[16].equals("")) {
				bidBroker = Integer.parseInt(data[16]);
			} else {
				bidBroker = 0;
			}
			final long askId = Util.parseLongWithRounding(data[11]);
			final int askBroker;
			if (!data[17].equals("")) {
				askBroker = Integer.parseInt(data[17]);
			} else {
				askBroker = 0;
			}
			if (type == Type.OFFTR) {
				return new OffMarketTrade(instrument, time, qualifiers, price,
						volume, bidId, bidBroker, askId, askBroker);
			} else {
				return new Trade(instrument, time, qualifiers, price, volume,
						bidId, bidBroker, askId, askBroker);
			}
		}
		case CANCEL_TRADE: {
			final float price = Float.parseFloat(data[4]);
			final int volume = Integer.parseInt(data[5]);
			return new CancelTrade(instrument, time, qualifiers, price, volume);
		}

		default:
			throw new Error("Not implemented");
		}
	}

	public int parseInt(char[] buf, int offset, int limit) {
		int j = 0;
		for (int i = offset; i < offset + limit; i++) {
			j *= 10;
			j += buf[i] - '0';
		}
		return j;
	}
}
