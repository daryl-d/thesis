Requirements:

- A Java 7 Compliant JRE
- Apache Tomcat 8 (must set CATALINA_HOME environment variable)
- Apache Axis 2 v 1.6.2 (installed within Tomcat as a war)
- Apache Cassandra 2.0.0 rc1 (Please use the attached cassandra.yaml) 
- Apache Maven 

To build the projects simply invoke the build.sh script located in the root directory of the project.
This script will invoke top level maven goals to produce binaries which will be output in the /binaries directory, where / represents the project root directory.

Projects:

- FinalEngine: is a simple trading engine implementation by Cameron Stewart, Michael Farah and Daryl D'Souza as part of SENG3020 workshop at UNSW
- CassandraTradingEngine: builds on FinalEngine, adds in Apache Cassandra 2.0.0 rc1 as a data source, based on the instrument name, date as yyyyMMdd, and an upper and lower bound in seconds representing the the seconds elasped since midnight in the day
- TradingEngineService: wraps the functionailty of CassandraTradingEngine as an Axis 2 webservice, where four parameters are needed, namely instrument, date, upperTime and lowerTime
- InvokeClient: is a basic webservice client, that sends the soap envelope containing the required parameters to the deployed TradingEngineService.
- SchemaConstants: contains schema/Cassandra specific constants used in other modules
- SimpleQueryExecutor: Can execute precomputed workloads or dynamic workloads against the TradingEngineService using InvokeClient
- TimeService: a service to record the execution times of queries from the CassandraTradingEngine
- hector-data-loader: provides a way to load csv data files containing trading information, see the csv directory
