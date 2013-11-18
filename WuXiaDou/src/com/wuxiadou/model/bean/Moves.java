package com.wuxiadou.model.bean;

import java.util.Map;

public class Moves {
	private long id;
	//基础招式名
	private String name;
	//基础招式key
	private String key_name;
	//与id类似为了方便修改定义的
	private int key;
	//描述
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
	public String getKey_name() {
		return key_name;
	}
	public void setKey_name(String key_name) {
		this.key_name = key_name;
	}
	public int getKey() {
		return key;
	}
	public void setKey(int key) {
		this.key = key;
	}
	public String getDescribe() {
		return describe;
	}
	public void setDescribe(String describe) {
		this.describe = describe;
	}

	@Override
	public String toString() {
		return "Moves [id=" + id + ", name=" + name + ", key_name=" + key_name
				+ ", key=" + key + ", describe=" + describe + "]";
	}
	
}
