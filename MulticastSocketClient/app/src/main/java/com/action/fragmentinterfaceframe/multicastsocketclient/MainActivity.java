package com.action.fragmentinterfaceframe.multicastsocketclient;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class MainActivity extends AppCompatActivity {

    private MulticastSocket multicastSocket;
    private TextView view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        view = findViewById(R.id.text);
        onBrodacastReceiver();
    }


    private void onBrodacastReceiver() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // 接收数据时需要指定监听的端口号
                    multicastSocket = new MulticastSocket(10001);
                    // 创建组播ID地址
                    InetAddress address = InetAddress.getByName("239.0.0.1");
                    // 加入地址
                    multicastSocket.joinGroup(address);
                    // 包长
                    byte[] buf = new byte[1024];
                    final StringBuilder builder = new StringBuilder();
                    while (true) {
                        // 数据报
                        DatagramPacket datagramPacket = new DatagramPacket(buf, buf.length);
                        // 接收数据，同样会进入阻塞状态
                        multicastSocket.receive(datagramPacket);
                        // 从buffer中截取收到的数据
                        byte[] message = new byte[datagramPacket.getLength()];
                        // 数组拷贝
                        System.arraycopy(buf, 0, message, 0, datagramPacket.getLength());


                        Log.e("tlh", "Address:" + datagramPacket.getAddress() + ",Message：" + new String(message));
                        builder.append("Address:" + datagramPacket.getAddress() + ",Message：" + new String(message));
                        builder.append("\n");
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                view.setText(builder.toString());

                            }
                        });

                        // 打印来自组播里其他服务的or客户端的ip
                        // 打印来自组播里其他服务的or客户端的消息
                        // 收到消息后可以进行记录然后二次确认，如果只是想获取ip，在发送方收到该消息后可关闭套接字，从而释放资源
                        onBrodacastSend(datagramPacket.getAddress());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void onBrodacastSend(InetAddress address) {

        // 假设 239.0.0.1 已经收到了来自其他组ip段的消息，为了进行二次确认，发送 "snoop"
        // 进行确认，当发送方收到该消息可以释放资源
        String out = "snoop";
        // 获取"snoop"的字节数组
        byte[] buf = out.getBytes();
        // 组报
        DatagramPacket datagramPacket = new DatagramPacket(buf, buf.length);
        // 设置地址，该地址来自onBrodacastReceiver()函数阻塞数据报，datagramPacket.getAddress()
        datagramPacket.setAddress(address);
        // 发送的端口号
        datagramPacket.setPort(8082);
        try {
            // 开始发送
            multicastSocket.send(datagramPacket);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
