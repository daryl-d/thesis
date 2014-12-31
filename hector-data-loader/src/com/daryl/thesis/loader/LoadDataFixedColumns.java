package com.daryl.thesis.loader;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Scanner;

import com.daryl.thesis.constants.CassandraConstants;
import com.daryl.thesis.constants.SchemaConstants;
import com.daryl.thesis.loader.helper.Tick;

import me.prettyprint.cassandra.serializers.DoubleSerializer;
import me.prettyprint.cassandra.serializers.IntegerSerializer;
import me.prettyprint.cassandra.serializers.LongSerializer;
import me.prettyprint.cassandra.serializers.StringSerializer;
import me.prettyprint.cassandra.service.CassandraHostConfigurator;
import me.prettyprint.cassandra.service.ThriftKsDef;
import me.prettyprint.hector.api.Cluster;
import me.prettyprint.hector.api.Keyspace;
import me.prettyprint.hector.api.ddl.ColumnFamilyDefinition;
import me.prettyprint.hector.api.ddl.ComparatorType;
import me.prettyprint.hector.api.ddl.KeyspaceDefinition;
import me.prettyprint.hector.api.factory.HFactory;
import me.prettyprint.hector.api.mutation.Mutator;

public class LoadDataFixedColumns {

	@SuppressWarnings("resource")
	public static void main(String args[]) throws FileNotFoundException {
		if (args.length != 1) {
			System.err.println("specify input csv file");
			System.exit(1);
		}

		String keySpaceName = CassandraConstants.KEYSPACE_NAME;
		String columnFamilyName = CassandraConstants.COLUMN_FAMILY_NAME;
		StringSerializer stringSerializer = StringSerializer.get();
		CassandraHostConfigurator chc = new CassandraHostConfigurator(
				"localhost");
		chc.setMaxActive(250);

		Cluster cluster = HFactory.getOrCreateCluster(
				CassandraConstants.CLUSTER_NAME, chc);
		if (cluster.describeKeyspace(keySpaceName) != null) {
			cluster.dropKeyspace(keySpaceName);
			System.err.println("removed ks: " + keySpaceName);
		}
		ColumnFamilyDefinition cfDef = HFactory.createColumnFamilyDefinition(
				keySpaceName, columnFamilyName, ComparatorType.UTF8TYPE);
		KeyspaceDefinition newKeyspace = HFactory.createKeyspaceDefinition(
				keySpaceName, ThriftKsDef.DEF_STRATEGY_CLASS, 1,
				Arrays.asList(cfDef));
		cluster.addKeyspace(newKeyspace);

		Keyspace ks = HFactory.createKeyspace(keySpaceName, cluster);
		// Scanner systemIn = new Scanner(System.in);
		int i = 0;
		// while(systemIn.hasNext()){
		File f = new File(args[0]);

		Scanner scanner = new Scanner(f);
		String line;
		int counter = 0;

		Mutator<String> mutator = HFactory.createMutator(ks, stringSerializer);
		while (scanner.hasNext()) {
			if (counter == 1000) {
				mutator.execute();
				mutator = HFactory.createMutator(ks, stringSerializer);
				counter = 0;
			}
			line = scanner.nextLine();
			Scanner scanLine = (new Scanner(line)).useDelimiter(",");

			Tick tick = new Tick(scanLine, line);
			if (tick.getTime() != 0) {
				String key = tick.getInstrument()
						+ String.format("%08d", tick.getDate())
						+ String.format("%09d", tick.getTime());
				mutator.addInsertion(key, columnFamilyName, HFactory
						.createStringColumn(SchemaConstants.INSTRUMENT,
								tick.getInstrument()));
				mutator.addInsertion(key, columnFamilyName,
						HFactory.createColumn(SchemaConstants.DATE,
								tick.getDate(), StringSerializer.get(),
								IntegerSerializer.get())); // change
															// Int32Type
				mutator.addInsertion(key, columnFamilyName, HFactory
						.createColumn(SchemaConstants.TIME,
								(long) tick.getTime(), StringSerializer.get(),
								LongSerializer.get())); // change LongType
				mutator.addInsertion(key, columnFamilyName, HFactory
						.createStringColumn(SchemaConstants.RECORD_TYPE,
								tick.getRecord_Type()));
				mutator.addInsertion(key, columnFamilyName, HFactory
						.createColumn(SchemaConstants.PRICE, tick.getPrice(),
								StringSerializer.get(), DoubleSerializer.get())); // change
																					// DoubleType
				mutator.addInsertion(key, columnFamilyName,
						HFactory.createColumn(SchemaConstants.VOLUME,
								tick.getVolume(), StringSerializer.get(),
								IntegerSerializer.get())); // change
															// Int32Type
				mutator.addInsertion(
						key,
						columnFamilyName,
						HFactory.createColumn(
								SchemaConstants.UNDISCLOSED_VOLUME,
								tick.getUndisclosed_Volume(),
								StringSerializer.get(), IntegerSerializer.get())); // change
																					// Int32Type
				mutator.addInsertion(key, columnFamilyName, HFactory
						.createColumn(SchemaConstants.VALUE, tick.getValue(),
								StringSerializer.get(), DoubleSerializer.get())); // change
																					// DoubleType
				mutator.addInsertion(key, columnFamilyName, HFactory
						.createStringColumn(SchemaConstants.QUALIFIERS,
								tick.getQualifiers()));
				mutator.addInsertion(key, columnFamilyName, HFactory
						.createColumn(SchemaConstants.TRANSACTION_ID,
								tick.getTrans_ID(), StringSerializer.get(),
								LongSerializer.get())); // change LongType
				mutator.addInsertion(key, columnFamilyName, HFactory
						.createColumn(SchemaConstants.BID_ID, tick.getBid_ID(),
								StringSerializer.get(), LongSerializer.get()));// change
																				// LongType
				mutator.addInsertion(key, columnFamilyName, HFactory
						.createColumn(SchemaConstants.ASK_ID, tick.getAsk_ID(),
								StringSerializer.get(), LongSerializer.get()));// change
																				// LongType
				mutator.addInsertion(key, columnFamilyName, HFactory
						.createStringColumn(SchemaConstants.BID_ASK,
								tick.getBid_Ask()));
				mutator.addInsertion(key, columnFamilyName, HFactory
						.createStringColumn(SchemaConstants.ENTRY_TIME,
								tick.getEntry_Time()));
				mutator.addInsertion(key, columnFamilyName, HFactory
						.createColumn(SchemaConstants.OLD_PRICE,
								tick.getOld_Price(), StringSerializer.get(),
								DoubleSerializer.get())); // change
															// DoubleType
				mutator.addInsertion(key, columnFamilyName, HFactory
						.createColumn(SchemaConstants.OLD_VOLUME,
								tick.getOld_Volume(), StringSerializer.get(),
								IntegerSerializer.get())); // change
															// Int32Type
				mutator.addInsertion(key, columnFamilyName, HFactory
						.createColumn(SchemaConstants.BUYER_BROKER_ID,
								tick.getBuyer_Broker_ID(),
								StringSerializer.get(), LongSerializer.get())); // LongType
				mutator.addInsertion(key, columnFamilyName, HFactory
						.createColumn(SchemaConstants.SELLER_BROKER_ID,
								tick.getSeller_Broker_ID(),
								StringSerializer.get(), LongSerializer.get())); // LongType

				++counter;
				++i;
				System.out.println(i);
			}

			scanLine.close();

		}
		if (counter != 0) {
			mutator.execute();
		}
		scanner.close();

		// }
	}
}
