package com.etrust.stategrid.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.etrust.stategrid.R;
import com.etrust.stategrid.bean.TaskBean;

public class TaskListAdapter extends BaseAdapter {

	private Context context;
	public List<TaskBean> data;
	
	private static int[] STATUS = new int []{
			R.drawable.task_nor,R.drawable.task_proc,
			R.drawable.task_pause,R.drawable.task_over,R.drawable.task_over
	};//任务状态图标
	public TaskListAdapter(Context context, List<TaskBean> data) {
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
		holder.tasklist_title.setText(data.get(position).name);
		holder.tasklist_state.setText(data.get(position).getStatusStr());
		holder.tasklist_state.setBackgroundResource(STATUS[data.get(position).status]);
		holder.tasklist_time.setText(
				"开始时间:"+ data.get(position).begintime
				+"    截至时间:"+ data.get(position).enddate);
		return convertView;
	}

	public static class ViewHolder {
		TextView tasklist_state;
		TextView tasklist_title;
		TextView tasklist_time;
	}

	public List<TaskBean> getData() {
		return data;
	}

	public void setData(List<TaskBean> data) {
		this.data = data;
	}
	
	
}
