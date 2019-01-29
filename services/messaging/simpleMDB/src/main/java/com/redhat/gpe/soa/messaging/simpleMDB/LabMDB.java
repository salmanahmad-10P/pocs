package com.redhat.gpe.soa.messaging.simpleMDB;

import java.util.Random;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.ejb.MessageDrivenContext;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.jms.*;

import org.apache.log4j.Logger;

@MessageDriven(name = "LabMDB", activationConfig = { 
    @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
    @ActivationConfigProperty(propertyName = "messageSelector", propertyValue = "target = 'mdb'"),
    @ActivationConfigProperty(propertyName = "destination", propertyValue = "jms/queue/KIE.SESSION")
})
@TransactionManagement(value = TransactionManagementType.CONTAINER)
public class LabMDB implements MessageListener {

    private static Object lockObj = new Object();
    private @Resource MessageDrivenContext ctx;

    private Logger log = Logger.getLogger("LabMDB");

    @PostConstruct
    void init() throws JMSException{
        log.info("init() ...");
    }

    @PreDestroy
    void close() throws Exception {
        log.info("close()");
    }

    public void onMessage(final Message message) {
        Session producerSession = null;
        try {
            String textBody = ((TextMessage)message).getText();
            log.info("onMessage() received message with body = "+textBody);
        } catch(RuntimeException x) {
            throw x;
        } catch(Exception x) {
            x.printStackTrace();
        } finally {
        }
    }
}
