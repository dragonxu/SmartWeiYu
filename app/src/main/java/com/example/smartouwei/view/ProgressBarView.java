package com.example.smartouwei.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader.TileMode;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.example.smartbathroom.R;

public class ProgressBarView extends View {
    /**
     * 进度条所占用的角度
     */
    private static final int ARC_FULL_DEGREE = 220;
    /**
     * 弧线的宽度
     */
    private int STROKE_WIDTH;
    /**
     * 组件的宽，高
     */
    private int width, height, sWidth, sHeight;

    /**
     * 进度条最大值和当前进度值
     */
    private float max, progress, min;


    /**
     * 是否允许拖动进度条
     */
    private boolean draggingEnabled = false;
    /**
     * 绘制弧线的矩形区域
     */
    private RectF circleRectF, zhizhenRectF;
    /**
     * 绘制弧线的画笔
     */
    private Paint progressPaint;
    /**
     * 绘制文字的画笔
     */
    private Paint textPaint;
    /**
     * 绘制当前进度值的画笔
     */
    private Paint thumbPaint;
    /**
     * 圆弧的半径
     */
    private int circleRadius;
    /**
     * 圆弧圆心位置
     */
    private int centerX, centerY;
    private int upBtCenterX, upBtCenterY, downBtCenterx, downBtCenterY;//控制按钮的坐标
    private Bitmap zhizhen;
    private Matrix matrix;//矩阵--控制指针图片的动画
    /**
     * 指针圆心
     */
    private float circleRectFCenterWidth;
    private float circleRectFCenterHeight;
    /**
     * 圆弧上渐变色的颜色值
     */
//    private final int[] colors = {Color.parseColor("#FFF68F"),Color.parseColor("#FFE700"),Color.parseColor("#FFD700"),Color.parseColor("#FFC700"),Color.parseColor("#FFB700"),Color.parseColor("#FFA700"),Color.parseColor("#FF9700"),Color.parseColor("#FF7F00")};
    private final int[] colors = {Color.TRANSPARENT, Color.TRANSPARENT, Color.TRANSPARENT, Color.TRANSPARENT, Color.TRANSPARENT, Color.TRANSPARENT, Color.TRANSPARENT, Color.TRANSPARENT};
    private float[] position;
    private int buttonRadius;
    private float newProgress;

    public ProgressBarView(Context context) {
        super(context);
        init();
    }


    public ProgressBarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }


    public ProgressBarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }


    private void init() {
        position = new float[colors.length];
        zhizhen = BitmapFactory.decodeResource(getResources(), R.drawable.zhizhen1);
        matrix = new Matrix();
//    	Log.e("init", "测试1");
        progressPaint = new Paint();
        progressPaint.setAntiAlias(true);

        textPaint = new Paint();
        textPaint.setColor(Color.BLACK);
        textPaint.setAntiAlias(true);

        thumbPaint = new Paint();
        thumbPaint.setAntiAlias(true);


        //使用自定义字体
//        textPaint.setTypeface(Typeface.createFromAsset(getContext().getAssets(), "fangz.ttf"));
    }

    public float getProgress() {
        return progress;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

//        Log.e("onMeasure", "获取长宽");
        if (width == 0 || height == 0) {
            sWidth = MeasureSpec.getSize(widthMeasureSpec);
            sHeight = MeasureSpec.getSize(heightMeasureSpec);
            width = sWidth;
            height = width;
//        	width=zhizhen.getWidth();
//        	height=zhizhen.getHeight();
//        	sWidth = 300;
//        	sHeight = 300;
//        	width=200;
//        	height=200;

            System.out.println("width=" + width + " ; height=" + height);

            //计算圆弧半径和圆心点
            circleRadius = (width - STROKE_WIDTH * 2) / 2;
//            circleRadius -= (STROKE_WIDTH/2);

//            STROKE_WIDTH = circleRadius / 12;  //圆弧描边的宽度


            centerX = sWidth / 2;
            centerY = centerX;
            // ratio = height/width;


            //圆弧所在矩形区域
            circleRectF = new RectF();
            circleRectF.left = centerX - circleRadius;
            circleRectF.top = centerY - circleRadius;
            circleRectF.right = centerX + circleRadius;
            circleRectF.bottom = centerY + circleRadius;

            //指针所在区域
            zhizhenRectF = new RectF();
            zhizhenRectF.left = centerX - circleRadius / 1.6f;
            zhizhenRectF.top = centerY - circleRadius / 1.6f;
            zhizhenRectF.right = centerX + circleRadius / 1.6f;
            zhizhenRectF.bottom = centerY + circleRadius / 1.6f;

            circleRectFCenterWidth = (circleRectF.right + circleRectF.left) / 2;
            circleRectFCenterHeight = (circleRectF.bottom + circleRectF.top) / 2;

            System.out.println("具体值：" + zhizhenRectF.left + " " + zhizhenRectF.right + " " + zhizhenRectF.top + " " + zhizhenRectF.bottom);
//            Log.e("onMeasure", "获取长宽完成");
        }
    }


    private Rect textBounds = new Rect();


    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
