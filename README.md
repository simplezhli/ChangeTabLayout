# ChangeTabLayout

[![Stars](https://img.shields.io/github/stars/simplezhli/ChangeTabLayout.svg)](https://github.com/simplezhli/ChangeTabLayout/stargazers) [![LICENSE](https://img.shields.io/badge/license-Apache%202-blue.svg)](https://raw.githubusercontent.com/simplezhli/ChangeTabLayout/master/LICENSE) [![作者](https://img.shields.io/badge/%E4%BD%9C%E8%80%85-weilu-orange.svg)](http://blog.csdn.net/qq_17766199)

ChangeTabLayout是模仿乐视LIVE App主界面TabLayout效果。

## Preview

原效果图

原效果图转为Gif过大，所以将录制的MP4效果视频已经放入了根目录的[preview](/preview)文件夹内，有兴趣可去查看。

实现效果图

![preview](/preview/preview.gif)

#### `ChangeTabLayout`在打开状态时

- 垂直方向切换时，文字的颜色大小变化。
- 水平方向切换时，文字的渐变与图片的变化。

#### `ChangeTabLayout`在收起状态时

- 垂直方向切换时，图片的变化。
- 点击`ChangeTabLayout`，切换为打开状态。

## 使用说明

因为使用场景的局限性等原因，暂时不上传至Maven中心仓库中，仅提供参考学习用途。

### xml使用部分
```xml
    <FrameLayout
        xmlns:android="http://schemas.android.com/apk/res/android"
        android:layout_width="match_parent"
        android:layout_height="match_parent">

        <android.support.v4.view.VerticalViewPager
            android:id="@+id/viewpager"
            android:background="@color/bg"
            android:layout_width="match_parent"
            android:fadingEdge="none"
            android:overScrollMode="never"
            android:layout_height="match_parent"/>

        <com.github.zl.changetablayout.ChangeTabLayout
            android:id="@+id/tabLayout"
            android:layout_width="110dp"
            android:layout_height="match_parent"/>

    </FrameLayout>
```

### 代码使用部分

```java
   VerticalViewPager mViewPager = (VerticalViewPager) this.findViewById(R.id.viewpager);
   //只传入自己想要变化的图片,颜色属性生效
   mTabLayout.setViewPager(mViewPager, icon, null);
   //传入默认图片与选中图片,颜色属性不生效
   mTabLayout.setViewPager(mViewPager, iconDefault, iconSelected);

   //监听TabLayout点击事件
   mTabLayout.setOnTabClickListener(new ChangeTabLayout.OnTabClickListener() {
       @Override
       public void onTabClicked(int position) {

       }
   });

   //出入水平方向Page滚动参数，实现TabLayout的收缩效果（可选）
   mTabLayout.setPageScrolled(page, position, positionOffset);

```

### 属性说明

xml | 默认值 | 说明
---|---|---
app:ctl_tabViewColor | 默认为#161616 | Tab背景颜色
app:ctl_indicatorColor | 默认为#70000000 | 指示器背景色
app:ctl_leftBorderColor | 默认为#cf212b | 指示器左侧竖条颜色
app:ctl_tabViewHeight | 默认为50dp | Tab高度
app:ctl_tabImageHeight | 默认为18dp | Tab中图片高度（宽度）
app:ctl_tabViewShadowColor | 默认为#161616 | Tab收起时阴影颜色
app:ctl_indicatorPadding | 默认为4dp | 指示器与Tab的上下边距之和
app:ctl_defaultTabTextSize | 默认为14sp | Tab中文字默认大小
app:ctl_leftBorderThickness | 默认为3dp | 指示器左侧竖条宽度
app:ctl_defaultTabTextColor | 默认为#494949 | Tab中文字默认颜色
app:ctl_selectedTabTextColor | 默认为#ffffff | Tab中文字选中颜色
app:ctl_defaultTabImageColor | 默认为#494949 | Tab中图片默认颜色
app:ctl_selectedTabImageColor | 默认为#cf212b | Tab中图片选中颜色

## 实现过程

具体参考这篇博客：[ChangeTabLayout实现过程](http://blog.csdn.net/qq_17766199/article/details/68941610)

## TODO

1. ~~`TabView`中兼容多行文字。~~
2. ~~竖屏状态下`ViewPage`的`onPageScrolled`监听不正常。~~

## Thanks For

- https://github.com/ogaclejapan/SmartTabLayout
- https://github.com/castorflex/VerticalViewPager
- https://gist.github.com/rharter/34051da57f8a6a0991ff

## License

	Copyright 2017 simplezhli

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
