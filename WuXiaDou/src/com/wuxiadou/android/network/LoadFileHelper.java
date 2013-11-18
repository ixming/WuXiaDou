package com.wuxiadou.android.network;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Vector;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;

import com.wuxiadou.android.utils.ImageUtil;
import com.wuxiadou.android.utils.Utils;

public class LoadFileHelper {
	private static final String TAG = LoadFileHelper.class.getSimpleName();
	private boolean isRun = true;
	private boolean exceBool = true;
	private static LoadFileHelper imgHelper = null;
	private static Vector<ReqImgBean> queue = new Vector<ReqImgBean>();
	private Object syncToken = new Object();

	private LoadFileHelper() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				while (isRun) {
					try {
						if ((queue == null) || queue.isEmpty()) {
							synchronized (syncToken) {
								Log.w(TAG, "syncToken.wait()");
								syncToken.wait();
							}
						} else {
							if (exceBool) {
								if (queue != null && !queue.isEmpty()) {
									Log.w(TAG,
											"queue.size()---->" + queue.size());
									exceBool = false;
									ReqImgBean bean = queue.get(0);
									Log.e(TAG, "start " + bean.getUrl());
									if (bean.getUrl() != null
											&& bean.getUrl().length() > 0) {
										downloadImg(bean);
									}
								}
							}
							Thread.sleep(10);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}).start();
	}

	public static synchronized LoadFileHelper getInstance() {
		if (imgHelper == null) {
			imgHelper = new LoadFileHelper();
		}
		return imgHelper;
	}

	public void addRequest(ReqImgBean bean) {
		if (null == bean) {
			return;
		}

		try {
			// 避免下载重复的图片或者文件
			if (bean.getUrl() != null && bean.getUrl().length() > 0) {
				if (queue.contains(bean)) {
					return;
				}
			}
			queue.add(bean);
			synchronized (syncToken) {
				Log.w(TAG, "syncToken.wait()");
				syncToken.notify();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void downloadImg(final ReqImgBean bean) {
		new Thread(new Runnable() {
			@Override
			public void run() { 
				try {
					File file = ImageUtil.createImageCacheFile(bean.getUrl(),
							bean.getContext(), bean.getParamPath());
					if (file != null) {
						if (file.length() <= 0) { 
							file.delete();
						} else {
							Bitmap bitmap = Utils.getBitmapFromnFile(file
									.getPath());
							// bitmap处理后重新写入文件
							String parentPath = file.getParentFile().getName();
							if (null != parentPath) {
								if (bitmap != null) {
									FileOutputStream fOut = null;
									fOut = new FileOutputStream(
											file);
									if (bitmap
											.compress(
													Bitmap.CompressFormat.PNG,
													100,
													fOut)) {
										bitmap.recycle();
										fOut.flush();
										fOut.close();
									} else {
										bitmap.recycle();
										fOut.close();
									}
									bitmap = Utils
											.getBitmapFromnFile(file);
								}
							}
							// end
							if (bitmap == null) {
								bitmap = Utils.getBitmapFromnFile(file);
							}
							final Bitmap bm=bitmap;
							ImageUtil.getInstance().addCacheUrl(bean.getUrl(), bm);
							Log.i(TAG,"6 thread id"+Thread.currentThread().getId());
							//设置图片
							bean.getHandler().post(new Runnable() {
								@Override
								public void run() {
									Log.i(TAG,"setImage  get bitmap by network !!!");
									bean.getImageView().setImageBitmap(bm);
									if(bean.getType()==ImageUtil.IMAGE_SRC)
									{
										bean.getImageView().setImageBitmap(bm);
									}else
									{
										//该方法过时 但是可以保证4.0之前不报错
										bean.getImageView().setBackgroundDrawable(new BitmapDrawable(bm));
									}
								}
							});
							//notifySuccessComm(bean, bitmap);
						}
					} else {
						// notifyErrorComm(bean);
					}
				} catch (Exception e) {
					Log.e(TAG, "downloadImg  Exception " + e.getMessage());
					e.printStackTrace();
					// notifyErrorComm(bean);
				} finally {
//					if (fOutStream != null) {
//						try {
//							fOutStream.flush();
//							fOutStream.close();
//						} catch (Exception e) {
//							// TODO: handle exception
//						}
//					}
					allowNextReq();
				}
			}

		}).start();
	}

	/*
	 * public void downloadContent(final ReqFileBean bean) { new Thread(new
	 * Runnable() {
	 * 
	 * @Override public void run() { try { File file =
	 * FileUtil.createContentCacheFile(bean.getContext(), bean.getNewsId(),
	 * bean.getJson(), FileUtil.NEWS_FILE_CONTENT); if (file != null) { String
	 * content = FileUtil.getNewsContent(bean.getContext(), file); if (content
	 * != null && content.length() > 0) { notifySuccessComm(bean, content); } }
	 * else { notifyErrorComm(bean); } } catch (Exception e) { Log.e(TAG,
	 * "downloadContent  Exception " + e.getMessage()); notifyErrorComm(bean); }
	 * finally { allowNextReq(); } } }).start(); }
	 */

	// 允许开启新的线程下载队列后的成员
	private void allowNextReq() {
		if (!queue.isEmpty()) {
			queue.remove(0);
		}
		exceBool = true;
	}

	// private void notifySuccessComm(final ReqFileBean bean, final Object o)
	// {
	// Handler handler = bean.getHanlder();
	// final OnFileLoadListener listener = bean.getListener();
	// if (o != null)
	// {
	// if (handler != null)
	// {
	// handler.post(new Runnable()
	// {
	// @Override
	// public void run()
	// {
	// if (listener != null)
	// {
	// listener.onFileLoad(bean.getIndex(), o);
	// SurfNewsUtil.freeReqFileBean(bean);
	// }
	// }
	// });
	// }
	// else
	// {
	// if (listener != null)
	// {
	// listener.onFileLoad(bean.getIndex(), o);
	// SurfNewsUtil.freeReqFileBean(bean);
	// }
	// }
	// }
	// else
	// {
	// notifyErrorComm(bean);
	// }
	// }
	//
	// public void notifyErrorComm(final ReqFileBean bean)
	// {
	// Handler handler = bean.getHanlder();
	// final OnFileLoadListener listener = bean.getListener();
	// if (handler != null)
	// {
	// handler.post(new Runnable()
	// {
	// @Override
	// public void run()
	// {
	// if (listener != null)
	// {
	// listener.onError(bean.getIndex());
	// SurfNewsUtil.freeReqFileBean(bean);
	// }
	// }
	// });
	// }
	// else
	// {
	// if (listener != null)
	// {
	// listener.onError(bean.getIndex());
	// SurfNewsUtil.freeReqFileBean(bean);
	// }
	// }
	// }
}