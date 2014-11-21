package com.etrust.stategrid.adapter;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.etrust.stategrid.ImagePagerActivity;
import com.etrust.stategrid.InfraredReportActivity;
import com.etrust.stategrid.R;
import com.etrust.stategrid.TempInfraredDetail;

public class TempInfraredAdapter extends BaseAdapter implements
		OnClickListener {

	public static final int PIC_CASE = 15;

	private Context context;
	public String[] lable;
	public String[] data;

	public EditText[] editText;

	public TempInfraredAdapter(Context context, String[] lable, String[] data) {
		this.context = context;
		this.data = data;
		this.lable = lable;

		editText = new EditText[15];
		for (int i = 0; i < editText.length; i++) {
			editText[i] = (EditText) LayoutInflater.from(context).inflate(
					R.layout.edittext_item, null);
			LinearLayout.LayoutParams lyz = new LinearLayout.LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
			editText[i].setLayoutParams(lyz);
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
		case PIC_CASE:
			convertView.setLayoutParams(new AbsListView.LayoutParams(
					LayoutParams.MATCH_PARENT, 120));
			if (data[PIC_CASE].isEmpty()) {

			} else {
				String[] pics = data[PIC_CASE].split(",");
				ImageView[] iv = new ImageView[pics.length];

				for (int i = 0; i < pics.length; i++) {
					iv[i] = new ImageView(context);
					iv[i].setScaleType(ScaleType.CENTER_CROP);
					LinearLayout.LayoutParams ly = new LinearLayout.LayoutParams(
							160, LayoutParams.MATCH_PARENT);
					ly.setMargins(3, 2, 3, 2);
					iv[i].setLayoutParams(ly);
					iv[i].setTag(R.id.tabview_right, i);
					iv[i].setTag(R.id.tabview_left, pics);
					FileInputStream fis = null;
					try {
						fis = new FileInputStream(pics[i]);
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					}
					Bitmap bap = BitmapFactory.decodeStream(fis);
					iv[i].setImageBitmap(bap);

					holder.tabview_right.addView(iv[i]);
				}
				if (pics.length < 4) {
					ImageView take_img = (ImageView) LayoutInflater.from(
							context).inflate(R.layout.take_img, null);
					take_img.setOnClickListener(this);
					holder.tabview_right.addView(take_img);
				}
			}
			break;
		default:
			editText[position].getEditableText().append(data[position]);
			editText[position].setTag(position);
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
