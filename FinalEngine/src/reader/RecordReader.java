package reader;

import java.io.IOException;
import java.text.ParseException;

import record.Record;

public interface RecordReader {
	public void close() throws IOException;

	public Record get() throws ParseException, IOException;
}
