package me.xmz.nio;

import com.sun.org.apache.bcel.internal.generic.Select;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.*;
import java.nio.Buffer;
import java.nio.channels.*;
import java.security.Key;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class NonBlock {

    public static void main(String[] args) {

        try {
            //打开通道，用于监听客户端连接
            ServerSocketChannel channel = ServerSocketChannel.open();
            //绑定监听端口
            channel.socket().bind(new InetSocketAddress("127.0.0.1",8080));
            //设置参数，非阻塞
            channel.configureBlocking(false);

            Selector selector = Selector.open();

            //注册channel，监听TCP连接请求
            SelectionKey register = channel.register(selector, SelectionKey.OP_ACCEPT);

            int select = selector.select();
            //获得就绪的key
            Set<SelectionKey> selectionKeys = selector.selectedKeys();
            Iterator<SelectionKey> iterator = selectionKeys.iterator();
            while(iterator.hasNext()) {
                SelectionKey key = iterator.next();
                //do io event
                doIoEvent(channel, key, selector);

            }

        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    private static void doIoEvent(ServerSocketChannel channel, SelectionKey key, Selector selector) throws IOException {
        //获取接入客户端连接
        SocketChannel accept = channel.accept();
        accept.configureBlocking(false);

        //注册到selector,监听读操作
        accept.register(selector, SelectionKey.OP_READ);
    }
}
