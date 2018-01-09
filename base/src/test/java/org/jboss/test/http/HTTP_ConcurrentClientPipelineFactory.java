package org.jboss.test.http;

import static org.jboss.netty.channel.Channels.*;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.handler.codec.frame.LengthFieldPrepender;
import org.jboss.netty.handler.codec.http.HttpChunkAggregator;
import org.jboss.netty.handler.codec.http.HttpClientCodec;
import org.jboss.netty.handler.codec.http.HttpContentDecompressor;

public class HTTP_ConcurrentClientPipelineFactory implements ChannelPipelineFactory {

    private final boolean ssl;

    public HTTP_ConcurrentClientPipelineFactory(boolean ssl) {
        this.ssl = ssl;
    }

    public ChannelPipeline getPipeline() throws Exception {
        // Create a default pipeline implementation.
        ChannelPipeline pipeline = pipeline();

        pipeline.addLast("codec", new HttpClientCodec());
       
        // Remove the following line if you don't want automatic content decompression.
        //pipeline.addLast("inflater", new HttpContentDecompressor());

        // Uncomment the following line if you don't want to handle HttpChunks.
        pipeline.addLast("aggregator", new HttpChunkAggregator(1048576));

        pipeline.addLast("handler", HTTP_ConcurrentClient.getNewClientHandler());
        return pipeline;
    }
}

