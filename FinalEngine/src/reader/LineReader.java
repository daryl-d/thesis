package reader;

import java.io.BufferedReader;
import java.io.IOException;

import record.Record;
import formatter.Formatter;

public class LineReader implements RecordReader {

	private final Formatter			f;

	public boolean					first	= true;

	private final BufferedReader	r;

	public LineReader(BufferedReader r, Formatter f) {
		this.r = r;
		this.f = f;
	}

	@Override
	public void close() throws IOException {
		r.close();
	}

	@Override
	public Record get() throws IOException {

		String input;
		do {
			do {
				input = r.readLine();
				if (input == null) {
					return null;
				}

			} while (first && input.startsWith("#"));
			first = false;
			try {
				Record r = f.parse(input);
				if (r != null) {
					return r;
				}
			} catch (final Exception e) {
				// TODO Auto-generated catch block
				System.err.println("Error while parsing CSV line :\"" + input
						+ "\"");
				e.printStackTrace();
			}
		} while (true);
	}

}
