package filter;

import java.io.IOException;
import java.text.ParseException;

import reader.RecordReader;
import record.Record;

public class ProgressFilter implements RecordReader {
	public interface ProgressCallback {
		public void finish();

		public void update(int value, int max);
	}

	private int					count		= 0;
	private ProgressCallback	pc			= null;
	private int					progress	= 0;
	private final RecordReader	rr;

	public ProgressFilter(RecordReader rr, ProgressCallback pc) {
		this.rr = rr;
		this.pc = pc;
	}

	@Override
	public void close() throws IOException {
		pc.finish();
		rr.close();
	}

	@Override
	public Record get() throws ParseException, IOException {
		if (pc != null) {
			count++;
			if (count % 1024 == 0) {
				updateProgress();
			}
		}
		final Record input = rr.get();
		return input;

	}

	private void updateProgress() {
		// We don't actually know how much more we'll need to read so just keep
		// increasing the bar and make the user happy

		progress += (Integer.MAX_VALUE - progress) / 40;
		pc.update(progress, Integer.MAX_VALUE);
	}

}
