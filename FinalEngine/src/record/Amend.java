package record;

import java.util.Date;
import java.util.EnumSet;

public class Amend extends Enter {
	public Amend(String instrument, Date time, EnumSet<Qualifier> qualifiers,
			boolean bid, float price, int volume, long id, int broker) {
		super(instrument, time, qualifiers, bid, price, volume, id, broker);
	}

	@Override
	public Type getType() {
		return Record.Type.AMEND;
	}

	@Override
	public String toString() {
		return "Amend [bid=" + bid + ", price=" + price + ", volume=" + volume
				+ ", id=" + id + ", broker=" + broker + ", instrument="
				+ instrument + ", time=" + time + ", qualifiers=" + qualifiers
				+ "]";
	}
}
