package com.action.fragmentinterfaceframe.multicastsocketservide;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private InetAddress address;
    private MulticastSocket multicastSocket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        onBrodacastReceiver();
    }

    private void init() {
        try {
            multicastSocket = new MulticastSocket(8082);
            address = InetAddress.getByName("239.0.0.1");

        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    public void sendMessage(View view) {

        String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        time += time + " >>> form server onBrodacastSend()";
        byte[] buf = time.getBytes();
        DatagramPacket datagramPacket = new DatagramPacket(buf, buf.length);
        datagramPacket.setAddress(address);
        datagramPacket.setPort(10001);
        try {
            multicastSocket.send(datagramPacket);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * @author Engineer-Jsp
     * onBrodacastSend() 发送
     */
    private void onBrodacastSend() {
        try {
            // 侦听的端口
            multicastSocket = new MulticastSocket(8082);
            // 使用D类地址，该地址为发起组播的那个ip段，即侦听10001的套接字
            address = InetAddress.getByName("239.0.0.1");
            new Thread(new Runnable() {
                @Override
                public void run() {
                    while (true) {
                        // 获取当前时间
                        String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
                        // 当前时间+标识后缀
                        time = time + " >>> form server onBrodacastSend()";
                        // 获取当前时间+标识后缀的字节数组
                        byte[] buf = time.getBytes();
                        // 组报
                        DatagramPacket datagramPacket = new DatagramPacket(buf, buf.length);
                        // 向组播ID，即接收group /239.0.0.1  端口 10001
                        datagramPacket.setAddress(address);
                        // 发送的端口号
                        datagramPacket.setPort(10001);
                        try {
                            // 开始发送
                            multicastSocket.send(datagramPacket);
                            // 每执行一次，线程休眠2s，然后继续下一次任务
                            Thread.sleep(2000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }).start();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * @author Engineer-Jsp
     * onBrodacastReceiver() 接收
     */
    private void onBrodacastReceiver() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // 字节数组的格式，即最大大小
                    byte[] buf = new byte[1024];
                    while (true) {
                        // 组报格式
                        DatagramPacket datagramPacket = new DatagramPacket(buf, buf.length);
                        // 接收来自group组播10001端口的二次确认，阻塞
                        multicastSocket.receive(datagramPacket);
                        // 从buf中截取收到的数据
                        byte[] message = new byte[datagramPacket.getLength()];
                        // 数组拷贝
                        System.arraycopy(buf, 0, message, 0, datagramPacket.getLength());


                        Log.e("tlh", "Address:" + datagramPacket.getAddress() + ",Message：" + new String(message));
                        // 这里打印ip字段
                        // 打印组播端口10001发送过来的消息
                        // 这里可以根据结接收到的内容进行分发处理，假如收到 10001的 "snoop"字段为关闭命令，即可在此处关闭套接字从而释放资源
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
