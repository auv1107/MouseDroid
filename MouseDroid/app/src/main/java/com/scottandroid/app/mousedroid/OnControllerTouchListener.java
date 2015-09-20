package com.scottandroid.app.mousedroid;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by scott on 9/20/15.
 */
public class OnControllerTouchListener implements View.OnTouchListener {
    public static final String TAG = OnControllerTouchListener.class.getSimpleName();
    SocketDelegate delegate;
    GestureDetector detector = null;
    Context context = null;

    int lastX, lastY;

    public OnControllerTouchListener(Context context, SocketDelegate delegate) {
        this.delegate = delegate;
        this.context = context;
        detector = new GestureDetector(context, new GestureListener());
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_MOVE:
                int x = (int) event.getRawX();
                int y = (int) event.getRawY();
                int offx = x - lastX;
                int offy = y - lastY;
                lastX = x;
                lastY = y;
                delegate.sendMsg(SwitcherService.MSG_MOVE + " " + offx + "," + offy);
                Log.d(TAG, "msg_move");
                break;
            case MotionEvent.ACTION_DOWN:
                lastX = (int) event.getRawX();
                lastY = (int) event.getRawY();
                delegate.sendMsg(SwitcherService.MSG_LEFTDOWN);
                Log.d(TAG, "msg_leftdown");
                break;
            case MotionEvent.ACTION_UP:
                delegate.sendMsg(SwitcherService.MSG_LEFTUP);
                Log.d(TAG, "msg_leftup");
                break;
        }
        return detector.onTouchEvent(event);
    }

    class GestureListener implements GestureDetector.OnGestureListener {

        @Override
        public boolean onDown(MotionEvent e) {
            return false;
        }

        @Override
        public void onShowPress(MotionEvent e) {

        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            delegate.sendMsg(SwitcherService.MSG_CLICK);
            Log.d(TAG, "msg_click");
            return false;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            return false;
        }

        @Override
        public void onLongPress(MotionEvent e) {
            delegate.sendMsg(SwitcherService.MSG_LONGPRESS);
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            return false;
        }
    }
}
