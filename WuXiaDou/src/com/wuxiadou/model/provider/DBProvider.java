package com.wuxiadou.model.provider;

import java.util.ArrayList;

import android.content.ContentProvider;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentValues;
import android.content.Context;
import android.content.OperationApplicationException;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

public class DBProvider extends ContentProvider{
	private final static String TAG=DBProvider.class.getSimpleName();
    private SQLiteDatabase db;
    private DBHelper dbHelper;
    private static final UriMatcher uriMatcher;
    static{
    	uriMatcher=new UriMatcher(UriMatcher.NO_MATCH);

    }	
	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) 
	{
		int count=0;
		switch(uriMatcher.match(uri))
		{
			
			default:
				break;
		}
		return count;
	}
	@Override
	public String getType(Uri uri) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Uri insert(Uri uri, ContentValues values) {
		// TODO Auto-generated method stub
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		Uri _uri=null;
		switch(uriMatcher.match(uri))
		{
		
			default:
				break;
		}
		return _uri;
	}
	@Override
	public boolean onCreate() {
        Log.i(TAG, "onCreate ");
        Context context = getContext();
        dbHelper = new DBHelper(context, DBTable.DATABASE_VERSION);
        db = dbHelper.getWritableDatabase();
        return (db == null) ? false : true;
	}
	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		Cursor c=null;
		switch(uriMatcher.match(uri))
		{
			
			default:
				break;
		}
		return c;
	}
	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		int count =0;
		switch(uriMatcher.match(uri))
		{
			
			default:
				break;
		}
		return count;
	}
    public ContentProviderResult[] applyBatch(ArrayList<ContentProviderOperation> operations)
            throws OperationApplicationException
    {
    		db.beginTransaction();
        try
        {
            ContentProviderResult[] results = super.applyBatch(operations);
            db.setTransactionSuccessful();
            Log.i(TAG, "SurfDBProvider applyBatch count ----->>" + results.length);
            return results;
        }finally
        {
            db.endTransaction();
        }
    }
}
