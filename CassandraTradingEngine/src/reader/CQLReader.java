package reader;

import java.util.Date;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.daryl.thesis.constants.CassandraConstants;
import com.daryl.thesis.constants.SchemaConstants;

import me.prettyprint.cassandra.serializers.StringSerializer;
import me.prettyprint.cassandra.service.CassandraHostConfigurator;
import me.prettyprint.hector.api.Cluster;
import me.prettyprint.hector.api.Keyspace;
import me.prettyprint.hector.api.beans.ColumnSlice;
import me.prettyprint.hector.api.beans.OrderedRows;
import me.prettyprint.hector.api.beans.Row;
import me.prettyprint.hector.api.factory.HFactory;
import me.prettyprint.hector.api.query.QueryResult;
import me.prettyprint.hector.api.query.RangeSlicesQuery;
import record.Amend;
import record.CancelTrade;
import record.Delete;
import record.Enter;
import record.OffMarketTrade;
import record.Record;
import record.Record.Type;
import record.Trade;

/**
 * 
 * This class enables the trading engine to read data from Cassandra pertaining
 * to an instrument for a particular date in integer format of yyyyMMdd e.g.
 * 20120402 for times within that day from midnight between lowerTime and
 * upperTime inclusive
 *
 */
public class CQLReader implements RecordReader {
	private static final Logger s_logger = Logger.getLogger(CQLReader.class.getName());
	private static final String CLUSTER_NAME = CassandraConstants.CLUSTER_NAME;
	private static final String KEYSPACE_NAME = CassandraConstants.KEYSPACE_NAME;
	private static final String COLUMN_FAMILY_NAME = CassandraConstants.COLUMN_FAMILY_NAME;
	private static final String LOCAL_HOST = CassandraConstants.HOST;
	private static final int LIMIT = 20000;
	private Cluster cluster;
	private Iterator<Row<String, String, String>> iterator;

	public CQLReader(String instrument, int date, int lowerTime, int upperTime) {
		try {
			CassandraHostConfigurator chc = new CassandraHostConfigurator(
					LOCAL_HOST);
			cluster = HFactory.getOrCreateCluster(CLUSTER_NAME, chc);

			Keyspace keyspace = HFactory.createKeyspace(KEYSPACE_NAME, cluster);

			RangeSlicesQuery<String, String, String> rangeQuery = HFactory
					.createRangeSlicesQuery(keyspace, StringSerializer.get(),
							StringSerializer.get(), StringSerializer.get());
			rangeQuery.setColumnFamily(COLUMN_FAMILY_NAME);
			rangeQuery.setKeys(createKey(instrument, date, lowerTime),
					createKey(instrument, date, upperTime));
			rangeQuery.setRange(null, null, false, LIMIT);

			QueryResult<OrderedRows<String, String, String>> result = rangeQuery
					.execute();

			OrderedRows<String, String, String> rows = result.get();

			iterator = rows.iterator();

		} catch (Exception e) {
			s_logger.log( Level.SEVERE, "Problem", e );
		}
	}

	private String createKey(String instrument, int date, int seconds) {
		StringBuilder sb = new StringBuilder();
		sb.append(instrument);
		sb.append(date);

		int millis = seconds * 1000;

		sb.append(String.format("%09d", millis));

		return sb.toString();

	}

	@Override
	public Record get() {
		if (iterator.hasNext()) {
			return parse(iterator.next());
		} else {
			s_logger.log(Level.INFO, "processed all records !");
			return null;
		}
	}

	@Override
	public void close() {
	}

	@SuppressWarnings("deprecation")
	private static Date fastDateParser(String date, long millis) {
		int year = Integer.parseInt(date.substring(0, 4));
		int month = Integer.parseInt(date.substring(4, 6));
		int day = Integer.parseInt(date.substring(6, 8));

		Date d = new Date(year - 1900, month, day);
		d.setTime(d.getTime() + millis);
		return d;
	}

