package com.wuxiadou.utils;

import java.util.HashMap;
import java.util.Map;

import android.annotation.SuppressLint;
import com.wuxiadou.model.bean.Moves;

@SuppressLint("UseSparseArrays")
public class CacheUtil {
	//网络获取
	public static Map<Integer, Moves> movesCache=
			new HashMap<Integer, Moves>();
	
}