//        Log.e("ondraw", "测试2");

        float start = 90 + ((360 - ARC_FULL_DEGREE) >> 1); //进度条起始点
        float sweep1 = ARC_FULL_DEGREE * (progress / max); //进度划过的角度
        float sweep2 = ARC_FULL_DEGREE - sweep1; //剩余的角度
        float progressRadians = (float) (((360.0f - ARC_FULL_DEGREE) / 2 + sweep1) / 180 * Math.PI);
        float thumbX = centerX - circleRadius * (float) Math.sin(progressRadians);
        float thumbY = centerY + circleRadius * (float) Math.cos(progressRadians);

        //绘制起始位置小圆形
        progressPaint.setColor(Color.WHITE);
        progressPaint.setStrokeWidth(0);
        progressPaint.setStyle(Paint.Style.FILL);
        float radians = (float) (((360.0f - ARC_FULL_DEGREE) / 2) / 180 * Math.PI);
        float startX = centerX - circleRadius * (float) Math.sin(radians);
        float startY = centerY + circleRadius * (float) Math.cos(radians);
        System.out.println("startX=" + startX + ";startY=" + startY);
        canvas.drawCircle(startX, startY, STROKE_WIDTH / 2, progressPaint);

        //绘制控制进度减按钮
        buttonRadius = circleRadius / 8;
        downBtCenterx = (int) (startX + buttonRadius);
        downBtCenterY = (int) (startY + 4 * buttonRadius);
        progressPaint.setColor(Color.parseColor("#8800FFFF"));
//        canvas.drawCircle(startX+buttonRadius, startY+4*buttonRadius, buttonRadius, progressPaint);
        progressPaint.setColor(Color.parseColor("#F0FFFF"));
        progressPaint.setStrokeWidth(5);
//        canvas.drawLine(startX+buttonRadius-buttonRadius*3/4, startY+4*buttonRadius, startX+buttonRadius+buttonRadius*3/4, startY+4*buttonRadius, progressPaint);
//        Log.e("onDraw", "测试-画圆");
        //绘制进度条
        for (int i = 0; i < colors.length; i++) {
            position[i] = (float) (0.37 + i * (progressRadians * 100 / 360) / colors.length);
        }
        progressPaint.setStrokeWidth(STROKE_WIDTH);
        progressPaint.setStyle(Paint.Style.STROKE);//设置空心
        LinearGradient linearGradient = new LinearGradient(startX, startY, thumbX, thumbY, colors, position, TileMode.CLAMP);
        progressPaint.setShader(linearGradient);
        canvas.drawArc(circleRectF, start, sweep1, false, progressPaint);
        //绘制进度条背景
        linearGradient = null;
        progressPaint.setShader(null);
//        progressPaint.setColor(Color.parseColor("#d64444"));
        progressPaint.setColor(Color.TRANSPARENT);
        canvas.drawArc(circleRectF, start + sweep1, sweep2, false, progressPaint);
