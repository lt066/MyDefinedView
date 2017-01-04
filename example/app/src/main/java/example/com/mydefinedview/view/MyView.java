package example.com.mydefinedview.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.Transformation;

/**
 * Created by Administrator on 2017/1/4.
 */

public class MyView extends View {
    private Paint paint;
    private RectF wheelRect=new RectF();
    private float wheelangle=0;
    private Paint wheelPaint;
    private MyAnimation animation;
    private final float wheelstrokeWidth=20;
    private final long DURATION_TIME_DEFALUT=10;
    private long durationTime=10;
    public MyView(Context context) {
        super(context);
        init();
    }

    public MyView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MyView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public MyView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init(){
        animation=new MyAnimation();
        LinearInterpolator lir = new LinearInterpolator();
        animation.setInterpolator(lir);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
               /* wheelangle=0;
                postInvalidate();*/
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
//        this.startAnimation(animation);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthSize=getMySize(100,widthMeasureSpec);
        int heightSize=getMySize(100,heightMeasureSpec);
        if(widthSize<heightSize){
            heightSize=widthSize;
        }else
            widthSize=heightSize;

        setMeasuredDimension(widthSize,heightSize);
        wheelRect.set(wheelstrokeWidth,wheelstrokeWidth,widthSize-wheelstrokeWidth,heightSize-wheelstrokeWidth);
    }

    /**
     * 计算大小
     * @param defaultSize
     * @param measureSpec
     * @return
     */
    private int getMySize(int defaultSize,int measureSpec){
        int  mysize=defaultSize;
        int size=MeasureSpec.getSize(measureSpec);
        int mode = MeasureSpec.getMode(measureSpec);
        switch (mode){
            case MeasureSpec.AT_MOST:
            case MeasureSpec.EXACTLY:
                mysize=size;
                break;
            case MeasureSpec.UNSPECIFIED:
                mysize=defaultSize;
                break;
        }
        return  mysize;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float r = getMeasuredWidth() / 2;
        if(paint==null) {
            paint = new Paint();

        }
        paint.setColor(Color.GRAY);
        paint.setAntiAlias(true);//抗锯齿
//        paint.setStyle(Paint.Style.STROKE);
//        paint.setStrokeWidth(5);
        canvas.drawCircle(r,r,r,paint);
        //在view中间写字
        int textSize=getMeasuredHeight()/2;
        paint.setTextSize(textSize);
        paint.setColor(Color.GREEN);
        String text=(int)Math.ceil((360-wheelangle)/360*DURATION_TIME_DEFALUT)+"";


        int textWidth= (int) paint.measureText(text);//获取字符串宽度
        Rect bounds = new Rect();
        paint.getTextBounds(text,0,text.length(),bounds);
        canvas.drawText(text,wheelRect.centerX()-textWidth/2,wheelRect.centerY()+bounds.height()/2,paint);

        if(wheelPaint==null){
            wheelPaint=new Paint();
            wheelPaint.setStyle(Paint.Style.STROKE);
            wheelPaint.setAntiAlias(true);
            wheelPaint.setStrokeCap(Paint.Cap.ROUND);
        }
        wheelPaint.setColor(0xFFeeefef);
        wheelPaint.setStrokeWidth(wheelstrokeWidth/2);
        canvas.drawArc(wheelRect,-90,360,false,wheelPaint);
        wheelPaint.setColor(0xFF29a6f6);
        wheelPaint.setStrokeWidth(wheelstrokeWidth);
        canvas.drawArc(wheelRect,-90,wheelangle,false,wheelPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                this.clearAnimation();
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                currentWheelangel=wheelangle;
                currentDividWheelangel=360-wheelangle;
                if(currentInterTime==1.0f){
                    wheelangle=0;
                    currentInterTime=0;
                }
                if(wheelangle==0)
                    durationTime=DURATION_TIME_DEFALUT*1000;
                else {
                    durationTime= (long) Math.ceil((currentDividWheelangel/360)*DURATION_TIME_DEFALUT*1000);
                    currentInterTime=0;
                }
                if(wheelangle>=360){
                    wheelangle=0;
                }
                animation.setDuration(durationTime);
                this.startAnimation(animation);
                break;
        }
        return true;
    }

    private float currentInterTime;//临时存储动画时间
    private float currentDividWheelangel;//临时存储差值圆弧角度
    private float currentWheelangel;//临时存储圆弧角度
    private class MyAnimation extends Animation{
        public MyAnimation(){

        }

        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t) {
            super.applyTransformation(interpolatedTime, t);
            currentInterTime=interpolatedTime;
            if(durationTime==(DURATION_TIME_DEFALUT*1000)) {
                wheelangle = interpolatedTime * 360;
            }else {
                wheelangle=currentWheelangel+currentDividWheelangel*interpolatedTime;
            }
            postInvalidate();

        }
    }
}
