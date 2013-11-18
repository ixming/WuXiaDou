package com.wuxiadou.android.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.ref.PhantomReference;
import java.lang.ref.ReferenceQueue;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.support.v4.util.LruCache;
import android.widget.ImageView;

import com.wuxiadou.android.network.HttpClientUtil;
import com.wuxiadou.android.network.SendRequestUtil;

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
			LogUtils.i("imageCache", "init imageCache");
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
					LogUtils.i("imageCache", "entryRemoved old" + oldValue);
				}
			};
		}
	}

	public void addCacheUrl(String url, Bitmap bitmap) {
		if (Utils.isNotNull(url) && imageCache != null && bitmap != null) {
			synchronized (imageCache) {
				imageCache.put(url, bitmap);
			}
			LogUtils.i("imageCache", "imageCache size--->" + imageCache.size());
			LogUtils.i("imageCache", "imageCache maxsize--->" + imageCache.maxSize());
			LogUtils.i("imageCache",
					"imageCache putCount--->" + imageCache.putCount());
			LogUtils.i("imageCache",
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
						LogUtils.i(TAG, "setImage  get bitmap by memory !!!"
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

			f = LocalFileUtility.findImageFileByPath(url, context,
					LocalFileUtility.FILE_IMG_PATH);
			if (f != null) {
				if (f.length() <= 0) {
					LogUtils.i("FileException:", "f.delete");
					f.delete();
				}
				// 第二优先级 尝试取本地
				final Bitmap bmp = Utils.getBitmapFromnFile(f);
				if (bmp != null) {
					addCacheUrl(url, bmp);
					if (bmp != null) {
						LogUtils.i(TAG,"setImage  get bitmap by sdcard or storage !!!");
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
			SendRequestUtil.sendRequestImg(iv, url, LocalFileUtility.FILE_IMG_PATH,
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
			String filepath = LocalFileUtility.getFilePath(context, paramPath);
			cacheFile = new File(filepath + fileName);
			LogUtils.i(TAG, " createImageCacheFile filepath: " + filepath
					+ " cacheFile: " + cacheFile);
			if (cacheFile.exists()) {
				LogUtils.i(TAG, "cacheFile exists!!!");
				cacheFile = HttpClientUtil.downloadImageFile(context, url,
						cacheFile);
				return cacheFile;
			} else {
				if (!new File(filepath).exists()) {
					LogUtils.w("TAG", "mkdirs");
					new File(filepath).mkdirs();
				}
				cacheFile = HttpClientUtil.downloadImageFile(context, url,
						cacheFile);
			}
		} catch (Exception e) {
			cacheFile = null;
			LogUtils.e(TAG,
					" FileUtil createImageCacheFile Exception "
							+ e.getMessage());
			e.printStackTrace();
		}
		return cacheFile;
	}
	
	public static Bitmap getBitmapFromRes(Context context, int resId) {
		Bitmap bitmap = null;
		try {
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inPreferredConfig = Config.RGB_565;
			options.inPurgeable = true;
			options.inInputShareable = true;
			bitmap = BitmapFactory.decodeResource(context.getResources(),
					resId, options);
		} catch (Exception e) {
			bitmap = null;
			LogUtils
					.e(TAG, "getBitmapFromRes Exception: " + e.getMessage());
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
			bitmap = BitmapFactory.decodeFileDescriptor(is.getFD(), null,
					options);
		} catch (Exception e) {
			bitmap = null;
			LogUtils
					.e(TAG,
							"getBitmapFromFileInputStream Exception: "
									+ e.getMessage());
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (Exception e) {
				}
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
			LogUtils.e(TAG,
					"getBitmapFromFile Exception: " + e.getMessage());
			return null;
		}
		return getBitmapFromFileInputStream(fis);
	}

	/**
	 * 将Bitmap转化为字节数组
	 * 
	 * @param bmp
	 *            target bitmap to read
	 * @param needRecycle
	 *            是否需要回收bitmap
	 * @added 1.0
	 */
	public static byte[] bmpToByteArray(final Bitmap bmp,
			final boolean needRecycle) {
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
			LogUtils.e(TAG, "bmpToByteArray Exception: " + e.getMessage());
			return null;
		} finally {
			if (null != output) {
				try {
					output.close();
				} catch (Exception ex) {
				}
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
				bitmap = Bitmap.createScaledBitmap(bitmapCopy, (int) width,
						(int) height, true);
				bitmapCopy.recycle();
				data = bmpToByteArray(bitmap, false);
			}
		} catch (Exception e) {
			data = null;
			LogUtils.e(TAG,
					"scaleBitmapIfNeededToSize Exception: " + e.getMessage());
		}
		return data;
	}
}
