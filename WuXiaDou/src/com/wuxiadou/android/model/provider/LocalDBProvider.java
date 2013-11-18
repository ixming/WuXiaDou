package com.frameworkexample.android.model.provider;

import org.ixming.android.model.provider.BaseDBProvider;

import android.content.Context;
import android.database.sqlite.SQLiteOpenHelper;

public class LocalDBProvider extends BaseDBProvider {

	public static final String AUTHORITY = "com.frameworkexample.android.model.db.provider";
	
	/*package*/static final String DATABASE_NAME = "wuxiadou.db";
	
	/*package*/static int VERSION_1_0 = 1;
	
	/**
	 * 给数据库框架提供AUTHORITY
	 */
	@Override
	protected String getAuthority() {
		return AUTHORITY;
	}

	/**
	 * 给数据库框架提供一个新的SQLiteOpenHelper对象
	 */
	@Override
	protected SQLiteOpenHelper provideSQLiteOpenHelper(Context context) {
		return new LocalDBHelper(context, getCurrentVersion());
	}
	
	/**
	 * 获得当前的数据库版本。<br/><br/>
	 * 此处需要持续更新。
	 */
	private int getCurrentVersion() {
		return VERSION_1_0;
	}

}