	private static Record parse(Row<String, String, String> row) {
		ColumnSlice<String, String> columnSlice = row.getColumnSlice();

		final String instrument = getString(columnSlice,
				SchemaConstants.INSTRUMENT);

		final Date time = fastDateParser(
				String.valueOf(getInt(columnSlice, SchemaConstants.DATE)),
				getLong(columnSlice, SchemaConstants.TIME));
		final Record.Type type;
		try {
			type = Record.Type.valueOf(getString(columnSlice,
					SchemaConstants.RECORD_TYPE));

		} catch (IllegalArgumentException e) {
			return null;
		}
		EnumSet<Record.Qualifier> qualifiers = EnumSet
				.noneOf(Record.Qualifier.class);

		try {
			final String tmp[] = getString(columnSlice,
					SchemaConstants.QUALIFIERS).split(" ");
			for (String element : tmp) {
				qualifiers.add(Record.Qualifier.valueOf(element));
			}

		} catch (Exception ex) {

		}

		switch (type) {
		case CONTROL:
			return null;
		case ENTER:
		case AMEND:
		case DELETE: {
			final boolean bid = getString(columnSlice, SchemaConstants.BID_ASK)
					.equals("B");
			float price = -1;
			int volume = -1;
			if (type != Record.Type.DELETE) {
				price = (float) getDouble(columnSlice, SchemaConstants.PRICE);
				volume = (int) getLong(columnSlice, SchemaConstants.VOLUME);
				volume += getLong(columnSlice,
						SchemaConstants.UNDISCLOSED_VOLUME);
			}
			long id;
			int broker;
			if (bid) {
				id = getLong(columnSlice, SchemaConstants.BID_ID);
				broker = getInt(columnSlice, SchemaConstants.BUYER_BROKER_ID);

			} else {
				id = getInt(columnSlice, SchemaConstants.ASK_ID);
				broker = getInt(columnSlice, SchemaConstants.SELLER_BROKER_ID);
			}
			if (type == Record.Type.ENTER) {
				return new Enter(instrument, time, qualifiers, bid, price,
						volume, id, broker);
			} else if (type == Record.Type.AMEND) {
				return new Amend(instrument, time, qualifiers, bid, price,
						volume, id, broker);
			} else {
				return new Delete(instrument, time, qualifiers, bid, id, broker);
			}
		}
		case OFFTR:
		case TRADE: {
			final float price = (float) getDouble(columnSlice,
					SchemaConstants.PRICE);
			final int volume = (int) getLong(columnSlice,
					SchemaConstants.VOLUME);
			final long bidId = getLong(columnSlice, SchemaConstants.BID_ID);
			final int bidBroker;

			bidBroker = (int) getLong(columnSlice,
					SchemaConstants.BUYER_BROKER_ID);

			final long askId = getLong(columnSlice, SchemaConstants.ASK_ID);
			final int askBroker;

			askBroker = (int) getLong(columnSlice,
					SchemaConstants.SELLER_BROKER_ID);

			if (type == Type.OFFTR) {
				return new OffMarketTrade(instrument, time, qualifiers, price,
						volume, bidId, bidBroker, askId, askBroker);
			} else {
				return new Trade(instrument, time, qualifiers, price, volume,
						bidId, bidBroker, askId, askBroker);
			}
		}
		case CANCEL_TRADE: {
			final float price = (float) getDouble(columnSlice,
					SchemaConstants.PRICE);
			final int volume = (int) getLong(columnSlice,
					SchemaConstants.VOLUME);
			return new CancelTrade(instrument, time, qualifiers, price, volume);
		}

		default:
			throw new Error("Not implemented");
		}
	}

	private static int getInt(ColumnSlice<String, String> columnSlice,
			String columnName) {
		try {
			return columnSlice.getColumnByName(columnName).getValueBytes()
					.getInt();
		} catch (Exception ex) {
			s_logger.log( Level.SEVERE, "Unable to get value for " + columnName );
			return 0;
		}
	}

	private static long getLong(ColumnSlice<String, String> columnSlice,
			String columnName) {
		try {
			return columnSlice.getColumnByName(columnName).getValueBytes()
					.getLong();
		} catch (Exception ex) {

			s_logger.log( Level.SEVERE, "Unable to get value for " + columnName );
			return 0L;
		}
	}

	private static String getString(ColumnSlice<String, String> columnSlice,
			String columnName) {
		try {
			return columnSlice.getColumnByName(columnName).getValue();
		} catch (Exception ex) {
			s_logger.log( Level.SEVERE, "Unable to get value for " + columnName );
			return "";
		}
	}

	private static double getDouble(ColumnSlice<String, String> columnSlice,
			String columnName) {
		try {
			return columnSlice.getColumnByName(columnName).getValueBytes()
					.getDouble();
		} catch (Exception ex) {
			s_logger.log( Level.SEVERE, "Unable to get value for " + columnName );
			return 0.00;
		}
	}

}
