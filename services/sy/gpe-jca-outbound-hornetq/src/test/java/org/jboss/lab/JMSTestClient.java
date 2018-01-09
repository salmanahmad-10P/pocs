package org.jboss.lab;

import java.io.File;
import java.math.BigDecimal;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentHashMap;

import javax.naming.InitialContext;

import javax.jms.*;
import javax.naming.Context;

import org.apache.log4j.Logger;

import org.hornetq.api.core.DiscoveryGroupConfiguration;
import org.hornetq.api.core.UDPBroadcastGroupConfiguration;
import org.hornetq.api.core.client.HornetQClient;
import org.hornetq.api.jms.HornetQJMSClient;
import org.hornetq.api.jms.JMSFactoryType;

/**
 *  JMSTestClient
 *  JA Bride
 *  23 March, 2008
 */
public class JMSTestClient {

    public static final String UNBLOCKING_CALL_THAT_WILL_NOT_GET_RESPONSE="Unblocking a blocking call that will never get a response";
    public static final String TIMED_OUT_WAITING_FOR_RESPONSE="Timed out waiting for response when sending packet";
    public static final String PROPERTIES_FILE_NAME="/lab.properties";
    private static final String COUNTER = "counter";
    private static Logger log = Logger.getLogger(JMSTestClient.class);

    private static String cFactoryName = "jms/RemoteConnectionFactory";
    private static int clientCount = 0;
    private static int requestsPerClient = 0;
    private static int byteMessageSize = 1;
    private static boolean isPersistent = false;
    private static String gwDObjName = null;
    private static String gwDObjPath = null;
    private static String consumeDestinationName = null;
    private static String consumeDestinationPath = null;
    private static int sleepTimeMillis = 0;
    private static int totalCount = 0;
    private static Connection connectionObj = null;
    private static Destination gwDObj = null;
    private static Destination consumeDestination = null;
    private static ExecutorService execObj = null;
    private static BigDecimal bdThousand;
    private static int discoverPort = 0;
    private static String discoverHost;
    private static int labPort = 0;
    private static String labHost;
    private static boolean useDiscover=true;
    private static int receiveBlockTimeMillis=30000;
    private static boolean setDupsStringProperty=true;
    private static final ConcurrentMap<String, AtomicInteger> serverNodeCountHash = new ConcurrentHashMap<String, AtomicInteger>();
    //private static final String[] orders = {"BREAD", "PIZZA", "JAM", "POTATO", "MILK", "JAM"};
    private static final String[] orders = {"BREAD"};
    private static boolean consumeMessages=true;
    private static AtomicInteger mCounter = new AtomicInteger();

