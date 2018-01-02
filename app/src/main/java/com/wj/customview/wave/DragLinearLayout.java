package com.wj.customview.wave;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.wj.customview.scale.ScreenUtil;


/**
 * 可拖动的LinearLayout
 * 注意：父View请用RelativeLayout
 * Created by wangjian on 2017/12/12.
 */

public class DragLinearLayout extends LinearLayout {

    private Context context;
    private float downX;
    private float downY;
    private RelativeLayout.LayoutParams layoutParams;
    private boolean isClick = true;//记录用户是否是想响应点击事件
    private int leftMargin;
    private int bottomMargin;

    public DragLinearLayout(Context context) {
        super(context);
        this.context = context;
    }

    public DragLinearLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    public DragLinearLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        layoutParams = (RelativeLayout.LayoutParams) getLayoutParams();
        float eventX = event.getRawX();
        float eventY = event.getRawY();
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN://手指按下
                if (touchEventLisenter != null){
                    touchEventLisenter.onDown();
                }
                isClick = true;//当按下以后重置点击标记
                downX = eventX;//记录下按下的坐标
                downY = eventY;//记录下按下的坐标
                Log.e("TAG","downX="+downX+";downY="+downY);
                break;
            case MotionEvent.ACTION_MOVE://手指移动
                if (touchEventLisenter != null){
                    touchEventLisenter.onMove();
                }
                float dx = eventX - downX;//计算出用户手指X的移动（相对于按下的坐标）
                float dy = eventY - downY;//计算出用户手指Y的移动（相对于按下的坐标）
                if (Math.abs(dx) >= 10){//移动距离大于10才算滑动
                    isClick = false;//判断用户是点击还是滑动,默认为true，只要有一次大于10，则不响应点击事件
                    Log.e("TAG","leftMargin="+layoutParams.leftMargin);
                    //
                    int left = leftMargin + (int) dx;
                    int right = -(int) dy + bottomMargin;
                    //限制滑动范围
                    if (left < 0){
                        left = 0;
                    }else if (left > ScreenUtil.getScreenWidth(context) - getWidth()){
                        left = ScreenUtil.getScreenWidth(context) - getWidth();
                    }
                    if (right < 0){
                        right = 0;
                    }else if (right > ScreenUtil.getScreenHeight(context) - getHeight() - 200){
                        right = ScreenUtil.getScreenHeight(context) - getHeight() - 200;
                    }
                    layoutParams.leftMargin = left;
                    layoutParams.bottomMargin = right;
                    setLayoutParams(layoutParams);
                }
                break;
            case MotionEvent.ACTION_UP://手指抬起
                if (touchEventLisenter != null){
                    touchEventLisenter.onUp();
                }
                //记录下当前的左边距和下边距
                leftMargin = layoutParams.leftMargin;
                bottomMargin = layoutParams.bottomMargin;
                if (isClick){
                    return super.onTouchEvent(event);//响应点击事件
                }
                return true;//不响应点击事件
        }

        return super.onTouchEvent(event);
    }

    /**
     * 让当前View跳动
     */
    public void beat(){
        ObjectAnimator objectAnimator = ObjectAnimator
                .ofFloat(this, "translationY", 0, -getHeight()/2, 0)
                .setDuration(750);
        objectAnimator.setRepeatCount(1);
        objectAnimator.start();
    }

    public interface TouchEventLisenter{
        void onDown();
        void onMove();
        void onUp();
    }

    public void setTouchEventLisenter(TouchEventLisenter touchEventLisenter){
        this.touchEventLisenter = touchEventLisenter;
    }

    TouchEventLisenter touchEventLisenter;
}
