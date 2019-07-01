package me.xmz.netty.Coder;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.FixedLengthFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;

public class AboutNettyCoderClient {

    private static String delimiter = "&";

    public AboutNettyCoderClient(ByteToMessageDecoder byteToMessageDecoder) {

        NioEventLoopGroup eventExecutors = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(eventExecutors)
                 .channel(NioSocketChannel.class)
                 .option(ChannelOption.TCP_NODELAY, true)
                 .handler(new ChannelInitializer<SocketChannel>() {
                     @Override
                     protected void initChannel(SocketChannel socketChannel) {
                         socketChannel.pipeline().addLast(byteToMessageDecoder);
                         socketChannel.pipeline().addLast(new StringDecoder());
                         socketChannel.pipeline().addLast(new MyMessageHandler());
                     }
                 });
        try {
            ChannelFuture sync = bootstrap.connect("localhost", 8081);
            sync.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            eventExecutors.shutdownGracefully();
        }
    }


    private class MyMessageHandler extends ChannelInboundHandlerAdapter {
        @Override
        public void channelActive(ChannelHandlerContext ctx) {
            String message = "a message need decode from client" + delimiter;
            ByteBuf byteBuf = Unpooled.copiedBuffer(message.getBytes());
            ctx.writeAndFlush(byteBuf);
        }

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) {
            String str = (String) msg;
            System.out.println(str);
        }
    }

    public static void main(String[] args) {
        //DelimiterBasedFrameDecoder
        /*ByteBuf byteBuf = Unpooled.copiedBuffer(delimiter.getBytes());
        new AboutNettyCoderClient(new DelimiterBasedFrameDecoder(1024, byteBuf));*/
        new AboutNettyCoderClient(new FixedLengthFrameDecoder(10));
    }
}
