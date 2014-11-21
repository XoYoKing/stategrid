package com.etrust.stategrid.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.etrust.stategrid.R;

public class DataQueryAdapter extends BaseAdapter {

	private Context context;
	List<String> data;
	
	public DataQueryAdapter(Context context, List<String> data) {
		this.context = context;
		this.data = data;
	}

	@Override
	public int getCount() {
		return data.size();
	}

	@Override
	public Object getItem(int position) {
		return data.get(position);
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
					R.layout.adapter_tasklist, null);
			holder.tasklist_state = (TextView) convertView
					.findViewById(R.id.tasklist_state);
			holder.tasklist_title = (TextView) convertView
					.findViewById(R.id.tasklist_title);
			holder.tasklist_time = (TextView) convertView
					.findViewById(R.id.tasklist_time);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.tasklist_title.setText(data.get(position));
		holder.tasklist_time.setVisibility(View.INVISIBLE);
		holder.tasklist_state.setVisibility(View.INVISIBLE);
		return convertView;
	}

	public static class ViewHolder {
		TextView tasklist_state;
		TextView tasklist_title;
		TextView tasklist_time;
	}
}
