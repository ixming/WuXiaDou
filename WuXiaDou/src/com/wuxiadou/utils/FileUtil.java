package com.wuxiadou.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

public class FileUtil {

	private static final String TAG = FileUtil.class.getSimpleName();
	public static final String FILE_IMG_PATH = "/SmartRebate/img/";
	public static final String FILE_UPLOAD_CACHE = "/SmartRebate/img_cache/";

	public static boolean isSDCardAvaliable() {
		return Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED);
	}

	public static String getFilePath(Context context, String paramPath) {
		String packageName = "/." + context.getPackageName();
		paramPath = packageName + paramPath;
		String filepath = context.getCacheDir().getPath() + paramPath;
		if (android.os.Build.VERSION.SDK_INT < 8) {
			if (isSDCardAvaliable()) {
				filepath = Environment.getExternalStorageDirectory()
						+ paramPath;
			}
		} else {
			if (isSDCardAvaliable()) {
				filepath = context.getCacheDir().getAbsolutePath()
						+ paramPath;
			}
		}
		return filepath;
	}
	/**
	 * 照相存储
	 * @return
	 */
	public static Uri getOutputMediaFileUri(Context context) {
		return Uri.fromFile(getOutputMediaFile(context));
	}

	private static File getOutputMediaFile(Context context) {
		String filepath = getFilePath(context, FILE_UPLOAD_CACHE);
		try {
			
			if (!new File(filepath).exists()) {
				Log.w("TAG", "mkdirs");
				new File(filepath).mkdirs();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
				.format(new Date());
		File mediaFile;
		mediaFile = new File(filepath+ File.separator
				+ "IMG_" + timeStamp + ".jpg");
		return mediaFile;
	}

	public static String uploadImageCacheFile(String imgName, Bitmap bitmap,
			Context context) {
		FileOutputStream b = null;
		String fileName="";
		try {
			String filepath = getFilePath(context, FILE_UPLOAD_CACHE);
			if (!new File(filepath).exists()) {
				Log.w("TAG", "mkdirs");
				new File(filepath).mkdirs();
			}
			
			fileName = filepath + imgName + ".jpg";
			b = new FileOutputStream(fileName);
			//压缩 50%
			bitmap.compress(Bitmap.CompressFormat.JPEG,50, b);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				b.flush();
				b.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return fileName;
	}

	public static File findImageFileByPath(String url, Context context,
			String paramPath) {
		File file = null;
		try {
			String fileName = MD5.digest2Str(url);
			String filepath = getFilePath(context, paramPath);
			file = new File(filepath + fileName);
			if (file.exists()) {
				Log.i(TAG, "file exists : true");
				return file;
			} else {
				return null;
			}
		} catch (Exception e) {
			Log.e(TAG,
					"findImageFileByPath paramPath Exception " + e.getMessage());
			e.printStackTrace();
			return null;
		}

	}

	public static final class FileOperator {
		private File mFile;
		private FileOperator(String path) {
			mFile = new File(path);
		}
		public static FileOperator newInstance(String path) {
			return new FileOperator(path);
		}
		
		public long getSize() {
			return caculateFileSize(mFile);
		}
		
		public boolean delete(boolean deleteRoot) {
			return deleteFile(mFile, deleteRoot);
		}
	}
	
	// 删除文件
	// 大小
	public static boolean deleteFile(File file, boolean deleteRoot) {
		if (null == file || !file.exists()) {
			return true;
		}
		if (file.isDirectory()) {
			boolean flag = true;
			File[] childFile = file.listFiles();
			if (null != childFile && childFile.length > 0) {
				for (File cFile : childFile) {
					flag &= deleteFile(cFile, deleteRoot);
				}
			}
			if (deleteRoot) {
				flag &= file.delete();
			}
			return flag;
		} else {
			return file.delete();
		}
	}
	public static boolean deleteFile(String filePath, boolean deleteRoot) {
		return deleteFile(new File(filePath), deleteRoot);
	}
	// 大小
	public static long caculateFileSize(File file) {
		if (null == file || !file.exists()) {
			return 0L;
		}
		if (file.isDirectory()) {
			long size = 0L;
			File[] childFile = file.listFiles();
			if (null != childFile && childFile.length > 0) {
				for (File cFile : childFile) {
					size += caculateFileSize(cFile);
				}
			}
			return size;
		} else {
			return file.length();
		}
	}
	public static long caculateFileSize(String filePath) {
		return caculateFileSize(new File(filePath));
	}
	
	/**
	 * 以M为单位
	 * @added 1.0
	 */
	public static String calFileSizeString(float bytes) {
		if (0F >= bytes) {
			bytes = 0F;
		}
		float result = bytes;
		result = result / 1024F;
		result = result / 1024F;
		return String.format("%.2f", result) + "M";
	}
}
