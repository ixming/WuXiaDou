package com.frameworkexample.android.common.sharebind;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;

import org.ixming.android.utils.FrameworkLog;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;

class BitmapUtil {
	
	static final String TAG = "sharebind_bitmaputil";
	
	public static Bitmap getBitmapFromRes(Context context, int resId) {
		Bitmap bitmap = null;
		try {
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inPreferredConfig = Config.RGB_565;
			options.inPurgeable = true;
			options.inInputShareable = true;
			bitmap = BitmapFactory.decodeResource(context.getResources(), resId, options);
		} catch (Exception e) {
			bitmap = null;
			FrameworkLog.e(TAG, "getBitmapFromRes Exception: " + e.getMessage());
		}
		return bitmap;
	}
	
	public static Bitmap getBitmapFromFileInputStream(FileInputStream is) {
		if (is == null)
			return null;
		Bitmap bitmap = null;
		try {
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inPreferredConfig = Config.RGB_565;
			options.inPurgeable = true;
			options.inInputShareable = true;
			bitmap = BitmapFactory.decodeFileDescriptor(is.getFD(), null, options);
		} catch (Exception e) {
			bitmap = null;
			FrameworkLog.e(TAG, "getBitmapFromFileInputStream Exception: " + e.getMessage());
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (Exception e) { }
			}
		}
		return bitmap;
	}

	public static Bitmap getBitmapFromFile(String filePath) {
		return getBitmapFromFile(new File(filePath));
	}

	public static Bitmap getBitmapFromFile(File file) {
		FileInputStream fis;
		try {
			fis = new FileInputStream(file);
		} catch (Exception e) {
			FrameworkLog.e(TAG, "getBitmapFromFile Exception: " + e.getMessage());
			return null;
		}
		return getBitmapFromFileInputStream(fis);
	}
	
	/**
	 * 将Bitmap转化为字节数组
	 * @param bmp target bitmap to read
	 * @param needRecycle 是否需要回收bitmap
	 * @added 1.0
	 */
	public static byte[] bmpToByteArray(final Bitmap bmp, final boolean needRecycle) {
		if (null == bmp) {
			return null;
		}
		ByteArrayOutputStream output = null;
		try {
			output = new ByteArrayOutputStream();
			bmp.compress(CompressFormat.PNG, 100, output);
			if (needRecycle) {
				bmp.recycle();
			}
			byte[] result = output.toByteArray();
			return result;
		} catch (Exception e) {
			FrameworkLog.e(TAG, "bmpToByteArray Exception: " + e.getMessage());
			return null;
		} finally {
			if (null != output) {
				try {
					output.close();
				} catch (Exception ex) { }
			}
		}
	}
	
	public static byte[] scaleBitmapIfNeededToSize(Bitmap bitmap, long size) {
		byte[] data = null;
		try {
			float width = bitmap.getWidth();
			float height = bitmap.getHeight();
			data = bmpToByteArray(bitmap, false);
			float des = data.length;
			des = des / size;
			if (des <= 1) {
				return data;
			}
			final float scale;
			if (des <= 2.5F) {
				scale = 0.95F;
			} else if (des <= 5.0F) {
				scale = 0.9F;
			} else if (des <= 7.5F) {
				scale = 0.85F;
			} else {
				scale = 0.8F;
			}
			while (true) {
				if (null == data || data.length <= size) {
					break;
				}
				Bitmap bitmapCopy = bitmap;
				width *= scale;
				height *= scale;
				bitmap = Bitmap.createScaledBitmap(bitmapCopy, (int) width, (int) height, true);
				bitmapCopy.recycle();
				data = bmpToByteArray(bitmap, false);
			}
		} catch (Exception e) { 
			data = null;
			FrameworkLog.e(TAG, "scaleBitmapIfNeededToSize Exception: " + e.getMessage());
		}
		return data;
	}
}
