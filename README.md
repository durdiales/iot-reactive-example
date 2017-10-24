# iot-reactive-example

## Motivation

This repository contains some code examples focused on developing some IoT (Internet of Things) reactive platform using modern web technologies, which may be used as a short introduction guide to the SMACK stack (Spark Mesos Akka Cassandra Kafka).

In order to get a deeper knowledge about how functional programming may help to develop modern reactive platforms, focus will be on how to develop some multi-tier system, that will include some of the most required features in modern systems, among others: based on microservices, using some NoSQL persistent storage, and processing input data for near-real-time analytics.

## Specifications

-	Git
-	IDE - IntelliJ 17.x (https://www.jetbrains.com/idea/download/download-thanks.html?platform=windows&code=IIC)
-	sbt 0.13.x (http://www.scala-sbt.org/download.html)
-	Scala 2.11.8 or later (https://downloads.lightbend.com/scala/2.12.4/scala-2.12.4.msi) 
-	Python 2.7
-	pip
-	cqlsh added to Python 2.7 for Connection to Cassandra

## Running Project: Dev Environment

-   One command to start all > sbt clean runAll
-   Hot reload code

## Http Resquest Examples: ARC (Advanced Rest Client)

-   ex_rest-client-iot-reactive-example.txt

## cqlsh on Windows

-   cd C:/Python27 -- <PYTHON_PATH>  
-   <PYTHON_PATH>/Scripts --> pip install cqlsh -> will be added to: C:\Python27\Scripts
-   run python   
    Python 2.7.13 (v2.7.13:a06454b1afa1, Dec 17 2016, 20:42:59) [MSC v.1500 32 bit (Intel)] on win32
    Type "help", "copyright", "credits" or "license" for more information.
-   Cql Commands using: ex_cql-commands-iot-reactive-example.txt

## Cql Commands to get data from Cassandra: Dev Environment

-   ex_cql-commands-iot-reactive-example.txt

## Measure Analytics

-   Download Hadoop: 
    http://www.apache.org/dyn/closer.cgi/hadoop/common/hadoop-2.7.4/hadoop-2.7.4.tar.gz
    or
    https://github.com/durdiales/hadoop-2.7.1
-   Descompress it, for example: C:/hadoop-2.7.1
-   Create env variable: 
    HADOOP_HOME=C:/hadoop-2.7.1
    HADOOP_HOME_BIN=C:/hadoop-2.7.1/bin and add them to Path

## Some Considerations

-   Error Starting Cassandra:
    .....Exception (java.lang.RuntimeException) encountered during startup: Cannot bind to URL [rmi://localhost:4099/jmxrmi]: javax.naming.ServiceUnavailableException [Root exception is java.rmi.ConnectException: Connection refused to host: localhost; nested exception is: 
    	java.net.ConnectException: Connection refused]
    java.lang.RuntimeException: Cannot bind to URL [rmi://localhost:4099/jmxrmi]: javax.naming.ServiceUnavailableException [Root exception is java.rmi.ConnectException: Connection refused to host: localhost; nested exception is: 
    	java.net.ConnectException: Connection refused]

-   You need to stop process that is using port: 4099
-   run in console > netstat -ao
-   see PID is using port:4099
-   kill -9 PID