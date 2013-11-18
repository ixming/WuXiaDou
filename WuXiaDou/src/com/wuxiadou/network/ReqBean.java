package com.wuxiadou.network;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcel;
import android.os.Parcelable;

import com.wuxiadou.network.listener.OnLoadListener;

public class ReqBean implements Parcelable
{
    // 当前的上下文
    private Context mContext = null;
    // 下载地址
    private String url = "";
    // 请求类型，post put get delete
    private String reqType = "";
    // 请求标示
    private int reqMode = 0;
    // 地址附带信息
    private String json = "";
    
    private Object obj = null;
    
	private int index = -1;
    
    private String sign = "";
    
	//handler 
    private Handler handler =null;
    
    //handler 
    private OnLoadListener lister =null;
    
    private String sourceUrl = ""; 
    
    private Bundle reqExtras; 

    public String getSourceUrl() {
		return sourceUrl;
	}
	public void setSourceUrl(String sourceUrl) {
		this.sourceUrl = sourceUrl;
	}
	public Context getmContext() {
		return mContext;
	}
	public void setmContext(Context mContext) {
		this.mContext = mContext;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

    public String getSign() {
		return sign;
	}
	public void setSign(String sign) {
		this.sign = sign;
	}
	public Handler getHandler() {
		return handler;
	}

	public void setHandler(Handler handler) {
		this.handler = handler;
	}

	public OnLoadListener getLister() {
		return lister;
	}

	public void setLister(OnLoadListener lister) {
		this.lister = lister;
	}

	public Context getContext()
    {
        return mContext;
    }

    public void setContext(Context mContext)
    {
        this.mContext = mContext;
    }

    public String getUrl()
    {
        return url;
    }

    public void setUrl(String url)
    {
        this.url = url;
    }

    public String getReqType()
    {
        return reqType;
    }

    public void setReqType(String reqType)
    {
        this.reqType = reqType;
    }

    public int getReqMode()
    {
        return reqMode;
    }

    public void setReqMode(int reqMode)
    {
        this.reqMode = reqMode;
    }

    public String getJson()
    {
        return json;
    }

    public void setJson(String json)
    {
        this.json = json;
    }

    @Override
    public String toString()
    {
        return " url : " + url + " reqType : " + reqType + " json: " + json;
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {

    }

	public void setObj(Object obj) {
		this.obj = obj;
	}

	public Object getObj() {
		return obj;
	}
	
	public Bundle getReqExtras() {
		return reqExtras;
	}
	
	public void putReqExtras(Bundle extras) {
		if (null == reqExtras) {
			reqExtras = new Bundle();
		}
		reqExtras.putAll(extras);
	}
	
	 @Override
    public boolean equals(Object obj)
    {
        if (obj.getClass().getName().equals(this.getClass().getName()))
        {
            if (obj instanceof ReqBean)
            {
            	ReqBean temp = (ReqBean) obj;
            	if (    temp.getUrl().equalsIgnoreCase(this.getUrl()) &&
            			temp.getReqMode() == this.getReqMode() && temp.getJson().equals(this.getJson()))
                    {
                       return true;
                    }
            }
        }
        return false;
    }
}