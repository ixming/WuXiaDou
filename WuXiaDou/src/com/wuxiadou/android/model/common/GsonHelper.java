package com.wuxiadou.android.model.common;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import org.apache.http.HttpEntity;
import org.apache.http.util.ByteArrayBuffer;

import com.wuxiadou.android.network.HttpRes;

import android.util.JsonReader;
import android.util.Log;

public class GsonHelper {
	private final static String TAG = GsonHelper.class.getSimpleName();

	private HttpEntity entity;
	private InputStream in;
	private InputStream gin;
	private JsonReader reader;

	public GsonHelper(HttpEntity entity) {
		this.entity = entity;
	}

	public static String getJson(HttpRes httpRes) {
		HttpEntity entity = httpRes.getEntity();
		StringBuilder entityStringBuilder = new StringBuilder();
		try {
			BufferedReader bufferedReader = new BufferedReader(
					new InputStreamReader(entity.getContent(), "UTF-8"),
					8 * 1024);
			String line = null;
			while ((line = bufferedReader.readLine()) != null) {
				entityStringBuilder.append(line);
			}
		} catch (Exception e) {
			Log.e(TAG, "execute getJson Exception :" + e.getMessage());
			e.printStackTrace();
		} finally {
			if (httpRes.getHttpGet() != null) {
				httpRes.getHttpGet().abort();
			}
			if (httpRes.getHttpPost() != null) {
				httpRes.getHttpPost().abort();
			}
			if (httpRes.getHttpDelete() != null) {
				httpRes.getHttpDelete().abort();
			}
			if (httpRes.getHttpPut() != null) {
				httpRes.getHttpPut().abort();
			}
		}
		return entityStringBuilder.toString();
	}

//	public JsonReader getReader() throws Exception {
//		in = entity.getContent();
//		if (in != null) {
//			byte[] bytes;
//			// 解决cmwap有时会自动解压缩所带来的bug
//			// Header contentEncoding = entity.getContentEncoding();
//			bytes = toByteArray(in, entity.getContentLength());
//			int ret = (int) ((bytes[0] << 8) | bytes[1] & 0xFF);
//			boolean isGzip = ret == 0x1f8b;
//			in = byteToInput(bytes);
//			// 判断是否是gzip
//			if (isGzip) {
//				gin = new GZIPInputStream(in);
//				reader = new JsonReader(new InputStreamReader(gin, "UTF-8"));
//			} else {
//				Header contentType = entity.getContentType();
//				String encoding = "UTF-8";
//				if (contentType != null) {
//					String value = contentType.getValue();
//					String[] array = value.split(";");
//					for (int i = 0; i < array.length; i++) {
//						if (array[i].contains("charset")) {
//							String[] chaArray = array[i].split("=");
//							if (chaArray.length > 1 && chaArray[1].length() > 0) {
//								encoding = chaArray[1];
//							}
//							break;
//						}
//					}
//				}
//				reader = new JsonReader(new InputStreamReader(in, encoding));
//			}
//		}
//		// End
//		return reader;
//	}

	
	public void close() {
		try {
			if (reader != null) {
				reader.close();
			}
			if (gin != null) {
				gin.close();
			}
			if (in != null) {
				in.close();
			}
			if (entity != null) {
				entity.consumeContent();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static byte[] toByteArray(InputStream instream, long contentLength)
			throws IOException {

		if (instream == null) {
			return new byte[] {};
		}
		if (contentLength > Integer.MAX_VALUE) {
			throw new IllegalArgumentException(
					"HTTP entity too large to be buffered in memory");
		}
		int i = (int) contentLength;
		if (i < 0) {
			i = 4096;
		}
		ByteArrayBuffer buffer = new ByteArrayBuffer(i);
		try {
			byte[] tmp = new byte[4096];
			int l;
			while ((l = instream.read(tmp)) != -1) {
				buffer.append(tmp, 0, l);
			}
		} finally {
			instream.close();
		}
		return buffer.toByteArray();
	}

	public static final InputStream byteToInput(byte[] buf) {
		return new ByteArrayInputStream(buf);
	}
}
