package me.xmz.netty.file;

import io.netty.buffer.ByteBuf;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class FileChannelApplet {

    public static void main(String[] args) {
        tranditionalIO();
        //NIO();
    }

    public static void tranditionalIO() {
        long start = System.currentTimeMillis();
        try {
            FileInputStream fileInputStream = new FileInputStream("E:\\apache-tomcat-7.0.90.zip");
            FileOutputStream fileOutputStream = new FileOutputStream("E:\\css\\123_bio.zip");
            byte[] b = new byte[1024];
            int len = 0;
            while((len = fileInputStream.read(b)) != -1) {
                fileOutputStream.write(b);
            }
            long end = System.currentTimeMillis();
            System.out.println("BIO消耗时间-》" + (end - start));
            fileInputStream.close();
            fileOutputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
        }
    }

    public static void NIO() {
        try {
            long start = System.currentTimeMillis();
            FileInputStream fileInputStream = new FileInputStream("E:\\apache-tomcat-7.0.90.zip");
            FileChannel channel = fileInputStream.getChannel();
            ByteBuffer allocate = ByteBuffer.allocate(fileInputStream.available());
            int read = channel.read(allocate);
            System.out.println(read);
            allocate.flip();

            FileOutputStream fileOutputStream = new FileOutputStream("E:\\css\\123_nio.zip");

            FileChannel channel1 = fileOutputStream.getChannel();
            channel1.write(allocate);
            long end = System.currentTimeMillis();
            System.out.println("NIO消耗时间-》" + (end - start));
            channel.close();
            channel1.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
        }
    }


}
