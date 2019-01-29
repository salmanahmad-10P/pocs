package org.jboss.test.http;

import java.math.BigDecimal;
import java.net.InetAddress;
import java.net.URI;
import java.net.UnknownHostException;
import java.util.concurrent.atomic.AtomicInteger;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.jboss.netty.handler.codec.http.DefaultHttpRequest;
import org.jboss.netty.handler.codec.http.HttpChunk;
import org.jboss.netty.handler.codec.http.HttpHeaders;
import org.jboss.netty.handler.codec.http.HttpMethod;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.jboss.netty.handler.codec.http.HttpVersion;
import org.jboss.netty.util.CharsetUtil;

import org.apache.log4j.Logger;

/**
 */
public abstract class HTTP_ConcurrentClientHandler extends SimpleChannelUpstreamHandler {

    private static final Logger log = Logger.getLogger(HTTP_ConcurrentClientHandler.class.getName());

    // # of loops this handler should iterate through
    private final AtomicInteger handlerCounter = new AtomicInteger();
    private int requestsPerClient = 100;

    private final HttpRequest request;
    private boolean enableLog = false;
    private boolean readingChunks = false;
    private long startTime = 0L;
    private BigDecimal bdThousand;
    protected URI requestUri;

    public HTTP_ConcurrentClientHandler() throws UnknownHostException { 
        requestUri = HTTP_ConcurrentClient.getUri();
        this.requestsPerClient = HTTP_ConcurrentClient.getRequestsPerClient();
        this.enableLog = HTTP_ConcurrentClient.getEnableLog();

        //request = 
        request = createInitialHttpRequest();
        request.setHeader(HttpHeaders.Names.HOST, InetAddress.getLocalHost().getHostName());
        request.setHeader(HttpHeaders.Names.CONNECTION, HttpHeaders.Values.KEEP_ALIVE);
        request.setHeader(HttpHeaders.Names.ACCEPT_ENCODING, HttpHeaders.Values.GZIP);
        bdThousand = bdThousand = new BigDecimal(1000);
    }
    
    public abstract HttpRequest createInitialHttpRequest();

    @Override
    public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e) {
        this.startTime = System.currentTimeMillis();
        e.getChannel().write(request);
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) {
        if (!readingChunks) {
            HttpResponse response = (HttpResponse) e.getMessage();
            if(200 != response.getStatus().getCode())
                throw new RuntimeException("messageReceived() response status code = "+response.getStatus().getCode());
            
            if (enableLog && !response.getHeaderNames().isEmpty()) {
                for (String name: response.getHeaderNames()) {
                    for (String value: response.getHeaders(name)) {
                        log.info("HEADER: " + name + " = " + value);
                    }
                }
            }
            
            if(response.isChunked())
                readingChunks = true;
            
            if (enableLog && readingChunks) {
                log.info("CHUNKED CONTENT {");
            } else if(enableLog){
                ChannelBuffer content = response.getContent();
                if (content.readable()) {
                    log.info("CONTENT {");
                    log.info(content.toString(CharsetUtil.UTF_8));
                    log.info("} END OF CONTENT");
                }
            }

            long currentCount = handlerCounter.incrementAndGet();

            // continue to send out 'requestsPerClient' # of requests
            if (currentCount < requestsPerClient)
                e.getChannel().write(request);
            else {
                e.getChannel().disconnect();
                e.getChannel().close();
            }
        } else {
            HttpChunk chunk = (HttpChunk) e.getMessage();
            if (chunk.isLast()) {
                readingChunks = false;
                log.info("} END OF CHUNKED CONTENT");
            } else {
                log.info(chunk.getContent().toString(CharsetUtil.UTF_8));
            }

        }
    }

    @Override
    public void exceptionCaught( ChannelHandlerContext ctx, ExceptionEvent e) {
        // Close the connection when an exception is raised.
        log.error("Unexpected exception from downstream. cause = "+e.getCause());
        e.getChannel().close();
    }

    public void channelClosed(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
        long handlerDuration = (System.currentTimeMillis() - startTime); 
        BigDecimal aveDuration = new BigDecimal(HTTP_ConcurrentClient.computeAverageDuration(handlerDuration)).divide(bdThousand);
        int tCount = HTTP_ConcurrentClient.computeTotal(handlerCounter.get());
        log.info("HANDLER_COMPLETE!\t"+ tCount+"\t"+aveDuration);
    }
}


