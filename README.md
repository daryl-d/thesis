Requirements:

A Java 7 Compliant JRE
Apache Tomcat 8
Apache Axis 2 v 1.6.2 (installed within Tomcat)
Apache Cassandra 2.0.0 rc1 (Must use the Order Preserving Partitioner) 

Maven has been chosen as the build tool.

To build the projects simply invoke the build.sh script located in the root directory of the project.
This script will invoke top level maven goals to produce binaries which will be output in the /binaries directory, where / represents the project root directory.
