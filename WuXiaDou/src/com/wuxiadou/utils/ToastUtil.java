package com.wuxiadou.utils;

import java.lang.ref.WeakReference;

import android.content.Context;
import android.os.Handler;
import android.widget.Toast;

/**
 * a reusable toast utility class
 * @author YinYong
 * @version 1.0
 */
public class ToastUtil {
	private ToastUtil() { }

	private static WeakReference<Toast> mToastRef;
	
	private static Toast ensureToastInstance(Context context){
		Toast temp;
		if (null == mToastRef || null == (temp = mToastRef.get())) {
			mToastRef = new WeakReference<Toast>(Toast.makeText(context, "", Toast.LENGTH_SHORT));
			temp = mToastRef.get();
		} 
		return temp;
	}
	
	public static void showToast(Context context, CharSequence message){
		try {
			Toast temp = ensureToastInstance(context);
			temp.setDuration(Toast.LENGTH_SHORT);
			temp.setText(message);
			temp.show();
		} catch (Exception e) { }
	}
	
	public static void showToast(Context context, int resId){
		try {
			Toast temp = ensureToastInstance(context);
			temp.setDuration(Toast.LENGTH_SHORT);
			temp.setText(resId);
			temp.show();
		} catch (Exception e) { }
	}
	
	public static void showToast(final Context context,Handler handler,final int resId){
		try {
			handler.post(new Runnable() {
				@Override
				public void run() {
					showToast(context, resId);
				}
			});
		} catch (Exception e) { }
	}
	public static void showLongToast(Context context, int resId){
		try {
			Toast temp = ensureToastInstance(context);
			temp.setDuration(Toast.LENGTH_LONG);
			temp.setText(resId);
			temp.show();
		} catch (Exception e) { }
	}
	
	public static void showLongToast(Context context, CharSequence message){
		try {
			Toast temp = ensureToastInstance(context);
			temp.setDuration(Toast.LENGTH_LONG);
			temp.setText(message);
			temp.show();
		} catch (Exception e) { }
	}


}
