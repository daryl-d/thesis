package filter;

import record.Record;

public interface InstrumentRecordParser {
	public Record extra();

	public Record parse(Record record);

}
