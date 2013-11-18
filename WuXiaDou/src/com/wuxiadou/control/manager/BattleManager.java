package com.wuxiadou.control.manager;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.Handler;

import com.wuxiadou.model.bean.Moves;

public class BattleManager extends BaseManager {
	public List<Moves> ownMovesList = new ArrayList<Moves>();
	public List<Moves> enemyMovesList = new ArrayList<Moves>();

	public BattleManager(Context context, Handler handler) {
		super(context, handler);
		for (int i = 0; i < 5; i++) {
			ownMovesList.add(null);
			enemyMovesList.add(null);
		}
	}

}
