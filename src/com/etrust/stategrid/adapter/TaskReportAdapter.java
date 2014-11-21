package com.etrust.stategrid.adapter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaRecorder;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.provider.MediaStore.Video.Thumbnails;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.etrust.stategrid.DefectDeviceListActivity;
import com.etrust.stategrid.DefectSelectListActivity;
import com.etrust.stategrid.ImagePagerActivity;
import com.etrust.stategrid.R;
import com.etrust.stategrid.TaskReportActivity;
import com.etrust.stategrid.TreeActivity;
import com.etrust.stategrid.utils.Player;
import com.etrust.stategrid.utils.Utils;
import com.nostra13.universalimageloader.utils.StorageUtils;

public class TaskReportAdapter extends BaseAdapter implements OnClickListener {

	public static final int DEFECT_DEVICE_CASE=3;
	public static final int DEFECT_CASE = 4;
	public static final int LEVEL_CASE = 5;
	public static final int DEAL_CASE = 6;
	public static final int VIDEEO_CASE = 7;
	public static final int AUDIO_CASE = 8;
	public static final int PIC_CASE = 9;
	public static final int DESCRIP_CASE = 10;
	
	
	
	private Context context;
	private TaskReportActivity tAct;

	public String[] lable;
	public String[] data;
	// record audio
	private Dialog dialog;
	private ImageView dialog_img;
	private Thread recordThread;

	private static float recodeTime = 0.0f; // 录音的时间
	private static double voiceValue = 0.0; // 麦克风获取的音量值
	private static int MAX_TIME = 180; // 最长录制时间，单位秒，0为无时间限制
	private boolean isRecord = false;
	private MediaRecorder mRecorder = null;
	private String mFileName = null;
	private String audioTime;
	// play audio
	private Player player;
	ImageView to_play;
	TextView audio_time;
	AnimationDrawable animationDrawable;

	public EditText report_editText;

