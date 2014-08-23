package ua.org.cofriends.reades.ui.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;

import java.util.ArrayList;

public class TabsAdapter extends FragmentStatePagerAdapter implements ActionBar.TabListener, ViewPager.OnPageChangeListener {
	
	protected final ActionBar mActionBar;
	protected final ViewPager mViewPager;
	protected final ActionBarActivity mActivity;
	protected final ArrayList<Fragment> mFragments = new ArrayList<Fragment>();

	public TabsAdapter(ActionBarActivity activity, ViewPager pager) {
		super(activity.getSupportFragmentManager());
		mActivity = activity;
		mActionBar = activity.getSupportActionBar();
		mViewPager = pager;
		mViewPager.setAdapter(this);
		mViewPager.setOnPageChangeListener(this);
	}

	public void addTab(ActionBar.Tab tab, Fragment fragment) {
		tab.setTag(fragment);
		tab.setTabListener(this);
		mFragments.add(fragment);
		mActionBar.addTab(tab);
		notifyDataSetChanged();
	}
	
	/**
	 * Returns fragment that is on the selected tab and is visible to the end user.
	 * @return current fragment
	 */
	public Fragment getCurrentFragment() {
		return getItem(mActionBar.getSelectedNavigationIndex());
	}

	@Override
	public int getCount() {
		return mFragments.size();
	}

	@Override
	public Fragment getItem(int position) {
		return mFragments.get(position);
	}
	
	/**
	 * Get context of the view pager
	 * @return context instance
	 */
	public Context getContext() {
		return mViewPager.getContext();
	}

	public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
	}

	public void onPageSelected(int position) {
		mActionBar.setSelectedNavigationItem(position);
	}

	public void onPageScrollStateChanged(int state) {
	}

	@Override
	public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
		Object tag = tab.getTag();
		for (int i = 0; i < mFragments.size(); i++) {
			if (mFragments.get(i) == tag) {
				mViewPager.setCurrentItem(i);
			}
		}
	}

	@Override
	public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {
	}

	@Override
	public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {
	}
}