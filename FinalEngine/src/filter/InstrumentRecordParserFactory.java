package filter;

public interface InstrumentRecordParserFactory<T extends InstrumentRecordParser> {
	public T getInstance(String instrument);
}
