package com.wuxiadou.android.network.listener;

import com.wuxiadou.android.network.ReqBean;

public interface OnLoadListener
{
    public void onSuccess(Object obj, ReqBean reqMode);

    public void onError(Object obj, ReqBean reqMode);
}