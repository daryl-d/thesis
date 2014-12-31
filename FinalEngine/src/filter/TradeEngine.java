package filter;

import java.util.ArrayList;
import java.util.Date;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.TreeSet;

import main.Entry;
import record.Amend;
import record.Delete;
import record.Enter;
import record.Marker;
import record.OffMarketTrade;
import record.Record;
import record.Record.Qualifier;
import record.Trade;

public class TradeEngine implements InstrumentRecordParser {
	public static class Factory implements
			InstrumentRecordParserFactory<TradeEngine> {

		@Override
		public TradeEngine getInstance(String instrument) {
			return new TradeEngine(instrument);
		}

	}

	enum State {
		CLOSED, OPEN
	}

	TreeSet<Entry>			asks			= new TreeSet<Entry>();
	private float			auctionPrice;
	TreeSet<Entry>			bids			= new TreeSet<Entry>();
	HashMap<Long, Entry>	idLookup		= new HashMap<Long, Entry>();
	private String			instrument;
	private Date			lastDate;
	private float			lastTradePrice	= Float.NaN;

	ArrayList<Entry>		old				= new ArrayList<Entry>();
	private int				sequence;
	private State			state;

	private int				volume;

	public TradeEngine(String instrument) {
		this.instrument = instrument;
		state = State.CLOSED;
	}

	public void auction() {
		// printOrderBook();
		auctionPrice = getAuctionPrice();
	}

	@Override
	public Record extra() {
		if (state == State.OPEN || auctionPrice != 0.0f) {
			final Trade t = makeTrade();
			if (t != null) {
				return t;
			}
			auctionPrice = 0.0f;
		}
		return null;
	}

	public Entry findAndRemove(long id) {
		final Entry e = idLookup.get(new Long(id));
		if (e == null) {
			return e;
		}
		bids.remove(e);
		asks.remove(e);
		return e;
	}

	public float getAsk() {
		if (asks.isEmpty()) {
			return Float.NaN;
		}
		return asks.first().price;
	}

	public float getAuctionPrice() {
		final Iterator<Entry> bidIterator = bids.iterator();
		final Iterator<Entry> askIterator = asks.iterator();

		int difference = 0;
		Entry bid = null;
		Entry ask = null;
		while (true) {
			if (difference >= 0) {
				Entry tmpBid;
				if (bidIterator.hasNext()) {
					tmpBid = bidIterator.next();
				} else {

					break;
				}
				if (ask != null && tmpBid.price < ask.price) {
					break;
				}
				bid = tmpBid;
				difference -= bid.volume;
			} else {
				Entry tmpAsk;
				if (askIterator.hasNext()) {
					tmpAsk = askIterator.next();
				} else {

					break;
				}
				if (tmpAsk.price > bid.price) {
					break;
				}
				ask = tmpAsk;
				difference += ask.volume;
			}
		}

		if (ask == null || bid == null) {
			return 0.0f;
		}

		if (bid.price != ask.price && Float.isNaN(lastTradePrice)) {
			System.err
					.println("Warning opening price may be inaccurate as last trading price was not specified ("
							+ bid.price + ", " + ask.price + ")");
		}
		if (lastTradePrice > bid.price && Float.isNaN(lastTradePrice)) {
			return bid.price;
		} else if (lastTradePrice < ask.price || Float.isNaN(lastTradePrice)) {
			return ask.price;
		} else {
			return lastTradePrice;
		}

	}

	public float getBid() {
		if (bids.isEmpty()) {
			return Float.NaN;
		}
		return bids.first().price;
	}

	public int getMarketDepth() {
		int depth = 0;
		for (final Entry i : bids) {
			depth += i.volume;
		}
		for (final Entry i : asks) {
			depth += i.volume;
		}
		return depth;
	}

	public float getPrice() {
		return lastTradePrice;
	}

	public float getSpread() {
		if (asks.isEmpty() || bids.isEmpty()) {
			return Float.NaN;
		}
		final Entry b = bids.first();
		final Entry a = asks.first();

		return b.price - a.price;
	}

	public int getVolume() {
		return volume;
	}

