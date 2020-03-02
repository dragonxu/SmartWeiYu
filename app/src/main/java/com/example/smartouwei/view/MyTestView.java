package com.example.smartouwei.view;

import com.example.smartbathroom.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.TextView;

public class MyTestView extends TextView {

    /**
     * 比例=宽/高
     */
    private float mRatio;

    public MyTestView(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
    }
    public MyTestView(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
    }
    public MyTestView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        // TODO Auto-generated constructor stub
    }

    /**
     * 初始化
     *
     * @param context
     *            上下文
     * @param attrs
     *            属性
     */
    private void init(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs,
                R.styleable.LoweTextView);
        mRatio = typedArray.getFloat(R.styleable.LoweTextView_ratio, 0);
        typedArray.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // 宽模式
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        // 宽大小
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        // 高大小
        int heightSize;
        // 只有宽的值是精确的才对高做精确的比例校对
        if (widthMode == MeasureSpec.EXACTLY && mRatio > 0) {
            heightSize = (int) (widthSize / mRatio + 0.5f);
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(heightSize,
                    MeasureSpec.EXACTLY);
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }




}
