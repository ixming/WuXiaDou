package com.wuxiadou.model.provider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
	private final static String TAG=DBHelper.class.getSimpleName(); 
	private Context mContext;
	public DBHelper(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
		// TODO Auto-generated constructor stub
	}
	public DBHelper(Context context,int version)
	{
		super(context,DBTable.DATABASE_NAME,null,version);
		mContext=context;
	}
	@Override
	public void onCreate(SQLiteDatabase db) {
		sql(db);
	}
	private void sql(SQLiteDatabase db) {
		//创建表

	}
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		
	}

}
