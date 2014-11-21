package com.etrust.stategrid.view;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

public class ExListView extends ExpandableListView implements OnScrollListener {
	
	ExpandableListAdapter adapter;
	
	@Override
	public void setAdapter(ExpandableListAdapter adapter) {
		// TODO Auto-generated method stub
		this.adapter = adapter;
		super.setAdapter(adapter);
	}

	private RelativeLayout _groupLayout;
	public int _groupIndex = -1;

	/**
	 * @param context
	 */
	public ExListView(Context context) {
		super(context);
		super.setOnScrollListener(this);
	}

	/**
	 * @param context
	 * @param attrs
	 */
	public ExListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		super.setOnScrollListener(this);
	}

	/**
	 * @param context
	 * @param attrs
	 * @param defStyle
	 */
	public ExListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		super.setOnScrollListener(this);
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {

		if (adapter == null)
			adapter = this.getExpandableListAdapter();

		int ptp = view.pointToPosition(0, 0);
		if (ptp != AdapterView.INVALID_POSITION) {
			ExListView qExlist = (ExListView) view;
			long pos = qExlist.getExpandableListPosition(ptp);
			int groupPos = ExpandableListView.getPackedPositionGroup(pos);
			int childPos = ExpandableListView.getPackedPositionChild(pos);

			if (childPos < 0) {
				groupPos = -1;
			}
			if (groupPos < _groupIndex) {

				_groupIndex = groupPos;

				if (_groupLayout != null) {
					_groupLayout.removeAllViews();
					_groupLayout.setVisibility(GONE);// 这里设置Gone 为了不让它遮挡后面header
				}
			} else if (groupPos > _groupIndex) {
				final FrameLayout fl = (FrameLayout) getParent();
				_groupIndex = groupPos;
				if (_groupLayout != null)
					fl.removeView(_groupLayout);

				_groupLayout = (RelativeLayout) getExpandableListAdapter()
						.getGroupView(groupPos, true, null, null);
				_groupLayout.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						collapseGroup(_groupIndex);
						 new Handler().post(new Runnable() {
							@Override
							public void run() {
								// TODO Auto-generated method stub
								fl.removeView(_groupLayout);
								fl.addView(_groupLayout, new LayoutParams(
										android.view.ViewGroup.LayoutParams.FILL_PARENT, 50));
							}
						});
					}
				});

				fl.addView(_groupLayout, fl.getChildCount(), new LayoutParams(
						android.view.ViewGroup.LayoutParams.FILL_PARENT, 50));

			}
		}
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
	}

}
