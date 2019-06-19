package me.xmz.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

public class NettyClient {

    public static void main(String[] args) {

        NioEventLoopGroup eventExecutors = new NioEventLoopGroup();

        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(eventExecutors)
                 .channel(NioSocketChannel.class)
                 .option(ChannelOption.TCP_NODELAY, true)
                 .handler(new ChannelInitializer<SocketChannel>() {
                     @Override
                     protected void initChannel(SocketChannel socketChannel) throws Exception {
                         socketChannel.pipeline().addLast(new ObjectEncoder());
                         socketChannel.pipeline().addLast(new ObjectDecoder(Integer.MAX_VALUE,
                                 ClassResolvers.cacheDisabled(this.getClass().getClassLoader())));
                         socketChannel.pipeline().addLast(new MyInHandler());

                     }
                 });

        try {
            ChannelFuture sync = bootstrap.connect("localhost", 8080);
            sync.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            eventExecutors.shutdownGracefully();
        }


    }

    private static class MyInHandler extends SimpleChannelInboundHandler<Response> {

        @Override
        protected void channelRead0(ChannelHandlerContext channelHandlerContext, Response repsonse) throws Exception {
            System.out.println(repsonse.getMessage());
        }

        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            ByteBuf byteBuf = Unpooled.copiedBuffer("a message from netty client".getBytes());
            ctx.writeAndFlush(byteBuf);
        }
    }

    private static class MyDirectChannelInHandler extends ChannelInboundHandlerAdapter {
        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            ByteBuf byteBuf = Unpooled.copiedBuffer("a message from netty client".getBytes());
            ctx.writeAndFlush(byteBuf);
        }

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            if(msg instanceof Response) {
                Response response = (Response)msg;
                System.out.println(response.getMessage());
                return;
            }
            ByteBuf buf = (ByteBuf)msg;
            byte[] bytes = new byte[buf.readableBytes()];
            buf.readBytes(bytes);
            System.out.println(new String(bytes, "utf-8"));
        }
    }


}
