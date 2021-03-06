package com.daryl.thesis.loader.helper;
import java.io.File;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicInteger;

import com.daryl.thesis.constants.CassandraConstants;
import com.daryl.thesis.constants.SchemaConstants;

import me.prettyprint.cassandra.serializers.DoubleSerializer;
import me.prettyprint.cassandra.serializers.IntegerSerializer;
import me.prettyprint.cassandra.serializers.LongSerializer;
import me.prettyprint.cassandra.serializers.StringSerializer;
import me.prettyprint.hector.api.Keyspace;
import me.prettyprint.hector.api.factory.HFactory;
import me.prettyprint.hector.api.mutation.Mutator;

public class LoadFile implements Runnable {

	private final String columnFamilyName = CassandraConstants.COLUMN_FAMILY_NAME;
	private final String file;
	private AtomicInteger count;
	private Keyspace ks;
	private StringSerializer st;

	public LoadFile(String fileName, AtomicInteger counter, Keyspace keyspace,
			StringSerializer s) {
		file = fileName;
		count = counter;
		ks = keyspace;
		st = s;

	}

	@Override
	public void run() {
		File f = new File(file);
		try {
			Scanner scanner = new Scanner(f);
			String line;
			int counter = 0;

			Mutator<String> mutator = HFactory.createMutator(ks, st);
			while (scanner.hasNext()) {

				if (counter == 1000) {
					mutator.execute();
					mutator = HFactory.createMutator(ks, st);
					counter = 0;
				}
				line = scanner.nextLine();
				Scanner scanLine = new Scanner(line);
				scanLine.useDelimiter(",");

				Tick tick = new Tick(scanLine, line);
				if (tick.getTime() != 0) {
					String key = tick.getInstrument()
							+ String.format("%08d", tick.getDate())
							+ String.format("%09d", tick.getTime());
					mutator.addInsertion(
							key,
							columnFamilyName,
							HFactory.createStringColumn(SchemaConstants.INSTRUMENT,
									tick.getInstrument()));
					mutator.addInsertion(key, columnFamilyName, HFactory
							.createColumn(SchemaConstants.DATE, tick.getDate(),
									StringSerializer.get(),
									IntegerSerializer.get())); // change
																// Int32Type
					mutator.addInsertion(key, columnFamilyName, HFactory
							.createColumn(SchemaConstants.TIME, (long) tick.getTime(),
									StringSerializer.get(),
									LongSerializer.get())); // change LongType
					mutator.addInsertion(
							key,
							columnFamilyName,
							HFactory.createStringColumn(SchemaConstants.RECORD_TYPE,
									tick.getRecord_Type()));
					mutator.addInsertion(key, columnFamilyName, HFactory
							.createColumn(SchemaConstants.PRICE, tick.getPrice(),
									StringSerializer.get(),
									DoubleSerializer.get())); // change
																// DoubleType
					mutator.addInsertion(key, columnFamilyName, HFactory
							.createColumn(SchemaConstants.VOLUME, tick.getVolume(),
									StringSerializer.get(),
									IntegerSerializer.get())); // change
																// Int32Type
					mutator.addInsertion(key, columnFamilyName, HFactory
							.createColumn(SchemaConstants.UNDISCLOSED_VOLUME,
									tick.getUndisclosed_Volume(),
									StringSerializer.get(),
									IntegerSerializer.get())); // change
																// Int32Type
					mutator.addInsertion(key, columnFamilyName, HFactory
							.createColumn(SchemaConstants.VALUE, tick.getValue(),
									StringSerializer.get(),
									DoubleSerializer.get())); // change
																// DoubleType
					mutator.addInsertion(
							key,
							columnFamilyName,
							HFactory.createStringColumn(SchemaConstants.QUALIFIERS,
									tick.getQualifiers()));
					mutator.addInsertion(key, columnFamilyName, HFactory
							.createColumn(SchemaConstants.TRANSACTION_ID, tick.getTrans_ID(),
									StringSerializer.get(),
									LongSerializer.get())); // change LongType
					mutator.addInsertion(key, columnFamilyName, HFactory
							.createColumn(SchemaConstants.BID_ID, tick.getBid_ID(),
									StringSerializer.get(),
									LongSerializer.get()));// change LongType
					mutator.addInsertion(key, columnFamilyName, HFactory
							.createColumn(SchemaConstants.ASK_ID, tick.getAsk_ID(),
									StringSerializer.get(),
									LongSerializer.get()));// change LongType
					mutator.addInsertion(
							key,
							columnFamilyName,
							HFactory.createStringColumn(SchemaConstants.BID_ASK,
									tick.getBid_Ask()));
					mutator.addInsertion(
							key,
							columnFamilyName,
							HFactory.createStringColumn(SchemaConstants.ENTRY_TIME,
									tick.getEntry_Time()));
					mutator.addInsertion(key, columnFamilyName, HFactory
							.createColumn(SchemaConstants.OLD_PRICE, tick.getOld_Price(),
									StringSerializer.get(),
									DoubleSerializer.get())); // change
																// DoubleType
					mutator.addInsertion(
							key,
							columnFamilyName,
							HFactory.createColumn(SchemaConstants.OLD_VOLUME,
									tick.getOld_Volume(),
									StringSerializer.get(),
									IntegerSerializer.get())); // change
																// Int32Type
					mutator.addInsertion(
							key,
							columnFamilyName,
							HFactory.createColumn(SchemaConstants.BUYER_BROKER_ID,
									tick.getBuyer_Broker_ID(),
									StringSerializer.get(),
									LongSerializer.get())); // LongType
					mutator.addInsertion(key, columnFamilyName, HFactory
							.createColumn(SchemaConstants.SELLER_BROKER_ID,
									tick.getSeller_Broker_ID(),
									StringSerializer.get(),
									LongSerializer.get())); // LongType
					System.out.println(count.incrementAndGet());
					++counter;
				}

				scanLine.close();

			}
			if (counter != 0) {
				mutator.execute();
			}
			scanner.close();
		} catch (Exception ex) {
			System.err.println("ERROR:");
			ex.printStackTrace();
		}

	}

}
