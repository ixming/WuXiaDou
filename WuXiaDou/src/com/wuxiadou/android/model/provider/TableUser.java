package com.frameworkexample.android.model.provider;

import android.provider.BaseColumns;

public interface TableUser extends BaseColumns {

	String TABLE_NAME = "edu_user";
	
	String COLUMN_USERID = "userId";
	
	String COLUMN_LOGINNAME = "loginName";
	
	String COLUMN_NICKNAME = "nickName";
	
}
