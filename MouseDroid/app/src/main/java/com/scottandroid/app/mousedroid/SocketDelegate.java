package com.scottandroid.app.mousedroid;

import android.util.Log;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Created by scott on 9/18/15.
 */
public class SocketDelegate {
    static final String TAG = SocketDelegate.class.getSimpleName();
    private DatagramSocket sendSocket ;
    private DatagramPacket sendPacket ;
    InetAddress ip;
    int port;
    Thread thread = null;
    LinkedList<String> msglist = new LinkedList<>();
    boolean closed = true;
    public SocketDelegate(InetAddress ip, int port) {
        this.port = port;
        this.ip = ip;
        thread = new MsgHandler();
        closed = false;
        thread.start();
    }

    public void sendMsg(String msg) {
        msglist.addLast(msg);
    }
    public void closeSocket() {
        closed = true;
        thread = null;
        msglist.clear();
    }
    public void openGate() {
        try {
            sendSocket = new DatagramSocket() ;
            sendPacket = new DatagramPacket("".getBytes(),0,ip,port);//初始化数据报
        } catch (SocketException e) {
            e.printStackTrace();
        }

        msglist.clear();
    }

    class MsgHandler extends Thread {
        @Override
        public void run() {
            openGate();
            while (!closed) {
                if (!msglist.isEmpty()) {
                    String msg = msglist.getFirst();
                    msglist.removeFirst();
                    try {
                        sendPacket.setData(msg.getBytes(), 0, msg.getBytes().length);
                        sendSocket.send(sendPacket);
                        Log.d(TAG, "send msg: " + msg);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
