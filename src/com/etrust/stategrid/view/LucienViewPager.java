package com.etrust.stategrid.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class LucienViewPager extends ViewPager {
	private boolean isCanScroll = false;

	public LucienViewPager(Context context) {
		super(context);
	}

	public LucienViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public void setScanScroll(boolean isCanScroll) {
		this.isCanScroll = isCanScroll;
	}

	@Override
	public void scrollTo(int x, int y) {
			super.scrollTo(x, y);
	}

	@Override
	public boolean onTouchEvent(MotionEvent arg0) {
		return isCanScroll;
	}
	@Override
	public boolean onInterceptTouchEvent(MotionEvent arg0) {
		return isCanScroll;
	}
	@Override
	public void setCurrentItem(int item, boolean smoothScroll) {
		super.setCurrentItem(item, smoothScroll);
	}

	@Override
	public void setCurrentItem(int item) {
		super.setCurrentItem(item);
	}
}