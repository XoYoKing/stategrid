package com.etrust.stategrid.adapter;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.etrust.stategrid.R;

public class PlanEquipAdapter extends BaseAdapter implements
		OnClickListener {


	private Context context;
	public String[] lable;
	public String[] data;

	public EditText[] editText;

	public PlanEquipAdapter(Context context, String[] lable, String[] data) {
		this.context = context;
		this.data = data;
		this.lable = lable;

		editText = new EditText[1];//选项个数
		for (int i = 0; i < editText.length; i++) {
			editText[i] = (EditText) LayoutInflater.from(context).inflate(
					R.layout.edittext_item, null);
			editText[i].setLines(8);
		}
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
					R.layout.adapter_report, null);
			holder.tabview_right = (LinearLayout) convertView
					.findViewById(R.id.tabview_right);
			holder.tabview_left = (TextView) convertView
					.findViewById(R.id.tabview_left);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		if (getCount() > 1) {
			if (position == 0) {
				holder.tabview_left
						.setBackgroundResource(R.drawable.shape_top_left);
				holder.tabview_right
						.setBackgroundResource(R.drawable.shape_top_right);
			} else if (position == getCount() - 1) {
				holder.tabview_left
						.setBackgroundResource(R.drawable.shape_bottom_left);
				holder.tabview_right
						.setBackgroundResource(R.drawable.shape_bottom_right);
			} else {
				holder.tabview_left.setBackgroundColor(Color
						.parseColor("#EEF6F9"));
				holder.tabview_right.setBackgroundColor(Color
						.parseColor("#FFFFFF"));
			}
		} else {
			holder.tabview_left.setBackgroundResource(R.drawable.shape_left);
			holder.tabview_right.setBackgroundResource(R.drawable.shape_right);
		}

		holder.tabview_left.setText(lable[position]);
		holder.tabview_right.removeAllViews();
		convertView.setLayoutParams(new AbsListView.LayoutParams(
				LayoutParams.MATCH_PARENT, 60));

		switch (position) {
		    case 0:
		    	
				convertView.setLayoutParams(new AbsListView.LayoutParams(
						LayoutParams.MATCH_PARENT, 250));
				LinearLayout.LayoutParams lyz = new LinearLayout.LayoutParams(
						LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
				
				editText[position].setLayoutParams(lyz);
				editText[position].setText(data[position]);
				editText[position].setTag(position);
				editText[position].setOnFocusChangeListener(new OnFocusChangeListener() {
							@Override
							public void onFocusChange(View v, boolean hasFocus) {
								if (!hasFocus) {
									int pos = (Integer) v.getTag();
									data[pos] = ((EditText) v).getEditableText().toString();
								}
							}
						});
				holder.tabview_right.addView(editText[position]);
				break;
		}

		return convertView;
	}

	public static class ViewHolder {
		LinearLayout tabview_right;
		TextView tabview_left;
	}

	@Override
	public void onClick(View v) {
	}
}
