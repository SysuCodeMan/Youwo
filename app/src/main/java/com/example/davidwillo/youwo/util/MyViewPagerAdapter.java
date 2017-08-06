package com.example.davidwillo.youwo.util;

/**
 * Created by davidwillo on 5/12/17.
 */

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;

import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class MyViewPagerAdapter extends FragmentPagerAdapter {


    private List<Fragment> mFragments = new ArrayList<>();
    private String tabTitles[] = new String[]{"户外", "生活", "学习"};
    private Context context;

    public void addFragment(Fragment fragment) {
        mFragments.add(fragment);
    }

    public MyViewPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
    }

    @Override
    public Fragment getItem(int position) {


        return mFragments.get(position);
    }

    @Override
    public int getCount() {
        return mFragments.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabTitles[position];
    }


}
