/*
 * Copyright 2017 simplezhli
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.zl.changetablayout;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;

/**
 * 作者：weilu on 2017/3/17 11:21
 */

class ChangeTextView extends View {

    private int level;
    private Paint mPaint;
    private TextPaint mTextPaint;
    private float textSize;
    private String text = "";
    private int indicatorPadding;
    private int defaultTabTextColor;
    private int selectedTabTextColor;
    private StaticLayout mStaticLayout;

    public ChangeTextView(Context context) {
        this(context, null);
    }

    public ChangeTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ChangeTextView(Context context,  AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        final DisplayMetrics dm = getResources().getDisplayMetrics();
        final float density = getResources().getDisplayMetrics().density;
        int indicatorPadding = (int) (Constant.SELECTED_INDICATOR_PADDING_DIPS * density);
        float textSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, Constant.TAB_VIEW_TEXT_SIZE_SP, dm);

        int defaultTabTextColor = Constant.GRAY;
        int selectedTabTextColor = Constant.WHITE;

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ChangeTabLayout);
        textSize = a.getDimension(R.styleable.ChangeTabLayout_ctl_defaultTabTextSize, textSize);
        defaultTabTextColor = a.getColor(R.styleable.ChangeTabLayout_ctl_defaultTabTextColor, defaultTabTextColor);
        selectedTabTextColor = a.getColor(R.styleable.ChangeTabLayout_ctl_selectedTabTextColor, selectedTabTextColor);
        indicatorPadding = a.getDimensionPixelSize(R.styleable.ChangeTabLayout_ctl_indicatorPadding, indicatorPadding);
        a.recycle();

        this.textSize = textSize;
        this.indicatorPadding = indicatorPadding;
        this.defaultTabTextColor = defaultTabTextColor;
        this.selectedTabTextColor = selectedTabTextColor;

        mTextPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setTextAlign(Paint.Align.LEFT);

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        PorterDuffXfermode mode = new PorterDuffXfermode(PorterDuff.Mode.SRC_IN); //取两层绘制交集,显示上层。
        mPaint.setXfermode(mode);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        setMeasuredDimension(widthSize,  heightSize);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        resetting();

        Bitmap srcBitmap = Bitmap.createBitmap(getMeasuredWidth(), getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas srcCanvas = new Canvas(srcBitmap);

        RectF rectF;
        //文字随指示器位置进行颜色变化
        if (level == 10000 || level == 0) {
            rectF = new RectF(0, 0, 0, 0);
        }else if (level == 5000) {
            rectF = new RectF(0, 0, getMeasuredWidth(), getMeasuredHeight());
        }else{
            float value = (level / 5000f) - 1f;

            if(value > 0){
                rectF = new RectF(0, getMeasuredHeight() * value + indicatorPadding, getMeasuredWidth(), getMeasuredHeight());
            }else{
                rectF = new RectF(0, 0, getMeasuredWidth(), getMeasuredHeight() * (1 - Math.abs(value)) - indicatorPadding);
            }
        }

        srcCanvas.save(); //为了保存之前的画布状态
        srcCanvas.translate(0, (getMeasuredHeight() - mStaticLayout.getHeight()) / 2);
        mStaticLayout.draw(srcCanvas);
        srcCanvas.restore(); //把当前画布返回到上一个save()状态之前，重置平移部分。

        mPaint.setColor(selectedTabTextColor);
        srcCanvas.drawRect(rectF, mPaint);
        canvas.drawBitmap(srcBitmap, 0, 0, null);
    }

    private void resetting(){
        float size;
        //字体随滑动变化
        if (level == 5000) {
            size = textSize * 1.1f;
        }else if(level == 10000 || level == 0){
            size = textSize * 1f;
        }else{
            float value = (level / 5000f) - 1f;
            size = textSize + textSize * (1 - Math.abs(value))* 0.1f;
        }

        mTextPaint.setTextSize(size);
        mTextPaint.setColor(defaultTabTextColor);
        int num = (getMeasuredWidth() - indicatorPadding) / (int) size; // 一行可以放下的字数，默认放置两行文字

//        mStaticLayout = new StaticLayout(text, mTextPaint, getMeasuredWidth() - indicatorPadding, Layout.Alignment.ALIGN_NORMAL, 1.0F, 0.0F, false);
        mStaticLayout = new StaticLayout(text, 0, text.length() > num * 2 ?  num * 2 : text.length(), mTextPaint, getMeasuredWidth() - indicatorPadding,
                Layout.Alignment.ALIGN_NORMAL, 1.0F, 0.0F, false);

    }

    void setLevel(int level){
        this.level = level;
        invalidate();
    }

    void setText(String text){
        this.text = text;
        invalidate();
    }

    /**
     * 设置指示器上下内边距
     * @param indicatorPadding 指示器上下内边距
     */
    void setIndicatorPadding(int indicatorPadding){
        this.indicatorPadding = indicatorPadding;
        invalidate();
    }

    /**
     * 设置默认文字颜色
     * @param defaultTabTextColor 默认文字颜色
     */
    void setDefaultTabTextColor(int defaultTabTextColor){
        this.defaultTabTextColor = defaultTabTextColor;
        invalidate();
    }

    /**
     * 设置选中文字颜色
     * @param selectedTabTextColor 选中文字颜色
     */
    void setSelectedTabTextColor(int selectedTabTextColor){
        this.selectedTabTextColor = selectedTabTextColor;
        invalidate();
    }

    /**
     * 设置文字大小
     * @param textSize 文字大小
     */
    void setTabViewTextSize(float textSize){
        this.textSize = textSize;
        invalidate();
    }

}
