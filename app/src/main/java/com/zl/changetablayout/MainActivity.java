package com.zl.changetablayout;

import android.os.Bundle;
import android.support.v4.view.VerticalViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.github.zl.changetablayout.ChangeTabLayout;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItem;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;

public class MainActivity extends AppCompatActivity {

    private String TAG = "MainActivity";
    private VerticalViewPager mViewPager;
    private ChangeTabLayout mTabLayout;

    private boolean mFlag = true;

    private String [] title = new String[] {
            "毛笔", "音乐", "风车", "房子", "通知",
            "笑脸", "花", "照相机", "路由器", "熊掌熊掌熊掌熊掌熊掌熊掌",
            "大山", "灰机", "电影", "汽车", "太阳"
    };

    private int [] icon = new int[] {
            R.drawable.ic_brush_black_24dp,
            R.drawable.ic_audiotrack_black_24dp,
            R.drawable.ic_toys_black_24dp,
            R.drawable.ic_home_black_24dp,
            R.drawable.ic_notifications_black_24dp,
            R.drawable.ic_face_black_24dp,
            R.drawable.ic_filter_vintage_black_24dp,
            R.drawable.ic_photo_camera_black_24dp,
            R.drawable.ic_router_black_24dp,
            R.drawable.ic_pets_black_24dp,
            R.drawable.ic_landscape_black_24dp,
            R.drawable.ic_local_airport_black_24dp,
            R.drawable.ic_local_movies_black_24dp,
            R.drawable.ic_drive_eta_black_24dp,
            R.drawable.ic_wb_sunny_black_24dp
    };

    private String [] title1 = new String[] {
            "安卓", "火焰", "音乐", "云朵"
    };

    private int [] iconDefault = new int[]{
            R.drawable.icon_android_,
            R.drawable.icon_fire_,
            R.drawable.icon_music_,
            R.drawable.icon_cloud_
    };

    private int [] iconSelected = new int[]{
            R.drawable.icon_android,
            R.drawable.icon_fire,
            R.drawable.icon_music,
            R.drawable.icon_cloud
    };

    private FragmentPagerItems pages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mViewPager = (VerticalViewPager) this.findViewById(R.id.viewpager);
        mTabLayout = (ChangeTabLayout) this.findViewById(R.id.tabLayout);

        pages = new FragmentPagerItems(this);
        mViewPager.setOffscreenPageLimit(title.length);
        for (String titleResId : title) {
            pages.add(FragmentPagerItem.of(titleResId, PageFragment.class));
        }

        FragmentPagerItemAdapter adapter = new FragmentPagerItemAdapter(getSupportFragmentManager(), pages);
        mViewPager.setAdapter(adapter);
        mTabLayout.setViewPager(mViewPager, icon, null);
        mTabLayout.setOnTabClickListener(new ChangeTabLayout.OnTabClickListener() {
            @Override
            public void onTabClicked(int position) {
                Log.d(TAG, "点击了" + position);
            }
        });

    }

    public void pageScrolled(int page, int position, float positionOffset){
        mTabLayout.setPageScrolled(page, position, positionOffset);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_switch) {
            mTabLayout.setTabLayoutState(!mTabLayout.getTabLayoutState());
            return true;
        }

        if (id == R.id.action_cut) {
            pages.clear();
            if(mFlag){
                for (String titleResId : title1) {
                    pages.add(FragmentPagerItem.of(titleResId, DemoFragment.class));
                }
                FragmentPagerItemAdapter adapter = new FragmentPagerItemAdapter(getSupportFragmentManager(), pages);
                mViewPager.setAdapter(adapter);
                mTabLayout.setViewPager(mViewPager, iconDefault, iconSelected);
            }else {
                for (String titleResId : title) {
                    pages.add(FragmentPagerItem.of(titleResId, PageFragment.class));
                }
                FragmentPagerItemAdapter adapter = new FragmentPagerItemAdapter(getSupportFragmentManager(), pages);
                mViewPager.setAdapter(adapter);
                mTabLayout.setViewPager(mViewPager, icon, null);
            }
            mFlag = !mFlag;
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
