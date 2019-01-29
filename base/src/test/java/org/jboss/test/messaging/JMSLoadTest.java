package org.jboss.test.messaging;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;
import java.util.Date;
import java.util.Properties;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentHashMap;

import javax.jms.*;

import org.apache.log4j.Logger;


/**
 * JMSLoadTest
 *
 * Purpose :  load test JMS broker via various configurable parameters
 * To-DO   :  resolve AUTO_ACKNOWLEDGE problems in 'producer' side of this test in a clustered broker environment using Hornetq 2.2.7
 *
 */
public class JMSLoadTest {

    protected static final String ID = "producerId";
    protected static final String MESSAGES_PER_CLIENT = "messagesPerClient";
    protected static final String MESSAGE_COUNTER = "messageCounter";
    protected static final String CONSUMED_COUNT = "consumedCount";
    protected static final String TERMINATION_COMMAND = "command='terminate'";
    protected static final String BULK_MESSAGES_NAME = "org.jboss.bulkMessages";
    protected static final String CONTROL_NAME = "org.jboss.controlQueue";
    protected static final String QUEUE_PREFIX = "queue/";
    protected static final String CONNECTION_FACTORY = "java:jboss/exported/jms/RemoteConnectionFactory";

    private static final ConcurrentMap<Integer, Long> producerTimeHash = new ConcurrentHashMap<Integer, Long>();
    private static final AtomicInteger consumerCount = new AtomicInteger();

    protected static Logger log = Logger.getLogger("JMSLoadTest");
    protected static int clientCount = 0;
    protected static int messagesPerClient = 0;
    protected static boolean isPersistent = false;
    protected static boolean isTransacted = false;
    protected static int batchSize = 0;
    protected static int byteMessageSize = 0;
    protected static String namingProviderUrl = "";
    protected static String connectionFactory = "/ConnectionFactory";
    protected static String bulkMessageDestinationPath = null;
    protected static String bulkMessageDestinationName = null;
    protected static String controlDestinationPath = null;
    protected static String controlDestinationName = null;
    protected static int producerSleepTimeMillis = 0;
    protected static int consumerSleepTimeMillis = 0;
    protected static boolean startProducer = false;
    protected static boolean startConsumer = false;
    protected static String implementationClass;
    protected static String brokerAddress;
    protected static int brokerPort;

    protected static Connection connection;
    protected static Destination bulkMessageDestination;
    protected static Destination controlDestination;
    
    protected void setConnectionAndDestination() throws JMSException {
        throw new RuntimeException("base setConnectionAndDestination(): need to over-ride me!");
    }


    public static void main(String args[]) {
        setProps();
        
        try {
            //  1)  invoke setConnectionAndDestination() method of jms client implementation
            Class implClass = Class.forName(implementationClass);
            IJMSLoadTest ltObj = (IJMSLoadTest)implClass.newInstance();
            ltObj.setConnectionAndDestination();

            //  2)  now that a JMS Connection has been instantiated, start it for consumption of bulk and/or control messages
            connection.start();


            //  3)  start a single consumer thread ... single consumer thread will spawn configurable # of onMessage() listeners
            if(startConsumer) { 
                Runnable consumerRunner = new ConsumerThread();
                Thread consumerThread = new Thread(consumerRunner);
                consumerThread.start();
            }
            
            //  4)  start a configurable number of producer threads
            if(startProducer) { 
                ExecutorService execObj = Executors.newFixedThreadPool(clientCount);
                long startTime = System.currentTimeMillis();
                for(int t=0; t < clientCount; t++) {
                    Runnable siClient = new ProducerThread(t);
                    execObj.execute(siClient);
                    Thread.sleep(producerSleepTimeMillis);
                }

                //  5)  producer main() thread will block until all producer threads have returned
                execObj.shutdown();
                execObj.awaitTermination(1200, TimeUnit.MINUTES);
                StringBuilder sBuilder = new StringBuilder();
                sBuilder.append("\n\nall tasks completed on ExecutorService .... control messages received in :\n\tNODE_ID\t\t\tcompletionTime (ms)\n\t-------\t\t\t--------------");


                //  6)  all producer threads have returned .... print time duration to send and consume bulk messages
                Iterator nodeIterator = producerTimeHash.keySet().iterator();
                while(nodeIterator.hasNext()) {
                    Integer nodeId = (Integer)nodeIterator.next();
                    sBuilder.append("\n\t"+nodeId+"\t\t\t"+(producerTimeHash.get(nodeId) - startTime));
                }
                log.info(sBuilder.toString());

                // will sleep for two seconds to make sure consumers have actually received all messages
                // appears that when paging kicks in, it's possible that messaging ordering is lost somewhat
                // therefore, the consumer sends a terminaton control message when it receives what it thinks was the last message sent by a producer
                Thread.sleep(2000);

                //  7)  send a control message to the 'consumer' threads to terminate
                Session pSession = connection.createSession(true, Session.SESSION_TRANSACTED);
                TextMessage tMessage = pSession.createTextMessage();
                tMessage.setStringProperty("command", "terminate");
                tMessage.setText("completed");
                MessageProducer m_sender = pSession.createProducer(controlDestination);
                m_sender.send(tMessage);
                pSession.commit();

                // 8)  close connection
                try {
                    if(connection != null)
                        connection.close();
                } catch(Exception x) {}
            }
        } catch(Throwable x) {
            x.printStackTrace();
        }
    }

