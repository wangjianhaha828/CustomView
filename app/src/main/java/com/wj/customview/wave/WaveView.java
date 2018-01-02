package com.wj.customview.wave;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

/**
 * 贝塞尔曲线实现波浪球效果
 * Created by wangjian on 2017/12/27.
 */

public class WaveView extends View{
    private int width;//宽度
    private int height;//高度
    private float waveLength = 320;//波长（根据View的宽度自适应）
    private int mFrontColor = Color.parseColor("#CCFF7C56");//前波浪的颜色
    private int mBgColor = Color.parseColor("#FFB832");//后波浪颜色
    private int mCircleColor = Color.parseColor("#CCFF7C56");
    private int progressTextColor = Color.parseColor("#FB5E44");//当前进度值数值颜色
    private Paint frontPaint;//前波浪的画笔
    private Paint bgPaint;//后波浪的画笔
    private int waveControlHeight = 20;//贝塞尔曲线控制点的高度（不是振幅）
    private float progress;//当前进度（总进度100）
    private Point frontPoint1,frontPoint2,frontPoint3,frontPoint4,frontPoint5;//前波浪的5个点
    private Point frontControlPoint1,frontControlPoint2,frontControlPoint3,frontControlPoint4;//前波浪的4个控制点
    private Point bgPoint1,bgPoint2,bgPoint3,bgPoint4,bgPoint5;//后波浪的5个点
    private Point bgControlPoint1,bgControlPoint2,bgControlPoint3,bgControlPoint4;//后波浪的4个控制点
    private Path frontPath;//前波浪的路径
    private Path bgPath;//后波浪的路径
    private ValueAnimator waveAnimator;//波动动画
    private Paint circlePaint;//球形遮罩画笔
    private Bitmap mBallBitmap;//球形遮罩
    private int ballPadding = 10;//球的内边距
    private PorterDuffXfermode porterDuffXfermode = new PorterDuffXfermode(PorterDuff.Mode.DST_IN);
    private Paint textPaint;//当前进度值画笔
    private float textY;//文字的Y坐标


    public WaveView(Context context) {
        super(context);
        init();
    }

    public WaveView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public WaveView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        initPoint();
        frontPaint = new Paint();
        frontPaint.setColor(mFrontColor);
        frontPaint.setFilterBitmap(true);

        bgPaint = new Paint();
        bgPaint.setColor(mBgColor);
        bgPaint.setFilterBitmap(true);

        circlePaint = new Paint();
        circlePaint.setColor(mCircleColor);
        circlePaint.setFilterBitmap(true);

        textPaint=new Paint();
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setAntiAlias(true);
        textPaint.setColor(progressTextColor);
        textPaint.setTextSize(80);

        frontPath = new Path();
        bgPath = new Path();

