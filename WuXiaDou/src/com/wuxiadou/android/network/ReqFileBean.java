package com.wuxiadou.android.network;

import android.content.Context;
import android.os.Handler;

public class ReqFileBean 
{
    // 当前的上下文
    private Context mContext = null;
    // 正文的id
    private String newsId = "";
    // 图片地址的URL
    private String imgUrl = "";
    // 图片存储地址
    private String paramPath = "";
    // 如用到listView或者其他组件的下载,对象下标
    private int index = -1;
    // 需要的图片宽度
    private int imageWidth = 0;
    // 需要的图片高度
    private int imageHeight = 0;
    // 下载图片需要的json
    private String json = "";
    // 主线程创建handler实例
    private Handler hanlder = null;
    // 主线程创建监听器
 //   private OnFileLoadListener listener = null;
    public Handler getHanlder()
    {
        return hanlder;
    }
    public void setHanlder(Handler hanlder)
    {
        this.hanlder = hanlder;
    }
    public Context getContext()
    {
        return mContext;
    }
    public void setContext(Context mContext)
    {
        this.mContext = mContext;
    }
    public String getNewsId()
    {
        return newsId;
    }
    public void setNewsId(String newsId)
    {
        this.newsId = newsId;
    }
    public String getImgUrl()
    {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl)
    {
        this.imgUrl = imgUrl;
    }
    public String getParamPath()
    {
        return paramPath;
    }

    public void setParamPath(String paramPath)
    {
        this.paramPath = paramPath;
    }

    public int getIndex()
    {
        return index;
    }

    public void setIndex(int index)
    {
        this.index = index;
    }

    public int getImageWidth()
    {
        return imageWidth;
    }

    public void setImageWidth(int imageWidth)
    {
        this.imageWidth = imageWidth;
    }

    public int getImageHeight()
    {
        return imageHeight;
    }

    public void setImageHeight(int imageHeight)
    {
        this.imageHeight = imageHeight;
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
    public boolean equals(Object obj) {

        if (obj.getClass().getName().equals(this.getClass().getName()))
        {
            if (obj instanceof ReqFileBean)
            {
            	ReqFileBean temp = (ReqFileBean) obj;
            	if (temp.getImgUrl().equals(this.getImgUrl()))
                {
                   return true;
                }
            }
        }
        return false;
    
    }
}