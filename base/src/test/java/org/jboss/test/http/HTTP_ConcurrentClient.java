package org.jboss.test.http;

import java.net.InetSocketAddress;
import java.net.URI;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;

import org.apache.log4j.Logger;

public class HTTP_ConcurrentClient {

    private static final Logger log = Logger.getLogger(HTTP_ConcurrentClient.class);
    private static String serverAddress = "localhost";
    private static int serverPort = 7555;
    private static String uri;
    private static int clientCount = 1;
    private static boolean keepAlive = true;
    private static boolean tcpNoDelay = true;
    private static int requestsPerClient = 1;
    private static int totalCount = 0;
    private static long totalDuration = 0;
    private static int timesInvoked = 1;
    private static boolean promptForConnectionFromRemoteDebugger = false;
    private static boolean enableLog = false;
    private static AtomicInteger numberOfActiveChannels = new AtomicInteger();
    private static String clientHandlerName;
    private static Class handlerClass = null;

    public static void main(String[] args) throws Exception {

        if(System.getProperty("com.wavechain.handlerImplementation") == null)
            throw new Exception("main() must define a value for 'com.wavechain.handlerImplementation'");
        else {
            clientHandlerName = System.getProperty("com.wavechain.handlerImplementation");
        }
        if(System.getProperty("com.wavechain.serverAddress") != null)
            serverAddress = System.getProperty("com.wavechain.serverAddress");
        if(System.getProperty("com.wavechain.serverPort") != null)
            serverPort = Integer.parseInt(System.getProperty("com.wavechain.serverPort"));
        if(System.getProperty("com.wavechain.uri") != null)
            uri = System.getProperty("com.wavechain.uri");
        if(System.getProperty("com.wavechain.clientCount") != null)
            clientCount = Integer.parseInt(System.getProperty("com.wavechain.clientCount"));
        if(System.getProperty("com.wavechain.requestsPerClient") != null)
            requestsPerClient = Integer.parseInt(System.getProperty("com.wavechain.requestsPerClient"));
        if(System.getProperty("com.wavechain.keepAlive") != null)
            keepAlive = Boolean.parseBoolean(System.getProperty("com.wavechain.keepAlive"));
        if(System.getProperty("com.wavechain.tcpNoDelay") != null)
            tcpNoDelay = Boolean.parseBoolean(System.getProperty("com.wavechain.tcpNoDelay"));
        if(System.getProperty("com.wavechain.promptForConnectionFromRemoteDebugger") != null)
            promptForConnectionFromRemoteDebugger = Boolean.parseBoolean(System.getProperty("com.wavechain.promptForConnectionFromRemoteDebugger"));
        if(System.getProperty("com.wavechain.enableLog") != null)
            enableLog = Boolean.parseBoolean(System.getProperty("com.wavechain.enableLog"));


        StringBuilder sBuilder = new StringBuilder();
        sBuilder.append("\n\tclientHandler = "+clientHandlerName);
        sBuilder.append("\n\tserverAddress = "+serverAddress);
        sBuilder.append("\n\tserverPort = "+serverPort);
        sBuilder.append("\n\turi = "+uri);
        sBuilder.append("\n\tclientCount = "+clientCount);
        sBuilder.append("\n\trequestsPerClient = "+requestsPerClient);
        sBuilder.append("\n\tkeepAlive = "+keepAlive);
        sBuilder.append("\n\ttcpNoDelay = "+tcpNoDelay);
        sBuilder.append("\n\tpromptForConnectionFromRemoteDebugger = "+promptForConnectionFromRemoteDebugger);
        sBuilder.append("\n\tenableLog = "+enableLog);
        log.info(sBuilder.toString());
        
        if(promptForConnectionFromRemoteDebugger){
            log.info("main() start remote debugger and press return at this prompt");
            System.in.read();
        }
        
        handlerClass = Class.forName(clientHandlerName);
        
        // Configure the client.
        ClientBootstrap bootstrap = new ClientBootstrap(new NioClientSocketChannelFactory(
                        Executors.newCachedThreadPool(),
                        Executors.newCachedThreadPool()));

        bootstrap.setOption("tcpNoDelay", tcpNoDelay);
        bootstrap.setOption("keepAlive", keepAlive);
        bootstrap.setOption("connectTimeoutMillis", 10000);
        bootstrap.setOption("client.reuseAddress", true);

        // Set up the pipeline factory.
        bootstrap.setPipelineFactory(new HTTP_ConcurrentClientPipelineFactory(false));

        // Start the connection attempt.
        for(int x = 0; x < clientCount; x++) {
            ChannelFuture future = bootstrap.connect(new InetSocketAddress(serverAddress, serverPort));
            numberOfActiveChannels.incrementAndGet();
            future.getChannel().getCloseFuture().addListener( new ChannelFutureListener() {
        
                public void operationComplete(ChannelFuture future) {
                    int activeChannels = numberOfActiveChannels.decrementAndGet();
                }

            });
                   
        }

        while(true) {
            if(numberOfActiveChannels.get() > 0) {
                Thread.sleep(100);
            } else {
                log.info("main() all channels have closed");
                bootstrap.releaseExternalResources();
                break;
            }
        }
    }

    public static synchronized long computeAverageDuration(long x) {
        totalDuration = totalDuration + x;
        long averageDuration = totalDuration / timesInvoked;
        timesInvoked++;
        return averageDuration;
    }

    public static synchronized int computeTotal(int x) {
        totalCount = totalCount + x;
        return totalCount;
    }
    public static URI getUri(){
        return URI.create(uri);
    }
    public static int getRequestsPerClient() {
        return requestsPerClient;
    }
    public static boolean getEnableLog() {
        return enableLog;
    }
    public static HTTP_ConcurrentClientHandler getNewClientHandler() throws Exception {
        HTTP_ConcurrentClientHandler clientHandler = (HTTP_ConcurrentClientHandler)handlerClass.newInstance();
        return clientHandler;
    }
}