//        Log.e("onDraw", "测试-画进度条");

        //绘制结束位置小圆形
        progressPaint.setStrokeWidth(0);
        progressPaint.setStyle(Paint.Style.FILL);
        float endX = centerX + circleRadius * (float) Math.sin(radians);
        float endY = centerY + circleRadius * (float) Math.cos(radians);
        canvas.drawCircle(endX, endY, STROKE_WIDTH / 2, progressPaint);
//        Log.e("onDraw", "测试-画按钮");

        // 绘制进度加按钮
        upBtCenterX = (int) (endX - buttonRadius);
        upBtCenterY = (int) (endY + 4 * buttonRadius);
        progressPaint.setColor(Color.parseColor("#8800FFFF"));
//        canvas.drawCircle(endX-buttonRadius, endY+4*buttonRadius, buttonRadius, progressPaint);
        progressPaint.setColor(Color.parseColor("#F0FFFF"));
        progressPaint.setStrokeWidth(5);
//        canvas.drawLine(endX-buttonRadius-buttonRadius*3/4, endY+4*buttonRadius, endX-buttonRadius+buttonRadius*3/4, endY+4*buttonRadius, progressPaint);
//        canvas.drawLine(endX-buttonRadius, endY+4*buttonRadius-buttonRadius*3/4, endX-buttonRadius, endY+4*buttonRadius+buttonRadius*3/4, progressPaint);
        progressPaint.setColor(Color.WHITE);
        //画出指针动画
//        matrix.reset();
//        matrix.postTranslate(circleRectFCenterWidth-width/2, circleRectFCenterHeight-height/2);
//        matrix.preRotate(40,width/2,height/2);
//		matrix.postRotate((float)(progressRadians*(180/Math.PI)),circleRectFCenterWidth, circleRectFCenterHeight);
//        System.out.println("宽="+ zhizhen.getWidth() + " 高=" + zhizhen.getHeight());
//        canvas.drawBitmap(zhizhen, matrix, progressPaint);
//        canvas.drawCircle(circleRectFCenterWidth, circleRectFCenterHeight, (float) (0.36*width), progressPaint);

        //上一行文字
        textPaint.setTextSize(circleRadius >> 1);
        String text = (int) (100 * progress / max) + "";
        float textLen = textPaint.measureText(text);
        //计算文字高度
        textPaint.getTextBounds("8", 0, 1, textBounds);
        float h1 = textBounds.height();
        //% 前面的数字水平居中，适当调整
        float extra = text.startsWith("1") ? -textPaint.measureText("1") / 2 : 0;
//        canvas.drawText(text, centerX - textLen / 2 + extra, centerY - 30 + h1 / 2, textPaint);

        //绘制进度条上的按钮
        thumbPaint.setColor(Color.parseColor("#33d64444"));
//        canvas.drawCircle(thumbX, thumbY, STROKE_WIDTH * 2.0f, thumbPaint);
        thumbPaint.setColor(Color.parseColor("#99d64444"));
        canvas.drawCircle(thumbX, thumbY, STROKE_WIDTH * 1.0f, thumbPaint);
        thumbPaint.setColor(Color.WHITE);
        canvas.drawCircle(thumbX, thumbY, STROKE_WIDTH * 0.6f, thumbPaint);
//        Log.e("onDraw", "测试-完成");
        System.out.println("进度值progress=" + progress);

        this.progressListener.onValueChange((int) progress);

    }


    private boolean isDragging = false;
    private int oldProgress;

    public int getOldProgress() {
        return oldProgress;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!draggingEnabled) {
            return super.onTouchEvent(event);
        }
