package com.daryl.thesis.loader;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Scanner;

import me.prettyprint.cassandra.serializers.CompositeSerializer;
import me.prettyprint.cassandra.serializers.IntegerSerializer;
import me.prettyprint.cassandra.serializers.StringSerializer;
import me.prettyprint.cassandra.service.CassandraHostConfigurator;
import me.prettyprint.cassandra.service.ThriftKsDef;
import me.prettyprint.hector.api.Cluster;
import me.prettyprint.hector.api.Keyspace;
import me.prettyprint.hector.api.beans.Composite;
import me.prettyprint.hector.api.ddl.ColumnFamilyDefinition;
import me.prettyprint.hector.api.ddl.ComparatorType;
import me.prettyprint.hector.api.ddl.KeyspaceDefinition;
import me.prettyprint.hector.api.factory.HFactory;
import me.prettyprint.hector.api.mutation.Mutator;

import org.apache.cassandra.db.marshal.UTF8Type;

import com.daryl.thesis.constants.CassandraConstants;
import com.daryl.thesis.loader.helper.Tick;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * 
 * It is best to use Fixed Columns to load data as the CassandraTradingEngine is expecting this format
 * Hence should use @see {@link LoadDataFixedColumns}
 *
 */

@Deprecated
public class LoadDataWideColumns {

	public static void main(String args[]) throws FileNotFoundException {
		if( args.length != 1 )
		{
			System.err.println( "Only specify input csv file");
			System.exit(1);
		}
		String keySpaceName = CassandraConstants.KEYSPACE_NAME;
		String columnFamilyName = CassandraConstants.COLUMN_FAMILY_NAME;
		CassandraHostConfigurator chc = new CassandraHostConfigurator(
				CassandraConstants.HOST);
		Cluster cluster = HFactory.getOrCreateCluster(CassandraConstants.CLUSTER_NAME, chc);
		
	
		
		ColumnFamilyDefinition columnFamilyDefinition = HFactory
				.createColumnFamilyDefinition(keySpaceName, columnFamilyName,
						ComparatorType.COMPOSITETYPE);

		columnFamilyDefinition
				.setComparatorTypeAlias("(IntegerType, IntegerType)");
		columnFamilyDefinition.setKeyValidationClass(UTF8Type.class.getName());
		columnFamilyDefinition.setDefaultValidationClass(UTF8Type.class
				.getName());

		KeyspaceDefinition keyspaceDefinition = HFactory
				.createKeyspaceDefinition(keySpaceName,
						ThriftKsDef.DEF_STRATEGY_CLASS, 1,
						Arrays.asList(columnFamilyDefinition));
		cluster.addKeyspace(keyspaceDefinition, true);
		Keyspace ks = HFactory.createKeyspace(keySpaceName, cluster);
		File f = new File(args[0]);
		Scanner scanner = new Scanner(f);
		String line;
		boolean first = true;
		String previousSecurity = null;
		Mutator<String> m = null;
		Gson gson = new GsonBuilder().serializeNulls().create();
		int counter = 0;
		while (scanner.hasNext()) {
			line = scanner.nextLine();
			@SuppressWarnings("resource")
			Scanner r = new Scanner(line).useDelimiter(",");
			Tick t = new Tick( r, line );
			r.close();
			
			if( !first )
			{
				if( t.getInstrument().length() != 3)
				{
					System.out.println( t.getInstrument() );
					System.out.println( line );
				}
				if( previousSecurity.equals(t.getInstrument()))
				{
					Composite columnKey = new Composite();
					columnKey.addComponent(t.getTime(), IntegerSerializer.get());
					columnKey.addComponent(counter, IntegerSerializer.get());
					m.addInsertion(t.getInstrument(), columnFamilyName, HFactory.createColumn(columnKey, gson.toJson(t),new CompositeSerializer(), StringSerializer.get()));
				}
				else
				{
					m.execute();
					m = HFactory.createMutator(ks, StringSerializer.get());
					Composite columnKey = new Composite();
					columnKey.addComponent(t.getTime(), IntegerSerializer.get());
					columnKey.addComponent(counter, IntegerSerializer.get());
					m.addInsertion(t.getInstrument(), columnFamilyName, HFactory.createColumn(columnKey, gson.toJson(t),new CompositeSerializer(), StringSerializer.get()));					
				}
			}
			else
			{
				m = HFactory.createMutator(ks, StringSerializer.get());
				Composite columnKey = new Composite();
				columnKey.addComponent(t.getTime(), IntegerSerializer.get());
				columnKey.addComponent(counter, IntegerSerializer.get());
				m.addInsertion(t.getInstrument(), columnFamilyName, HFactory.createColumn(columnKey, gson.toJson(t),new CompositeSerializer(), StringSerializer.get()));
			}

			first = false;
			++counter;
			previousSecurity = t.getInstrument();
			System.gc();
			System.out.println(counter);
		}
		
		if( m != null )
		{
			m.execute();
		}
		
		scanner.close();
	}
}
