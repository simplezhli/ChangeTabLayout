package com.zl.changetablayout;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItem;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;

public class PageFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                           @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_page, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ViewPager mViewPager = (ViewPager) view.findViewById(R.id.vp);
        FragmentPagerItems pages = new FragmentPagerItems(getActivity());
        final int page = FragmentPagerItem.getPosition(getArguments());

        for (int i = 0; i < 5; i++) {
            Bundle bundle = new Bundle();
            bundle.putInt("page", page);
            bundle.putInt("position", i);
            pages.add(FragmentPagerItem.of(String.valueOf(i), DemoFragment.class, bundle));
        }
        FragmentPagerItemAdapter adapter = new FragmentPagerItemAdapter(getChildFragmentManager(), pages); //Fragment嵌套Fragment时，要用getChildFragmentManager
        mViewPager.setAdapter(adapter);
        mViewPager.setOffscreenPageLimit(2);
        mViewPager.setPageMargin(30);
        final MainActivity mainActivity = (MainActivity) getActivity();
        mViewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                mainActivity.pageScrolled(page, position, positionOffset);
            }
        });
    }

}
