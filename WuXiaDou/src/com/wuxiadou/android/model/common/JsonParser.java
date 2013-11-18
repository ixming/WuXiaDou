package com.wuxiadou.android.model.common;

import org.ixming.android.utils.FrameworkLog;

import com.wuxiadou.android.network.HttpRes;

/**
 * 工具类--解析JSON，并将之转为目标类
 * @author YinYong
 * @version 1.0
 */
public class JsonParser {
	private static final String TAG = JsonParser.class.getSimpleName();
	private JsonParser() {}
	/**
	 * 解析网络请求，并将返回的内容转化为与json互转的类对象
	 * @added 1.0
	 */
	public static <T> T parseJson(HttpRes entity, Class<? extends T> clz) {
		T temp = null;
		try {
			if (null == entity) {
				FrameworkLog.w(TAG, "parseJson entity is null!!!");
				return null;
			}
			String json = GsonHelper.getJson(entity);
			FrameworkLog.d(TAG, "parseJson: " + json);
			temp = JsonService.outObjFromJson(json, clz);
		} catch (Exception e) {
			FrameworkLog.e(TAG, "execute parseJson Exception:" + e.getMessage());
		}
		return temp;
	}
}
