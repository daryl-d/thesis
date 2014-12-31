package record;

import java.util.EnumSet;

public class Marker extends Record {
	public enum Type {
		AUCTION("auction"), CLOSE("close"), DAY_RESET("new day"), OPEN(
				"market openning auction"), PRE_OPEN(
				"finished loading previous day");
		private String	s;

		Type(String s) {
			this.s = s;
		}

		public String getMessage() {
			return s;
		}
	}

	public final Type	markerType;

	public Marker(Record r, Type type) {
		super(r.instrument, r.time, EnumSet.noneOf(Qualifier.class));
		markerType = type;
	}

	@Override
	public int compareTo(Record arg0) {
		int tmp = super.compareTo(arg0);
		if (tmp != 0) {
			return tmp;
		}
		final Marker other = (Marker) arg0;
		tmp = markerType.compareTo(other.markerType);
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
		final Marker other = (Marker) obj;
		if (markerType != other.markerType) {
			return false;
		}
		return true;
	}

	@Override
	public Record.Type getType() {
		return Record.Type.MARKER;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ (markerType == null ? 0 : markerType.hashCode());
		return result;
	}

	@Override
	public String toString() {
		return "Marker [markerType=" + markerType + ", instrument="
				+ instrument + ", time=" + time + ", qualifiers=" + qualifiers
				+ "]";
	}
}