//        Log.e("onTouchEvent", "测试3");

        //处理拖动事件
        float currentX = event.getX();
        float currentY = event.getY();


        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                //判断是否在进度条thumb位置
                if (checkOnArc(currentX, currentY)) {
                    newProgress = calDegreeByPosition(currentX, currentY) / ARC_FULL_DEGREE * max;
                    setProgressSync(newProgress);
                    isDragging = true;
                } else if (checkOnButtonUp(currentX, currentY)) {
                    // TODO Auto-generated method stub
                    setProgress(progress + 10);
                    isDragging = false;
                } else if (checkOnButtonDwon(currentX, currentY)) {
                    setProgress(progress - 10);
                    isDragging = false;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (isDragging) {
                    //判断拖动时是否移出去了
                    if (checkOnArc(currentX, currentY)) {
                        setProgressSync(calDegreeByPosition(currentX, currentY) / ARC_FULL_DEGREE * max);
                    } else {
                        isDragging = false;
                    }
                }
                break;

            // 手抬起的地方
            case MotionEvent.ACTION_UP:
                oldProgress = (int) progress + (int) min;

                isDragging = false;
                this.progressListener.onMotionEventUp((int) progress + (int) min);
                break;
        }
        return true;
    }


    private float calDistance(float x1, float y1, float x2, float y2) {
        return (float) Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));
    }

    private boolean checkOnButtonUp(float currentX, float currentY) {
        float distance = calDistance(currentX, currentY, upBtCenterX, upBtCenterY);
        return distance < 1.5 * buttonRadius;
    }

    private boolean checkOnButtonDwon(float currentX, float currentY) {
        float distance = calDistance(currentX, currentY, downBtCenterx, downBtCenterY);
        return distance < 1.5 * buttonRadius;
    }

    /**
     * 判断该点是否在弧线上（附近）
     */
    private boolean checkOnArc(float currentX, float currentY) {
        float distance = calDistance(currentX, currentY, centerX, centerY);
        float degree = calDegreeByPosition(currentX, currentY);
        return distance > circleRadius - STROKE_WIDTH * 5 && distance < circleRadius + STROKE_WIDTH * 5
                && (degree >= -8 && degree <= ARC_FULL_DEGREE + 8);
    }


    /**
     * 根据当前位置，计算出进度条已经转过的角度。
     */
    private float calDegreeByPosition(float currentX, float currentY) {
        float a1 = (float) (Math.atan(1.0f * (centerX - currentX) / (currentY - centerY)) / Math.PI * 180);
        if (currentY < centerY) {
            a1 += 180;
        } else if (currentY > centerY && currentX > centerX) {
            a1 += 360;
        }


        return a1 - (360 - ARC_FULL_DEGREE) / 2;
    }


    public void setMax(int max) {
        this.max = max;
        invalidate();
    }

    public void setMin(int min) {
        this.min = min;
        invalidate();
    }


    public void setProgress(float progress) {
//        final float validProgress = checkProgress(50);
        final float validProgress = checkProgress(progress);
        //动画切换进度值
        new Thread(new Runnable() {
            @Override
            public void run() {
                float oldProgress = ProgressBarView.this.progress;
                for (int i = 1; i <= 100; i++) {
                    ProgressBarView.this.progress = oldProgress + (validProgress - oldProgress) * (1.0f * i / 100);
                    postInvalidate();
                    SystemClock.sleep(3);
                }
            }
        }).start();
    }

    public void setProgressSync(float progress) {
        this.progress = checkProgress(progress);
        invalidate();
    }


    //保证progress的值位于[0,max]
    private float checkProgress(float progress) {
        if (progress < 0) {
            return 0;
        }


        return progress > max ? max : progress;
    }


    public void setDraggingEnabled(boolean draggingEnabled) {
        this.draggingEnabled = draggingEnabled;
    }

    /**
     * 设置进度条宽度
     */
    public void setProgressBarHouDu(int iHouDu) {

        STROKE_WIDTH = iHouDu;

    }


    private ProgressBarListener progressListener;


    public ProgressBarListener getProgressListener() {
        return progressListener;
    }


    public void setProgressListener(ProgressBarListener progressListener) {
        this.progressListener = progressListener;
    }


    public interface ProgressBarListener {

        public void onValueChange(int NewValue);

        public void onMotionEventUp(int NewValue);

    }

}  