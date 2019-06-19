package me.xmz.nio;

import sun.reflect.generics.scope.Scope;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Set;

public class NioClient {

    private SocketChannel sc;

    private ByteBuffer buffer;

    private Selector selector;

    public NioClient(String address, int port) {
        try {
            sc = SocketChannel.open();
            sc.configureBlocking(false);
            selector = Selector.open();
            boolean connect = sc.connect(new InetSocketAddress(address, port));

            if(connect) {
                sc.register(selector, SelectionKey.OP_READ);
            }else {
                sc.register(selector, SelectionKey.OP_CONNECT);
            }
            doInConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void doInConnection() {
        try {
            while(true) {
                selector.select();
                Set<SelectionKey> selectionKeys = selector.selectedKeys();
                for (SelectionKey key : selectionKeys) {
                    handleKey(key);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void handleKey(SelectionKey key) {

        if (key.isValid()) {
            SocketChannel socketChannel = (SocketChannel) key.channel();
            if (key.isConnectable()) {
                try {
                    if (sc.finishConnect()) {
                        socketChannel.register(selector, SelectionKey.OP_READ);
                    }
                    doWrite();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (key.isReadable()) {
                buffer = ByteBuffer.allocate(1024);
                try {
                    int read = socketChannel.read(buffer);
                    if (read > 0) {
                        buffer.flip();
                        byte[] bytes = new byte[buffer.remaining()];
                        buffer.get(bytes);
                        System.out.println(new String(bytes, "utf-8"));
                        socketChannel.register(selector, SelectionKey.OP_WRITE);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void doWrite() {
        ByteBuffer allocate = ByteBuffer.allocate(1024);
        allocate.put("nio message from client".getBytes());
        allocate.flip();
        try {
            sc.write(allocate);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    public static void main(String[] args)  {
        NioClient nioClient = new NioClient("127.0.0.1", 8080);
    }

}
