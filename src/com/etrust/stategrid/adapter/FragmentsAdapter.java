package com.etrust.stategrid.adapter;

import java.util.List;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class FragmentsAdapter extends FragmentPagerAdapter {
	
	String[] CONTENT;
    List<Fragment> views;
    
    public FragmentsAdapter(FragmentManager fm,List<Fragment> views,String[] CONTENT) {
    	super(fm);
		this.CONTENT = CONTENT;
		this.views =views;
	}

    @Override
    public Fragment getItem(int position) {
        return views.get(position);
    }

    @Override
    public int getCount() {
        return views.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
      return CONTENT[position % CONTENT.length];
    }
}