    public static void main(String args[]) {
        Properties properties = new Properties();
        try {
            properties.load(JMSTestClient.class.getResourceAsStream(PROPERTIES_FILE_NAME));
            if(properties.size() == 0)
                throw new RuntimeException("start() no properties defined in "+PROPERTIES_FILE_NAME);

            clientCount = Integer.parseInt(properties.getProperty("org.jboss.lab.clientCount", "1"));
            requestsPerClient = Integer.parseInt(properties.getProperty("org.jboss.lab.requestsPerClient", "1"));

            receiveBlockTimeMillis = Integer.parseInt(properties.getProperty("org.jboss.lab.receive.block.time.millis", "30000"));

            useDiscover = Boolean.parseBoolean(properties.getProperty("org.jboss.lab.test.useDiscover", "true"));
            setDupsStringProperty = Boolean.parseBoolean(properties.getProperty("org.jboss.lab.test.setDupsStringProperty", "true"));

            sleepTimeMillis = Integer.parseInt(properties.getProperty("org.jboss.lab.sleepTimeMillis", "100"));

            discoverHost = properties.getProperty("jboss.messaging.group.address", "localhost");
            discoverPort = Integer.parseInt(properties.getProperty("jboss.messaging.group.port", "4547"));
            labHost = properties.getProperty("remote.connection.lab.host", "localhost");
            labPort = Integer.parseInt(properties.getProperty("remote.connection.lab.port", "4547"));

            consumeMessages = Boolean.parseBoolean(properties.getProperty("org.jboss.lab.test.consumeMessages", "false"));

            isPersistent = Boolean.parseBoolean(properties.getProperty("org.jboss.lab.isPersistent", "TRUE"));
            gwDObjName = properties.getProperty("org.jboss.destinationName");
            gwDObjPath = properties.getProperty("org.jboss.destinationPath");
            consumeDestinationName = properties.getProperty("org.jboss.consumeDestinationName");
            consumeDestinationPath = properties.getProperty("org.jboss.consumeDestinationPath");


            StringBuilder sBuilder = new StringBuilder();
            sBuilder.append("\n\tclientCount = "+clientCount);
            sBuilder.append("\n\tmessagesPerClient = "+requestsPerClient);
            sBuilder.append("\n\tisPersistent = "+isPersistent);
            sBuilder.append("\n\tgateway destination = "+gwDObjName);
            sBuilder.append("\n\tsleepTime = "+sleepTimeMillis);
            sBuilder.append("\n\tdiscoverHost = "+discoverHost);
            sBuilder.append("\n\tdiscoverPort = "+discoverPort);
            sBuilder.append("\n\tuseDiscover = "+useDiscover);
            sBuilder.append("\n\tsetDupsStringProperty = "+setDupsStringProperty);
            sBuilder.append("\n\treceiveBlockTimeMillis = "+receiveBlockTimeMillis);
            sBuilder.append("\n\tconsumeMessages = "+consumeMessages);
            System.out.println(sBuilder.toString());
            Thread.sleep(1000);

            ConnectionFactory cFactory = null;

            if(useDiscover) {
                // use proprietary hornetq classes to discover brokers on the network
                UDPBroadcastGroupConfiguration udpCfg = new UDPBroadcastGroupConfiguration(discoverHost, discoverPort, null, -1);
                DiscoveryGroupConfiguration groupConfiguration = new DiscoveryGroupConfiguration(HornetQClient.DEFAULT_DISCOVERY_INITIAL_WAIT_TIMEOUT, HornetQClient.DEFAULT_DISCOVERY_INITIAL_WAIT_TIMEOUT, udpCfg);
                org.hornetq.jms.client.HornetQConnectionFactory hqcFactory = HornetQJMSClient.createConnectionFactoryWithHA(groupConfiguration, JMSFactoryType.QUEUE_CF);
                hqcFactory.setReconnectAttempts(-1);
                cFactory = (ConnectionFactory)hqcFactory;
                gwDObj = HornetQJMSClient.createQueue(gwDObjName);
                consumeDestination = HornetQJMSClient.createQueue(consumeDestinationName);
            }else {
                Properties env = new Properties();
                env.put(Context.INITIAL_CONTEXT_FACTORY, "org.jboss.naming.remote.client.InitialContextFactory");
                env.put(Context.PROVIDER_URL, "remote://"+labHost+":"+labPort);
                Context jndiContext = new InitialContext(env);
                cFactory = (ConnectionFactory)jndiContext.lookup(cFactoryName);
                gwDObj = (Destination)jndiContext.lookup(gwDObjPath);
                consumeDestination = (Destination)jndiContext.lookup(consumeDestinationPath);
                jndiContext.close();
            }

            //session objects have all of the load-balancing and fail-over magic .... only need one Connection object
            connectionObj = cFactory.createConnection();
            connectionObj.setExceptionListener(new ExceptionListener() {
                public void onException(final JMSException e) {
                    System.out.println("JMSException = "+e.getLocalizedMessage());
                }
            });
            connectionObj.start();
            System.out.println("main() just created smart hornetq connection to remote broker : "+connectionObj);

            if(!consumeMessages) {
                execObj = Executors.newFixedThreadPool(clientCount);
                for(int t=1; t <= clientCount; t++) {
                    Runnable threadObj = new JMSClient(new Integer(t));
                    execObj.execute(threadObj);
                }

                execObj.shutdown();
                execObj.awaitTermination(1200, TimeUnit.MINUTES);
                System.out.println("main() all tasks completed on ExecutorService .... server node counts as follows : ");
                Iterator nodeIterator = serverNodeCountHash.keySet().iterator();
                while(nodeIterator.hasNext()) {
                    String nodeId = (String)nodeIterator.next();
                    System.out.println("\t"+nodeId+"\t"+serverNodeCountHash.get(nodeId));
                }
            } else {
                Runnable consumerRunner = new ConsumerThread();
                Thread consumerThread = new Thread(consumerRunner);
                consumerThread.start();
            }

        } catch(Throwable x) {
            x.printStackTrace();
        } finally {
/*
            try {
                if(connectionObj != null)
                    connectionObj.close();
            } catch(Exception x) {
                x.printStackTrace();
            }
*/
        }
    }

    private synchronized static int computeTotal(int x) {
        totalCount = totalCount + x;
        return totalCount;
    }

    static class ConsumerThread implements Runnable {

        private int consumedCount = 0;

