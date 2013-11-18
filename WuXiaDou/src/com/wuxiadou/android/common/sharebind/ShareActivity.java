package com.frameworkexample.android.common.sharebind;

import com.frameworkexample.android.R;
import com.frameworkexample.android.activity.base.BaseActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;

public class ShareActivity extends BaseActivity {

	/**
	 * @param from
	 * @param title
	 * @param msg
	 * @added 1.0
	 */
	public static void startShareActivity(Activity from,
			String title, String msg) {
		// 使用官方的URL
		startShareActivity(from, title, msg, ShareConstants.URL_SHARE);
	}
	
	/**
	 * @param from
	 * @param title
	 * @param msg
	 * @param url
	 * @added 1.0
	 */
	public static void startShareActivity(Activity from,
		String title, String msg, String url) {
		if (null == from) {
			return ;
		}
		from.startActivityForResult(new Intent(from, ShareActivity.class)
			.putExtra(ShareBindBean.EXTRA_SHARE_BIND,
				new ShareBindBean().setTitle(title)
					.setMessage(msg)
					.setUrl(url)
					.setResId(R.drawable.ic_launcher)),
			0xFFF1);
	}
	
	/**
	 * @param from
	 * @param bean
	 * @added 1.0
	 */
	public static void startShareActivity(Activity from, ShareBindBean bean) {
		if (null == from || null == bean) {
			return ;
		}
		if (TextUtils.isEmpty(bean.getUrl())) {
			bean.setUrl(ShareConstants.URL_SHARE);
		}
		from.startActivityForResult(new Intent(from, ShareActivity.class)
				.putExtra(ShareBindBean.EXTRA_SHARE_BIND, bean),
			0xFFF1);
	}
	
	final String TAG = ShareActivity.class.getSimpleName();
	
	private ShareDispatcher shareDispatcher;
	
	private void resultOk() {
//		shareDispatcher.undispactch();
//		ToastUtil.showToast(this, R.string.share_tip_common_success);
//		setResult(RESULT_OK);
//		customBack();
	}
	
	private void resultCancel(boolean finish) {
		shareDispatcher.undispactch();
		setResult(RESULT_CANCELED);
		if (finish) {
			customBack();
		}
	}
	
	private View container_V;
	private ShareBindBean bean;
	@Override
	public int getLayoutResId() {
		return 0;
//		return R.layout.activity_share;
	}

	@Override
	public void initView(View view) {
//		container_V = findViewById(R.id.share_root_container_tv);
	}

	@Override
	public void initListener() {
//		bindClickListener(R.id.share_item_weibo_tv);
//		bindClickListener(R.id.share_item_weixin_tv);
//		bindClickListener(R.id.share_item_pengyouquan_tv);
//		
//		bindClickListener(R.id.share_cancel_btn);
	}

	@Override
	public void initData(View view, Bundle savedInstanceState) {
		bean = getIntent().getParcelableExtra(ShareBindBean.EXTRA_SHARE_BIND);
		if (null == bean) {
			customBack();
			return ;
		}
		
		shareDispatcher = new ShareDispatherImpl(ShareActivity.this);
	}

	@Override
	protected Handler createActivityHandler() {
		return null;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		shareDispatcher.undispactch();
	}
	
	@Override
	public void onClick(View v) {
		
//		switch (v.getId()) {
//		case R.id.share_cancel_btn:
//			customBack();
//			break;
//		case R.id.share_item_weibo_tv:
//			ShareByWeiboActivity.shareByWeiboActivity(this, bean);
//			break;
//		case R.id.share_item_weixin_tv:
//			WXEntryActivity.shareByWeixinActivity(this, bean);
//			break;
//		case R.id.share_item_pengyouquan_tv:
//			WXEntryActivity.shareByPengyouquanActivity(this, bean);
//			break;
//		}
//		
//		shareDispatcher.dispactch();
	}
	
	private final Rect touchBoundsRect = new Rect();
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		if (ev.getAction() == MotionEvent.ACTION_DOWN) {
			int y = (int) ev.getRawY();
			if (container_V.getGlobalVisibleRect(touchBoundsRect)){
				if (y < touchBoundsRect.top) {
					customBack();
					return true;
				}
			}
		}
		return super.dispatchTouchEvent(ev);
	}

	/**
	 * 内部处理分享回调的类
	 * @author YinYong
	 * @version 1.0
	 */
	private final class ShareDispatherImpl extends ShareDispatcher{
		protected ShareDispatherImpl(Context context) {
			super(context);
			
		}

		@Override
		protected void onResultOk(int type) {
			resultOk();
		}

		@Override
		protected void onResultCancel(int type, int reason) {
//			boolean flagFinish = false;
//			switch (reason) {
//				case ShareConstants.VAL_ERROR_CLIENT_UNEXIST: {
//					switch (type) {
//					case ShareConstants.TYPE_WEIBO:
//						break;
//					case ShareConstants.TYPE_WEIXIN:
//					case ShareConstants.TYPE_PENGYOUQUAN:
//						// 提示微信没有安装
//						ToastUtil.showToast(ShareActivity.this,
//								R.string.share_tip_weixin_unexist);
//						break;
//					}
//					break;
//				}
//				case ShareConstants.VAL_ERROR_CLIENT_UNSUPPORT: {
//					switch (type) {
//					case ShareConstants.TYPE_WEIBO:
//						break;
//					case ShareConstants.TYPE_WEIXIN:
//						ToastUtil.showToast(ShareActivity.this,
//								R.string.share_tip_weixin_unsupport);
//						break;
//					case ShareConstants.TYPE_PENGYOUQUAN:
//						ToastUtil.showToast(ShareActivity.this,
//								R.string.share_tip_weixin_pengyouquan_unsupport);
//						break;
//					}
//					break;
//				}
//				case ShareConstants.VAL_ERROR_USER_CANCELED: {
//					// tip nothing
//					break;
//				}
//				case ShareConstants.VAL_ERROR_UNKNOWN:
//				default: {
//					ToastUtil.showToast(ShareActivity.this,
//							R.string.share_tip_common_failed);
//					flagFinish = true;
//					break;
//				}
//			}
//			resultCancel(flagFinish);
		}
	}
}