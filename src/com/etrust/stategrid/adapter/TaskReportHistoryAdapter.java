package com.etrust.stategrid.adapter;

import java.io.File;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore.Video.Thumbnails;
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
import com.etrust.stategrid.utils.Player;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

public class TaskReportHistoryAdapter extends BaseAdapter implements
		OnClickListener {

	public static final int VIDEEO_CASE = 9;
	public static final int PIC_CASE = 10;
	public static final int AUDIO_CASE = 11;
	private Context context;

	public String[] lable;
	public String[] data;

	// play audio
	private Player player;
	ImageView to_play;
	TextView audio_time;
	AnimationDrawable animationDrawable;

	private ImageLoader imageLoader;
	private DisplayImageOptions options;

	public TaskReportHistoryAdapter(Context context, String[] lable,
			String[] data) {
		this.context = context;
		this.data = data;
		this.lable = lable;

		player = new Player();
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
			if (!data[PIC_CASE].isEmpty()) {
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
					iv[i].setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							int imgP = (Integer) v.getTag(R.id.tabview_right);
							String[] allPic = (String[]) v
									.getTag(R.id.tabview_left);
							Intent it = new Intent(context,
									ImagePagerActivity.class);
							it.putExtra("from", "network");
							it.putExtra("ImgP", imgP);
							it.putExtra("AllPic", allPic);
							context.startActivity(it);
						}
					});
					Log.i("Lucien", "pic:>" + pics[i]);
					iv[i].setImageBitmap(BitmapUtils.decodeSampledBitmapFromFile(new File(pics[i]),160,160));
//					imageLoader.displayImage(Constant.DOMAN + pics[i], iv[i],
//							options);
					holder.tabview_right.addView(iv[i]);
				}
			}
			break;
		case AUDIO_CASE:

			if (!data[position].isEmpty()) {
				convertView.setLayoutParams(new AbsListView.LayoutParams(
						LayoutParams.MATCH_PARENT, 80));

				View newsfeed_list_audio = LayoutInflater.from(context)
						.inflate(R.layout.audio_item, null);
				to_play = (ImageView) newsfeed_list_audio
						.findViewById(R.id.to_play);
				TextView audio_time = (TextView) newsfeed_list_audio
						.findViewById(R.id.audio_time);
				audio_time.setVisibility(View.GONE);

				LinearLayout.LayoutParams ly = new LinearLayout.LayoutParams(
						LayoutParams.MATCH_PARENT, 70);

				ly.setMargins(0, 8, 80, 8);
				newsfeed_list_audio.setLayoutParams(ly);
				holder.tabview_right.addView(newsfeed_list_audio);

				newsfeed_list_audio.setOnClickListener(this);
			}
			break;
		case VIDEEO_CASE:
			if (!data[position].isEmpty()) {
				convertView.setLayoutParams(new AbsListView.LayoutParams(
						LayoutParams.MATCH_PARENT, 120));

				ImageView take_img = (ImageView) LayoutInflater.from(context)
						.inflate(R.layout.take_img, null);
				Bitmap bitmap = ThumbnailUtils.createVideoThumbnail(
						data[position], Thumbnails.MICRO_KIND);
				BitmapDrawable bd = new BitmapDrawable(context.getResources(),
						bitmap);
				take_img.setBackgroundDrawable(bd);
				take_img.setImageResource(R.drawable.video_recove);
				take_img.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						toDownAndPlayVideo(data[VIDEEO_CASE]);
					}
				});
				holder.tabview_right.addView(take_img);
			}
			break;
		default:
			TextView report_textview = (TextView) LayoutInflater.from(context)
					.inflate(R.layout.report_textview, null);
			report_textview.setText(data[position]);
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
		switch (v.getId()) {
		case R.id.newsfeed_list_audio:
			if (player.isPlaying()) {
				animationDrawable.stop();
				to_play.setImageResource(R.drawable.voice_playing);
				player.stop();
			} else {
				to_play.setImageDrawable(animationDrawable);
				animationDrawable.start();
				toDownAndPlay(data[AUDIO_CASE]);
			}
			break;
		}
	}
	private void toDownAndPlayVideo(String url) {
		playVideo(context,new File(url));
	}
	public void playVideo(Context context,File f) {
		Intent it = new Intent(Intent.ACTION_VIEW);
		it.setDataAndType(Uri.fromFile(f), "video/mp4");
		context.startActivity(it);
	}
	private void toDownAndPlay(String url) {
		final File file = new File(url);
		if (file.exists() && file.length() > 0) {
			playMusic(file);
		} 
		new Thread(new Runnable() {

			@Override
			public void run() {
				boolean isplaying;
				while (isplaying = player.isPlaying()) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				hand.sendEmptyMessage(0);
			}
		}).start();
	}

	public void playMusic(File f) {
		player.playUrl(f.getAbsolutePath());
		player.play();
	}

	Handler hand = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			animationDrawable.stop();
			to_play.setImageResource(R.drawable.voice_playing);
		}
	};
}
