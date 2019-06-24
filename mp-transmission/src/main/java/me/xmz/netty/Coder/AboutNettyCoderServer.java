package me.xmz.netty.Coder;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;

public class AboutNettyCoderServer {

    private static String delimiter = "&";

    public AboutNettyCoderServer(ByteToMessageDecoder byteToMessageDecoder) {
        NioEventLoopGroup boss = new NioEventLoopGroup();
        NioEventLoopGroup work = new NioEventLoopGroup();
        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(boss, work)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, 1024)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) {
                        socketChannel.pipeline().addLast(byteToMessageDecoder);
                        socketChannel.pipeline().addLast(new StringDecoder());
                        socketChannel.pipeline().addLast(new MyMessageHandler());
                    }
                });
        try {
            ChannelFuture sync = bootstrap.bind(8081).sync();
            sync.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            boss.shutdownGracefully();
            work.shutdownGracefully();
        }
    }


    private class MyMessageHandler extends ChannelInboundHandlerAdapter {
        @Override
        public void channelActive(ChannelHandlerContext ctx) {
            String message = "a message need decode from server" + delimiter;
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
        ByteBuf byteBuf = Unpooled.copiedBuffer(delimiter.getBytes());
        new AboutNettyCoderServer(new DelimiterBasedFrameDecoder(1024, byteBuf));
    }
}
