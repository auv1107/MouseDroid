package com.scottandroid.app.mousedroid;

import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class SwitcherService extends FloatWindowHostService implements View.OnClickListener {
    public static final String TAG = SwitcherService.class.getSimpleName();
    LayoutInflater mInflater = null;
    View mViewController = null;
    View mViewSwitcher = null;
    boolean isControllerShowing = false;

    WindowManager.LayoutParams mControllerParams = null;

    OnControllerTouchListener mControllerTouchListener = null;
    SocketDelegate delegate = null;

    String ip = "192.168.31.178";
    int port = 8081;

    public static String MSG_INIT = "init";
    public static String MSG_CLICK = "click";
    public static String MSG_DOUBLECLICK = "doubleclick";
    public static String MSG_RIGHTCLICK = "rightclick";
    public static String MSG_MIDDLECLICK = "middleclick";
    public static String MSG_LEFTDOWN = "leftdown";
    public static String MSG_LEFTUP = "leftup";
    public static String MSG_MOVE = "move";
    public static String MSG_LONGPRESS = "longpress";
    private int width;
    private int height;

    public SwitcherService() {
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return super.onTouch(v, event);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mInflater = LayoutInflater.from(getApplicationContext());
        mViewSwitcher = mInflater.inflate(R.layout.layout_switcher, null);
        mViewSwitcher.setOnClickListener(this);
        mViewController = mInflater.inflate(R.layout.layout_controller, null);
//        mControllerServiceIntent = new Intent(this, ControllerService.class);
        mControllerParams = getParams();
        mControllerParams.flags |= WindowManager.LayoutParams.FLAG_BLUR_BEHIND;
        mControllerParams.y = 440;

        width = mWindowManager.getDefaultDisplay().getWidth();
        height = mWindowManager.getDefaultDisplay().getHeight();

        try {
            delegate = new SocketDelegate(InetAddress.getByName(ip), port);
            mControllerTouchListener = new OnControllerTouchListener(this, delegate);
            sendInitInfo();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    protected View getView() {
        return mViewSwitcher;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (isControllerShowing)
            mWindowManager.removeView(mViewController);
    }

    @Override
    public void onClick(View v) {
        Log.d(TAG, "x,y " + mParam.x + "," + mParam.y);
        Log.d(TAG, "onclick");
        if (isMoved)
            return;
        if (isControllerShowing) {
            mWindowManager.removeView(mViewController);
            isControllerShowing = false;
            mViewController.setOnTouchListener(null);
            mViewSwitcher.setOnTouchListener(this);
        } else {
            mWindowManager.addView(mViewController, mControllerParams);
            isControllerShowing = true;
            int x = (int) getResources().getDimension(R.dimen.switcher_x_when_controller_showing);
            int y = (int) getResources().getDimension(R.dimen.switcher_y_when_controller_showing);
            showSwitcherMoveToCornerAnimation(0, 850, 400);
            mViewController.setOnTouchListener(mControllerTouchListener);
//            Toast.makeText(this, "move to 320,650", Toast.LENGTH_LONG).show();
//            showSwitcherMoveToCornerAnimation(300, 300, 1000);
//            Toast.makeText(this, "move to 300,300", Toast.LENGTH_LONG).show();
//            showSwitcherMoveToCornerAnimation(400, 500, 1000);
//            Toast.makeText(this, "move to 400,500", Toast.LENGTH_LONG).show();
//            showSwitcherMoveToCornerAnimation(0, 0, 1000);
//            Toast.makeText(this, "move to 0,0", Toast.LENGTH_LONG).show();
        }
    }

    public void sendInitInfo() {
//        int width = getWindowManager().getDefaultDisplay().getWidth();
//        int height = getWindowManager().getDefaultDisplay().getHeight();
        int width = 1080;
        int height = 1920;
        delegate.sendMsg(MSG_INIT + " " + width + "," + height);
        Log.d(TAG, "send_init");
    }

    private long lastUpdateTime = 0;
    private long startTime = 0;

    public void showSwitcherMoveToCornerAnimation(final int x, final int y, final int duration) {
        lastUpdateTime = System.currentTimeMillis();
        startTime = lastUpdateTime;
        int cx = mParam.x;
        int cy = mParam.y;
        final float xspeed = (x - cx) * 1.0f / duration;
        final float yspeed = (y - cy) * 1.0f / duration;
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    long t = System.currentTimeMillis();
                    if (t - startTime >= duration) {
                        mParam.x = x;
                        mParam.y = y;
                        mViewSwitcher.post(
                                new Runnable() {
                                    @Override
                                    public void run() {
                                        mWindowManager.updateViewLayout(mViewSwitcher, mParam);
                                    }
                                }
                        );
                        break;
                    } else {
                        int delta = (int) (t - lastUpdateTime);
                        mParam.x += (int) (xspeed * delta);
                        mParam.y += (int) (yspeed * delta);
                        mViewSwitcher.post(
                                new Runnable() {
                                    @Override
                                    public void run() {
                                        mWindowManager.updateViewLayout(mViewSwitcher, mParam);
                                    }
                                }
                        );
                        lastUpdateTime = t;
                    }
                    try {
                        Thread.sleep(20);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
            }
        }).start();
    }
}
