package com.etrust.stategrid.adapter;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.etrust.stategrid.HongwaiReportActivity;
import com.etrust.stategrid.ImagePagerActivity;
import com.etrust.stategrid.InfraredReportActivity;
import com.etrust.stategrid.ProduceReportActivity;
import com.etrust.stategrid.R;

public class AddHongWaiadapter extends BaseAdapter implements
		OnClickListener {
	
	
	public static final int PIC_CASE = 5;
//	public static final int LEVEL_CASE_PRODUCE = 2;
//	public static final int QUEXIAN_FEILEI = 3;

	private Context context;
	private HongwaiReportActivity prAct;
	public String[] lable;
	public String[] data;

	public EditText[] editText;//这个数组存放红外各项的输入

	public AddHongWaiadapter(Context context, String[] lable, String[] data) {
		this.context = context;
		this.data = data;
		this.lable = lable;
		prAct = (HongwaiReportActivity) context;

		editText = new EditText[10];//选项个数9个的话会出现数组下标越界
		for (int i = 0; i < editText.length; i++) {
			editText[i] = (EditText) LayoutInflater.from(context).inflate(
					R.layout.edittext_item, null);
			LinearLayout.LayoutParams lyz = new LinearLayout.LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
			editText[i].setLayoutParams(lyz);
			//editText[0].setEnabled(false);
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
				ImageView take_img = (ImageView) LayoutInflater.from(context)
						.inflate(R.layout.take_img, null);
				take_img.setOnClickListener(this);
				holder.tabview_right.addView(take_img);
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
					iv[i].setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							int imgP = (Integer) v.getTag(R.id.tabview_right);
							String[] allPic = (String[]) v.getTag(R.id.tabview_left);
							Intent it = new Intent(context,ImagePagerActivity.class);
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
					ImageView take_img = (ImageView) LayoutInflater.from(context).inflate(R.layout.take_img, null);
					take_img.setOnClickListener(this);
					holder.tabview_right.addView(take_img);
				}
			}
			break;
		default:
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
			
			
			
//		case LEVEL_CASE_PRODUCE:
//			View level_item = LayoutInflater.from(context).inflate(
//					R.layout.video_item, null);
//			TextView level_text_id = (TextView) level_item
//					.findViewById(R.id.view_text_id);
//			if ("1".equals(data[position])) {
//				level_text_id.setText("一般缺陷");
//			} else if ("2".equals(data[position])) {
//				level_text_id.setText("严重缺陷");
//			} else if ("3".equals(data[position])) {
//				level_text_id.setText("危急缺陷");
//			}
//			level_item.setOnClickListener(new OnClickListener() {
//
//				@Override
//				public void onClick(View v) {
//					showLevelProduce();
//				}
//			});
//			holder.tabview_right.addView(level_item);
//			break;
			
//		case QUEXIAN_FEILEI:
//			View level_ite = LayoutInflater.from(context).inflate(
//					R.layout.video_item, null);
//			TextView level_text_i = (TextView) level_ite
//					.findViewById(R.id.view_text_id);
//			if ("1".equals(data[position])) {
//				level_text_i.setText("房屋设施");
//			} else if ("2".equals(data[position])) {
//				level_text_i.setText("通风设施");
//			} else if ("3".equals(data[position])) {
//				level_text_i.setText("上下水系统");
//			}
//			else if ("4".equals(data[position])) {
//				level_text_i.setText("空调系统");
//			}
//			else if ("5".equals(data[position])) {
//				level_text_i.setText("照明系统");
//			}
//			else if ("6".equals(data[position])) {
//				level_text_i.setText("围墙大门");
//			}
//			else if ("7".equals(data[position])) {
//				level_text_i.setText("电缆沟");
//			}
//			else if ("8".equals(data[position])) {
//				level_text_i.setText("绿化");
//			}
//			else if ("9".equals(data[position])) {
//				level_text_i.setText("其他");
			}
			
//			
//			level_ite.setOnClickListener(new OnClickListener() {
//
//				@Override
//				public void onClick(View v) {
//					showLevelFenLei();
//				}
//			});
//			holder.tabview_right.addView(level_ite);
//			break;	
//			
//		}
//
	return convertView;
	}
//	private void showLevelProduce() {
//		final Context dialogContext = new ContextThemeWrapper(context,
//				android.R.style.Theme_Light);
//		String[] dengji = new String[] { "一般缺陷", "严重缺陷", "危急缺陷" };
//		 //String[] dengji= new String[] { "I类缺陷","II类缺陷","III类缺陷"};
//		final ListAdapter adapter = new ArrayAdapter<String>(dialogContext,
//				android.R.layout.simple_list_item_1, dengji);
//
//		final AlertDialog.Builder builder = new AlertDialog.Builder(
//				dialogContext);
//		builder.setTitle("缺陷等级");
//		builder.setSingleChoiceItems(adapter, -1,
//				new DialogInterface.OnClickListener() {
//					@Override
//					public void onClick(DialogInterface dialog, int which) {
//						dialog.dismiss();
//						switch (which) {
//						case 0:
//							data[LEVEL_CASE_PRODUCE] = "1";
//							break;
//						case 1:
//							data[LEVEL_CASE_PRODUCE] = "2";
//							break;
//						case 2:
//							data[LEVEL_CASE_PRODUCE] = "3";
//							break;
//						}
//						notifyDataSetChanged();
//					}
//				});
//		builder.create().show();
//	}
//	private void showLevelFenLei() {
//		final Context dialogContext = new ContextThemeWrapper(context,
//				android.R.style.Theme_Light);
//		
//		 String[] fenlei= new String[] { "房屋设施","通风设施", "上下水系统", "空调系统","照明系统","围墙大门","电缆沟","绿化","其他"};
//		final ListAdapter adapter = new ArrayAdapter<String>(dialogContext,
//				android.R.layout.simple_list_item_1, fenlei);
//
//		final AlertDialog.Builder builder = new AlertDialog.Builder(
//				dialogContext);
//		builder.setTitle("缺陷分类");
//		builder.setSingleChoiceItems(adapter, -1,
//				new DialogInterface.OnClickListener() {
//					@Override
//					public void onClick(DialogInterface dialog, int which) {
//						dialog.dismiss();
//						switch (which) {
//						case 0:
//							data[QUEXIAN_FEILEI] = "1";
//							break;
//						case 1:
//							data[QUEXIAN_FEILEI] = "2";
//							break;
//						case 2:
//							data[QUEXIAN_FEILEI] = "3";
//							break;
//						case 3:
//							data[QUEXIAN_FEILEI] = "4";
//							break;
//						case 4:
//							data[QUEXIAN_FEILEI] = "5";
//							break;
//						case 5:
//							data[QUEXIAN_FEILEI] = "6";
//							break;
//						case 6:
//							data[QUEXIAN_FEILEI] = "7";
//							break;
//						case 7:
//							data[QUEXIAN_FEILEI] = "8";
//							break;
//							
//						case 8:
//							data[QUEXIAN_FEILEI] = "9";
//							break;
//						case 9:
//							data[QUEXIAN_FEILEI] = "10";
//							break;
//						}
//						notifyDataSetChanged();
//					}
//				});
//		builder.create().show();
//	}
	public static class ViewHolder {
		LinearLayout tabview_right;//listview页面右边
		TextView tabview_left;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.take_img:
			prAct.doPickPhotoAction();
			break;
		}
	}
}
