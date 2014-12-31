package filter;

import java.util.Date;

import record.Marker;
import record.Record;
import record.Record.Qualifier;

public class MarkerRecordParser implements InstrumentRecordParser {
	public static class Factory implements
			InstrumentRecordParserFactory<MarkerRecordParser> {

		@Override
		public MarkerRecordParser getInstance(String instrument) {
			return new MarkerRecordParser();
		}

	}

	public enum State {
		CLOSED, CLOSING, LOADING_PREVIOUS, OPEN, OPENING, PRE_CLOSE, PRE_OPEN
	}

	public Record	buffer;
	private Date	lastDate;
	public Record	marker_buffer;

	private State	state;

	public MarkerRecordParser() {
		this(State.LOADING_PREVIOUS);
	}

	public MarkerRecordParser(State s) {
		state = s;
	}

	@Override
	public Record extra() {
		if (marker_buffer != null) {
			final Record tmp = marker_buffer;
			marker_buffer = null;
			return tmp;
		}
		if (buffer != null) {
			final Record tmp = buffer;
			buffer = null;
			return tmp;
		}
		return null;
	}

	@Override
	@SuppressWarnings("deprecation")
	public Record parse(Record record) {
		buffer = record;
		if (buffer.getType() == Record.Type.TRADE) {
			for (final Qualifier q : buffer.qualifiers) {
				if (q == Qualifier.AC) {
					if (state == State.PRE_OPEN) {
						state = State.OPENING;
						marker_buffer = new Marker(buffer, Marker.Type.OPEN);
						return new Marker(buffer, Marker.Type.AUCTION);
					}
					if (state == State.PRE_CLOSE) {
						state = State.CLOSING;
						return new Marker(buffer, Marker.Type.AUCTION);
					}
				}
			}
			if (state == State.PRE_OPEN) {
				state = State.OPEN;
				return new Marker(buffer, Marker.Type.OPEN);
			}
		} else if (state == State.CLOSING || state == State.OPENING) {
			if (state == State.CLOSING) {
				state = State.CLOSED;
			} else {
				state = State.OPEN;
			}
		}
		if (buffer.time.getHours() >= 16 && lastDate.getHours() < 16) {
			state = State.PRE_CLOSE;
			marker_buffer = new Marker(buffer, Marker.Type.CLOSE);
		} else if (lastDate != null
				&& lastDate.getDay() != buffer.time.getDay()) {
			state = State.LOADING_PREVIOUS;
			marker_buffer = new Marker(buffer, Marker.Type.DAY_RESET);
		} else if (lastDate != null && state == State.LOADING_PREVIOUS
				&& buffer.time.compareTo(lastDate) != 0) {
			state = State.PRE_OPEN;
			marker_buffer = new Marker(buffer, Marker.Type.PRE_OPEN);
		}
		lastDate = buffer.time;

		if (marker_buffer != null) {
			final Record tmp = marker_buffer;
			marker_buffer = null;
			return tmp;
		}
		final Record tmp = buffer;
		buffer = null;
		return tmp;
	}
}
