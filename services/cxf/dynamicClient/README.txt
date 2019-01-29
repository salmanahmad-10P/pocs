Overview
    - CXF JaxWsDynamicClientFactory is the implementation used in BPMS6 org.jbpm.process.workitem.bpmn2.ServiceTaskHandler
    - This project isolates just the CXF client invoking a SOAP service

1)  start EAP6.* in standalone mode
2)  clone this project from github and cd into this directory
3)  mvn clean install -Pcommunity
4)  cp service/target/auditReview.war $JBOSS_HOME/standalone/deployments
5)  cd client
6)  mvn clean test -Pcommunity
    - should see a dump of the Policy quote object passed in by the client




