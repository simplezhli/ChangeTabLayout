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
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

/**
 * 作者：weilu on 2017/3/16 11:49
 */

class ChangeTabStrip extends LinearLayout{

    private int lastPosition;
    private int selectedPosition;
    private float selectionOffset;
    private float selectionOffsetX = 1;

    private final Paint shadowPaint;
    private final Paint borderPaint;
    private final Paint indicatorPaint;
    private final Paint backgroundPaint;
    private final RectF indicatorRectF = new RectF();


    private int shadowColor;
    private int indicatorColor;
    private int leftBorderColor;
    private int indicatorPadding;
    private final int shadowWidth;
    private int leftBorderThickness;
    private final float shadowProportion;

    final float density = getResources().getDisplayMetrics().density;

    public ChangeTabStrip(Context context, @Nullable AttributeSet attrs) {
        super(context);
        setWillNotDraw(false); //需要重写onDraw
        setOrientation(VERTICAL);

        int tabImageHeight = (int) (Constant.TAB_IMAGE_HEIGHT * density);
        int leftBorderThickness = (int) (Constant.DEFAULT_BORDER_THICKNESS_DIPS * density);
        int indicatorPadding = (int) (Constant.SELECTED_INDICATOR_PADDING_DIPS * density);
        int shadowColor = Constant.BG;
        int backgroundColor= Constant.BG;
        int leftBorderColor = Constant.RED;
        int indicatorColor = Constant.TRANSLUCENCE;

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ChangeTabLayout);
        shadowColor = a.getColor(R.styleable.ChangeTabLayout_ctl_tabViewShadowColor, shadowColor);
        backgroundColor = a.getColor(R.styleable.ChangeTabLayout_ctl_tabViewColor, backgroundColor);
        indicatorColor = a.getColor(R.styleable.ChangeTabLayout_ctl_indicatorColor, indicatorColor);
        leftBorderColor = a.getColor(R.styleable.ChangeTabLayout_ctl_leftBorderColor, leftBorderColor);
        tabImageHeight = a.getDimensionPixelSize(R.styleable.ChangeTabLayout_ctl_tabImageHeight, tabImageHeight);
        indicatorPadding = a.getDimensionPixelSize(R.styleable.ChangeTabLayout_ctl_indicatorPadding, indicatorPadding);
        leftBorderThickness = a.getDimensionPixelSize(R.styleable.ChangeTabLayout_ctl_leftBorderThickness, leftBorderThickness);
        a.recycle();

        this.shadowColor = shadowColor;
        this.indicatorColor = indicatorColor;
        this.leftBorderColor = leftBorderColor;
        this.indicatorPadding = indicatorPadding;
        this.leftBorderThickness = leftBorderThickness;

        this.shadowWidth = tabImageHeight * 2 + (int) (9 * density);
        this.shadowProportion = (tabImageHeight / 2 + (9 * density)) / shadowWidth;
        this.indicatorPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        this.borderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        this.shadowPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        this.backgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        backgroundPaint.setColor(backgroundColor);
    }

    void onViewPagerPageChanged(int position, float positionOffset) {
        selectedPosition = position;
        selectionOffset = positionOffset;
        if (positionOffset == 0f && lastPosition != selectedPosition) {
            lastPosition = selectedPosition;
        }
        invalidate();
    }

    void onViewPagerPageChanged(float positionOffset) {
        selectionOffsetX = 1 - positionOffset;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        drawShadow(canvas);
        drawBackground(canvas);
        drawDecoration(canvas);
    }

    private void drawDecoration(Canvas canvas) {
        final int tabCount = getChildCount();

        if (tabCount > 0) {
            View selectedTab = getChildAt(selectedPosition);
            int selectedTop = selectedTab.getTop();
            int selectedBottom = selectedTab.getBottom();
            int top = selectedTop;
            int bottom = selectedBottom;

            if (selectionOffset > 0f && selectedPosition < (getChildCount() - 1)) {

                View nextTab = getChildAt(selectedPosition + 1);
                int nextTop = nextTab.getTop();
                int nextBottom = nextTab.getBottom();
                top = (int) (selectionOffset * nextTop + (1.0f - selectionOffset) * top);
                bottom = (int) (selectionOffset * nextBottom + (1.0f - selectionOffset) * bottom);
            }
            drawIndicator(canvas, top, bottom);
        }

    }

    /**
     * 绘制左边阴影
     */
    private void drawShadow(Canvas canvas){
        final float width = shadowWidth * (1 - selectionOffsetX);
        LinearGradient linearGradient = new LinearGradient(0, getHeight(), width, getHeight(), new int[] {shadowColor, Color.TRANSPARENT}, new float[]{shadowProportion, 1f}, Shader.TileMode.CLAMP);
        shadowPaint.setShader(linearGradient);
        canvas.drawRect(0, 0, width, getHeight(), shadowPaint);
    }

    /**
     * 绘制背景
     */
    private void drawBackground(Canvas canvas){
        final float width = getWidth() * selectionOffsetX;
        canvas.drawRect(0, 0, width, getHeight(), backgroundPaint);
    }

    /**
     * 绘制指示器
     */
    private void drawIndicator(Canvas canvas, int top, int bottom) {

        final float width = getWidth() * selectionOffsetX;
        top = top + indicatorPadding;
        bottom = bottom - indicatorPadding;

        float leftBorderThickness = this.leftBorderThickness - getWidth() * (1 - selectionOffsetX);
        if(leftBorderThickness < 0){
            leftBorderThickness = 0;
        }

        borderPaint.setColor(leftBorderColor);
        canvas.drawRect(0, top, leftBorderThickness, bottom, borderPaint);

        indicatorPaint.setColor(indicatorColor);
        indicatorRectF.set(leftBorderThickness, top, width, bottom);

        canvas.drawRect(indicatorRectF, indicatorPaint);
    }

    /**
     * 设置阴影颜色
     * @param shadowColor 阴影颜色
     */
    void setShadowColor(int shadowColor){
        this.shadowColor = shadowColor;
        invalidate();
    }

    /**
     * 设置背景颜色
     * @param backgroundColor 背景颜色
     */
    void setTabLayoutBackgroundColor(int backgroundColor){
        backgroundPaint.setColor(backgroundColor);
        invalidate();
    }

    /**
     * 设置指示器颜色
     * @param indicatorColor 指示器颜色
     */
    void setIndicatorColor(int indicatorColor){
        this.indicatorColor = indicatorColor;
        invalidate();
    }

    /**
     * 设置指示器标记颜色
     * @param leftBorderColor 指示器标记颜色
     */
    void setLeftBorderColor(int leftBorderColor){
        this.leftBorderColor = leftBorderColor;
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
     * 设置指示器标记宽度
     * @param leftBorderThickness 指示器标记宽度
     */
    void setLeftBorderThickness(int leftBorderThickness){
        this.leftBorderThickness = leftBorderThickness;
        invalidate();
    }

}
