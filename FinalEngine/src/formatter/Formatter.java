package formatter;

import java.text.ParseException;

import record.Record;

public interface Formatter {
	public String format(Record record);

	public Record parse(String line) throws ParseException;
}
