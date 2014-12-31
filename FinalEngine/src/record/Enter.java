package record;

import java.util.Date;
import java.util.EnumSet;

import main.Util;

public class Enter extends Record {
	public final boolean	bid;

	public final int		broker;

	public final long		id;
	public final float		price;
	public final int		volume;

	public Enter(String instrument, Date time, EnumSet<Qualifier> qualifiers,
			boolean bid, float price, int volume, long id, int broker) {
		super(instrument, time, qualifiers);
		this.bid = bid;
		this.price = price;
		this.volume = volume;
		this.id = id;
		this.broker = broker;
	}

	@Override
	public int compareTo(Record arg0) {
		int tmp = super.compareTo(arg0);
		if (tmp != 0) {
			return tmp;
		}
		final Enter other = (Enter) arg0;
		tmp = Util.compare(id, other.id);
		if (tmp != 0) {
			return tmp;
		}
		tmp = Util.compare(broker, other.broker);
		if (tmp != 0) {
			return tmp;
		}
		tmp = Util.compare(bid, other.bid);
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
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final Enter other = (Enter) obj;
		if (bid != other.bid) {
			return false;
		}
		if (broker != other.broker) {
			return false;
		}
		if (id != other.id) {
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
	public Record.Type getType() {
		return Record.Type.ENTER;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (bid ? 1231 : 1237);
		result = prime * result + broker;
		result = prime * result + (int) (id ^ id >>> 32);
		result = prime * result + Float.floatToIntBits(price);
		result = prime * result + volume;
		return result;
	}

	@Override
	public String toString() {
		return "Enter [bid=" + bid + ", price=" + price + ", volume=" + volume
				+ ", id=" + id + ", broker=" + broker + ", instrument="
				+ instrument + ", time=" + time + ", qualifiers=" + qualifiers
				+ "]";
	}

}
