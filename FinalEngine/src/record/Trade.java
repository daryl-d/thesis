package record;

import java.util.Date;
import java.util.EnumSet;

import main.Util;

public class Trade extends Record {

	public final int	askBroker;

	public final long	askId;

	public final int	bidBroker;
	public final long	bidId;
	public final float	price;
	public final int	volume;

	public Trade(String instrument, Date time, EnumSet<Qualifier> qualifiers,
			float price, int volume, long bidId, int bidBroker, long askId,
			int askBroker) {
		super(instrument, time, qualifiers);
		this.price = price;
		this.volume = volume;
		this.bidId = bidId;
		this.bidBroker = bidBroker;
		this.askId = askId;
		this.askBroker = askBroker;
	}

	@Override
	public int compareTo(Record arg0) {
		int tmp = super.compareTo(arg0);
		if (tmp != 0) {
			return tmp;
		}
		final Trade other = (Trade) arg0;
		tmp = Util.compare(askId, other.askId);
		if (tmp != 0) {
			return tmp;
		}
		tmp = Util.compare(bidId, other.bidId);
		if (tmp != 0) {
			return tmp;
		}
		tmp = Util.compare(askBroker, other.askBroker);
		if (tmp != 0) {
			return tmp;
		}
		tmp = Util.compare(bidBroker, other.bidBroker);
		if (tmp != 0) {
			return tmp;
		}
		tmp = Float.compare(price, other.price);
		if (tmp != 0) {
			return tmp;
		}
		tmp = Util.compare(volume, other.volume);
		if (tmp != 0) {
			return tmp;
		}
		return 0;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!super.equals(obj)) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final Trade other = (Trade) obj;
		if (askBroker != other.askBroker) {
			return false;
		}
		if (askId != other.askId) {
			return false;
		}
		if (bidBroker != other.bidBroker) {
			return false;
		}
		if (bidId != other.bidId) {
			return false;
		}
		if (Float.floatToIntBits(price) != Float.floatToIntBits(other.price)) {
			return false;
		}
		if (volume != other.volume) {
			return false;
		}
		return true;
	}

	@Override
	public Type getType() {
		return Type.TRADE;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + askBroker;
		result = prime * result + (int) (askId ^ askId >>> 32);
		result = prime * result + bidBroker;
		result = prime * result + (int) (bidId ^ bidId >>> 32);
		result = prime * result + Float.floatToIntBits(price);
		result = prime * result + volume;
		return result;
	}

	@Override
	public String toString() {
		return "Trade [bidId=" + bidId + ", askId=" + askId + ", bidBroker="
				+ bidBroker + ", askBroker=" + askBroker + ", price=" + price
				+ ", volume=" + volume + ", instrument=" + instrument
				+ ", time=" + time + ", qualifiers=" + qualifiers + "]";
	}

}
