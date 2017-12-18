package com.wj.customview.scale;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

import com.wj.customview.R;


/**
 * 刻度表
 * Created by wangjian on 2017/12/8.
 */

public class ScaleTableView extends View {
    private Paint backgroundPaint;//底部圆弧画笔
    private int backgroundPaintStroke = 21;//底部圆弧宽度
    private String backgroundPaintColor = "#FFEFD6";//底部圆弧画笔颜色

    private Paint scalePaint;//外圆圆弧画笔
    private int scalePaintStroke = 31;//外圆圆弧宽度
    private String scalePaintStartColor = "#FFB221";//外圆圆弧画笔开始颜色
    private String scalePaintEndColor = "#FF743B";//外圆圆弧画笔结束颜色
    private int scalePaintCurrColor = Color.parseColor("#FFB221");//外圆圆弧画笔当前颜色


    private Paint linePatin;//刻度线画笔
    private int linePatinStroke = 1;//刻度线画笔宽度
    private String linePatinColor = "#C9C9C9";//刻度线画笔颜色
    private int lineLength = 8;//刻度线长度

    private Paint textPaint;//文字画笔
    private int scaleTextSize = 10;//刻度文字大小
    private int introTextSize = 10;//介绍文字大小
    private int progressTextSize = 50;//景气度文字大小
    private String scaleTextColor = "#C9C9C9";//刻度值文字颜色
    private String introTextColor = "#000000";//介绍文字颜色（今日景气值）


    private  int radius;//两个圆弧的半径
    private RectF rectF;//两个圆弧的矩形边框
    private int width;//整个图的宽度
    private int padding = 20;//圆弧与图边界的距离
    private int progressPadding = 70;//当前刻度值与指针的上下间距
    private int angle = 220;//整个图的总角度,应大于180°
    private Bitmap bitmap;//水滴bitmap
    private int progress = 50;//当前刻度值
    /**
     * 初始化尺寸
     * @param context
     */
    private void initDimension(Context context) {
        backgroundPaintStroke = ScreenUtil.dp2px(context,backgroundPaintStroke);
        scalePaintStroke = ScreenUtil.dp2px(context,scalePaintStroke);
        linePatinStroke = ScreenUtil.dp2px(context,linePatinStroke);
        lineLength = ScreenUtil.dp2px(context,lineLength);
        padding = ScreenUtil.dp2px(context,padding);
        progressPadding = ScreenUtil.dp2px(context,progressPadding);
        scaleTextSize = ScreenUtil.dp2px(context,scaleTextSize);
        introTextSize = ScreenUtil.dp2px(context,introTextSize);
        progressTextSize = ScreenUtil.dp2px(context,progressTextSize);
    }

