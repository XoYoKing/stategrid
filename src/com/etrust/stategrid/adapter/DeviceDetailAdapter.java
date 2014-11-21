package com.etrust.stategrid.adapter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.etrust.stategrid.ImagePagerActivity;
import com.etrust.stategrid.R;
import com.etrust.stategrid.utils.BitmapUtils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

public class DeviceDetailAdapter extends BaseAdapter implements
		OnClickListener {

	public static final int PIC_CASE =6;
	private Context context;

	public String[] lable;
	public String[] data;

	AnimationDrawable animationDrawable;

	private ImageLoader imageLoader;
	private DisplayImageOptions options;

	public DeviceDetailAdapter(Context context, String[] lable,
			String[] data) {
		this.context = context;
		this.data = data;
		this.lable = lable;

		animationDrawable = new AnimationDrawable();
		for (int i = 1; i <= 3; i++) {
			int id = context.getResources().getIdentifier(
					"voice_playing_f" + i, "drawable", "com.etrust.stategrid");

			animationDrawable.addFrame(context.getResources().getDrawable(id),
					150);

		}
		animationDrawable.setOneShot(false);
		imageLoader = ImageLoader.getInstance();
		options = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.bg_loading)
				.showImageForEmptyUri(R.drawable.chat_balloon_break)
				.showImageOnFail(R.drawable.chat_balloon_break)
				.bitmapConfig(Bitmap.Config.RGB_565).cacheOnDisc(true)
				.imageScaleType(ImageScaleType.EXACTLY).cacheInMemory(true)
				.build();
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
				final String fileName=pics[i];
				iv[i].setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
	            	      if("".equals(fileName)){
	            	    	  return;
	            	      }
	            	      File picFile=new File(fileName);
	            	      if(!picFile.exists()){
	            	    	  return;
	            	      }
			          	  Intent intent = new Intent(Intent.ACTION_VIEW);
			          	  if(picFile.exists()){
					      			Uri uri = Uri.fromFile(picFile); intent.setDataAndType(uri, "image/*");
					      			context.startActivity(intent);
			          	  }						
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
			
			
			
			
			
			
			break;
		default:
			TextView report_textview = (TextView) LayoutInflater.from(context)
					.inflate(R.layout.report_textview, null);
			String dataText=data[position];
			if(dataText!=null){
				dataText=dataText.trim();
				if("null".equals(dataText)){
					dataText="";
				}
			}
			report_textview.setText(dataText);
			holder.tabview_right.addView(report_textview);
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

	Handler hand = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			animationDrawable.stop();
		}
	};
	
	
	private Bitmap getLoacalBitmap(String url) {
		   try {
		          FileInputStream fis = new FileInputStream(url);
		          return BitmapFactory.decodeStream(fis);
		     } catch (FileNotFoundException e) {
		          e.printStackTrace();
		          return null;
		     }
	}	
}
