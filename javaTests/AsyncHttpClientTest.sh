#!/bin/sh

CLASSPATH=.
CLASSPATH=$CLASSPATH:/shared/source/async-http-client/target/async-http-client-1.6.3-SNAPSHOT.jar
CLASSPATH=$CLASSPATH:$HORNETQ_HOME/lib/netty.jar
CLASSPATH=$CLASSPATH:~/.m2/repository/org/slf4j/slf4j-api/1.6.1/slf4j-api-1.6.1.jar

javac -classpath $CLASSPATH AsyncHttpClientTest.java

java -classpath $CLASSPATH AsyncHttpClientTest $1