    static class ConsumerThread implements Runnable, MessageListener {

        public void run() {
            try {
                for(int t=0; t < clientCount; t++) {
                    Session mSession = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
                    MessageConsumer m_consumer = mSession.createConsumer(bulkMessageDestination);
                    m_consumer.setMessageListener(new ConsumerThread());
                }
                log.info("run() started consumers ... now waiting for termination message ....");
                Session mSession = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
                MessageConsumer mConsumer = mSession.createConsumer(controlDestination, TERMINATION_COMMAND, false);
                TextMessage tMessage = (TextMessage)mConsumer.receive();
                log.info("run() termination message received.... total bulk messages = "+consumerCount.get());
            } catch(Exception x) {
                throw new RuntimeException(x);
            } finally {
                try {
                    if(connection != null)
                        connection.close();
                } catch(Exception x) {}
            }
        }

        public void onMessage(Message message) {
            byte[] bodyAsBytes = null;
            if(message instanceof BytesMessage) {
                Session pSession = null;
                try {
                    consumerCount.incrementAndGet();
                    final BytesMessage bMessage = (BytesMessage)message;
                    int messagesPerClient = bMessage.getIntProperty(MESSAGES_PER_CLIENT);
                    int messageCounter = bMessage.getIntProperty(MESSAGE_COUNTER);
                    if(messageCounter == messagesPerClient) {
                        ByteArrayOutputStream out = new ByteArrayOutputStream();
                        byte[] ba = new byte[1000];
                        int iQread;
                        while (-1 != (iQread = bMessage.readBytes(ba))) {
                            if (iQread > 0)
                                out.write(ba, 0, iQread);
                            out.close();
                        }
                        bodyAsBytes = out.toByteArray();
                        log.info("onMessage() bulk # "+messageCounter+" : producer # "+bMessage.getStringProperty(ID)+" : size= "+bodyAsBytes.length);
                        pSession = connection.createSession(true, Session.SESSION_TRANSACTED);
                        TextMessage tMessage = pSession.createTextMessage();
                        tMessage.setStringProperty(ID, bMessage.getStringProperty(ID));
                        tMessage.setText("completed");
                        MessageProducer m_sender = pSession.createProducer(controlDestination);
                        m_sender.send(tMessage);
                        pSession.commit();
                    }
                    Thread.sleep(consumerSleepTimeMillis);
                } catch(Exception x) {
                    throw new RuntimeException(x);
                } finally {
                    try {
                        if(pSession != null)
                            pSession.close();
                    } catch(Exception x) {
                        throw new RuntimeException(x);
                    }
                }
            } else {
                throw new RuntimeException("onMessage() not expecting message of type = "+message.getClass());
            }
        }
    }

