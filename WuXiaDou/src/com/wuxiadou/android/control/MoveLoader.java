package com.wuxiadou.android.control;

import java.io.InputStream;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.ixming.utils.NumberUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.wuxiadou.android.R;
import com.wuxiadou.android.model.battle.BasicMove;
import com.wuxiadou.android.utils.LogUtils;

import android.content.Context;
import android.util.SparseArray;

/**
 * 加载并管理基本招式，及招式属性，组合界面显示，相关初始化操作的类。
 * @author Yin Yong
 * @version 1.0
 */
public class MoveLoader {
	private static final String TAG = MoveLoader.class.getSimpleName();
	
	private static MoveLoader mInstance;
	public static void init(Context context) {
		mInstance = new MoveLoader(context);
	}
	
	public static MoveLoader getInstance() {
		return mInstance;
	}
	
	// application context
	private Context mContext;
	private ArrayList<BasicMove> mMoveList;
	private SparseArray<BasicMove> mMoveMap;
	private String[] mMoveAlias;
	private MoveLoader(Context context) {
		mContext = context;
		loadData();
		mMoveAlias = mContext.getResources().getStringArray(R.array.battle_move_placeholders);
	}
	
	public int getBasicMoveCount() {
		return mMoveList.size();
	}
	
	public ArrayList<BasicMove> getOriginalBasicMoveList() {
		ArrayList<BasicMove> copy = new ArrayList<BasicMove>(mMoveList.size());
		for (int i = 0; i < mMoveList.size(); i++) {
			copy.add(mMoveList.get(i).copyOf());
		}
		return copy;
	}
	
	public String[] getOriginalBasicMoveNames() {
		String[] copy = new String[mMoveList.size()];
		for (int i = 0; i < mMoveList.size(); i++) {
			copy[i] = mMoveList.get(i).getName();
		}
		return copy;
	}
	
	public String[] getOriginalBasicMoveNamesWithAttr() {
		String[] copy = new String[mMoveList.size()];
		for (int i = 0; i < mMoveList.size(); i++) {
			copy[i] = toAppearanceString(mMoveList.get(i));
		}
		return copy;
	}
	
	/**
	 * 随机获取单个基本招式
	 */
	public BasicMove randomGetBasicMove() {
		final int count = mMoveList.size();
		double rand = Math.random() * count;
		int randomIndex = Math.max(Math.min((int) rand, count - 1), 0);
		return mMoveList.get(randomIndex);
	}
	
	/**
	 * 随机获取含招数个数的基础招式列表
	 */
	public ArrayList<BasicMove> randomGetBasicMoveList() {
		final int count = mMoveList.size();
		ArrayList<BasicMove> copy = new ArrayList<BasicMove>(count);
		for (int i = 0; i < count; i++) {
			copy.add(randomGetBasicMove());
		}
		return copy;
	}
	
	/**
	 * 将技能转化为UI中显示的字符串。
	 */
	public String toAppearanceString(BasicMove basicMove) {
		return mContext.getResources().getString(R.string.battle_move_apparence,
				basicMove.getName(), basicMove.getAttrName());
	}
	
	/**
	 * 当玩家没有对招式进行选择设置时，position对应位置相应的替换字符。
	 */
	public String noSetBasicMoveAlias(int position) {
		return mMoveAlias[Math.max(Math.min(position, mMoveAlias.length - 1), 0)];
	}
	
	private void loadData() {
		final String LOCAL_FILENAME = "move/data.wxd";
		// nodes
		//final String ROOT_NODE = "resource";
		//final String MOVE_LIST_NODE = "moves";
		final String MOVE_NODE = "move";
		//final String MOVE_ATTR_LIST_NODE = "attrs";
		//final String MOVE_ATTR_NODE = "attr";
		// attributes
		final String ATTR_ID = "id";
		final String ATTR_NAME = "name";
		final String ATTR_ATTRID = "attrid";
		final String ATTR_ATTRNAME = "attrname";
		final String ATTR_DESCRIBE = "describe";
		
		DocumentBuilderFactory docBuilderFactory = null;
		DocumentBuilder docBuilder = null;
		Document dom = null;
		InputStream is = null;
		try {
			docBuilderFactory = DocumentBuilderFactory.newInstance();
			docBuilder = docBuilderFactory.newDocumentBuilder();
			is = mContext.getResources().getAssets().open(LOCAL_FILENAME);
			dom = docBuilder.parse(is);
			Element entry = dom.getDocumentElement();
			NodeList nlMoves = entry.getElementsByTagName(MOVE_NODE);
			if (null == nlMoves || nlMoves.getLength() <= 0) {
				throw new IllegalStateException("load moves error, no move mode in xml file!") ;
			}
			Element entryItem = null;
			final int count = nlMoves.getLength();
			mMoveList = new ArrayList<BasicMove>(count);
			mMoveMap = new SparseArray<BasicMove>(count);
			for (int m = 0; m < count; m++) {
				entryItem = (Element) nlMoves.item(m);
				int id = NumberUtils.getInt(entryItem.getAttribute(ATTR_ID));
				BasicMove basicMove = new BasicMove();
				basicMove.setId(id);
				basicMove.setName(entryItem.getAttribute(ATTR_NAME));
				basicMove.setDescribe(entryItem.getAttribute(ATTR_DESCRIBE));
				
				basicMove.setAttrId(NumberUtils.getLong(entryItem.getAttribute(ATTR_ATTRID)));
				basicMove.setAttrName(entryItem.getAttribute(ATTR_ATTRNAME));
				// add to list
				mMoveList.add(basicMove);
				// add to map
				mMoveMap.put(id, basicMove);
			}
		} catch (Throwable e) {
			LogUtils.e(TAG, "loadData Exception: " + e.getMessage());
			throw new RuntimeException(e);
		} finally {
			if (null != is) {
				try {
					is.close();
				} catch (Exception ex) { }
			}
			dom = null;
			docBuilder = null;
			docBuilderFactory = null;
		}
	}
	
}
