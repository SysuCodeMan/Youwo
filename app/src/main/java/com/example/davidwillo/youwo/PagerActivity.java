package com.example.davidwillo.youwo;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.widget.Toast;

import com.example.davidwillo.youwo.life.LifeMainFragment;
import com.example.davidwillo.youwo.sport.SportMainFragment;
import com.example.davidwillo.youwo.study.StudyMainFragment;
import com.example.davidwillo.youwo.util.MyViewPagerAdapter;

public class PagerActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pager);


        final ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);

        MyViewPagerAdapter myViewPagerAdapter = new MyViewPagerAdapter(getSupportFragmentManager(), this);
        SportMainFragment sportMainFragment1 = new SportMainFragment();
        LifeMainFragment lifeMainFragment2 = new LifeMainFragment();
        StudyMainFragment studyMainFragment3 = new StudyMainFragment();
        myViewPagerAdapter.addFragment(sportMainFragment1);
        myViewPagerAdapter.addFragment(lifeMainFragment2);
        myViewPagerAdapter.addFragment(studyMainFragment3);

        viewPager.setAdapter(myViewPagerAdapter);


        tabLayout.setupWithViewPager(viewPager);
        tabLayout.getTabAt(0).setIcon(R.drawable.tab_sport);
        tabLayout.getTabAt(1).setIcon(R.drawable.tab_life);
        tabLayout.getTabAt(2).setIcon(R.drawable.tab_study);

    }

    long firstTime = 0;
    @Override
    public void onBackPressed() {
        long secondTime = System.currentTimeMillis();
        if (secondTime - firstTime > 2000) {
            Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
            firstTime = secondTime;
        } else {
            Intent intent = new Intent(PagerActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("exit", true);
            startActivity(intent);
        }
    }
}
