package filter;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;

import main.Util;
import reader.RecordReader;
import record.Record;

public class ThreadFilter {

	private class ThreadFilterOutput implements RecordReader {

		private boolean	closed	= false;
		private int		current	= 0;
		public int		output	= 0;
		private Record	privateBuffer[];

		@Override
		public void close() throws IOException {
			synchronized (ThreadFilter.this) {
				closed = true;
				ThreadFilter.this.notifyAll();
			}
		}

		@Override
		public Record get() throws ParseException, IOException {
			if (privateBuffer == null || current == privateBuffer.length) {
				privateBuffer = newBuffer();
				current = 0;
			}
			return privateBuffer[current++];
		}

		private Record[] newBuffer() throws IOException, ParseException {
			if (thread == null) {
				start();
			}
			Record r[];
			synchronized (ThreadFilter.this) {
				while (Util.modulo(input - output, buffer.length) == 0) {
					if (e != null) {
						if (e instanceof IOException) {
							throw (IOException) e;
						} else if (e instanceof ParseException) {
							throw (ParseException) e;
						} else {
							e.printStackTrace();
							throw new Error("Unknown Exception");
						}
					}
					try {
						ThreadFilter.this.wait(100);
					} catch (final InterruptedException e) {
						e.printStackTrace();
					}
				}
				r = buffer[output];
				output = (output + 1) % buffer.length;
				ThreadFilter.this.notifyAll();

			}
			return r;
		}

	}

	private final Record						buffer[][]	= new Record[5][];
	private Exception							e			= null;
	private int									input		= 0;
	private final ArrayList<ThreadFilterOutput>	outputs		= new ArrayList<ThreadFilterOutput>();
	private final RecordReader					rr;
	private Thread								thread		= null;

	public ThreadFilter(RecordReader rr) {
		this.rr = rr;

	}

	public boolean allClosed() {
		for (ThreadFilterOutput t : outputs) {
			if (!t.closed) {
				return false;
			}
		}
		return true;
	}

	public RecordReader getOutput() {
		final ThreadFilterOutput tfo = new ThreadFilterOutput();
		outputs.add(tfo);
		return tfo;

	}

	private boolean hasFree() {
		for (final ThreadFilterOutput tfo : outputs) {
			if (Util.modulo(tfo.output - input, buffer.length) == 1) {
				return false;
			}
		}
		return true;
	}

	private void start() {
		thread = new Thread() {
			@Override
			public void run() {
				threadStart();
			}
		};
		thread.start();

	}

	private void threadStart() {
		try {
			while (true) {
				final Record[] r = new Record[1024];
				for (int i = 0; i < r.length; i++) {
					r[i] = rr.get();
				}
				synchronized (this) {
					while (!hasFree()) {
						if (allClosed()) {
							rr.close();
							return;
						}
						try {
							this.wait();
						} catch (final InterruptedException e) {
							e.printStackTrace();
						}
					}

					buffer[input] = r;
					input = (input + 1) % buffer.length;
					notifyAll();

				}

			}

		} catch (final ParseException e) {
			e.printStackTrace();
			synchronized (this) {
				this.e = e;
			}
		} catch (final IOException e) {
			e.printStackTrace();
			synchronized (this) {
				this.e = e;
			}
		}
		try {
			rr.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
