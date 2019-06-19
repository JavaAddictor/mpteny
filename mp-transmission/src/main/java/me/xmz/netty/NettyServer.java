package me.xmz.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.util.concurrent.EventExecutorGroup;

import java.io.ObjectOutputStream;

public class NettyServer {

    public static void main(String[] args) {
        EventLoopGroup boss = new NioEventLoopGroup();
        EventLoopGroup work = new NioEventLoopGroup();

        ServerBootstrap serverBootstrap = new ServerBootstrap();

        serverBootstrap.group(boss,work)
                       .channel(NioServerSocketChannel.class)
                       .option(ChannelOption.SO_BACKLOG,1024) //相当于NIO的父通道ServerSocketChannel
                       .childHandler(new ChildChannel()); //相当于NIO的子通道SocketChannel

        try {
            ChannelFuture sync = serverBootstrap.bind(8080).sync();
            sync.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            boss.shutdownGracefully();
            work.shutdownGracefully();
        }
    }

    private static class ChildChannel extends ChannelInitializer<SocketChannel> {
        @Override
        protected void initChannel(SocketChannel socketChannel) throws Exception {
            socketChannel.pipeline().addLast(new ObjectEncoder());
           /* socketChannel.pipeline().addLast(new ObjectDecoder(Integer.MAX_VALUE,
                    ClassResolvers.cacheDisabled(this.getClass().getClassLoader())));*/
            socketChannel.pipeline().addLast(new MyChannleInHandler());
        }

        private class MyChannleInHandler extends SimpleChannelInboundHandler<Object> {

            @Override
            protected void channelRead0(ChannelHandlerContext channelHandlerContext, Object o) throws Exception {
                ByteBuf buf = (ByteBuf) o;
                byte[] bytes = new byte[buf.readableBytes()];
                buf.readBytes(bytes);
                System.out.println(new String(bytes, "utf-8"));
                /*ByteBuf byteBuf = Unpooled.copiedBuffer("a message from netty server".getBytes());
                channelHandlerContext.writeAndFlush(byteBuf);*/

            }

            @Override
            public void channelActive(ChannelHandlerContext ctx) throws Exception {
                Response response = new Response("a response from netty server", 200);
                ctx.writeAndFlush(response);
            }
        }
    }
}