        waveAnimator = ValueAnimator.ofFloat(-waveLength,0);
        waveAnimator.setDuration(1000);
        waveAnimator.setInterpolator(new LinearInterpolator());
        waveAnimator.setRepeatMode(ValueAnimator.RESTART);
        waveAnimator.setRepeatCount(ValueAnimator.INFINITE);
        waveAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float animatedValue = (float) animation.getAnimatedValue();
                frontPoint1.x = (int) animatedValue;
                bgPoint1.x = (int) (animatedValue - waveLength/2);//背景波浪提前半个波长
                invalidate();
            }
        });
    }

    //初始化坐标对象
    private void initPoint() {
        frontPoint1 = new Point();
        frontPoint2 = new Point();
        frontPoint3 = new Point();
        frontPoint4 = new Point();
        frontPoint5 = new Point();
        frontControlPoint1 = new Point();
        frontControlPoint2 = new Point();
        frontControlPoint3 = new Point();
        frontControlPoint4 = new Point();
        bgPoint1 = new Point();
        bgPoint2 = new Point();
        bgPoint3 = new Point();
        bgPoint4 = new Point();
        bgPoint5 = new Point();
        bgControlPoint1 = new Point();
        bgControlPoint2 = new Point();
        bgControlPoint3 = new Point();
        bgControlPoint4 = new Point();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        width = MeasureSpec.getSize(widthMeasureSpec);
        height = MeasureSpec.getSize(heightMeasureSpec);
        if (width > 0 && height > 0){
            frontPoint1.x = (int) -waveLength;
            frontPoint1.y = (int) (height - progress/100*height);
            bgPoint1.x = (int) (-waveLength/2*3);
            bgPoint1.y = (int) (height - progress/100*height);
            textY = height/2 - (textPaint.getFontMetrics().top + textPaint.getFontMetrics().bottom) / 2;

            mBallBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(mBallBitmap);
            canvas.drawCircle(width/2,height/2,width/2 - ballPadding,circlePaint);
            setMeasuredDimension(width,height);
        }

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        notifyFrontPoint();
        notifyBgPoint();

        frontPath.reset();
        bgPath.reset();
        frontPath.moveTo(frontPoint1.x,frontPoint1.y);
        frontPath.quadTo(frontControlPoint1.x,frontControlPoint1.y,frontPoint2.x,frontPoint2.y);
        frontPath.quadTo(frontControlPoint2.x,frontControlPoint2.y,frontPoint3.x,frontPoint3.y);
        frontPath.quadTo(frontControlPoint3.x,frontControlPoint3.y,frontPoint4.x,frontPoint4.y);
        frontPath.quadTo(frontControlPoint4.x,frontControlPoint4.y,frontPoint5.x,frontPoint5.y);
        frontPath.lineTo(frontPoint5.x,height);
        frontPath.lineTo(frontPoint1.x,height);
        frontPath.lineTo(frontPoint1.x,frontPoint1.y);

        bgPath.moveTo(bgPoint1.x,bgPoint1.y);
        bgPath.quadTo(bgControlPoint1.x,bgControlPoint1.y,bgPoint2.x,bgPoint2.y);
        bgPath.quadTo(bgControlPoint2.x,bgControlPoint2.y,bgPoint3.x,bgPoint3.y);
        bgPath.quadTo(bgControlPoint3.x,bgControlPoint3.y,bgPoint4.x,bgPoint4.y);
        bgPath.quadTo(bgControlPoint4.x,bgControlPoint4.y,bgPoint5.x,bgPoint5.y);
        bgPath.lineTo(bgPoint5.x,height);
        bgPath.lineTo(bgPoint1.x,height);
        bgPath.lineTo(bgPoint1.x,bgPoint1.y);

        frontPath.close();
        bgPath.close();


        canvas.drawText((int)progress+"",width/2,textY,textPaint);
        int layerId = canvas.saveLayer(0, 0, width, height, null, Canvas.ALL_SAVE_FLAG);
        canvas.drawPath(bgPath,bgPaint);
        canvas.drawPath(frontPath,frontPaint);
        circlePaint.setXfermode(porterDuffXfermode);
        canvas.drawBitmap(mBallBitmap,0,0,circlePaint);
        circlePaint.setXfermode(null);
        canvas.restoreToCount(layerId);

    }

    public void notifyFrontPoint(){
        frontPoint2.x = (int) (frontPoint1.x + waveLength/2);
        frontPoint2.y = frontPoint1.y;
        frontPoint3.x = (int) (frontPoint1.x + waveLength);
        frontPoint3.y = frontPoint1.y;
        frontPoint4.x = (int) (frontPoint1.x + waveLength / 2 * 3);
        frontPoint4.y = frontPoint1.y;
        frontPoint5.x = (int) (frontPoint1.x + waveLength *2);
        frontPoint5.y = frontPoint1.y;
        frontControlPoint1.x = (int) (frontPoint1.x + waveLength/4);
        frontControlPoint1.y = frontPoint1.y - waveControlHeight;
        frontControlPoint2.x = (int) (frontPoint1.x + waveLength/4*3);
        frontControlPoint2.y = frontPoint1.y + waveControlHeight;
        frontControlPoint3.x = (int) (frontPoint1.x + waveLength/4*5);
        frontControlPoint3.y = frontPoint1.y - waveControlHeight;
        frontControlPoint4.x = (int) (frontPoint1.x + waveLength/4*7);
        frontControlPoint4.y = frontPoint1.y + waveControlHeight;
    }
    public void notifyBgPoint(){
        bgPoint2.x = (int) (bgPoint1.x + waveLength/2);
        bgPoint2.y = bgPoint1.y;
        bgPoint3.x = (int) (bgPoint1.x + waveLength);
        bgPoint3.y = bgPoint1.y;
        bgPoint4.x = (int) (bgPoint1.x + waveLength / 2 * 3);
        bgPoint4.y = bgPoint1.y;
        bgPoint5.x = (int) (bgPoint1.x + waveLength *2);
        bgPoint5.y = bgPoint1.y;
        bgControlPoint1.x = (int) (bgPoint1.x + waveLength/4);
        bgControlPoint1.y = bgPoint1.y - waveControlHeight;
        bgControlPoint2.x = (int) (bgPoint1.x + waveLength/4*3);
        bgControlPoint2.y = bgPoint1.y + waveControlHeight;
        bgControlPoint3.x = (int) (bgPoint1.x + waveLength/4*5);
        bgControlPoint3.y = bgPoint1.y - waveControlHeight;
        bgControlPoint4.x = (int) (bgPoint1.x + waveLength/4*7);
        bgControlPoint4.y = bgPoint1.y + waveControlHeight;
    }

    public void setProgress(int progress){
        this.progress = progress;
    }

    public void setProgressAnim(int progress){
        ValueAnimator animator = ValueAnimator.ofFloat(0,progress);
        animator.setDuration(2000);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float animatedValue = (float) valueAnimator.getAnimatedValue();
                frontPoint1.y = (int) (height - animatedValue/100*height) ;
                bgPoint1.y = (int) (height - animatedValue/100*height) ;
                setProgress((int) animatedValue);
            }
        });
        animator.start();
    }

    //启动波动动画
    public void wave(){
        waveAnimator.start();
    }

    public void switchWave(boolean isWave){
        if (isWave){
            waveAnimator.start();
        }else {
            waveAnimator.end();
        }
    }
    /**
     * 记录坐标的类
     */
    class Point{
        int x;
        int y;

         public Point() {
         }
        public Point(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }
}