	public Trade makeTrade() {
		if (bids.isEmpty() || asks.isEmpty()) {
			return null;
		}
		final Entry bid = bids.first();
		final Entry ask = asks.first();
		float price;
		if (bid.price >= ask.price) {
			int volume = bid.volume;
			if (ask.volume < volume) {
				volume = ask.volume;
				bid.volume = bid.volume - volume;
				asks.remove(ask);
				idLookup.remove(new Long(ask.id));
			} else if (ask.volume == volume) {
				asks.remove(ask);
				bids.remove(bid);
				idLookup.remove(new Long(ask.id));
				idLookup.remove(new Long(bid.id));
			} else {
				ask.volume = ask.volume - volume;
				bids.remove(bid);
				idLookup.remove(new Long(bid.id));
			}
			if (auctionPrice == 0.0f) {
				if (bid.sequence > ask.sequence) {
					price = ask.price;
				} else {
					price = bid.price;
				}
			} else {
				price = auctionPrice;
			}
			lastTradePrice = price;
			this.volume += volume;
			return new Trade(instrument, lastDate,
					EnumSet.noneOf(Qualifier.class), price, volume, bid.id,
					bid.broker, ask.id, ask.broker);
		} else {
			return null;
		}
	}

	@SuppressWarnings("incomplete-switch")
	@Override
	public Record parse(Record record) {

		lastDate = record.time;
		switch (record.getType()) {
			case MARKER:
				final Marker marker = (Marker) record;
				switch (marker.markerType) {
					case AUCTION:
						auction();
						break;
					case OPEN:
						state = State.OPEN;
						break;
					case CLOSE:
						state = State.CLOSED;
						break;
					case DAY_RESET:
						asks.clear();
						bids.clear();
						idLookup.clear();
						break;
				}
				break;
			case OFFTR:
				final OffMarketTrade offMarketTrade = (OffMarketTrade) record;
				final Entry bid = findAndRemove(offMarketTrade.bidId);
				if (bid == null) {
					// System.err
					// .println("warning: off market trade with bid id that did not exist in the order book");
				} else {
					bid.volume = bid.volume - offMarketTrade.volume;
					if (bid.volume > 0) {
						bids.add(bid);
					}
				}
				final Entry ask = findAndRemove(offMarketTrade.askId);
				if (ask == null) {
					// System.err
					// .println("warning: off market trade with ask id that did not exist in the order book");
				} else {
					ask.volume = ask.volume - offMarketTrade.volume;
					if (ask.volume > 0) {
						asks.add(ask);
					}
				}
				volume += offMarketTrade.volume;
				break;
			case ENTER:
				instrument = record.instrument;
				final Enter enter = (Enter) record;
				final Entry e = new Entry(enter.volume, enter.price, enter.id,
						enter.broker, enter.qualifiers, sequence++, enter.bid);
				if (enter.bid) {
					bids.add(e);
				} else {
					asks.add(e);
				}
				idLookup.put(new Long(e.id), e);

				break;
			case DELETE:
				final Delete delete = (Delete) record;
				findAndRemove(delete.id);
				return record;
			case AMEND:
				final Amend amend = (Amend) record;
				final Entry entry = findAndRemove(amend.id);
				if (entry == null) {
					// modification to one that already exists
					break;
				}
				final int newSequence = sequence++;
				if (entry.price != amend.price) {
					entry.price = amend.price;
					entry.sequence = newSequence;
				}
				if (entry.volume != amend.volume) {
					if (entry.volume < amend.volume) {
						entry.sequence = newSequence;
					}
					entry.volume = amend.volume;
				}
				if (entry.bid) {
					bids.add(entry);
				} else {
					asks.add(entry);
				}
				idLookup.put(new Long(entry.id), entry);
		}
		return record;
	}

	public void printOrderBook() {
		System.out.println("Bids: ");
		int volume = 0;
		for (final Entry e : bids) {
			volume += e.volume;
			System.out.println(volume);
			System.out.println(e.toString());
		}

		System.out.println("Asks: ");
		volume = 0;
		for (final Entry e : asks) {
			volume += e.volume;
			System.out.println(volume);
			System.out.println(e.toString());
		}

	}

}
