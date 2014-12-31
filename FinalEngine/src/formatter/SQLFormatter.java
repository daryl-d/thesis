package formatter;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import record.Amend;
import record.CancelTrade;
import record.Delete;
import record.Enter;
import record.OffMarketTrade;
import record.Record;
import record.Trade;

public class SQLFormatter implements Formatter {
	private final DateFormat df = new SimpleDateFormat("yyyyMMdd HH:mm:ss.SSS");

	/**
	 * Use an Enum instead of hardcoded strings ! this will make debugging
	 * easier
	 */
	private int oldDay;
	private int oldHour;
	private int oldMillisecond;
	private int oldMinute;
	private int oldMonth;
	private int oldSecond;
	private long oldTime;
	private int oldYear;

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
	public Record parse(String line) throws ParseException {
		// TODO Auto-generated method stub
		return null;
	}

	public int parseInt(char[] buf, int offset, int limit) {
		int j = 0;
		for (int i = offset; i < offset + limit; i++) {
			j *= 10;
			j += buf[i] - '0';
		}
		return j;
	}

	@SuppressWarnings("deprecation")
	public Date fastDateParser(char[] buf) {

		Date d = new Date(parseInt(buf, 0, 4) - 1900, parseInt(buf, 4, 2),
				parseInt(buf, 6, 2), parseInt(buf, 9, 2), parseInt(buf, 12, 2),
				parseInt(buf, 15, 2));
		d.setTime(d.getTime() + parseInt(buf, 18, 3));
		return d;
	}

	
	// public Record parse(Map<Columns, String> map) throws ParseException {
	// final String instrument = map.get(Columns.instrument);
	// String dateTime = map.get(Columns.date) + " " + map.get(Columns.time);
	// final Date time = fastDateParser(dateTime.toCharArray());
	// final Record.Type type;
	// try {
	// type = Record.Type.valueOf(map.get(Columns.recordType));
	//
	// } catch (IllegalArgumentException e) {
	// return null;
	// }
	// EnumSet<Record.Qualifier> qualifiers = EnumSet
	// .noneOf(Record.Qualifier.class);
	//
	// if (!map.get("qualifiers").equals("")) {
	// final String tmp[] = map.get(Columns.qualifiers).split(" ");
	// for (String element : tmp) {
	// qualifiers.add(Record.Qualifier.valueOf(element));
	// }
	// }
	//
	// switch (type) {
	// case CONTROL:
	// return null;
	// case ENTER:
	// case AMEND:
	// case DELETE: {
	// final boolean bid = map.get(Columns.bidAsk).equals("B");
	// float price = -1;
	// int volume = -1;
	// if (type != Record.Type.DELETE) {
	// price = Float.parseFloat(map.get(Columns.price));
	// volume = Integer.parseInt(map.get(Columns.volume));
	// if (!map.get(Columns.undisclosedVolume).equals("")) {
	// Integer.parseInt(map.get(Columns.undisclosedVolume));
	// }
	// }
	// long id;
	// int broker;
	// if (bid) {
	// id = Util.parseLongWithRounding(map.get(Columns.bidId));
	// if (!map.get(Columns.buyerBrokerId).equals("")) {
	// broker = Integer.parseInt(map.get(Columns.buyerBrokerId));
	// } else {
	// broker = 0;
	// }
	// } else {
	// id = Util.parseLongWithRounding(map.get(Columns.askId));
	// if (!map.get(Columns.sellerBrokerId).equals("")) {
	// broker = Integer.parseInt(map.get(Columns.sellerBrokerId));
	// } else {
	// broker = 0;
	// }
	// }
	// if (type == Record.Type.ENTER) {
	// return new Enter(instrument, time, qualifiers, bid, price,
	// volume, id, broker);
	// } else if (type == Record.Type.AMEND) {
	// return new Amend(instrument, time, qualifiers, bid, price,
	// volume, id, broker);
	// } else {
	// return new Delete(instrument, time, qualifiers, bid, id, broker);
	// }
	// }
	// case OFFTR:
	// case TRADE: {
	// final float price = Float.parseFloat(map.get(Columns.price));
	// final int volume = Integer.parseInt(map.get(Columns.volume));
	// final long bidId = Util.parseLongWithRounding(map
	// .get(Columns.bidId));
	// final int bidBroker;
	//
	// if (!map.get(Columns.buyerBrokerId).equals("")) {
	// bidBroker = Integer.parseInt(map.get(Columns.buyerBrokerId));
	// } else {
	// bidBroker = 0;
	// }
	// final long askId = Util.parseLongWithRounding(map
	// .get(Columns.askId));
	// final int askBroker;
	// if (!map.get(Columns.sellerBrokerId).equals("")) {
	// askBroker = Integer.parseInt(map.get(Columns.sellerBrokerId));
	// } else {
	// askBroker = 0;
	// }
	// if (type == Type.OFFTR) {
	// return new OffMarketTrade(instrument, time, qualifiers, price,
	// volume, bidId, bidBroker, askId, askBroker);
	// } else {
	// return new Trade(instrument, time, qualifiers, price, volume,
	// bidId, bidBroker, askId, askBroker);
	// }
	// }
	// case CANCEL_TRADE: {
	// final float price = Float.parseFloat(map.get(Columns.price));
	// final int volume = Integer.parseInt(map.get(Columns.volume));
	// return new CancelTrade(instrument, time, qualifiers, price, volume);
	// }
	//
	// default:
	// throw new Error("Not implemented");
	// }
	// }
}
