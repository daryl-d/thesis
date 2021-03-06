package com.daryl.thesis.retreive;
import com.daryl.thesis.constants.CassandraConstants;
import com.daryl.thesis.loader.LoadDataFixedColumns;
import com.daryl.thesis.loader.LoadDataWideColumns;

import me.prettyprint.cassandra.serializers.CompositeSerializer;
import me.prettyprint.cassandra.serializers.IntegerSerializer;
import me.prettyprint.cassandra.serializers.StringSerializer;
import me.prettyprint.cassandra.service.CassandraHostConfigurator;
import me.prettyprint.hector.api.Cluster;
import me.prettyprint.hector.api.Keyspace;
import me.prettyprint.hector.api.beans.ColumnSlice;
import me.prettyprint.hector.api.beans.Composite;
import me.prettyprint.hector.api.beans.HColumn;
import me.prettyprint.hector.api.factory.HFactory;
import me.prettyprint.hector.api.query.QueryResult;
import me.prettyprint.hector.api.query.SliceQuery;

/**
 * 
 * "Fixed Columns is preferred to wide columns e.g. @see {@link LoadDataFixedColumns}, however if data loaded using @see {@link LoadDataWideColumns} this class should be used
 *
 */
@Deprecated
public class FetchDataWideColumns 
{
	public static void main(String args[]) 
	{
		String keySpaceName = CassandraConstants.KEYSPACE_NAME;
		String columnFamilyName = CassandraConstants.COLUMN_FAMILY_NAME;
		CassandraHostConfigurator chc = new CassandraHostConfigurator(
				CassandraConstants.HOST);
		Cluster cluster = HFactory.getOrCreateCluster(CassandraConstants.CLUSTER_NAME, chc);

		Keyspace keyspace = HFactory.createKeyspace(keySpaceName, cluster);
		
		SliceQuery<String,Composite,String> sliceQuery = HFactory.createSliceQuery(keyspace, StringSerializer.get(), new CompositeSerializer(), StringSerializer.get());
		sliceQuery.setColumnFamily(columnFamilyName);
		sliceQuery.setKey("NAB");
		
		Composite startRange = new Composite();
		startRange.addComponent(0, IntegerSerializer.get());
		startRange.addComponent(0, IntegerSerializer.get());
		
		Composite endRange = new Composite();
		endRange.addComponent(42300*1000, IntegerSerializer.get());
		endRange.addComponent(400, IntegerSerializer.get());
		
		sliceQuery.setRange(startRange, endRange, false, 1200);
		
		QueryResult<ColumnSlice<Composite, String>> result = sliceQuery.execute();
		ColumnSlice<Composite, String> cs = result.get();
		
		  for ( HColumn<Composite, String> col: cs.getColumns() ) {
		      System.out.println(col.getValue());
		  }
	}

}
