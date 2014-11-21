package com.etrust.stategrid.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.etrust.stategrid.R;
import com.etrust.stategrid.bean.CheckItem;
import com.etrust.stategrid.bean.ItemCategory;

public class DefectSelectAdapter extends BaseExpandableListAdapter {

	private Context context;
	public List<ItemCategory> data;

	public DefectSelectAdapter(Context context) {
		if (data == null) {
			data = new ArrayList<ItemCategory>();
		}
		this.context = context;
	}

	public void setData(List<ItemCategory> data) {
		this.data = data;
	}

	@Override
	public CheckItem getChild(int groupPosition, int childPosition) {
		return data.get(groupPosition).getCheckItem().get(childPosition);
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		return data.get(groupPosition).getCheckItem().size();
	}

	@Override
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		View view = convertView;
		if (view == null) {
			view = LayoutInflater.from(context).inflate(R.layout.child_adapter,
					null);
		}

		TextView title = (TextView) view.findViewById(R.id.childto);
		title.setText(getChild(groupPosition, childPosition).name);

		return view;
	}

	@Override
	public ItemCategory getGroup(int groupPosition) {
		return data.get(groupPosition);
	}

	@Override
	public int getGroupCount() {
		return data.size();
	}

	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		View view = convertView;
		if (view == null) {
			view = LayoutInflater.from(context).inflate(R.layout.group_adapter,
					null);
		}

		TextView title = (TextView) view.findViewById(R.id.groupto);
		title.setText(getGroup(groupPosition).name);
		ImageView arraw_up = (ImageView) view.findViewById(R.id.arraw_up);
		if(isExpanded){
			arraw_up.setImageResource(R.drawable.arrow_up);
		}else{
			arraw_up.setImageResource(R.drawable.arrow_down);
		}
		return view;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}

	@Override
	public boolean hasStableIds() {
		return true;
	}

}