        public void run() {
            try {
                Session mSession = connectionObj.createSession(false, Session.AUTO_ACKNOWLEDGE);
                MessageConsumer m_consumer = mSession.createConsumer(consumeDestination);
                System.out.println("ConsumerThread.run() now listening for incoming messages on destination = "+consumeDestinationName);
                while(true) {
                    consumedCount++;
                    Message mObj = m_consumer.receive();
                    System.out.println("onMessage() consumedCount = "+consumedCount+" : message = "+mObj);
                }
            } catch(Exception x) {
                x.printStackTrace();
            }
        }
    }

    static class JMSClient implements Runnable{
        private Integer id = 0;
        int counter = 0;
        long threadStart = 0L;
        boolean sessionGood=true;
        Destination tempQueue = null;
        Session sessionObj = null;
        MessageProducer m_sender = null;
        MessageConsumer m_consumer = null;
 
        public JMSClient(Integer id) {
            this.id = id;
        }

        public void run() {
            try {
                sessionObj = connectionObj.createSession(false, Session.CLIENT_ACKNOWLEDGE);
                tempQueue = sessionObj.createTemporaryQueue();
                m_sender = sessionObj.createProducer(gwDObj);
                m_consumer = sessionObj.createConsumer(tempQueue);
                
                // in this particular use-case, not using either a unique Id nor timestamp on the message
                m_sender.setDisableMessageID(true);
                m_sender.setDisableMessageTimestamp(true);

                if(isPersistent)
                    m_sender.setDeliveryMode(DeliveryMode.PERSISTENT);
                else
                    m_sender.setDeliveryMode(DeliveryMode.NON_PERSISTENT);

                threadStart = System.currentTimeMillis();
                String response = null;
                for(counter = 0; counter < requestsPerClient; counter++) {
                    long originalTime = System.currentTimeMillis();
                    for (String order : orders) {
                        final TextMessage tMessage = sessionObj.createTextMessage();
                        tMessage.setText(order);
                        int thisCount = mCounter.incrementAndGet();
                        tMessage.setIntProperty(COUNTER, thisCount);
                        if(setDupsStringProperty){
                            tMessage.setStringProperty(org.hornetq.api.core.Message.HDR_DUPLICATE_DETECTION_ID.toString(), createRandomString());
                        }
                        System.out.println("sending message : count = "+thisCount+" "+tMessage);
                        sendMessage(tMessage);
                    }
                    Thread.sleep(sleepTimeMillis );
                }

                int tCount = computeTotal(counter);
                response = "THREAD_COMPLETE!\t"+id+"\t"+tCount+"\t "+((System.currentTimeMillis() - threadStart)/1000);
                System.out.println(response);
        
            } catch(Throwable x) {
                System.out.println("run() id  = "+id+"    :    Throwable = "+x);
                x.printStackTrace();
            } finally {
                try {
                    if(sessionObj != null) {
                        sessionObj.close();
                    }
                } catch(Exception x) {
                    x.printStackTrace();
                }
            }
        }

        private void handleException(JMSException x) {
            x.printStackTrace();
        }

        private void sendMessage(Message tMessage){
            String id = null;
            try {
                id = tMessage.getStringProperty(org.hornetq.api.core.Message.HDR_DUPLICATE_DETECTION_ID.toString());
                m_sender.send(tMessage);
            } catch(JMSException x) {
                if(x.getMessage().contains(TIMED_OUT_WAITING_FOR_RESPONSE)) {
                    // as per section 39.2.1.3 of hornetq manual:  Handling Blocking Calls During Failover
                    // http://docs.jboss.org/hornetq/2.2.14.Final/user-manual/en/html_single/index.html#ha.automatic.failover.blockingcalls
                    System.out.println("sendMessage() resending message "+id+" because hornetq client previously responded with:  "+TIMED_OUT_WAITING_FOR_RESPONSE);
                    sendMessage(tMessage);
                }else if(x.getMessage().contains(UNBLOCKING_CALL_THAT_WILL_NOT_GET_RESPONSE)) {
                    System.out.println("sendMessage() resending message "+id+" because hornetq client previously responded with:  "+UNBLOCKING_CALL_THAT_WILL_NOT_GET_RESPONSE);
                    sendMessage(tMessage);
                }else {
                    x.printStackTrace();
                    throw new RuntimeException("sendMessage() unable to send message due to following JMSException:  "+x.getLocalizedMessage());
                }
            }
        }
    }



    private static String createRandomString() {
        Random random = new Random(System.currentTimeMillis());
        long randomLong = random.nextLong();
        return Long.toHexString(randomLong);
    }

}
