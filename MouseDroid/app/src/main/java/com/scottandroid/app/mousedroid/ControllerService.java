package com.scottandroid.app.mousedroid;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class ControllerService extends FloatWindowHostService {
    public static final String TAG = ControllerService.class.getSimpleName();
    OnControllerTouchListener mControllerTouchListener = null;
    SocketDelegate delegate = null;

    String ip = "192.168.31.178";
    int port = 8081;
    LayoutInflater mInflater = null;
    View mViewController = null;

    public static String MSG_INIT = "init";
    public static String MSG_CLICK = "click";
    public static String MSG_DOUBLECLICK = "doubleclick";
    public static String MSG_RIGHTCLICK = "rightclick";
    public static String MSG_MIDDLECLICK = "middleclick";
    public static String MSG_LEFTDOWN = "leftdown";
    public static String MSG_LEFTUP = "leftup";
    public static String MSG_MOVE = "move";
    public static String MSG_LONGPRESS = "longpress";
    public ControllerService() {
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return mControllerTouchListener.onTouch(v, event);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mInflater = LayoutInflater.from(getApplicationContext());
        mViewController = mInflater.inflate(R.layout.layout_controller, null);
        try {
            delegate = new SocketDelegate(InetAddress.getByName(ip), port);
            mControllerTouchListener = new OnControllerTouchListener(this, delegate);
            sendInitInfo();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected View getView() {
        return mViewController;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }


    public void sendInitInfo() {
//        int width = getWindowManager().getDefaultDisplay().getWidth();
//        int height = getWindowManager().getDefaultDisplay().getHeight();
        int width = 1080;
        int height = 1920;
        delegate.sendMsg(MSG_INIT + " " + width + "," + height);
        Log.d(TAG, "send_init");
    }
}
