package com.ijays.operatonsysexample.activity;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.ijays.operatonsysexample.R;
import com.ijays.operatonsysexample.fragment.ImageListFragment;
import com.ijays.operatonsysexample.fragment.MemManagementFragment;
import com.ijays.operatonsysexample.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;

/**
 * Created by ijaysdev on 16/5/18.
 */
public class PicExploreActivity extends BaseActivity implements View.OnClickListener {
    @Bind(R.id.toolbar)
    Toolbar mToolbar;
    @Bind(R.id.viewpager)
    ViewPager mViewPager;
    @Bind(R.id.tabs)
    TabLayout mTabLayout;
    @Bind(R.id.no_net_view)
    View mNoNetView;
    @Bind(R.id.no_net_img)
    ImageView mNonetImg;

    @Override
    protected int getContentViewId() {
        return R.layout.pic_explore_activity;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        super.init(savedInstanceState);

        setSupportActionBar(mToolbar);

        final ActionBar ab = getSupportActionBar();
        if (ab != null) {
//            ab.setHomeAsUpIndicator(R.drawable.ic_menu);
            ab.setTitle("PicExplore");
            ab.setDisplayHomeAsUpEnabled(true);
        }
        mNoNetView.setOnClickListener(this);
        initView();
    }

    private void initView() {
        if (!Utils.isConnectToNet(PicExploreActivity.this.getApplicationContext())) {
            mNoNetView.setVisibility(View.VISIBLE);
            mTabLayout.setVisibility(View.GONE);

        } else {
            if (mViewPager != null) {
                setupViewPager(mViewPager);
                mTabLayout.setVisibility(View.VISIBLE);
                mNoNetView.setVisibility(View.GONE);
                mTabLayout.setupWithViewPager(mViewPager);
            }
        }
    }

    /**
     * 初始化viewpager
     *
     * @param viewPager
     */
    private void setupViewPager(ViewPager viewPager) {
        Adapter adapter = new Adapter(getSupportFragmentManager());
        ImageListFragment fragment = new ImageListFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("type", 1);
        fragment.setArguments(bundle);
        adapter.addFragment(fragment, "one");
        fragment = new ImageListFragment();
        bundle = new Bundle();
        bundle.putInt("type", 2);
        fragment.setArguments(bundle);
        adapter.addFragment(fragment, "two");
        fragment = new ImageListFragment();
        bundle = new Bundle();
        bundle.putInt("type", 3);
        fragment.setArguments(bundle);
        adapter.addFragment(fragment, "three");
        MemManagementFragment memTipFragment = new MemManagementFragment();
        adapter.addFragment(memTipFragment, "Mem_Tip");
        viewPager.setAdapter(adapter);
    }

    @Override
    public void onClick(View view) {
        initView();
    }


    static class Adapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragments = new ArrayList<>();
        private final List<String> mFragmentTitles = new ArrayList<>();

        public Adapter(FragmentManager fm) {
            super(fm);
        }

        public void addFragment(Fragment fragment, String title) {
            mFragments.add(fragment);
            mFragmentTitles.add(title);
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
            return mFragmentTitles.get(position);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