    static class ProducerThread implements Runnable {
        private int id = 0;
 
        public ProducerThread(int id) {
            this.id = id;
        }

        public void run() {
            Session sSession = null;
            Session cSession = null;
            try {
                if(isTransacted) {
                    /* message will be sent after each send(), but will not block for an ack
                     * instead, a single ack is sent at commit()
                     */
                    sSession = connection.createSession(true, Session.SESSION_TRANSACTED);
                    cSession = connection.createSession(true, Session.SESSION_TRANSACTED);
                } else {
                    /*  doesn't matter what type of ack_mode is set .... applicable only for consuming messages
                     *  regardless of what ack_mode is specified,  send() method will block waiting for ack from broker
                     */
                    sSession = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
                    cSession = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
                }
                BytesMessage bMessage = sSession.createBytesMessage();
                bMessage.setStringProperty(ID, new Integer(id).toString());
                bMessage.setIntProperty(MESSAGES_PER_CLIENT, messagesPerClient);
                byte[] bytePayload = new byte[byteMessageSize];
                for(int s=0; s < byteMessageSize; s++) {
                    bytePayload[s] = (byte)s;
                }
                bMessage.writeBytes(bytePayload);
                MessageProducer m_sender = sSession.createProducer(bulkMessageDestination);

                // in this particular use-case, not using either a unique Id nor timestamp on the message
                m_sender.setDisableMessageID(true);
                m_sender.setDisableMessageTimestamp(true);

                if(isPersistent)
                    m_sender.setDeliveryMode(DeliveryMode.PERSISTENT);
                else
                    m_sender.setDeliveryMode(DeliveryMode.NON_PERSISTENT);

                for(int s = 1; s <= messagesPerClient; s++) {
                    bMessage.setIntProperty(MESSAGE_COUNTER, s);
                    m_sender.send(bMessage);
                    if(isTransacted && s%batchSize == 0) {
                        sSession.commit();
                    }
                    Thread.sleep(producerSleepTimeMillis);
                }
                if(isTransacted)
                    sSession.commit();

                log.info("ProducerThread.run() delivered # of messages = "+messagesPerClient+"\t: will now block until receive control message to shutdown");
                MessageConsumer mConsumer = cSession.createConsumer(controlDestination, "producerId='"+id+"'", false);
                TextMessage tMessage = (TextMessage)mConsumer.receive();
                if(isTransacted)
                    cSession.commit();

                //log.info("ProducerThread.run() received control message  ... will now shutdown thread");
                producerTimeHash.putIfAbsent(id, System.currentTimeMillis());
                
            } catch(Throwable x) {
                throw new RuntimeException(x);
            } finally {
                try {
                    if(sSession != null)
                        sSession.close();
                    if(cSession != null)
                        cSession.close();
                } catch(Exception x) {
                    x.printStackTrace();
                }
            }
        }
    }
    
