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
import com.etrust.stategrid.TreeActivity;

public class InfraredReportAdapter extends BaseAdapter implements
		OnClickListener {
	public static final int DEFECT_DEVICE_CASE = 0;
	public static final int PIC_CASE = 15;
	public static final int Kejianguang_PIC = 16;

	private Context context;
	private InfraredReportActivity iAct;
	public String[] lable;
	public String[] data;

	public EditText[] editText;
	public int imgType = 1;

	public InfraredReportAdapter(Context context, String[] lable, String[] data) {
		this.context = context;
		this.data = data;
		this.lable = lable;
		iAct = (InfraredReportActivity) context;

		editText = new EditText[16];
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
					R.layout.adapter_report, null);// 红外上传页面listview左右部分
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

		case DEFECT_DEVICE_CASE:// 选取设备显示

			View device_item = LayoutInflater.from(context).inflate(
					R.layout.video_item, null);// 布局
			TextView device_name = (TextView) device_item
					.findViewById(R.id.view_text_id);// 绑定id
			if (data[position].isEmpty()) {
				device_name.setText("请选择");
			} else {
				device_name.setText(data[position]);
			}
			device_item.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent();
					intent.setClass(iAct, TreeActivity.class);
					intent.putExtra("mTransSubID", iAct.defectTsid);
					intent.putExtra("from", "ProDefectDetailAdapter");// ProDefectDetailAdapter标志跳转的
					iAct.startActivityForResult(
							intent,
							InfraredReportActivity.activity_result_DEFECT_DEVICE_SELECT);// 跳转到上个页面做某事
				}
			});
			holder.tabview_right.addView(device_item);
			break;
		case PIC_CASE:

			convertView.setLayoutParams(new AbsListView.LayoutParams(
					LayoutParams.MATCH_PARENT, 120));
//			if (data[PIC_CASE].isEmpty()) {//数据为空就添加照片
//System.out.println("照片为空");
//				ImageView take_img = (ImageView) LayoutInflater.from(context)
//						.inflate(R.layout.take_img, null);
//				take_img.setOnClickListener(this);
//				imgType = 1;
//				System.out.println("PIC_CASE");
//				holder.tabview_right.addView(take_img);
//			} 
			if ("pic".equals(data[PIC_CASE])) {
				ImageView take_img = (ImageView) LayoutInflater.from(context)
						.inflate(R.layout.take_img, null);
				take_img.setOnClickListener(this);
				holder.tabview_right.addView(take_img);
			} else {
				String[] pics = data[PIC_CASE].split(",");
				
				System.out.println("PIC长度+++++++++++++++++++"+pics.length);
				ImageView[] iv = new ImageView[pics.length];
				System.out.println("ImageView长度+++++++++++++++++++"+iv);
				for (int i = 0; i < pics.length; i++) {
					iv[i] = new ImageView(context);
					iv[i].setScaleType(ScaleType.CENTER_CROP);
					LinearLayout.LayoutParams ly = new LinearLayout.LayoutParams(
							160, LayoutParams.MATCH_PARENT);
					ly.setMargins(3, 2, 3, 2);
					iv[i].setLayoutParams(ly);
					iv[i].setTag(R.id.tabview_right, i);
					iv[i].setTag(R.id.tabview_left, pics);
					iv[i].setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							int imgP = (Integer) v.getTag(R.id.tabview_right);
							String[] allPic = (String[]) v
									.getTag(R.id.tabview_left);
							Intent it = new Intent(context,
									ImagePagerActivity.class);
							it.putExtra("ImgP", imgP);
							it.putExtra("AllPic", allPic);
							context.startActivity(it);
						}
					});
					Log.i("Lucien", "pic:>" + pics[i]);
					FileInputStream fis = null;
					try {
						fis = new FileInputStream(pics[i]);
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					}
					Bitmap bap = BitmapFactory.decodeStream(fis);
					iv[i].setImageBitmap(bap);

					holder.tabview_right.addView(iv[i]);//往linearlayout里绘图
				}
				if (pics.length < 4) {//图片数组的长度小于4的时候
					ImageView take_img = (ImageView) LayoutInflater.from(
							context).inflate(R.layout.take_img, null);
					take_img.setOnClickListener(this);
					holder.tabview_right.addView(take_img);
				}
			}
			break;

		//
		case Kejianguang_PIC:

			convertView.setLayoutParams(new AbsListView.LayoutParams(
					LayoutParams.MATCH_PARENT, 120));
			if ("pic".equals(data[Kejianguang_PIC])) {
				ImageView take_img = (ImageView) LayoutInflater.from(context)
						.inflate(R.layout.take_kejianguang, null);
				take_img.setOnClickListener(this);
				holder.tabview_right.addView(take_img);
			} else {
				String[] pics = data[Kejianguang_PIC].split(",");
				System.out.println("PIC长度+++++++++++++++++++"+pics.length);
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
					iv[i].setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							int imgP = (Integer) v.getTag(R.id.tabview_right);
							String[] allPic = (String[]) v
									.getTag(R.id.tabview_left);
							Intent it = new Intent(context,
									ImagePagerActivity.class);
							it.putExtra("ImgP", imgP);
							it.putExtra("AllPic", allPic);
							context.startActivity(it);
						}
					});
					Log.i("Lucien", "pic:>" + pics[i]);
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
							context).inflate(R.layout.take_kejianguang, null);
					take_img.setOnClickListener(this);
					holder.tabview_right.addView(take_img);
				}
			}
			break;
		default:// 其他默认进行赋值
			editText[position].setText(data[position]);
			editText[position].setTag(position);
			editText[position]
					.setOnFocusChangeListener(new OnFocusChangeListener() {

						@Override
						public void onFocusChange(View v, boolean hasFocus) {
							if (!hasFocus) {
								int pos = (Integer) v.getTag();
								data[pos] = ((EditText) v).getEditableText()
										.toString();
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
		switch (v.getId()) {
		case R.id.take_img:// 此view有自定义样式
			imgType = 1;
			iAct.doPickPhotoAction(imgType);// 点击添加图片的图标出现dialog选择本地或者直接拍照
			System.out.println("照片地址" + imgType);
			break;
		case R.id.take_kejianguang:
			imgType = 2;
			iAct.doPickPhotoAction(imgType);// 点击添加图片的图标出现dialog选择本地或者直接拍照
			System.out.println("照片地址" + imgType);
			break;
		}

	}

}
