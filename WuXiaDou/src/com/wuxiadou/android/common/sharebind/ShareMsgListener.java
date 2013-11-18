package com.frameworkexample.android.common.sharebind;

interface ShareMsgListener {

	public void sendSuccess(String result);
    
    public void sendFailure(String errorMsg);
	
}
