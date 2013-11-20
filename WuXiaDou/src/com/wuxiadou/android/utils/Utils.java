package com.wuxiadou.android.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.ixming.io.file.FileOperator;
import org.ixming.utils.StringUtil;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

public class Utils {
	private static final String TAG = Utils.class.getSimpleName();
	private static int displayWidth = 0;
	private static int displayHeight = 0;
	private static String DEVICE_ID = null;
	public static final String ENCRYPTION_HEAD = "Upsmart";

	static InputMethodManager inputMethodManager;

	public static Bitmap getBitmapFromAssets(Context context, String fileName) {
		if (null == fileName) {
			return null;
		}
		Bitmap bitmap = null;
		InputStream is = null;
		try {
			AssetManager am = context.getAssets();
			is = am.open(fileName);
			bitmap = getBitmapFromInputStream(is);
		} catch (Exception e) {
			Log.i(TAG, e.getMessage());
			e.printStackTrace();
		} finally {
			if (null != is)
				try {
					is.close();
				} catch (IOException e) {
					Log.i(TAG, e.getMessage());
					e.printStackTrace();
				}
		}
		return bitmap;
	}

	/**
	 * 所有图片资源都需要通过
	 * 
	 * @param is
	 * @return
	 */
	public static Bitmap getBitmapFromInputStream(InputStream is) {
		if (is == null)
			return null;
		Bitmap bitmap = null;
		try {
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inPreferredConfig = Config.RGB_565;
			options.inPurgeable = true;
			options.inInputShareable = true;
			bitmap = BitmapFactory.decodeStream(is, null, options);
		} catch (Exception e) {
			bitmap = null;
			e.printStackTrace();
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return bitmap;
	}

	/**
	 */
	public static Bitmap getBitmapFromnFileInputStream(FileInputStream is) {
		if (is == null)
			return null;
		Bitmap bitmap = null;
		try {
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inPreferredConfig = Config.RGB_565;
			options.inPurgeable = true;
			options.inInputShareable = true;
			bitmap = BitmapFactory.decodeFileDescriptor(is.getFD(), null,
					options);
		} catch (Exception e) {
			bitmap = null;
			e.printStackTrace();
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return bitmap;
	}

	public static Bitmap getBitmapFromnFile(String filePath) {
		return getBitmapFromnFile(new File(filePath));
	}

	public static Bitmap getBitmapFromnFile(File file) {
		try {
			FileInputStream fis = new FileInputStream(file);
			return getBitmapFromnFileInputStream(fis);
		} catch (Exception ex) {
			 ex.printStackTrace();
			return null;
		}
	}

	// 获取屏幕宽度
	public static int getDisplayWidth(Context context) {
		if (displayWidth <= 0) {
			WindowManager wm = (WindowManager) context.getApplicationContext()
					.getSystemService(Context.WINDOW_SERVICE);
			DisplayMetrics dm = new DisplayMetrics();
			wm.getDefaultDisplay().getMetrics(dm);
			displayWidth = dm.widthPixels;
		}
		return displayWidth;
	}

	// 获取屏幕高度
	public static int getDisplayHeight(Context context) {
		if (displayHeight <= 0) {
			WindowManager wm = (WindowManager) context.getApplicationContext()
					.getSystemService(Context.WINDOW_SERVICE);
			DisplayMetrics dm = new DisplayMetrics();
			wm.getDefaultDisplay().getMetrics(dm);
			displayHeight = dm.heightPixels;
		}
		return displayHeight;
	}

	/**
	 * 获取deviceId
	 */
	public static String getDeviceId(Context context) {
		if (DEVICE_ID == null || DEVICE_ID.length() > 0) {
			DeviceUuidFactory factory = new DeviceUuidFactory(context);
			DEVICE_ID = factory.getDeviceUuid();
		}
		return DEVICE_ID;
	}

	public static boolean isNotNull(String str) {
		if (str != null && !TextUtils.isEmpty(str) && !"".equals(str.trim())
				&& !"null".equals(str)) {
			return true;
		}
		return false;
	}

	// 键盘开启
	public static void keyboardOn(Context context) {
		if (inputMethodManager == null) {
			inputMethodManager = (InputMethodManager) context
					.getSystemService(Context.INPUT_METHOD_SERVICE);
		}
		inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED,
				InputMethodManager.HIDE_NOT_ALWAYS);
	}

	// 键盘关闭
	public static void keyboardOff(Context context, EditText et) {
		if (inputMethodManager == null) {
			inputMethodManager = (InputMethodManager) context
					.getSystemService(Context.INPUT_METHOD_SERVICE);
		}
		
		inputMethodManager.hideSoftInputFromWindow(et.getWindowToken(), 0);
	}



	/**
	 * 图片等比缩放
	 * 
	 * @return
	 * 
	 */
	public static Bitmap scaleBitmap(Bitmap bitmap) {
		// 图片缩放
		Log.i(TAG,"execute scaleBitmap!!!");
		int newWidth = 720;
		float scale = (float) newWidth / bitmap.getWidth();
		int newHeight = (int) (bitmap.getHeight() * scale);
		return Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true);
	}

