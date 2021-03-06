package filter;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.text.ParseException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import reader.LineReader;
import reader.RecordReader;
import record.Record;
import writer.LineWriter;
import formatter.CSVFormatter;

public class FilterFactory {

	@SuppressWarnings("resource")
	public static Reader getFileReader(String file) {
		if (file == null) {
			return null;
		}
		InputStream is;
		try {
			if (file.startsWith("jar://")) {
				is = FilterFactory.class.getResourceAsStream(file.replace(
						"jar://", ""));
			} else {
				FileInputStream fs = new FileInputStream(file);
				System.out.println(fs.getChannel().position());
				is = fs;
			}
		} catch (final FileNotFoundException e) {
			e.printStackTrace();
			System.out.println("File not found: " + file);
			return null;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		if (file.endsWith(".gz")) {
			try {
				is = new GZIPInputStream(is);
			} catch (final IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			}
		}

		return new InputStreamReader(is);
	}

	public static Writer getFileWriter(String file) {

		if (file == null) {
			return null;
		}
		OutputStream os;
		try {
			os = new FileOutputStream(file);
		} catch (final FileNotFoundException e) {
			e.printStackTrace();
			return null;
		}
		if (file.endsWith(".gz")) {
			try {
				os = new GZIPOutputStream(os);
			} catch (final IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			}
		}

		return new OutputStreamWriter(os);
	}

	public static RecordReader recordReaderFromReader(Reader r) {
		if (r == null) {
			return null;
		}

		return new LineReader(new BufferedReader(r), new CSVFormatter());
	}

	public static RecordReader rr(String s) {
		return FilterFactory.recordReaderFromReader(FilterFactory
				.getFileReader(s));
	}

	public static void saveToFile(final RecordReader rr, final String file) {
		new Thread() {
			@Override
			public void run() {
				final Writer fw = FilterFactory.getFileWriter(file);
				try {
					FilterFactory.write(rr, fw);

					fw.close();
				} catch (final ParseException e) {
					e.printStackTrace();
				} catch (final IOException e) {
					e.printStackTrace();
				}
			}
		}.start();
	}

	public static RecordReader tradeEngine(RecordReader rr) {
		return new TradeFilterRemover(FilterFactory.tradeEngineCore(rr),
				Record.Type.MARKER);
	}

	public static InstrumentSpecificFilter<TradeEngine> tradeEngineCore(
			RecordReader rr) {
		return new InstrumentSpecificFilter<TradeEngine>(
				new TradeFilterRemover(
						new InstrumentSpecificFilter<MarkerRecordParser>(rr,
								new MarkerRecordParser.Factory()),
						Record.Type.TRADE), new TradeEngine.Factory());
	}

	public static void write(RecordReader rr, Writer w) throws ParseException,
			IOException {
		new LineWriter(rr, new CSVFormatter()).print(w);
	}
}