    public ScaleTableView(Context context) {
        super(context);
        initDimension(context);
        initPaint();
        bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.shuidi);
    }


    public ScaleTableView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initDimension(context);
        initPaint();
        bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.shuidi);
    }

    public ScaleTableView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initDimension(context);
        initPaint();
        bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.shuidi);
    }




    //初始化画笔
    private void initPaint() {
        //底部圆弧画笔
        backgroundPaint = new Paint();
        backgroundPaint.setColor(Color.parseColor(backgroundPaintColor));
        backgroundPaint.setAntiAlias(true);
        backgroundPaint.setStyle(Paint.Style.STROKE);
        backgroundPaint.setStrokeWidth(backgroundPaintStroke);

        //外部圆弧画笔
        scalePaint = new Paint();
        scalePaint.setColor(scalePaintCurrColor);
        scalePaint.setAntiAlias(true);
        scalePaint.setStyle(Paint.Style.STROKE);
        scalePaint.setStrokeWidth(scalePaintStroke);

        //刻度线画笔
        linePatin=new Paint();
        linePatin.setColor(Color.parseColor(linePatinColor));
        linePatin.setStrokeWidth(linePatinStroke);
        linePatin.setAntiAlias(true);

        //文字画笔
        textPaint=new Paint();
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setAntiAlias(true);
    }
    /**
     * 测量
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        //预留出padding
        rectF = new RectF(padding + scalePaintStroke/2,padding + scalePaintStroke/2,width - scalePaintStroke/2 - padding,width - scalePaintStroke/2 - padding);
        radius = width - scalePaintStroke;
        setMeasuredDimension(width,height);
    }

    /**
     * 绘制
     * @param canvas
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //画背景圆弧
        canvas.drawArc(rectF,360-angle+(angle - 180)/2,angle,false, backgroundPaint);
        //画刻度线以及刻度线上的标注
        drawLScaleine(canvas);
        //画今日景气值（文字介绍）
        drawIntroduceText(canvas);
        //画外圆（进度）圆弧
        drawScaleCircle(canvas);
        //画水滴指针
        drawPoint(canvas);
        //画当前景气值数值
        drawProgressText(canvas);
    }


    /**
     * 画外圆（进度）圆弧
     * @param canvas
     */
    public void drawScaleCircle(Canvas canvas){
        scalePaint.setColor(scalePaintCurrColor);
        canvas.drawArc(rectF,360-angle+(angle - 180)/2,angle* progress /100,false, scalePaint);
    }
    /**
     * 画当前景气值数值
     * @param canvas
     */
    private void drawProgressText(Canvas canvas) {
        textPaint.setColor(scalePaintCurrColor);
        textPaint.setTextSize(progressTextSize);
        canvas.save();
        canvas.translate(width/2,width/2);//将坐标系移到中点
        canvas.drawText(progress+"",0,progressPadding,textPaint);
        canvas.restore();
    }

    //画今日景气值（文字介绍）
    private void drawIntroduceText(Canvas canvas) {
        textPaint.setColor(Color.parseColor(introTextColor));
        textPaint.setTextSize(introTextSize);
        canvas.save();
        Path path = new Path();
        path.addCircle(width/2,width/2,70, Path.Direction.CW);
        canvas.drawTextOnPath("今日景气值",path, (float) 112,0,textPaint);
        canvas.restore();
    }

    //画水滴指针
    private void drawPoint(Canvas canvas) {
        canvas.save();
        canvas.translate(width/2,width/2);//将坐标系移到中点
        canvas.rotate((360-angle)/2);//旋转坐标系至零刻度
        canvas.rotate(angle* progress /100);
        canvas.drawBitmap(bitmap,-bitmap.getWidth()/2,-bitmap.getHeight()/2,new Paint());
        canvas.restore();
    }

    /**
     * 画刻度线以及刻度线上的标注
     * @param canvas
     */
    private void drawLScaleine(Canvas canvas) {
        textPaint.setColor(Color.parseColor(scaleTextColor));
        textPaint.setTextSize(scaleTextSize);
        canvas.save();
        canvas.translate(width/2,width/2);//将坐标系移到中点
        canvas.rotate((360-angle)/2);//旋转坐标系至零刻度
        float startY = rectF.width()/2 + backgroundPaintStroke/2;//起始Y轴坐标
        for (int i = 0; i < 6; i++) {
            canvas.drawLine(0,startY,0,startY+lineLength,linePatin);

            canvas.rotate(180);//旋转坐标系，保证文字不是反的
            canvas.drawText(20*i+"",0,-(startY + lineLength + 10),textPaint);
            canvas.rotate(-180);//将坐标系复原

            canvas.rotate(angle/5);//旋转坐标系
        }

        canvas.restore();
    }

    /**
     * 设置刻度值
     * @param progress
     */
    public void setProgress(float progress){
        this.progress = (int) progress;
        //重绘
        invalidate();
    }

    //抖动动画
    public void shakeAnimator(final int progress){
        final ArgbEvaluator argbEvaluator = new ArgbEvaluator();
        scalePaintCurrColor = (int) argbEvaluator.evaluate(progress / 100.0f, Color.parseColor(scalePaintStartColor), Color.parseColor(scalePaintEndColor));
        ValueAnimator animator = ValueAnimator.ofFloat(0,100,progress);
        animator.setDuration(1500);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.start();
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                scalePaintCurrColor = (int) argbEvaluator.evaluate(progress / 100.0f, Color.parseColor(scalePaintStartColor), Color.parseColor(scalePaintEndColor));
                setProgress((Float) animation.getAnimatedValue());
            }
        });
    }

    public int getScalePaintCurrColor() {
        return scalePaintCurrColor;
    }
}