	public TaskReportAdapter(Context context, String[] lable, String[] data) {
		this.context = context;
		this.data = data;
		this.lable = lable;
		tAct = (TaskReportActivity) context;

		player = new Player();
		animationDrawable = new AnimationDrawable();
		for (int i = 1; i <= 3; i++) {
			int id = context.getResources().getIdentifier(
					"voice_playing_f" + i, "drawable", "com.etrust.stategrid");

			animationDrawable.addFrame(context.getResources().getDrawable(id),
					150);

		}
		animationDrawable.setOneShot(false);
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
			if ("pic".equals(data[PIC_CASE])) {
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
					iv[i].setTag(R.id.tabview_right, i);//当多个imageview用到同一个监听器时使用
					iv[i].setTag(R.id.tabview_left, pics);//照片
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
							context).inflate(R.layout.take_img, null);
					take_img.setOnClickListener(this);
					holder.tabview_right.addView(take_img);
				}
			}
			break;
		case AUDIO_CASE:
			convertView.setLayoutParams(new AbsListView.LayoutParams(
					LayoutParams.MATCH_PARENT, 80));
			if ("audio".equals(data[position])) {
				Button record_btn = (Button) LayoutInflater.from(context)
						.inflate(R.layout.record_btn, null);
				LinearLayout.LayoutParams ly = new LinearLayout.LayoutParams(
						LayoutParams.MATCH_PARENT, 60);

				ly.setMargins(0, 8, 80, 8);
				record_btn.setLayoutParams(ly);
				holder.tabview_right.addView(record_btn);
				record_btn.setOnTouchListener(new OnTouchListener() {
					@Override
					public boolean onTouch(View arg0, MotionEvent event) {
						if (event.getAction() == MotionEvent.ACTION_DOWN) {
							isRecord = true;
							showVoiceDialog();
							startRecording();
							mythread();
						}
						if (event.getAction() == MotionEvent.ACTION_UP
								|| event.getAction() == MotionEvent.ACTION_CANCEL) {
							if (isRecord) {
								stopRecording();
								dialog.dismiss();
								isRecord = false;
								Toast.makeText(context, "录音完成", 800).show();
								audioTime = String.valueOf((int) recodeTime);
								setAudioPlayView();
							}
						}
						return false;
					}

				});
			} else {
				View newsfeed_list_audio = LayoutInflater.from(context)
						.inflate(R.layout.audio_item, null);
				to_play = (ImageView) newsfeed_list_audio
						.findViewById(R.id.to_play);
				TextView audio_time = (TextView) newsfeed_list_audio
						.findViewById(R.id.audio_time);
				audio_time.setText(Utils.formatAudioTime(audioTime));

				LinearLayout.LayoutParams ly = new LinearLayout.LayoutParams(
						LayoutParams.MATCH_PARENT, 70);

				ly.setMargins(0, 8, 80, 8);
				newsfeed_list_audio.setLayoutParams(ly);
				holder.tabview_right.addView(newsfeed_list_audio);

				newsfeed_list_audio.setOnClickListener(this);
			}
			break;
		case VIDEEO_CASE:
			if ("video".equals(data[position])) {
				View video_item = LayoutInflater.from(context).inflate(
						R.layout.video_item, null);
				video_item.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						Intent intent = new Intent(
								MediaStore.ACTION_VIDEO_CAPTURE);
						intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
						tAct.startActivityForResult(
								intent,
								TaskReportActivity.activity_result_VIDEO_CAPTURE);
					}
				});
				holder.tabview_right.addView(video_item);
			} else {
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
				take_img.setTag(data[position]);
				take_img.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						String url = (String) v.getTag();
						Intent it = new Intent(Intent.ACTION_VIEW);
						it.setDataAndType(Uri.parse(url), "video/mp4");
						context.startActivity(it);
					}
				});
				holder.tabview_right.addView(take_img);
			}
			break;
		case DEFECT_DEVICE_CASE:
			
			
			View device_item = LayoutInflater.from(context).inflate(
					R.layout.video_item, null);
			TextView device_name = (TextView) device_item
					.findViewById(R.id.view_text_id);
			if (data[position].isEmpty()) {
				device_name.setText("请选择");
			} else {
				device_name.setText(data[position]);
			}
			device_item.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent();
					intent.setClass(tAct, TreeActivity.class);
					intent.putExtra("mTransSubID", tAct.defectTsid);
					intent.putExtra("from", "TaskReportAdapter");
					tAct.startActivityForResult(intent,
							TaskReportActivity.activity_result_DEFECT_DEVICE_SELECT);
				}
			});
			holder.tabview_right.addView(device_item);
			break;
		case DEFECT_CASE:
			View defect_item = LayoutInflater.from(context).inflate(
					R.layout.video_item, null);
			TextView view_text_id = (TextView) defect_item
					.findViewById(R.id.view_text_id);
			if (data[position].isEmpty()) {
				view_text_id.setText("缺陷类目");
			} else {
				String show[] = data[position].split(",");
				view_text_id.setText(show[0]+" "+show[1]);
			}
			defect_item.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Intent intent = new Intent();
					intent.setClass(tAct, DefectSelectListActivity.class);
					tAct.startActivityForResult(intent,
							TaskReportActivity.activity_result_DEFECT_SELECT);
				}
			});
			holder.tabview_right.addView(defect_item);

			break;
		case LEVEL_CASE:
			View level_item = LayoutInflater.from(context).inflate(
					R.layout.video_item, null);
			TextView level_text_id = (TextView) level_item
					.findViewById(R.id.view_text_id);
			if ("1".equals(data[position])) {
				level_text_id.setText("III类缺陷");
			} else if ("2".equals(data[position])) {
				level_text_id.setText("II类缺陷");
			} else if ("3".equals(data[position])) {
				level_text_id.setText("I类缺陷");
			}
			level_item.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					showBugLevel();
				}
			});
			holder.tabview_right.addView(level_item);
			break;
		case DEAL_CASE:
			View deal_item = LayoutInflater.from(context).inflate(
					R.layout.video_item, null);
			TextView deal_text_id = (TextView) deal_item
					.findViewById(R.id.view_text_id);
			if ("1".equals(data[position])) {
				deal_text_id.setText("自然消失");
			} else if ("2".equals(data[position])) {
				deal_text_id.setText("上报处理");
			}
			deal_item.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					showDeal();
				}
			});
			holder.tabview_right.addView(deal_item);
			break;
		case DESCRIP_CASE:
			convertView.setLayoutParams(new AbsListView.LayoutParams(
					LayoutParams.MATCH_PARENT, 120));
			report_editText = (EditText) LayoutInflater.from(context).inflate(
					R.layout.edittext_item, null);
			LinearLayout.LayoutParams lyz = new LinearLayout.LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
			report_editText.setLayoutParams(lyz);
			report_editText.setText(data[position]);
			report_editText
					.setOnFocusChangeListener(new OnFocusChangeListener() {

						@Override
						public void onFocusChange(View v, boolean hasFocus) {
							if (!hasFocus) {
								data[DESCRIP_CASE] = ((EditText) v)
										.getEditableText().toString();
							}
						}
					});
			holder.tabview_right.addView(report_editText);
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

	private void showBugLevel() {
		final Context dialogContext = new ContextThemeWrapper(context,
				android.R.style.Theme_Light);
		String[] choices = new String[] { "III类缺陷", "II类缺陷", "I类缺陷" };

		final ListAdapter adapter = new ArrayAdapter<String>(dialogContext,
				android.R.layout.simple_list_item_1, choices);

		final AlertDialog.Builder builder = new AlertDialog.Builder(
				dialogContext);
		builder.setTitle("缺陷等级");
		builder.setSingleChoiceItems(adapter, -1,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						switch (which) {
						case 0:
							data[LEVEL_CASE] = "1";
							break;
						case 1:
							data[LEVEL_CASE] = "2";
							break;
						case 2:
							data[LEVEL_CASE] = "3";
							break;
						}
						notifyDataSetChanged();
					}
				});
		builder.create().show();
	}

	private void showDeal() {
		final Context dialogContext = new ContextThemeWrapper(context,
				android.R.style.Theme_Light);
		String[] choices = new String[] { "自然消失", "上报处理" };

		final ListAdapter adapter = new ArrayAdapter<String>(dialogContext,
				android.R.layout.simple_list_item_1, choices);

		final AlertDialog.Builder builder = new AlertDialog.Builder(
				dialogContext);
		builder.setTitle("处理方法");
		builder.setSingleChoiceItems(adapter, -1,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						switch (which) {
						case 0:
							data[DEAL_CASE] = "1";
							break;
						case 1:
							data[DEAL_CASE] = "2";
							break;
						}
						notifyDataSetChanged();
					}
				});
		builder.create().show();
	}

	public static class ViewHolder {
		LinearLayout tabview_right;
		TextView tabview_left;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.take_img:
			tAct.doPickPhotoAction();
			break;
		case R.id.newsfeed_list_audio:
			if (player.isPlaying()) {
				animationDrawable.stop();
				to_play.setImageResource(R.drawable.voice_playing);
				player.stop();
			} else {
				to_play.setImageDrawable(animationDrawable);
				animationDrawable.start();
				player.playUrl(data[AUDIO_CASE]);
				player.play();
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
			break;
		}
	}

	Handler hand = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			animationDrawable.stop();
			to_play.setImageResource(R.drawable.voice_playing);
		}
	};

	// record audio
	public void showVoiceDialog() {
		dialog = new Dialog(context, R.style.DialogStyleSpeak);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		dialog.setContentView(R.layout.speak_dialog);
		dialog_img = (ImageView) dialog.findViewById(R.id.dialog_img);
		dialog.show();
	}

	private void startRecording() {
		// 创建录音文件
		String fileName = System.currentTimeMillis() + ".amr";
		File temp = new File(StorageUtils.getCacheDirectory(context), fileName);
		mFileName = temp.getAbsolutePath();

		temp.getParentFile().mkdirs();

		mRecorder = new MediaRecorder();
		mRecorder.reset();
		mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
		mRecorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);
		mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
		mRecorder.setOutputFile(mFileName);

		try {
			mRecorder.prepare();
		} catch (IOException e) {
			e.printStackTrace();
		}
		mRecorder.start();
	}

	private void stopRecording() {
		mRecorder.stop();
		mRecorder.release();
		mRecorder = null;
	}

	void mythread() {
		recordThread = new Thread(ImgThread);
		recordThread.start();
	}

	private Runnable ImgThread = new Runnable() {

		@Override
		public void run() {
			recodeTime = 0.0f;
			while (isRecord) {
				if (recodeTime >= MAX_TIME && MAX_TIME != 0) {
					imgHandle.sendEmptyMessage(0);
				} else {
					try {
						Thread.sleep(200);
						recodeTime += 0.2;
						Log.i("Lucien", "isRecord 1:" + isRecord);
						if (isRecord) {
							voiceValue = mRecorder.getMaxAmplitude();
							imgHandle.sendEmptyMessage(1);
						}
					} catch (InterruptedException e) {
						e.printStackTrace();
						imgHandle.sendEmptyMessage(0);
					} catch (RuntimeException exp) {
						imgHandle.sendEmptyMessage(0);
					}
				}
			}
		}

		Handler imgHandle = new Handler() {
			@Override
			public void handleMessage(Message msg) {

				switch (msg.what) {
				case 0:
					Log.i("Lucien", "isRecord 2:" + isRecord);
					if (isRecord) {
						isRecord = false;
						if (dialog.isShowing()) {
							dialog.dismiss();
						}
						mRecorder.stop();
						voiceValue = 0.0;
						Toast.makeText(context, "录音完成", 800).show();
						audioTime = String.valueOf((int) recodeTime);
						setAudioPlayView();
					}
					break;
				case 1:
					setDialogImage();
					break;
				default:
					break;
				}
			}
		};
	};

	private void setAudioPlayView() {
		data[AUDIO_CASE] = mFileName;
		this.notifyDataSetChanged();
	}

	void setDialogImage() {
		if (voiceValue < 200.0) {
			dialog_img.setImageResource(R.drawable.record_animate_01);
		} else if (voiceValue > 200.0 && voiceValue < 400) {
			dialog_img.setImageResource(R.drawable.record_animate_02);
		} else if (voiceValue > 400.0 && voiceValue < 800) {
			dialog_img.setImageResource(R.drawable.record_animate_03);
		} else if (voiceValue > 800.0 && voiceValue < 1600) {
			dialog_img.setImageResource(R.drawable.record_animate_04);
		} else if (voiceValue > 1600.0 && voiceValue < 3200) {
			dialog_img.setImageResource(R.drawable.record_animate_05);
		} else if (voiceValue > 3200.0 && voiceValue < 5000) {
			dialog_img.setImageResource(R.drawable.record_animate_06);
		} else if (voiceValue > 5000.0 && voiceValue < 7000) {
			dialog_img.setImageResource(R.drawable.record_animate_07);
		} else if (voiceValue > 7000.0 && voiceValue < 10000.0) {
			dialog_img.setImageResource(R.drawable.record_animate_08);
		} else if (voiceValue > 10000.0 && voiceValue < 14000.0) {
			dialog_img.setImageResource(R.drawable.record_animate_09);
		} else if (voiceValue > 14000.0 && voiceValue < 17000.0) {
			dialog_img.setImageResource(R.drawable.record_animate_10);
		} else if (voiceValue > 17000.0 && voiceValue < 20000.0) {
			dialog_img.setImageResource(R.drawable.record_animate_11);
		} else if (voiceValue > 20000.0 && voiceValue < 24000.0) {
			dialog_img.setImageResource(R.drawable.record_animate_12);
		} else if (voiceValue > 24000.0 && voiceValue < 28000.0) {
			dialog_img.setImageResource(R.drawable.record_animate_13);
		} else if (voiceValue > 28000.0) {
			dialog_img.setImageResource(R.drawable.record_animate_14);
		}
	}
}
