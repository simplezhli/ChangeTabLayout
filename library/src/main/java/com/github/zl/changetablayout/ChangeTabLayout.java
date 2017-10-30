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
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.VerticalViewPager;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import static com.github.zl.changetablayout.Constant.ARRAY_INITIAL_VALUE;

/**
 * 作者：weilu on 2017/3/15 13:59
 */

public class ChangeTabLayout extends ScrollView{

    /**
     * tab收起与展开
     */
    private boolean tabLayoutState = true;
    /**
     * tabLayout是否可以点击
     */
    private boolean tabLayoutIsClick = true;
    /**
     * tabView切换是否需要文字实时变化
     */
    private boolean flag = false;
    private final int tabViewHeight;
    private final int tabImageHeight;
    private final int defaultTabImageColor;
    private final int selectedTabImageColor;
    private float textSize;
    private int indicatorPadding;
    private int defaultTabTextColor;
    private int selectedTabTextColor;

    private VerticalViewPager viewPager;
    private final ChangeTabStrip tabStrip;

    private OnTabClickListener onTabClickListener;
    private InternalTabClickListener internalTabClickListener;

    private float density = 0;
    private int page = 0; //当前页数位置

    public ChangeTabLayout(Context context) {
        this(context, null);
    }

    public ChangeTabLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ChangeTabLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        //禁用滚动条
        setVerticalScrollBarEnabled(false);
        //去除阴影
        setOverScrollMode(OVER_SCROLL_NEVER);
        //防止ScrollView子View不能撑满全屏显示问题
        setFillViewport(true);

        final DisplayMetrics dm = getResources().getDisplayMetrics();
        density = dm.density;

