package com.daryl.thesis.loader;

import java.util.Arrays;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicInteger;

import me.prettyprint.cassandra.serializers.StringSerializer;
import me.prettyprint.cassandra.service.CassandraHostConfigurator;
import me.prettyprint.cassandra.service.ThriftKsDef;
import me.prettyprint.hector.api.Cluster;
import me.prettyprint.hector.api.Keyspace;
import me.prettyprint.hector.api.ddl.ColumnFamilyDefinition;
import me.prettyprint.hector.api.ddl.ComparatorType;
import me.prettyprint.hector.api.ddl.KeyspaceDefinition;
import me.prettyprint.hector.api.factory.HFactory;

import com.daryl.thesis.constants.CassandraConstants;
import com.daryl.thesis.loader.helper.LoadFile;

public class LoadDataFixedInParallel {

	public static void main(String args[]) {
		AtomicInteger counter = new AtomicInteger(0);
		String keySpaceName = CassandraConstants.KEYSPACE_NAME;
		String columnFamilyName = CassandraConstants.COLUMN_FAMILY_NAME;
		StringSerializer stringSerializer = StringSerializer.get();
		CassandraHostConfigurator chc = new CassandraHostConfigurator(
				CassandraConstants.HOST);
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
		Scanner systemIn = new Scanner(System.in);

		while (systemIn.hasNext()) {
			String file = systemIn.next();

			Thread t = new Thread(new LoadFile(file, counter, ks,
					stringSerializer));
			t.start();

		}

		systemIn.close();

	}
}
