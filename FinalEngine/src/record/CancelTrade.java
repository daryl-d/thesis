package record;

import java.util.Date;
import java.util.EnumSet;

import main.Util;

public class CancelTrade extends Record {

	public final float	price;

	public final int	volume;

	public CancelTrade(String instrument, Date time,
			EnumSet<Qualifier> qualifiers, float price, int volume) {
		super(instrument, time, qualifiers);
		this.price = price;
		this.volume = volume;
	}

	@Override
	public int compareTo(Record arg0) {
		int tmp = super.compareTo(arg0);
		if (tmp != 0) {
			return tmp;
		}
		final CancelTrade other = (CancelTrade) arg0;
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
	public boolean equals(Record obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final CancelTrade other = (CancelTrade) obj;
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
		return Type.CANCEL_TRADE;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Float.floatToIntBits(price);
		result = prime * result + volume;
		return result;
	}

}
