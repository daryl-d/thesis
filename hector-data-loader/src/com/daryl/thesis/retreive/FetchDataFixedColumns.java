package com.daryl.thesis.retreive;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.daryl.thesis.constants.CassandraConstants;
import com.daryl.thesis.constants.SchemaConstants;

import me.prettyprint.cassandra.serializers.StringSerializer;
import me.prettyprint.cassandra.service.CassandraHostConfigurator;
import me.prettyprint.hector.api.Cluster;
import me.prettyprint.hector.api.Keyspace;
import me.prettyprint.hector.api.beans.HColumn;
import me.prettyprint.hector.api.beans.OrderedRows;
import me.prettyprint.hector.api.beans.Row;
import me.prettyprint.hector.api.factory.HFactory;
import me.prettyprint.hector.api.query.QueryResult;
import me.prettyprint.hector.api.query.RangeSlicesQuery;


public class FetchDataFixedColumns 
{

	public static void main(String args[])
	{
		Set<String> doubleValues = new HashSet<String>();
		Set<String> longValues = new HashSet<String>();
		Set<String> integerValues = new HashSet<String>();
		
		doubleValues.add(SchemaConstants.PRICE);
		doubleValues.add(SchemaConstants.VALUE);
		doubleValues.add(SchemaConstants.OLD_PRICE);
		
		longValues.add(SchemaConstants.TIME);
		longValues.add(SchemaConstants.TRANSACTION_ID);
		longValues.add(SchemaConstants.BID_ID);
		longValues.add(SchemaConstants.ASK_ID);
		longValues.add(SchemaConstants.BUYER_BROKER_ID);
		longValues.add(SchemaConstants.SELLER_BROKER_ID);
		
		integerValues.add(SchemaConstants.DATE);
		integerValues.add(SchemaConstants.VOLUME);
		integerValues.add(SchemaConstants.UNDISCLOSED_VOLUME);
		integerValues.add(SchemaConstants.OLD_VOLUME);
		
		String keySpaceName = CassandraConstants.KEYSPACE_NAME;
		String columnFamilyName = CassandraConstants.COLUMN_FAMILY_NAME;
		CassandraHostConfigurator chc = new CassandraHostConfigurator(
				CassandraConstants.HOST);
		Cluster cluster = HFactory.getOrCreateCluster(CassandraConstants.CLUSTER_NAME, chc);

		Keyspace keyspace = HFactory.createKeyspace(keySpaceName, cluster);
		
		
		RangeSlicesQuery<String, String, String> rangeQuery = HFactory.createRangeSlicesQuery(keyspace, StringSerializer.get(), StringSerializer.get(), StringSerializer.get());
		rangeQuery.setColumnFamily(columnFamilyName);
		rangeQuery.setKeys("ABC20120831000000000", "ABC20120831864000000");
		rangeQuery.setRange(null, null, false, 20);
		rangeQuery.setRowCount(14624);
		QueryResult<OrderedRows<String, String, String>> result = rangeQuery.execute();
		
		OrderedRows<String, String, String> rows =  result.get();
		
		Iterator<Row<String, String, String>> iterator = rows.iterator();
		int i = 0;
		while(iterator.hasNext())
		{
			++i;
			Row<String, String, String> r = iterator.next();
			System.out.println(r.getKey() + ":");
			for( HColumn<String, String> c :r.getColumnSlice().getColumns())
			{
				if(doubleValues.contains(c.getName()))
				{
					
					System.out.println( c.getName() + ": " + c.getValueBytes().getDouble());
				}
				else if(integerValues.contains(c.getName()))
				{
					System.out.println( c.getName() + ": " + c.getValueBytes().getInt());
				}
				else if(longValues.contains(c.getName()))
				{
					System.out.println( c.getName() + ": " + c.getValueBytes().getLong());
				}
				else
				{
					System.out.println( c.getName() + ": " + c.getValue());
				}
				
			}
			System.out.println("-----------------------------------");
			
		}
		System.out.println(i);
		
		
	}
}
