package com.etrust.stategrid;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

public class ImagePagerActivity extends Activity implements OnClickListener {

	private static final String STATE_POSITION = "STATE_POSITION";
	private ImageLoader imageLoader = ImageLoader.getInstance();
	private float downXValue;
	private float currentXValue;
	private DisplayImageOptions options;

	private ViewPager pager;

	private int mPosition;
	private String[] mImageUrls;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_image_pager);

		Bundle bundle = getIntent().getExtras();
		mImageUrls = bundle.getStringArray("AllPic");

		mPosition = bundle.getInt("ImgP", -1);

		if (savedInstanceState != null) {
			mPosition = savedInstanceState.getInt(STATE_POSITION);
		}

		pager = (ViewPager) findViewById(R.id.pager);
		pager.setAdapter(new ImagePagerAdapter(mImageUrls));
		pager.setCurrentItem(mPosition);
		((ImageButton) findViewById(R.id.left_back_btn))
				.setOnClickListener(this);
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		outState.putInt(STATE_POSITION, pager.getCurrentItem());
	}

	private class ImagePagerAdapter extends PagerAdapter {

		private String[] images;
		private LayoutInflater inflater;

		ImagePagerAdapter(String[] images) {
			this.images = images;
			inflater = getLayoutInflater();
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			((ViewPager) container).removeView((View) object);
		}

		@Override
		public void finishUpdate(View container) {
		}

		@Override
		public int getCount() {
			return images.length;
		}

		@Override
		public Object instantiateItem(ViewGroup view, int position) {
			View imageLayout = inflater.inflate(R.layout.item_pager_image,
					view, false);
			ImageView imageView = (ImageView) imageLayout
					.findViewById(R.id.image);
			final ProgressBar spinner = (ProgressBar) imageLayout
					.findViewById(R.id.loading);
			// 没有缩略图则不显示
			FileInputStream fis = null;
			try {
				fis = new FileInputStream(images[position]);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			Bitmap bap = BitmapFactory.decodeStream(fis);

			imageView.setImageBitmap(bap);

			imageLayout.setOnTouchListener(new OnTouchListener() {

				@Override
				public boolean onTouch(View v, MotionEvent event) {
					switch (event.getAction()) {
					case MotionEvent.ACTION_DOWN:
						downXValue = event.getX();
						break;
					case MotionEvent.ACTION_UP:
						// 实现单击关闭
						currentXValue = event.getX();
						if (Math.abs(downXValue - currentXValue) < 20) {
							finish();
						}
						break;
					default:
						break;
					}
					return true;
				}
			});

			((ViewPager) view).addView(imageLayout, 0);
			return imageLayout;
		}

		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view.equals(object);
		}

		@Override
		public void restoreState(Parcelable state, ClassLoader loader) {
		}

		@Override
		public Parcelable saveState() {
			return null;
		}

		@Override
		public void startUpdate(View container) {
		}
	}

	@Override
	public void onClick(View v) {
		if (R.id.left_back_btn == v.getId()) {
			finish();
		}
	}

	Bitmap bp=null;  
    ImageView imageview;  
    float scaleWidth;  
    float scaleHeight;  
      
   int h;  
    boolean num=false;  

}