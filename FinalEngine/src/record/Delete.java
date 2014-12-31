package record;

import java.util.Date;
import java.util.EnumSet;

import main.Util;

public class Delete extends Record {

	public final boolean	bid;

	public final int		broker;

	public final long		id;

	public Delete(String instrument, Date time, EnumSet<Qualifier> qualifiers,
			boolean bid, long id, int broker) {
		super(instrument, time, qualifiers);
		this.bid = bid;
		this.id = id;
		this.broker = broker;
	}

	@Override
	public int compareTo(Record arg0) {
		int tmp = super.compareTo(arg0);
		if (tmp != 0) {
			return tmp;
		}
		final Delete other = (Delete) arg0;
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
		final Delete other = (Delete) obj;
		if (bid != other.bid) {
			return false;
		}
		if (broker != other.broker) {
			return false;
		}
		if (id != other.id) {
			return false;
		}
		return true;
	}

	@Override
	public Type getType() {
		return Record.Type.DELETE;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (bid ? 1231 : 1237);
		result = prime * result + broker;
		result = prime * result + (int) (id ^ id >>> 32);
		return result;
	}

	@Override
	public String toString() {
		return "Delete [bid=" + bid + ", id=" + id + ", broker=" + broker
				+ ", instrument=" + instrument + ", time=" + time
				+ ", qualifiers=" + qualifiers + "]";
	}

}
