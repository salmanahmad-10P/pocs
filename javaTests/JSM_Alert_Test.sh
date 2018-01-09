#!/bin/bash

CLASSPATH=$JBOSS_HOME/server/esb-default/deploy/c4idev/c4idev-alert-domain.jar
#CLASSPATH=$CLASSPATH:$JBOSS_HOME/server/esb-default/deploy/ejb3.deployer/jboss-annotations-ejb3.jar
#CLASSPATH=$CLASSPATH:$JBOSS_HOME/server/esb-default/deploy/ejb3.deployer/jboss-ejb3x.jar
#CLASSPATH=$CLASSPATH:$JBOSS_HOME/server/esb-default/deploy/ejb3.deployer/jboss-ejb3.jar
CLASSPATH=$CLASSPATH:$JBOSS_HOME/client/ejb3-persistence.jar
CLASSPATH=$CLASSPATH:$JBOSS_HOME/client/hibernate-annotations.jar
CLASSPATH=$CLASSPATH:$JBOSS_HOME/client/log4j.jar
CLASSPATH=$CLASSPATH:.

echo "about to compile"
javac -classpath $CLASSPATH JSM_Alert_Test.java
returnCode=$?
if [ $returnCode -ne 0 ];then
            exit 1;
fi

echo "about to run"
java -classpath $CLASSPATH -Djava.security.manager JSM_Alert_Test
