package main;

import java.util.EnumSet;

import record.Record.Qualifier;

public class Entry implements Comparable<Entry> {
	public final boolean			bid;
	public final int				broker;

	public final long				id;

	public float					price;

	public final EnumSet<Qualifier>	qualifiers;

	public int						sequence;

	public int						volume;

	public Entry(int volume, float price, long id, int broker,
			EnumSet<Qualifier> enumSet, int sequence, boolean bid) {
		this.volume = volume;
		this.price = price;
		this.id = id;
		this.broker = broker;
		qualifiers = enumSet;
		this.sequence = sequence;
		this.bid = bid;
	}

	@Override
	public int compareTo(Entry other) {
		if (bid) {
			if (other.price != price) {
				return Float.compare(other.price, price);
			}
		} else {
			if (price != other.price) {
				return Float.compare(price, other.price);
			}
		}
		return sequence - other.sequence;
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
		Entry other = (Entry) obj;
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
		if (qualifiers == null) {
			if (other.qualifiers != null) {
				return false;
			}
		} else if (!qualifiers.equals(other.qualifiers)) {
			return false;
		}
		if (sequence != other.sequence) {
			return false;
		}
		if (volume != other.volume) {
			return false;
		}
		return true;
	}

	@Override
	public int hashCode() {
		return sequence;
	}

	@Override
	public String toString() {
		return "Entry [volume=" + volume + ", price=" + price + ", id=" + id
				+ ", broker=" + broker + ", qualifiers=" + qualifiers
				+ ", sequence=" + sequence + ", bid=" + bid + "]";
	}

}
