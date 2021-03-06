package writer;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Writer;
import java.text.ParseException;

import reader.RecordReader;
import record.Record;
import formatter.Formatter;

public class LineWriter {
	private final Formatter		f;
	private final RecordReader	rr;

	public LineWriter(RecordReader rr, Formatter f) {
		this.rr = rr;
		this.f = f;
	}

	public void print(Writer w) throws ParseException, IOException {
		w = new BufferedWriter(w);
		while (true) {
			final Record r = rr.get();
			if (r == null) {
				w.close();
				rr.close();
				break;
			}
			w.write(f.format(r));
			w.write("\n");

		}

		System.out.println("Finished");

	}
}
