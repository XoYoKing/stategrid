package com.etrust.stategrid.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import com.nostra13.universalimageloader.utils.StorageUtils;

import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.MediaStore.MediaColumns;
import android.text.TextUtils;

/**
 * @Name BitmapUtils
 * @Description 如果能够根据图片大小自动计算缩放比 ， 防止bitmap size exceeds vm budget
 * @Date 2013-3-11
 * @author max
 */
public class BitmapUtils {
	private static int mCompressBitmapSideLength = 150;
	/**
	 * 放大图片 add by max [2013-8-7]
	 * 
	 * @param bitmap
	 * @return
	 */
	public static Bitmap big(Bitmap bitmap) {
		Matrix matrix = new Matrix();

		matrix.postScale(2.4f, 2.4f); // 长和宽放大缩小的比例
		Bitmap resizeBmp = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
				bitmap.getHeight(), matrix, true);
		return resizeBmp;
	}
	public static Bitmap decodeSampledBitmapFromFile(File file, int reqWidth,
			int reqHeight) {

		// First decode with inJustDecodeBounds=true to check dimensions
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;

		BitmapFactory.decodeFile(file.getAbsolutePath(), options);

		// Calculate inSampleSize
		options.inSampleSize = calculateInSampleSize(options, reqWidth,
				reqHeight);

		// Decode bitmap with inSampleSize set
		options.inJustDecodeBounds = false;
		Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath(),
				options);
		return bitmap;
	}
	/** Calculate the scaling factor */
	public static int calculateInSampleSize(BitmapFactory.Options options,
			int reqWidth, int reqHeight) {
		// Raw height and width of image
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {

			// Calculate ratios of height and width to requested height and
			// width
			final int heightRatio = Math.round((float) height
					/ (float) reqHeight);
			final int widthRatio = Math.round((float) width / (float) reqWidth);

			// Choose the smallest ratio as inSampleSize value, this will
			// guarantee
			// a final image with both dimensions larger than or equal to the
			// requested height and width.
			inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
		}

		return inSampleSize;
	}
	/**
	 ** 读取照片exif信息中的旋转角度
	 ** 
	 * @param path
	 *            照片路径
	 ** @return角度          
	 */
	public static int readPictureDegree(String path) {
		int degree = 0;
		try {
			ExifInterface exifInterface = new ExifInterface(path);
			int orientation = exifInterface.getAttributeInt(
					ExifInterface.TAG_ORIENTATION,
					ExifInterface.ORIENTATION_NORMAL);
			switch (orientation) {
				case ExifInterface.ORIENTATION_ROTATE_90 :
					degree = 90;
					break;
				case ExifInterface.ORIENTATION_ROTATE_180 :
					degree = 180;
					break;
				case ExifInterface.ORIENTATION_ROTATE_270 :
					degree = 270;
					break;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return degree;
	}
	/**
	 * 压缩图片
	 * 
	 * @param bitmap
	 * @param displayWidth
	 *            缩放后的最长边尺寸
	 * @param displayHeight
	 * @return
	 */
	public static Bitmap zoomBitmap(Bitmap bitmap, int displayWidth,
			int displayHeight) {
		int imgW = bitmap.getWidth();
		int imgH = bitmap.getHeight();
		int layoutW = imgW > displayWidth ? displayWidth : imgW;
		int layoutH = imgH > displayHeight ? displayHeight : imgH;
		// 图片高宽判断
		if (imgW >= imgH) {
			if (layoutW == displayWidth) {
				layoutH = (int) (imgH * ((float) displayWidth / imgW));
			}
		} else {
			if (layoutH == displayHeight) {
				layoutW = (int) (imgW * ((float) displayHeight / imgH));
			}
		}
		// 计算缩放因子
		float heightScale = ((float) layoutH) / imgH;
		float widthScale = ((float) layoutW) / imgW;
		// 新建立矩阵
		Matrix matrix = new Matrix();
		matrix.postScale(heightScale, widthScale);
		// 将图片大小压缩
		// 压缩后图片的宽和高以及kB大小均会变化
		Bitmap newBitmap = Bitmap.createBitmap(bitmap, 0, 0, imgW, imgH,
				matrix, true);
		bitmap.recycle();
		bitmap = null;
		return newBitmap;
	}
	/**
	 * Drawable压缩
	 * 
	 * @param drawable
	 * @param w
	 * @param h
	 * @return
	 */
	public static Drawable zoomDrawable(Drawable drawable, int w, int h) {
		int width = drawable.getIntrinsicWidth();
		int height = drawable.getIntrinsicHeight();
		// drawable转换成bitmap
		Bitmap oldbmp = drawableToBitmap(drawable);
		// 创建操作图片用的Matrix对象
		Matrix matrix = new Matrix();
		// 计算缩放比例
		float sx = ((float) w / width);
		float sy = ((float) h / height);
		// 设置缩放比例
		matrix.postScale(sx, sy);
		// 建立新的bitmap，其内容是对原bitmap的缩放后的图
		Bitmap newbmp = Bitmap.createBitmap(oldbmp, 0, 0, width, height,
				matrix, true);
		return new BitmapDrawable(newbmp);
	}
	/**
	 * 图片转字节
	 * 
	 * @param bm
	 * @return
	 */
	public byte[] Bitmap2Bytes(Bitmap bm) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
		return baos.toByteArray();
	}
	/**
	 * 字节转图片
	 * 
	 * @param b
	 * @return
	 */
	public static Bitmap bytes2Bimap(byte[] b) {
		if (b.length != 0) {
			return BitmapFactory.decodeByteArray(b, 0, b.length);
		} else {
			return null;
		}
	}
	/**
	 * 将Drawable转化为Bitmap
	 * 
	 * @param drawable
	 * @return
	 */
	public static Bitmap drawableToBitmap(Drawable drawable) {
		// 取 drawable 的长宽
		int w = drawable.getIntrinsicWidth();
		int h = drawable.getIntrinsicHeight();

		// 取 drawable 的颜色格式
		Bitmap.Config config = drawable.getOpacity() != PixelFormat.OPAQUE
				? Bitmap.Config.ARGB_8888
				: Bitmap.Config.RGB_565;
		// 建立对应 bitmap
		Bitmap bitmap = Bitmap.createBitmap(w, h, config);
		// 建立对应 bitmap 的画布
		Canvas canvas = new Canvas(bitmap);
		drawable.setBounds(0, 0, w, h);
		// 把 drawable 内容画到画布中
		drawable.draw(canvas);
		return bitmap;
	}
	/**
	 * 取得圆角图片
	 * 
	 * @param bitmap
	 * @param roundPx
	 * @return
	 */
	public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, float roundPx) {
		int w = bitmap.getWidth();
		int h = bitmap.getHeight();
		Bitmap output = Bitmap.createBitmap(w, h, Config.ARGB_8888);
		Canvas canvas = new Canvas(output);
		final int color = 0xff424242;
		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, w, h);
		final RectF rectF = new RectF(rect);
		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);
		bitmap.recycle();
		return output;
	}
	/**
	 * 获得带倒影的图片
	 * 
	 * @param bitmap
	 * @return
	 */
	public static Bitmap createReflectionImageWithOrigin(Bitmap bitmap) {
		final int reflectionGap = 4;
		int w = bitmap.getWidth();
		int h = bitmap.getHeight();

		Matrix matrix = new Matrix();
		matrix.preScale(1, -1);

		Bitmap reflectionImage = Bitmap.createBitmap(bitmap, 0, h / 2, w,
				h / 2, matrix, false);

		Bitmap bitmapWithReflection = Bitmap.createBitmap(w, (h + h / 2),
				Config.ARGB_8888);

		Canvas canvas = new Canvas(bitmapWithReflection);
		canvas.drawBitmap(bitmap, 0, 0, null);
		Paint deafalutPaint = new Paint();
		canvas.drawRect(0, h, w, h + reflectionGap, deafalutPaint);

		canvas.drawBitmap(reflectionImage, 0, h + reflectionGap, null);

		Paint paint = new Paint();
		LinearGradient shader = new LinearGradient(0, bitmap.getHeight(), 0,
				bitmapWithReflection.getHeight() + reflectionGap, 0x70ffffff,
				0x00ffffff, TileMode.CLAMP);
		paint.setShader(shader);
		// Set the Transfer mode to be porter duff and destination in
		paint.setXfermode(new PorterDuffXfermode(Mode.DST_IN));
		// Draw a rectangle using the paint with our linear gradient
		canvas.drawRect(0, h, w, bitmapWithReflection.getHeight()
				+ reflectionGap, paint);

		return bitmapWithReflection;
	}

	/**
	 * 从本地取出原始图像
	 * 
	 * @param filename
	 *            图像名
	 * @param savePath
	 *            保存路径
	 * @return
	 */
	public static BitmapDrawable getBitmapFromSDcard(Context context,
			String filename, String savePath) {
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_UNMOUNTED)) {
			return null;
		}
		try {
			File image = new File(savePath + File.separator + filename);
			if (image.exists()) {

				FileInputStream fileInputStream = new FileInputStream(image);
				Bitmap bm = BitmapFactory.decodeStream(fileInputStream);
				if (null == bm) {
					return null;
				}
				BitmapDrawable drawable = new BitmapDrawable(
						context.getResources(), bm);
				return drawable;
			}
		} catch (OutOfMemoryError e) {

		} catch (FileNotFoundException e) {

		} catch (Exception e) {

		}
		return null;
	}
	public static BitmapDrawable getCompressBitmapFromSDcard(Context context,
			String filename, String savePath, int minSideLength) {
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_UNMOUNTED)) {
			return null;
		}
		try {
			File image = new File(savePath + File.separator + filename);
			if (image.exists()) {

				FileInputStream fileInputStream = new FileInputStream(image);
				// 压缩图片，减少内存占用
				FileDescriptor fd = fileInputStream.getFD();

				BitmapFactory.Options options = new BitmapFactory.Options();
				options.inJustDecodeBounds = true;
				BitmapFactory.decodeFileDescriptor(fd, null, options);
				options.inSampleSize = BitmapUtils.computeSampleSize(options,
						minSideLength, 480 * 480);
				options.inJustDecodeBounds = false;

				Bitmap bm = BitmapFactory.decodeStream(fileInputStream, null,
						options);
				BitmapDrawable drawable = new BitmapDrawable(
						context.getResources(), bm);
				return drawable;
			}
		} catch (OutOfMemoryError e) {

		} catch (FileNotFoundException e) {

		} catch (Exception e) {

		}
		return null;
	}
	/**
	 * 从本地取出压缩过的图像
	 * 
	 * @param filename
	 *            图像名
	 * @param savePath
	 *            保存路径
	 * @return
	 */
	public static BitmapDrawable getCompressBitmapFromSDcard(Context context,
			String filename, String savePath) {
		return getCompressBitmapFromSDcard(context, filename, savePath,
				mCompressBitmapSideLength);
	}
	/**
	 * 保存图片到sd卡 add by max [2013-2-26]
	 * 
	 * @param url
	 *            图片地址
	 * @param bitmap
	 *            图片
	 */
	public static String addBitmapToSDcard(Context context, String url,
			Bitmap bitmap) {
		addBitmapToSDcard(context, StorageUtils.getCacheDirectory(context)
				.getPath(), url, bitmap);
		return StorageUtils.getCacheDirectory(context).getPath()
				+ File.separator + Utils.getFileFullName(url);
	}
	/**
	 * 添加图片文件到媒体库 add by max [2013-7-18]
	 * 
	 * @param context
	 * @param f
	 * @param fileName
	 * @param imgPath
	 */
	public static void addImgFileToMediaStore(Context context, File f,
			String fileName, String imgPath) {
		ContentValues newValues = new ContentValues(6);

		newValues.put(MediaColumns.TITLE, fileName);
		newValues.put(MediaColumns.DISPLAY_NAME, fileName);
		newValues.put(MediaColumns.DATA, imgPath);
		newValues.put(MediaColumns.DATE_MODIFIED,
				System.currentTimeMillis() / 1000);
		newValues.put(MediaColumns.SIZE, f.length());
		newValues.put(MediaColumns.MIME_TYPE, "image/jpeg");
		Uri uri = context.getContentResolver().insert(
				MediaStore.Images.Media.EXTERNAL_CONTENT_URI, newValues);
	}
	/**
	 * 保存图片到sd卡 add by max [2013-2-26]
	 * 
	 * @param url
	 *            图片地址
	 * @param bitmap
	 *            图片
	 */
	public static boolean addBitmapToSDcard(Context context, String savePath,
			String url, Bitmap bitmap) {
		if (bitmap == null || TextUtils.isEmpty(savePath)) {
			return false;
		}

		String filename = Utils.getFileFullName(url);
		BitmapDrawable drawable = new BitmapDrawable(context.getResources(),
				bitmap);

		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_UNMOUNTED)) {
			return false;
		}
		try {
			// 先判断目录是否存在
			File dir = new File(savePath);

			if (!dir.exists()) { // 不存在则创建
				dir.mkdirs();
			}

			// 判断文件是否存在
			File image = new File(savePath + File.separator + filename);

			if (image.exists()) { // 存在则删除，不存在则新建
				image.deleteOnExit();
			} else {
				image.createNewFile();
			}
			FileOutputStream fileOutputStream = new FileOutputStream(image);
			if (drawable.getBitmap().compress(Bitmap.CompressFormat.JPEG, 50,
					fileOutputStream)) {
				fileOutputStream.flush();
			}
			fileOutputStream.close();
			// }
		} catch (IOException e) {
		} catch (Exception e) {
		}
		return true;
	}
	/**
	 * 图片等比压缩 add by max [2013-3-11]
	 * 
	 * @param options
	 * @param minSideLength
	 *            最大宽度
	 * @param maxNumOfPixels
	 *            像素密度
	 * @return 压缩后的图片尺寸 options.inSampleSize
	 */
	public static int computeSampleSize(BitmapFactory.Options options,
			int minSideLength, int maxNumOfPixels) {
		int initialSize = computeInitialSampleSize(options, minSideLength,
				maxNumOfPixels);

		int roundedSize;
		if (initialSize <= 8) {
			roundedSize = 1;
			while (roundedSize < initialSize) {
				roundedSize <<= 1;
			}
		} else {
			roundedSize = (initialSize + 7) / 8 * 8;
		}

		return roundedSize;
	}

	/**
	 * 
	 * add by max [2013-3-11]
	 * 
	 * @param options
	 *            图片等比压缩
	 * @param minSideLength
	 *            赋值-1 使用默认值 1
	 * @param maxNumOfPixels
	 *            赋值-1 使用默认值 128
	 * @return
	 */
	private static int computeInitialSampleSize(BitmapFactory.Options options,
			int minSideLength, int maxNumOfPixels) {
		double w = options.outWidth;
		double h = options.outHeight;

		int lowerBound = (maxNumOfPixels == -1) ? 1 : (int) Math.ceil(Math
				.sqrt(w * h / maxNumOfPixels));
		int upperBound = (minSideLength == -1) ? 128 : (int) Math.min(
				Math.floor(w / minSideLength), Math.floor(h / minSideLength));

		if (upperBound < lowerBound) {
			// return the larger one when there is no overlapping zone.
			return lowerBound;
		}

		if ((maxNumOfPixels == -1) && (minSideLength == -1)) {
			return 1;
		} else if (minSideLength == -1) {
			return lowerBound;
		} else {
			return upperBound;
		}
	}
}