    private static void setProps() {
        InputStream iStream = null;
        try {
            iStream = JMSLoadTest.class.getResourceAsStream("/build.properties");
            if(iStream == null)
                throw new RuntimeException("setProps() unable to find build.properties on classpath");

            Properties props = new Properties();
            props.load(iStream);
            iStream.close();

        if(props.getProperty("org.jboss.implementationClass") == null)
            throw new RuntimeException("must pass system property :  org.jboss.implementationClass");
        implementationClass = props.getProperty("org.jboss.implementationClass");

        if(props.getProperty("org.jboss.clientCount") != null)
            clientCount = Integer.parseInt(props.getProperty("org.jboss.clientCount"));

        if(props.getProperty("org.jboss.messagesPerClient") != null)
            messagesPerClient = Integer.parseInt(props.getProperty("org.jboss.messagesPerClient"));

        if(props.getProperty("org.jboss.isPersistent") != null)
            isPersistent = Boolean.parseBoolean(props.getProperty("org.jboss.isPersistent"));

        if(props.getProperty("org.jboss.isTransacted") != null)
            isTransacted = Boolean.parseBoolean(props.getProperty("org.jboss.isTransacted"));
        
        namingProviderUrl = props.getProperty("org.jboss.namingProviderUrl", "NO NAMING PROVIDER URL SPECIFIED");
        connectionFactory = props.getProperty("org.jboss.connectionFactory", CONNECTION_FACTORY);
        bulkMessageDestinationName = props.getProperty("org.jboss.bulkMessageDestinationName", BULK_MESSAGES_NAME);
        bulkMessageDestinationPath = props.getProperty("org.jboss.bulkMessageDestinationPath", QUEUE_PREFIX+BULK_MESSAGES_NAME);
        controlDestinationName = props.getProperty("org.jboss.controlDestinationName", CONTROL_NAME);
        controlDestinationPath = props.getProperty("org.jboss.controlDestinationPath", QUEUE_PREFIX+CONTROL_NAME);
        brokerAddress = props.getProperty("org.jboss.brokerAddress", java.net.InetAddress.getLocalHost().toString());
        
        if(props.getProperty("org.jboss.brokerPort") != null)
            brokerPort = Integer.parseInt(props.getProperty("org.jboss.brokerPort"));

        if(props.getProperty("org.jboss.producer.sleepTimeMillis") != null)
            producerSleepTimeMillis = Integer.parseInt(props.getProperty("org.jboss.producer.sleepTimeMillis"));
        if(props.getProperty("org.jboss.consumer.sleepTimeMillis") != null)
            consumerSleepTimeMillis = Integer.parseInt(props.getProperty("org.jboss.consumer.sleepTimeMillis"));
        
        if(props.getProperty("org.jboss.startProducer") != null)
            startProducer = Boolean.parseBoolean(props.getProperty("org.jboss.startProducer"));

        if(props.getProperty("org.jboss.startConsumer") != null)
            startConsumer = Boolean.parseBoolean(props.getProperty("org.jboss.startConsumer"));

        if(props.getProperty("org.jboss.batchSize") != null)
            batchSize = Integer.parseInt(props.getProperty("org.jboss.batchSize"));
        
        if(props.getProperty("org.jboss.byteMessageSize") != null)
            byteMessageSize = Integer.parseInt(props.getProperty("org.jboss.byteMessageSize"));

        }catch(RuntimeException x) {
            throw x;
        }catch(Exception x) { 
            throw new RuntimeException(x);
        }
        
        
        StringBuilder sBuilder = new StringBuilder();
        sBuilder.append("\n\tbrokerAddress = "+brokerAddress);
        sBuilder.append("\n\tbrokerPort = "+brokerPort);
        sBuilder.append("\n\tclientCount = "+clientCount);
        sBuilder.append("\n\tmessagesPerClient = "+messagesPerClient);
        sBuilder.append("\n\timplementationClass = "+implementationClass);
        sBuilder.append("\n\tisPersistent = "+isPersistent);
        sBuilder.append("\n\tisTransacted = "+isTransacted);
        sBuilder.append("\n\tnamingProviderUrl = "+namingProviderUrl);
        sBuilder.append("\n\tconnectionFactory = "+connectionFactory);
        sBuilder.append("\n\tcontrolDestinationName = "+controlDestinationName);
        sBuilder.append("\n\tcontrolDestinationPath = "+controlDestinationPath);
        sBuilder.append("\n\tbulkMessageDestinationName = "+bulkMessageDestinationName);
        sBuilder.append("\n\tbulkMessageDestinationPath = "+bulkMessageDestinationPath);
        sBuilder.append("\n\tconsumerSleepTimeMillis = "+consumerSleepTimeMillis);
        sBuilder.append("\n\tproducerSleepTimeMillis = "+producerSleepTimeMillis);
        sBuilder.append("\n\tstartProducer = "+startProducer);
        sBuilder.append("\n\tstartConsumer = "+startConsumer);
        sBuilder.append("\n\tbatchSize = "+batchSize);
        sBuilder.append("\n\tbyteMessageSize = "+byteMessageSize);
        
        log.info(sBuilder.toString());
    }
}

