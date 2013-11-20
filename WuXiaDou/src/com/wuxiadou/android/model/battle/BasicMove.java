package com.wuxiadou.android.model.battle;

import com.wuxiadou.android.utils.Utils;

/**
 * 基础招式，如杀闪...
 * @author Yin Yong
 * @version 1.0
 */
public class BasicMove implements Cloneable {

	// 基础招式对应的ID
	private long id;
	// 基础招式的名称
	private String name;
	// 基本招式对应的属性
	private long attrId;
	// 基本招式对应的属性
	private String attrName;
	// 基本招式的介绍
	private String describe;
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public long getAttrId() {
		return attrId;
	}

	public void setAttrId(long attrId) {
		this.attrId = attrId;
	}

	public String getAttrName() {
		return attrName;
	}

	public void setAttrName(String attrName) {
		this.attrName = attrName;
	}

	public String getDescribe() {
		return describe;
	}

	public void setDescribe(String describe) {
		this.describe = describe;
	}

	public BasicMove copyOf() {
		try {
			return clone();
		} catch (Exception e) {
			return null;
		}
	}
	
	@Override
	public boolean equals(Object o) {
		if (super.equals(o)) {
			return true;
		}
		if (!(o instanceof BasicMove)) {
			return false;
		}
		BasicMove other = (BasicMove) o;
		return other.id == id && other.attrId == attrId;
	}
	
	@Override
	public int hashCode() {
		int hash = (int) id;
		hash = hash * 31 + Utils.hashOfString(name);
		hash = hash * 31 + (int) attrId;
		hash = hash * 31 + Utils.hashOfString(attrName);
		return hash;
	}
	
	@Override
	protected BasicMove clone() throws CloneNotSupportedException {
		return (BasicMove) super.clone();
	}
}
