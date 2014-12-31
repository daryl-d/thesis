#!/bin/bash

cd SchemaConstants

mvn clean install

cd ../FinalEngine

mvn clean install

cd ../CassandraTradingEngine

mvn clean install

cd ../hector-data-loader

mvn clean install

cd ../InvokeClient

mvn clean install

cd ../SeedProvider

mvn clean install

cd ../SimpleQueryExecutor

mvn clean install

cd ../MakeWorkLoad

mvn clean install

cd ../TimeService

mvn clean install

cd ../TradingEngineService

mvn clean package

rm -rf ../binaries

mkdir -p ../binaries

cp ../hector-data-loader/target/hector-data-loader-jar-with-dependencies.jar ../binaries
cp ../MakeWorkLoad/target/MakeWorkLoad-jar-with-dependencies.jar ../binaries
cp ../SeedProvider/target/SeedProvider.war ../binaries
cp ../SimpleQueryExecutor/target/SimpleQueryExecutor-jar-with-dependencies.jar ../binaries
cp ../TimeService/target/TimeService.war ../binaries
cp ../TradingEngineService/target/TradingEngineService.aar ../binaries







