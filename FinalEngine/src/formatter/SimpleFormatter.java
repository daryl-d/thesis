package formatter;

import java.text.ParseException;

import record.Record;

public class SimpleFormatter implements Formatter {

	@Override
	public String format(Record record) {
		return record.toString();
	}

	@Override
	public Record parse(String line) throws ParseException {
		// TODO Auto-generated method stub
		return null;
	}

	// @Override
	// public Record parse(Map<Columns, String> map) throws Exception {
	// // TODO Auto-generated method stub
	// return null;
	// }

}
