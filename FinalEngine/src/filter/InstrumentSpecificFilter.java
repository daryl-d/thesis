package filter;

import java.io.IOException;
import java.text.ParseException;
import java.util.HashMap;

import reader.RecordReader;
import record.Record;

public class InstrumentSpecificFilter<T extends InstrumentRecordParser>
		implements RecordReader {

	private final InstrumentRecordParserFactory<T>	f;
	private final HashMap<String, T>				instrumentParsers	= new HashMap<String, T>();
	private T										lastParser;
	private final RecordReader						rr;

	public InstrumentSpecificFilter(RecordReader rr,
			InstrumentRecordParserFactory<T> f) {
		this.rr = rr;
		this.f = f;
	}

	@Override
	public void close() throws IOException {
		rr.close();
	}

	@Override
	public Record get() throws ParseException, IOException {
		if (lastParser != null) {
			Record buffer = lastParser.extra();
			if (buffer != null) {
				return buffer;
			}
		}
		Record r = rr.get();
		if (r == null) {
			return null;
		}
		T ip = instrumentParsers.get(r.instrument);
		if (ip == null) {
			ip = f.getInstance(r.instrument);
			instrumentParsers.put(r.instrument, ip);
		}
		lastParser = ip;
		return ip.parse(r);
	}

	public T getForInstrument(String s) {
		return instrumentParsers.get(s);
	}
}
