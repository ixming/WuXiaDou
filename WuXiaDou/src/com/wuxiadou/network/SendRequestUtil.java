package com.wuxiadou.network;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.ImageView;

import com.wuxiadou.network.listener.OnLoadListener;
import com.wuxiadou.utils.MD5;
import com.wuxiadou.utils.Utils;

public class SendRequestUtil
{
	
    private static final String TAG = SendRequestUtil.class.getSimpleName();
    private static LoadHelper loadHelper = null;
    private static LoadFileHelper loadFileHelper=null;
    public static final String POST="post";
    public static final String POST_FILE="post_file";
    public static final String PUT_FILE="put_file";
    
    public static final String GET="get";
    public static final String DELETE="delete";
    public static final String PUT="put";
    public static void sendRequest(Context context, Handler handler, OnLoadListener listener,
            int reqMode, String url, String json,String reqType)
    {
    	sendRequest(context, handler, listener, reqMode, url, json, reqType, null);
    }
    
    public static void sendRequest(Context context, Handler handler, OnLoadListener listener,
            int reqMode, String url, String json,String reqType, Bundle reqExtras)
    {
    	Log.w(TAG, " loadHelper---->"+(loadHelper==null)+"url---"+url);
        if (loadHelper == null)
        {
        	loadHelper = LoadHelper.getInstance();
        }
        ReqBean bean = new ReqBean();
        bean.setContext(context);
        bean.setUrl(url);
        bean.setReqMode(reqMode);
        //set post get put delete  请求类型
        bean.setReqType(reqType);
        //验证数据
		String md5 = MD5.digest2Str(Utils.ENCRYPTION_HEAD+json);
		Log.i(TAG,"json:"+md5);
		bean.setSign(md5);
        bean.setJson(json);
        bean.setLister(listener);
        if (null != reqExtras) {
        	bean.putReqExtras(reqExtras);
        }
        loadHelper.addRequest(bean);
    }
    
    //请求图片
    public static void sendRequestImg(ImageView iv,String url,String paramPath,Handler handler,Context context,int type)
    {
    	if(loadFileHelper==null)
    	{
    		loadFileHelper=LoadFileHelper.getInstance();
    	}
        ReqImgBean bean = new ReqImgBean();
        bean.setHandler(handler);
        bean.setParamPath(paramPath);
        bean.setImageView(iv);
        bean.setUrl(url);
        bean.setContext(context);
        bean.setHandler(handler);
        bean.setType(type);
        loadFileHelper.addRequest(bean);
    }
    public static void stopNet()
    {
        if (loadHelper != null)
        {
            loadHelper.stopNet();
            Log.w(TAG, " loadHelper.stopNet(); ");
        }
    }
}