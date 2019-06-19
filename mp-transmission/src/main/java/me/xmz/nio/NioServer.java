package me.xmz.nio;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Set;

public class NioServer {
    private ServerSocketChannel ssc;

    private ByteBuffer buffer;

    private Selector selector;

    public NioServer(String address, int port) {
        if(address == null) return;

        try {
            //打开通道
            this.ssc = ServerSocketChannel.open();
            //绑定监听端口
            this.ssc.bind(new InetSocketAddress("127.0.0.1",8080));
            //设置非阻塞，默认阻塞
            this.ssc.configureBlocking(false);
            //开启状态选择器
            this.selector = Selector.open();
            //注册通道到选择器
            this.ssc.register(selector, SelectionKey.OP_ACCEPT);
            //开始循环监听
            listen();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void listen() {
        while (true) {
            try {
                selector.select();
                Set<SelectionKey> selectionKeys = selector.selectedKeys();
                for (SelectionKey key : selectionKeys) {
                    if(key.isValid()) {
                        processKey(key);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void processKey(SelectionKey key) {

        //处理新接入的连接
        if(key.isAcceptable()) {
            ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();

            try {
                SocketChannel socketChannel = serverSocketChannel.accept();
                if(socketChannel != null) {
                    socketChannel.configureBlocking(false);
                    socketChannel.register(selector, SelectionKey.OP_READ);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }if(key.isReadable()) {
            SocketChannel socketChannel = (SocketChannel)key.channel();
            try {
                buffer = ByteBuffer.allocate(1024);
                int read = socketChannel.read(buffer);
                if(read > 0) {
                    buffer.flip();
                    byte[] bytes = new byte[buffer.remaining()];
                    buffer.get(bytes);
                    System.out.println(new String(bytes, "utf-8"));
                    socketChannel.register(selector, SelectionKey.OP_WRITE);
                }else if(read < 0) {
                    key.cancel();
                    socketChannel.close();
                }else{

                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }if(key.isWritable()) {
            SocketChannel socketChannel = (SocketChannel) key.channel();
            try {
                buffer = ByteBuffer.allocate(1024);
                buffer.put("nio message from server".getBytes());
                buffer.flip();
                socketChannel.write(buffer);
                socketChannel.register(selector, SelectionKey.OP_READ);
                socketChannel.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        NioServer nioServer = new NioServer("127.0.0.1", 8080);
    }
}
