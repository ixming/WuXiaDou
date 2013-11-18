package com.wuxiadou.utils;

import java.io.File;
import java.io.IOException;
import java.lang.ref.PhantomReference;
import java.lang.ref.ReferenceQueue;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.support.v4.util.LruCache;
import android.util.Log;
import android.widget.ImageView;

import com.wuxiadou.network.HttpClientUtil;
import com.wuxiadou.network.SendRequestUtil;

public class ImageUtil {
	private static final String TAG = ImageUtil.class.getSimpleName();
	private static LruCache<String, Bitmap> imageCache = null;
	private static ImageUtil instance;
	public static final int IMAGE_SRC=0;
	public static final int IMAGE_BACKGROUND=1;
	public static ImageUtil getInstance() {
		if (instance == null) {
			instance = new ImageUtil();
		}
		return instance;
	}
	private ImageUtil() {
		if (imageCache == null) {
			Log.i("imageCache", "init imageCache");
			int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
			int cacheSize = maxMemory / 8;
			imageCache = new LruCache<String, Bitmap>(cacheSize) {
				@Override
				protected int sizeOf(String key, Bitmap value) {
					int biSize = value.getRowBytes();
					int hei = value.getHeight();
					int bitSize = biSize * hei;
					int rSize = bitSize / 1024;
					return rSize;
				}

				@Override
				protected void entryRemoved(boolean evicted, String key,
						Bitmap oldValue, Bitmap newValue) {
					imageCache.remove(key);
					new PhantomReference<Bitmap>(oldValue,
							new ReferenceQueue<Bitmap>());
					oldValue = null;
					System.gc();
					Log.i("imageCache", "entryRemoved old" + oldValue);
				}
			};
		}
	}

	public void addCacheUrl(String url, Bitmap bitmap) {
		if (Utils.isNotNull(url) && imageCache != null && bitmap != null) {
			synchronized (imageCache) {
				imageCache.put(url, bitmap);
			}
			Log.i("imageCache", "imageCache size--->" + imageCache.size());
			Log.i("imageCache", "imageCache maxsize--->" + imageCache.maxSize());
			Log.i("imageCache",
					"imageCache putCount--->" + imageCache.putCount());
			Log.i("imageCache",
					"imageCache evictionCount--->" + imageCache.evictionCount());
		}
	}
	public Bitmap getImageCacheBitmap(String key)
	{
		if(Utils.isNotNull(key))
		{
			return imageCache.get(key);
		}
		return null;
	}
	/**
	 * 本地assets下图片
	 * 
	 * @param path
	 * @return
	 */
	public Bitmap getBitmapFromAssets(Context context, String path) {
		Bitmap bitmap = null;
		try {
			if (imageCache.get(path) != null) {
				bitmap = imageCache.get(path);
			}
			if (bitmap == null) {
				bitmap = Utils.getBitmapFromAssets(context, path);
			}
			addCacheUrl(path, bitmap);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return bitmap;
	}
	/**
	 * 
	 * @param context
	 * @param iv 被設置圖片的ImageView 
	 * @param url 网络图片的请求路径
	 * @param handler 主UI创建的handler
	 * @param type 设置图片为  0 setImageBitmap  1 setBackground
	 */
	public void setImage(Context context, final ImageView iv,
			String url, Handler handler,int type) {
		if (Utils.isNotNull(url)) {
			try {
				// 第一优先级 取内存
				if (imageCache.get(url) != null) {
					final Bitmap bitmap = imageCache.get(url);
					if (bitmap != null) {
						Log.i(TAG, "setImage  get bitmap by memory !!!"
								+ Thread.currentThread().getId());
						if(type==IMAGE_SRC)
						{
						iv.setImageBitmap(bitmap);
						}else
						{
							//该方法过时 但是可以保证4.0之前不报错
							iv.setBackgroundDrawable(new BitmapDrawable(bitmap));
						}
						return;
					}
				}
				loadImageFromUrl(context, url, iv, handler,type);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}


	public void loadImageFromUrl(Context context, String url,
			final ImageView iv, Handler handler,int type) throws IOException {
		File f = null;
		try {

			f = FileUtil.findImageFileByPath(url, context,
					FileUtil.FILE_IMG_PATH);
			if (f != null) {
				if (f.length() <= 0) {
					Log.i("FileException:", "f.delete");
					f.delete();
				}
				// 第二优先级 尝试取本地
				final Bitmap bmp = Utils.getBitmapFromnFile(f);
				if (bmp != null) {
					addCacheUrl(url, bmp);
					if (bmp != null) {
						Log.i(TAG,"setImage  get bitmap by sdcard or storage !!!");
						if(type==IMAGE_SRC)
						{
							iv.setImageBitmap(bmp);
						}else
						{
							//该方法过时 但是可以保证4.0之前不报错
							iv.setBackgroundDrawable(new BitmapDrawable(bmp));
						}
					}
					return;
				}
			}
			// 第三优先级
			// 取网络图片网络图片取出后要存储到本地
			SendRequestUtil.sendRequestImg(iv, url, FileUtil.FILE_IMG_PATH,
					handler, context,type);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * 判断该图片url命名的图片是否已经存在，存在则返回改图片文件 否则：不存在则调用httpclientUtil的downloadImg方法下载图片
	 */
	public static File createImageCacheFile(String url, Context context,
			String paramPath) {
		String fileName = MD5.digest2Str(url);
		File cacheFile = null;
		try {
			String filepath = FileUtil.getFilePath(context, paramPath);
			cacheFile = new File(filepath + fileName);
			Log.i(TAG, " createImageCacheFile filepath: " + filepath
					+ " cacheFile: " + cacheFile);
			if (cacheFile.exists()) {
				Log.i(TAG, "cacheFile exists!!!");
				cacheFile = HttpClientUtil.downloadImageFile(context, url,
						cacheFile);
				return cacheFile;
			} else {
				if (!new File(filepath).exists()) {
					Log.w("TAG", "mkdirs");
					new File(filepath).mkdirs();
				}
				cacheFile = HttpClientUtil.downloadImageFile(context, url,
						cacheFile);
			}
		} catch (Exception e) {
			cacheFile = null;
			Log.e(TAG,
					" FileUtil createImageCacheFile Exception "
							+ e.getMessage());
			e.printStackTrace();
		}
		return cacheFile;
	}
}
