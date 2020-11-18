package com.example.es100dome.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.FrameLayout;

import com.es100.MyState;
import com.es100.baserx.RxBus;
import com.es100.util.DisplayUtil;
import com.ifreecomm.debug.MLog;

@SuppressLint("AppCompatCustomView")
public class DragView extends FrameLayout {

    private int width;
    private int height;
    private int screenWidth;
    private int screenHeight;
    private Context context;

    //是否拖动
    private boolean isDrag = false;

    public boolean isDrag() {
        return isDrag;
    }

    public DragView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        width = getMeasuredWidth();
        height = getMeasuredHeight();
        screenWidth = DisplayUtil.getScreenWidth(context);
        screenHeight = DisplayUtil.getScreenHeight(context) - getStatusBarHeight();

    }

    public int getStatusBarHeight() {
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        return getResources().getDimensionPixelSize(resourceId);
    }


    private float downX;
    private float downY;
    private float x;
    private float y;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);
        if (this.isEnabled()) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    isDrag = false;
                    downX = event.getX();
                    downY = event.getY();
                    x = event.getRawX();
                    y = event.getRawY();
                    Log.e("kid", "ACTION_DOWN downX = "+downX+" ,downY = " +downY);
                    break;
                case MotionEvent.ACTION_MOVE:
                    Log.e("kid", "ACTION_MOVE");
                    final float xDistance = event.getX() - downX;
                    final float yDistance = event.getY() - downY;
                    int l, r, t, b;
                    //当水平或者垂直滑动距离大于10,才算拖动事件
                    if(MyState.getInstance().layoutMode==2){
                        if (Math.abs(xDistance) > 10 || Math.abs(yDistance) > 10) {
                            Log.e("kid", "Drag");
                            isDrag = true;
                            l = (int) (getLeft() + xDistance);
                            r = l + width;
                            t = (int) (getTop() + yDistance);
                            b = t + height;
                            //不划出边界判断,此处应按照项目实际情况,因为本项目需求移动的位置是手机全屏,
                            // 所以才能这么写,如果是固定区域,要得到父控件的宽高位置后再做处理
                            if (l < 0) {
                                l = 0;
                                r = l + width;
                            } else if (r > screenWidth) {
                                r = screenWidth;
                                l = r - width;
                            }
                            if (t < 0) {
                                t = 0;
                                b = t + height;
                            } else if (b > screenHeight) {
                                b = screenHeight;
                                t = b - height;
                            }

                            this.layout(l, t, r, b);
                    }

                    }
                    break;
                case MotionEvent.ACTION_UP:
                    Log.e("kid", "ACTION_DOWN downX = "+downX+" ,downY = " +downY);
                    final float xDistance1 = event.getRawX() - x;
                    final float yDistance1 = event.getRawY() - y;
                    if(Math.abs(xDistance1) <10 && Math.abs(yDistance1) < 10){
                        MLog.e("kid","执行");
                        RxBus.getInstance().post("click",true);
                    }
                    setPressed(false);
                    break;
                case MotionEvent.ACTION_CANCEL:
                    setPressed(false);
                    break;
                default:
                    break;
            }
            return true;
        }
        return false;
    }

}
