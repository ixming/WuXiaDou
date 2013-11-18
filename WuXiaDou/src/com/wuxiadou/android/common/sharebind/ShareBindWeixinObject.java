package com.frameworkexample.android.common.sharebind;

import android.content.Context;
import android.content.Intent;

import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.SendMessageToWX;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.mm.sdk.openapi.WXMediaMessage;
import com.tencent.mm.sdk.openapi.WXTextObject;
import com.tencent.mm.sdk.openapi.WXWebpageObject;

public class ShareBindWeixinObject{
	final String TAG = ShareBindWeixinObject.class.getSimpleName();
	
	/**
	 * 提供微信开放平台的ID号
	 */
	public final static String wxAppID = "wx0ab6674bfe9f1ddf";
	/**
	 * 提供微信开放平台的KEY值
	 */
	public final static String wxAppKey = "36d974c77309cda18a310e9bc8b7229d";
	
	// 朋友圈支持的最低版本号
	private static final int TIMELINE_SUPPORTED_VERSION = 0x21020001;
	
	public static boolean isSupportedWeixin(Context context) {
		return new ShareBindWeixinObject(context).isSupportWXSharing();
	}
	
	Context mContext;
	private final IWXAPI mAPI;
	private boolean mIsSupport = false;
	public ShareBindWeixinObject(Context context) {
		this.mContext = context;
		mAPI = WXAPIFactory.createWXAPI(context, wxAppID, false);
		if (null == mAPI) {
			return ;
		}
		
		if (!mAPI.isWXAppInstalled()) {
//			Toast.makeText(mContext, "没有安装微信客户端", Toast.LENGTH_SHORT).show();
			return;
		}
		
		if (!mAPI.isWXAppSupportAPI()) {
//			Toast.makeText(mContext, "微信客户端不支持第三方应用", Toast.LENGTH_SHORT).show();
			return ;
		}
		
		if (!mAPI.registerApp(wxAppID)) {
//			Toast.makeText(mContext, "微信客户端注册失败", Toast.LENGTH_SHORT).show();
			return ;
		}
		
		mIsSupport = true;
	}
	
	public boolean isWXAppInstalled() {
		return mAPI.isWXAppInstalled();
	}
	
	public boolean isSupportWXSharing() {
		return mIsSupport;
	}
	
	/**
	 * 是否支持朋友圈
	 * @added 1.0
	 */
	public boolean isSupportTimeline() {
		return null != mAPI && mAPI.getWXAppSupportAPI() >= TIMELINE_SUPPORTED_VERSION;
	}
	
	public boolean handleIntent(Intent intent, IWXAPIEventHandler handler) {
		return mAPI.handleIntent(intent, handler);
	}
	
	private int mScene = SendMessageToWX.Req.WXSceneSession;
	/**
	 * 设置当前的场景模式为朋友圈
	 * @added 1.0
	 */
	public ShareBindWeixinObject setTimeLineScene() {
		return setScene(SendMessageToWX.Req.WXSceneTimeline);
	}
	/**
	 * 设置当前的场景模式为普通模式（选择好友）
	 * @added 1.0
	 */
	public ShareBindWeixinObject setSessionScene() {
		return setScene(SendMessageToWX.Req.WXSceneSession);
	}
	private ShareBindWeixinObject setScene(int scene) {
		mScene = scene;
		if (mScene != SendMessageToWX.Req.WXSceneSession
				&& mScene != SendMessageToWX.Req.WXSceneTimeline) {
			mScene = SendMessageToWX.Req.WXSceneSession;
		}
		return this;
	}
	
	private String buildTransaction(String type) {
		if (null == type) {
			type = "";
		}
		return type + String.valueOf(System.currentTimeMillis());
	}
	
	public boolean sendTextMsg(String text, String title, String description) {
		if (!isSupportWXSharing()) {
			return false;
		}
		WXTextObject textObj = new WXTextObject();
		textObj.text = text;

		WXMediaMessage msg = new WXMediaMessage();
		msg.mediaObject = textObj;
		msg.title = title;
		msg.description = description;

		SendMessageToWX.Req req = new SendMessageToWX.Req();
		req.transaction = buildTransaction("text");
		req.message = msg;
		req.scene = mScene;
		return mAPI.sendReq(req);
	}
	
	public boolean sendWebPageMsg(Context context, String targetUrl,
			byte[] thumbData, String title, String description, String specForTimeline) {
		if (!isSupportWXSharing()) {
			return false;
		}
		if (null == context) {
			return false;
		}
		WXWebpageObject webpage = new WXWebpageObject();
		webpage.webpageUrl = targetUrl;
		WXMediaMessage msg = new WXMediaMessage(webpage);
		msg.title = title;
		msg.description = description;
		msg.thumbData = thumbData;
		if (mScene == SendMessageToWX.Req.WXSceneTimeline) {
			if (null == specForTimeline || specForTimeline.length() == 0) {
				specForTimeline = description;
			}
			msg.title = specForTimeline;
		}
		
		SendMessageToWX.Req req = new SendMessageToWX.Req();
		req.transaction = buildTransaction("webpage");
		req.message = msg;
		req.scene = mScene;
		return mAPI.sendReq(req);
	}
	
	public boolean sendMsgWithAppIcon(Context context, ShareBindBean bean) {
		return sendWebPageMsg(context, bean.getUrl(),
			bean.getImageData(context, 32 * 1024), /*微信对图片大小的限制*/
			bean.getTitle(), bean.getMessage(), bean.getSpecMsgForWeiXinTimeLine());
	}
	
}