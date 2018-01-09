package org.switchyard.quickstarts.jca.outbound;

import javax.annotation.Resource;
import javax.jms.*;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;

@MessageDriven(name="TestMDB", activationConfig = {
    @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
    //@ActivationConfigProperty(propertyName = "destination", propertyValue="java:/queue/OrderQueue")
    @ActivationConfigProperty(propertyName = "destination", propertyValue="java:/queue/FillingStockQueue")
})
public class TestMDB implements MessageListener {

    @Resource(name="java:/JmsXA") ConnectionFactory cFactory;
    @Resource(name="java:/queue/ShippingQueue") Destination dObj;

    private int sleepTimeMillis = 1000;

    public void onMessage(Message mObj) {
        Connection connectionObj = null;
        Session sessionObj = null;
        try{
            connectionObj = cFactory.createConnection();
            sessionObj = connectionObj.createSession(false, Session.AUTO_ACKNOWLEDGE);
            MessageProducer m_sender = sessionObj.createProducer(dObj);
            m_sender.send(mObj);
            Thread.sleep(sleepTimeMillis);
            System.out.println("onMessage about send : "+mObj);
        }catch(Exception x) {
            x.printStackTrace();
        }finally{
            try {
                if(sessionObj != null) 
                    sessionObj.close();
                if(connectionObj != null) 
                    connectionObj.close();
            } catch(Exception x) {
                x.printStackTrace();
            }
        }
    }
}
