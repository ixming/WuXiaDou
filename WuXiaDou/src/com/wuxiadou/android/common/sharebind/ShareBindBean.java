package com.frameworkexample.android.common.sharebind;


import android.content.Context;
import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

public class ShareBindBean implements Parcelable {

	/**
	 * 调用分享时，将一个ShareBindBean传递给需要进行再次封装的分享方式
	 */
	public static final String EXTRA_SHARE_BIND = "extra_share_bind";
	
	/**图片来自资源文件*/
	public static final int TYPE_FORM_RES = 1;
	/**图片来自文件系统*/
	public static final int TYPE_FORM_FILE = 2;
	private int type;
	private String title;
	private String message;
	private String url;
	private String imageFile;
	private int imageRes;
	private String specMsgForWeiXinTimeLine;

	public ShareBindBean() { }
	
	public String getTitle() {
		return title;
	}
	
	public String getMessage() {
		return message;
	}
	
	public String getSpecMsgForWeiXinTimeLine() {
		return specMsgForWeiXinTimeLine;
	}
	
	public String getUrl() {
		return url;
	}
	
	public int getImageResId() {
		return imageRes;
	}

	/**
	 * 设置图片来源类型
	 * @added 1.0
	 */
	public ShareBindBean setType(int type) {
		this.type = type;
		return this;
	}
	
	public ShareBindBean setTitle(String title) {
		this.title = title;
		return this;
	}
	
	public ShareBindBean setMessage(String message) {
		this.message = message;
		return this;
	}
	
	public ShareBindBean setSpecMsgForWeiXinTimeLine(String message) {
		this.specMsgForWeiXinTimeLine = message;
		return this;
	}
	
	/**
	 * 设置该分享的链接，对于有些分享客户端可能只支持在内容中添加，有些可以给内容添加跳转至
	 * 该链接的功能，这些处理都在具体的分享中实现，使用者只需要设置即可
	 * @added 1.0
	 */
	public ShareBindBean setUrl(String url) {
		this.url = url;
		return this;
	}

	public ShareBindBean setImageFile(String imageFile) {
		type = TYPE_FORM_FILE;
		this.imageFile = imageFile;
		return this;
	}

	/**
	 * 保证该图片的大小
	 * @added 1.0
	 */
	public ShareBindBean setResId(int resId) {
		type = TYPE_FORM_RES;
		imageRes = resId;
		return this;
	}
	
	public boolean hasImage() {
		return type > 0;
	}
	
	public Bitmap getImage(Context context) {
		switch (type) {
		case TYPE_FORM_RES:
			return BitmapUtil.getBitmapFromRes(context, imageRes);
		case TYPE_FORM_FILE:
			return BitmapUtil.getBitmapFromFile(imageFile);
		default:
			return null;
		}
	}
	
	/**
	 * @param size 限制大小
	 * @added 1.0
	 */
	public byte[] getImageData(Context context, long size) {
		Bitmap bitmap = getImage(context);
		if (null != bitmap) {
			return BitmapUtil.scaleBitmapIfNeededToSize(bitmap, size);
		}
		return null;
	}
	
	/**
	 * @param size 限制大小
	 * @added 1.0
	 */
	public Bitmap getImage(Context context, long size) {
		Bitmap bitmap = getImage(context);
		if (null != bitmap) {
			BitmapUtil.scaleBitmapIfNeededToSize(bitmap, size);
		}
		return bitmap;
	}
	
	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(title);
		dest.writeString(message);
		dest.writeString(url);
		dest.writeString(imageFile);
		dest.writeInt(imageRes);
		dest.writeInt(type);
	}

	public static final Parcelable.Creator<ShareBindBean> CREATOR = new Parcelable.Creator<ShareBindBean>() {
		public ShareBindBean createFromParcel(Parcel in) {
			return new ShareBindBean(in);
		}

		public ShareBindBean[] newArray(int size) {
			return new ShareBindBean[size];
		}
	};
	
	private ShareBindBean(Parcel in) {
		title = in.readString();
		message = in.readString();
		url = in.readString();
		imageFile = in.readString();
		imageRes = in.readInt();
		type = in.readInt();
    }
}
