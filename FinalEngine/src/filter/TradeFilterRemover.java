package filter;

import java.io.IOException;
import java.text.ParseException;

import reader.RecordReader;
import record.Record;
import record.Record.Type;

public class TradeFilterRemover implements RecordReader {

	private final RecordReader	rr;
	private final Type			type;

	public TradeFilterRemover(RecordReader rr, Record.Type type) {
		this.rr = rr;
		this.type = type;
	}

	@Override
	public void close() throws IOException {
		rr.close();
	}

	@Override
	public Record get() throws ParseException, IOException {
		Record r;
		do {
			r = rr.get();
			if (r == null) {
				return null;
			}
		} while (r.getType() == type);
		return r;
	}
}
