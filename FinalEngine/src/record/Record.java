package record;

import java.util.Date;
import java.util.EnumSet;

import main.Util;

public abstract class Record implements Comparable<Record>, Cloneable {

	public enum Qualifier {
		AC, AK, BL, BP, CP, CPX, CT, EC, EP, ET, FK, L1, L2, L3, L4, L5, LT, LX, LXT, MK, ML, OS, P1, PRE_NR, PXT, S1, SHL, SP, SX, TM, UV, XT
	}

	public enum Type {
		AMEND, CANCEL_TRADE, CONTROL, DELETE, ENTER, MARKER, OFFTR, QUOTE, TRADE;
	}

	public final String				instrument;

	public final EnumSet<Qualifier>	qualifiers;

	public final Date				time;

	public Record(String instrument, Date time, EnumSet<Qualifier> qualifiers) {
		this.instrument = instrument;
		this.time = time;
		this.qualifiers = qualifiers;
	}

	@Override
	public int compareTo(Record arg0) {
		int tmp = time.compareTo(arg0.time);
		if (tmp != 0) {
			return tmp;
		}
		tmp = getType().compareTo(arg0.getType());
		if (tmp != 0) {
			return tmp;
		}
		tmp = instrument.compareTo(arg0.instrument);
		if (tmp != 0) {
			return tmp;
		}
		tmp = Util.compare(qualifiers, arg0.qualifiers);
		return tmp;
	}

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
		final Record other = obj;
		if (instrument == null) {
			if (other.instrument != null) {
				return false;
			}
		} else if (!instrument.equals(other.instrument)) {
			return false;
		}
		if (!qualifiers.equals(other.qualifiers)) {
			return false;
		}
		if (time == null) {
			if (other.time != null) {
				return false;
			}
		} else if (!time.equals(other.time)) {
			return false;
		}
		return true;
	}

	public String getQualifiersString() {
		return Util.join(qualifiers, " ");
	}

	public abstract Type getType();

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ (instrument == null ? 0 : instrument.hashCode());
		result = prime * result + qualifiers.hashCode();
		result = prime * result + (time == null ? 0 : time.hashCode());
		return result;
	}
}
