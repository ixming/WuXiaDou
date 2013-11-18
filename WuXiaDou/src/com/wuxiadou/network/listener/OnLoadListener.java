package com.wuxiadou.network.listener;

import com.wuxiadou.network.ReqBean;


public interface OnLoadListener
{
    public void onSuccess(Object obj, ReqBean reqMode);

    public void onError(Object obj, ReqBean reqMode);
}