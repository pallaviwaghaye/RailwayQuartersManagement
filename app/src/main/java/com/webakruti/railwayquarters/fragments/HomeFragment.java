package com.webakruti.railwayquarters.fragments;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.webakruti.railwayquarters.R;
import com.webakruti.railwayquarters.adapter.CauroselPageAdapter;
import com.webakruti.railwayquarters.pageindicator.PageControl;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


public class HomeFragment extends Fragment {

    private View rootView;
    private ViewPager viewPager;
    private PageControl page_control;
    private CauroselPageAdapter pagerAdapter;
    Timer timer;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_home, container, false);


        List<Drawable> list = new ArrayList<>();
        list.add(getResources().getDrawable(R.drawable.image1));
        list.add(getResources().getDrawable(R.drawable.image2));
        list.add(getResources().getDrawable(R.drawable.image3));
        list.add(getResources().getDrawable(R.drawable.image4));
        list.add(getResources().getDrawable(R.drawable.image5));


        // send this list to carousel adapter.

        initCarouselData(list);

        return rootView;
    }


    private void initCarouselData(final List<Drawable> imageList) {
        viewPager = (ViewPager) rootView.findViewById(R.id.viewPager);
        page_control = (PageControl) rootView.findViewById(R.id.page_control);

        /*viewPager.setPageMargin(10);
        viewPager.setClipToPadding(false);
        pagerAdapter = new CauroselPageAdapter(getActivity(), imageList);
        viewPager.setAdapter(pagerAdapter);
        page_control.setViewPager(viewPager);
        page_control.setPosition(0);*/

        viewPager.setPageMargin(10);
        viewPager.setClipToPadding(false);

        pagerAdapter = new CauroselPageAdapter(getActivity(), imageList);
        viewPager.setAdapter(pagerAdapter);

        page_control.setViewPager(viewPager);
        page_control.setPosition(0);

        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                viewPager.post(new Runnable(){

                    @Override
                    public void run() {
                        viewPager.setCurrentItem((viewPager.getCurrentItem()+1)%imageList.toArray().length);
                    }
                });
            }
        };
        timer = new Timer();
        timer.schedule(timerTask, 3000, 3000);

    }

    @Override
    public void onDestroy() {
        timer.cancel();
        super.onDestroy();
    }
}