        int tabViewHeight = (int) (Constant.TAB_VIEW_HEIGHT * density);
        int tabImageHeight = (int) (Constant.TAB_IMAGE_HEIGHT * density);
        int defaultTabImageColor = Constant.GRAY;
        int selectedTabImageColor = Constant.RED;
        int indicatorPadding = (int) (Constant.SELECTED_INDICATOR_PADDING_DIPS * density);
        float textSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, Constant.TAB_VIEW_TEXT_SIZE_SP, dm);
        int defaultTabTextColor = Constant.GRAY;
        int selectedTabTextColor = Constant.WHITE;

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ChangeTabLayout);
        tabViewHeight = a.getDimensionPixelSize(R.styleable.ChangeTabLayout_ctl_tabViewHeight, tabViewHeight);
        tabImageHeight = a.getDimensionPixelSize(R.styleable.ChangeTabLayout_ctl_tabImageHeight, tabImageHeight);
        defaultTabImageColor = a.getColor(R.styleable.ChangeTabLayout_ctl_defaultTabImageColor, defaultTabImageColor);
        selectedTabImageColor = a.getColor(R.styleable.ChangeTabLayout_ctl_selectedTabImageColor, selectedTabImageColor);
        textSize = a.getDimension(R.styleable.ChangeTabLayout_ctl_defaultTabTextSize, textSize);
        defaultTabTextColor = a.getColor(R.styleable.ChangeTabLayout_ctl_defaultTabTextColor, defaultTabTextColor);
        selectedTabTextColor = a.getColor(R.styleable.ChangeTabLayout_ctl_selectedTabTextColor, selectedTabTextColor);
        indicatorPadding = a.getDimensionPixelSize(R.styleable.ChangeTabLayout_ctl_indicatorPadding, indicatorPadding);
        a.recycle();

        this.tabViewHeight = tabViewHeight;
        this.tabImageHeight = tabImageHeight;
        this.defaultTabImageColor = defaultTabImageColor;
        this.selectedTabImageColor = selectedTabImageColor;
        this.textSize = textSize;
        this.indicatorPadding = indicatorPadding;
        this.defaultTabTextColor = defaultTabTextColor;
        this.selectedTabTextColor = selectedTabTextColor;

        this.tabStrip = new ChangeTabStrip(context, attrs);
        this.internalTabClickListener = new InternalTabClickListener();

        addView(tabStrip, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
    }

    private int [] icon;
    private int [] iconSelected;
    private int[] lastPosition; //水平ViewPages position位置
    private float[] lastValue; //水平ViewPages positionOffset位置

    public void setViewPager(VerticalViewPager viewPager, int [] iconDefault, int [] iconSelected) {
        tabStrip.removeAllViews();

        this.viewPager = viewPager;
        this.icon = iconDefault;
        this.iconSelected = iconSelected;
        page = 0;
        if (viewPager != null && viewPager.getAdapter() != null) {
            viewPager.addOnPageChangeListener(new InternalViewPagerListener());
            viewPager.setOnTouchListener(new ViewPagerTouchListener());
            lastPosition = new int[viewPager.getAdapter().getCount()];
            lastValue = new float[viewPager.getAdapter().getCount()];
            if(icon != null){
                populateTabStrip();
            }
        }
    }

    private void populateTabStrip() {
        final PagerAdapter adapter = viewPager.getAdapter();

        if(adapter.getCount() != icon.length){
            return;
        }

        if(iconSelected != null && iconSelected.length != icon.length){
            return;
        }

        int size = adapter.getCount();
        for (int i = 0; i < size; i++) {
            LinearLayout tabView;
            if(iconSelected == null){
                tabView = createTabView(adapter.getPageTitle(i), icon[i], 0);
            }else{
                tabView = createTabView(adapter.getPageTitle(i), icon[i], iconSelected[i]);
            }

            if (tabView == null) {
                throw new IllegalStateException("tabView is null.");
            }

            if (internalTabClickListener != null) {
                //分别为图片与文字设置点击事件，为了收起状态时文字不能点击做准备
                tabView.getChildAt(0).setOnClickListener(internalTabClickListener);
                tabView.getChildAt(1).setOnClickListener(internalTabClickListener);
            }

            tabStrip.addView(tabView);

            if (i == viewPager.getCurrentItem()) {
                ChangeTextView textView = (ChangeTextView) tabView.getChildAt(1);
                textView.setLevel(5000);
            }

            lastPosition[i] = ARRAY_INITIAL_VALUE;
        }
        setTabLayoutState(true); //默认设置为打开状态
    }

    protected LinearLayout createTabView(CharSequence title, int icon, int selectIcon) {

        LinearLayout mLinearLayout = new LinearLayout(getContext());
        mLinearLayout.setOrientation(LinearLayout.HORIZONTAL);
        mLinearLayout.setGravity(Gravity.CENTER_VERTICAL);
        mLinearLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, tabViewHeight));

        ImageView imageView = new ImageView(getContext());
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(tabImageHeight + (int) (16 * density), tabViewHeight);
        imageView.setPadding((int) (9 * density), 0, (int) (7 * density), 0);
        imageView.setLayoutParams(lp);

        RevealDrawable drawable;

        if(selectIcon != 0){
            drawable = new RevealDrawable(DrawableUtils.getDrawable(getContext(), icon), DrawableUtils.getDrawable(getContext(), selectIcon), RevealDrawable.VERTICAL);
        }else{
            drawable = new RevealDrawable(DrawableUtils.getTabDrawable(getContext(), icon, defaultTabImageColor),
                    DrawableUtils.getTabDrawable(getContext(), icon, selectedTabImageColor), RevealDrawable.VERTICAL);
        }

        imageView.setImageDrawable(drawable);

        ChangeTextView textView = new ChangeTextView(getContext());
        textView.setDefaultTabTextColor(defaultTabTextColor);
        textView.setSelectedTabTextColor(selectedTabTextColor);
        textView.setIndicatorPadding(indicatorPadding);
        textView.setTabViewTextSize(textSize);
        textView.setText(title.toString());
        textView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));

        mLinearLayout.addView(imageView);
        mLinearLayout.addView(textView);
        return mLinearLayout;
    }

    private class InternalViewPagerListener implements VerticalViewPager.OnPageChangeListener {

        private int scrollState;

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            int tabStripChildCount = tabStrip.getChildCount();
            if ((tabStripChildCount == 0) || (position < 0) || (position >= tabStripChildCount)) {
                return;
            }
            tabStrip.onViewPagerPageChanged(position, positionOffset);
            scrollToTab(position, positionOffset);
        }

        @Override
        public void onPageScrollStateChanged(int state) {
            scrollState = state;
        }

        @Override
        public void onPageSelected(int position) {
            if (scrollState == ViewPager.SCROLL_STATE_IDLE) {
                scrollToTab(position, 0);
            }
            page = position; // 记录位置

            for (int i = 0, size = tabStrip.getChildCount(); i < size; i++) {
                ChangeTextView textView = (ChangeTextView) ((LinearLayout) tabStrip.getChildAt(i)).getChildAt(1);
                if (position == i) {
                    textView.setLevel(5000);
                }else {
                    textView.setLevel(0);
                }
            }
        }
    }

    private class ViewPagerTouchListener implements OnTouchListener{

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_MOVE:
                    flag = true;
                    break;
            }
            return false;
        }
    }

    private void scrollToTab(int tabIndex, float positionOffset) {

        final int tabStripChildCount = tabStrip.getChildCount();
        if (tabStripChildCount == 0 || tabIndex < 0 || tabIndex >= tabStripChildCount) {
            return;
        }

        LinearLayout selectedTab = (LinearLayout) getTabAt(tabIndex);

        if (0f <= positionOffset && positionOffset < 1f) {
            if(!tabLayoutState){
                ImageView imageView = (ImageView) selectedTab.getChildAt(0);
                ((RevealDrawable)imageView.getDrawable()).setOrientation(RevealDrawable.VERTICAL);
                imageView.setImageLevel((int) (positionOffset * 5000 + 5000));
            }
            if(flag){
                ChangeTextView textView = (ChangeTextView) selectedTab.getChildAt(1);
                textView.setLevel((int) (positionOffset * 5000 + 5000));
            }
        }

        if(!(tabIndex + 1 >= tabStripChildCount)){
            LinearLayout tab = (LinearLayout) getTabAt(tabIndex + 1);

            if(!tabLayoutState){
                ImageView img = (ImageView) tab.getChildAt(0);
                ((RevealDrawable)img.getDrawable()).setOrientation(RevealDrawable.VERTICAL);
                img.setImageLevel((int) (positionOffset * 5000));
            }
            if(flag){
                ChangeTextView text = (ChangeTextView) tab.getChildAt(1);
                text.setLevel((int) (positionOffset * 5000));
            }
        }

        int titleOffset = tabViewHeight * 2;
        int extraOffset = (int) (positionOffset * selectedTab.getHeight());

        int y = (tabIndex > 0 || positionOffset > 0) ? -titleOffset : 0;
        int start = selectedTab.getTop();
        y += start + extraOffset;

        scrollTo(0, y);
    }

    /**
     * 水平VIewPager控制
     */
    public void setPageScrolled(int p, int position, float positionOffset) {
        if (page != p){
            return;
        }
        //竖屏状态下`ViewPage`的`onPageScrolled`监听不正常修复（统一数据）
        if (positionOffset > 0.99 && positionOffset < 1){
            positionOffset = 0;
            position = position + 1;
        }else if (positionOffset < 0.01 && positionOffset > 0.00001){
            positionOffset = 0;
        }

        if (position - lastPosition[page] > 0) {
            if (lastPosition[page] != ARRAY_INITIAL_VALUE){
                tabLayoutState = false; //每次向左滑动结束时，进入判断，菜单关闭状态
                tabLayoutIsClick = true;
            }
        }else if(position - lastPosition[page] < 0){
            if(lastValue[page] - positionOffset < 0){
                //向左滑动时，不操作。
                return;
            }
        }

        lastPosition[page] = position;
        lastValue[page] = positionOffset;

        if(positionOffset == 0){
            return;
        }

        if(tabLayoutState){ //防止重复收起
            final int tabStripChildCount = tabStrip.getChildCount();
            if (tabStripChildCount == 0 || page < 0 || page >= tabStripChildCount) {
                return;
            }

            LinearLayout selectedTab = (LinearLayout) getTabAt(page);
            ImageView imageView = (ImageView) selectedTab.getChildAt(0);
            ((RevealDrawable)imageView.getDrawable()).setOrientation(RevealDrawable.HORIZONTAL);
            if (0f < positionOffset && positionOffset <= 1f) {
                imageView.setImageLevel((int) ((1 - positionOffset) * 5000 + 5000));
            }

            for (int i = 0, size = tabStrip.getChildCount(); i < size; i++) {
                ChangeTextView textView = (ChangeTextView) ((LinearLayout) tabStrip.getChildAt(i)).getChildAt(1);
                if (0f < positionOffset && positionOffset <= 1f) {
                    textView.setAlpha((1 - positionOffset));
                    if(positionOffset > 0.9f){
                        textView.setVisibility(INVISIBLE);
                        tabLayoutIsClick = false; //防止同时点击，导致状态混乱
                    }else{
                        textView.setVisibility(VISIBLE);
                        tabLayoutIsClick = true;
                    }
                }
            }

            if (positionOffset > 0.98){
                positionOffset = 1;
            }
            tabStrip.onViewPagerPageChanged(positionOffset);
        }
    }

    /**
     * 点击事件
     */
    private class InternalTabClickListener implements OnClickListener {
        @Override
        public void onClick(View v) {

            if(!tabLayoutState){ // 收起状态点击，切换为打开状态
                setTabLayoutState(true);
                return;
            }

            if(!tabLayoutIsClick){ //只阻止打开状态时点击
                return;
            }

            for (int i = 0, size = tabStrip.getChildCount(); i < size; i++) {
                if (v.getParent() == tabStrip.getChildAt(i)) {

                    viewPager.setCurrentItem(i);
                    flag = false;
//                    viewPager.setCurrentItem(i, false);
                    if (onTabClickListener != null) {
                        onTabClickListener.onTabClicked(i);
                    }
                }
            }
        }
    }

    /**
     * 返回指定位置的选项卡.
     * @param position 位置
     * @return 指定位置的选项卡View
     */
    public View getTabAt(int position) {
        return tabStrip.getChildAt(position);
    }

    public interface OnTabClickListener {

        void onTabClicked(int position);

    }

    /**
     * Tab点击事件{@link OnTabClickListener}
     * @param listener 点击事件监听器
     */
    public void setOnTabClickListener(OnTabClickListener listener) {
        onTabClickListener = listener;
    }

    /**
     * 获取TabLayout的状态
     */
    public boolean getTabLayoutState(){
        return tabLayoutState;
    }

    /**
     * 设置TabLayoutState
     * @param state 状态
     */
    public void setTabLayoutState(boolean state){
        tabLayoutState = true; //先设为可操作
        if(state){
            setPageScrolled(page, lastPosition[page], 0.00001f);
        }else{
            setPageScrolled(page, lastPosition[page], 1f);
        }
        tabLayoutState = state;
    }

    /**
     * 设置阴影颜色
     * @param shadowColor 阴影颜色
     */
    public void setShadowColor(int shadowColor){
        tabStrip.setShadowColor(shadowColor);
    }

    /**
     * 设置背景颜色
     * @param backgroundColor 背景颜色
     */
    public void setTabLayoutBackgroundColor(int backgroundColor){
        tabStrip.setTabLayoutBackgroundColor(backgroundColor);
    }

    /**
     * 设置指示器颜色
     * @param indicatorColor 指示器颜色
     */
    public void setIndicatorColor(int indicatorColor){
        tabStrip.setIndicatorColor(indicatorColor);
    }

    /**
     * 设置指示器标记颜色
     * @param leftBorderColor 指示器标记颜色
     */
    public void setLeftBorderColor(int leftBorderColor){
        tabStrip.setLeftBorderColor(leftBorderColor);
    }

    /**
     * 设置指示器上下内边距
     * @param indicatorPadding 指示器上下内边距
     */
    public void setIndicatorPadding(int indicatorPadding){
        tabStrip.setIndicatorPadding(indicatorPadding);
    }

    /**
     * 设置指示器标记宽度
     * @param leftBorderThickness 指示器标记宽度
     */
    public void setLeftBorderThickness(int leftBorderThickness){
        tabStrip.setLeftBorderThickness(leftBorderThickness);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(tabLayoutState){
            return super.onTouchEvent(event);
        }else {
            final int action = event.getAction();
            switch (action) {
                case MotionEvent.ACTION_DOWN: //收起时点击不拦截，传入下层
                    return false;
                case MotionEvent.ACTION_MOVE: //收起时，滑动文字部分拦截
                    if(tabImageHeight + (int) (20 * density) < event.getRawX()){
                        return true;
                    }
                    break;
            }
            return super.onTouchEvent(event);
        }
    }
}