	/**
	 * 11 校验银行卡卡号 12
	 * 
	 * @param cardId
	 *            13
	 * @return 14
	 */
	public static boolean checkBankCard(String cardId) {
		if(!isNotNull(cardId))
		{
			return false;
		}
		char bit = getBankCardCheckCode(cardId
				.substring(0, cardId.length() - 1));
		if (bit == 'N') {
			return false;
		}
		return cardId.charAt(cardId.length() - 1) == bit;
	}

	/**
	 * 24 从不含校验位的银行卡卡号采用 Luhm 校验算法获得校验位 25
	 * 
	 * @param nonCheckCodeCardId
	 *            26
	 * @return 27
	 */
	public static char getBankCardCheckCode(String nonCheckCodeCardId) {
		if (nonCheckCodeCardId == null
				|| nonCheckCodeCardId.trim().length() == 0
				|| !nonCheckCodeCardId.matches("\\d+")) {
			// 如果传的不是数据返回N
			return 'N';
		}
		char[] chs = nonCheckCodeCardId.trim().toCharArray();
		int luhmSum = 0;
		for (int i = chs.length - 1, j = 0; i >= 0; i--, j++) {
			int k = chs[i] - '0';
			if (j % 2 == 0) {
				k *= 2;
				k = k / 10 + k % 10;
			}
			luhmSum += k;
		}
		return (luhmSum % 10 == 0) ? '0' : (char) ((10 - luhmSum % 10) + '0');
	}

	/**
	 * @param context
	 */
	public static void deviceScreen(Context context) {
		WindowManager wm = (WindowManager) context.getApplicationContext()
				.getSystemService(Context.WINDOW_SERVICE);
		DisplayMetrics dm = new DisplayMetrics();
		wm.getDefaultDisplay().getMetrics(dm);
		int width = dm.widthPixels; // 屏幕宽度（像素）
		int height = dm.heightPixels; // 屏幕高度（像素）
		float density = dm.density; // 屏幕密度（0.75 / 1.0 / 1.5）
		int densityDpi = dm.densityDpi; // 屏幕密度DPI（120 / 160 / 240）
		System.out.println("----------------------------------");
		System.out.println("width:" + width);
		System.out.println("height:" + height);
		System.out.println("density:" + density);
		System.out.println("densityDpi:" + densityDpi);
		System.out.println("----------------------------------");
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
			return null;
		} finally {
			if (null != output) {
				try {
					output.close();
				} catch (Exception ex) { }
			}
		}
	}
	public static final void clearUploadImgCache(Context context)
	{
		final FileOperator fo = FileOperator.newInstance(
				LocalFileUtility.getFilePath(context, LocalFileUtility.FILE_UPLOAD_CACHE));
		new Thread() {
			@Override
			public void run() {
				fo.delete(false);
			}
		}.start();
	}

	public static int hashOfString(String str) {
		return StringUtil.isEmpty(str) ? 0 : str.hashCode();
	}

}
