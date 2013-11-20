package com.wuxiadou.android.utils;

import java.util.HashMap;
import java.util.Map;

import com.wuxiadou.android.model.battle.BasicMove;

import android.annotation.SuppressLint;

@SuppressLint("UseSparseArrays")
public class CacheUtil {
	//网络获取
	public static Map<Integer, BasicMove> movesCache=
			new HashMap<Integer, BasicMove>();
	
}
