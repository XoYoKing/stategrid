package com.etrust.stategrid.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.etrust.stategrid.R;

public class TabViewAdapter extends BaseAdapter {

	private Context context;
	public String[] lable;
	public String[] data;

	public TabViewAdapter(Context context,String[] lable,String[] data) {
		this.context = context;
		this.data  = data;
		this.lable = lable;
	}
	
	@Override
	public int getCount() {
		return data.length;
	}

	@Override
	public Object getItem(int position) {
		return data[position];
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();

			convertView = LayoutInflater.from(context).inflate(
					R.layout.adapter_tabview, null);
			holder.tabview_right = (TextView) convertView
					.findViewById(R.id.tabview_right);
			holder.tabview_left = (TextView) convertView
					.findViewById(R.id.tabview_left);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		if (getCount() > 1) {
			if (position == 0) {
				holder.tabview_left.setBackgroundResource(R.drawable.shape_top_left);
				holder.tabview_right.setBackgroundResource(R.drawable.shape_top_right);
			} else if (position == getCount() - 1) {
				holder.tabview_left.setBackgroundResource(R.drawable.shape_bottom_left);
				holder.tabview_right.setBackgroundResource(R.drawable.shape_bottom_right);
			} else {
				holder.tabview_left.setBackgroundColor(Color.parseColor("#EEF6F9"));
				holder.tabview_right.setBackgroundColor(Color.parseColor("#FFFFFF"));
			}
		} else {
			holder.tabview_left.setBackgroundResource(R.drawable.shape_left);
			holder.tabview_right.setBackgroundResource(R.drawable.shape_right);
		}
		holder.tabview_left.setText(lable[position]);
		holder.tabview_right.setText(data[position]);
		return convertView;
	}

	public static class ViewHolder {
		TextView tabview_right;
		TextView tabview_left;
	}
}
