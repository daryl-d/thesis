package record;

import java.util.Date;
import java.util.EnumSet;

public class OffMarketTrade extends Trade {

	public OffMarketTrade(String instrument, Date time,
			EnumSet<Qualifier> qualifiers, float price, int volume, long bidId,
			int bidBroker, long askId, int askBroker) {
		super(instrument, time, qualifiers, price, volume, bidId, bidBroker,
				askId, askBroker);
	}

	@Override
	public Type getType() {
		return Type.OFFTR;
	}

	@Override
	public String toString() {
		return "OffMarketTrade [bidId=" + bidId + ", askId=" + askId
				+ ", bidBroker=" + bidBroker + ", askBroker=" + askBroker
				+ ", price=" + price + ", volume=" + volume + ", instrument="
				+ instrument + ", time=" + time + ", qualifiers=" + qualifiers
				+ "]";
	}

}
