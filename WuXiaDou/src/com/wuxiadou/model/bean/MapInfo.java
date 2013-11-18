package com.wuxiadou.model.bean;

import org.xml.sax.Attributes;

public class MapInfo {
	public MapInfo()
	{
	}
	public MapInfo(Attributes attributes)
	{
		id=Long.parseLong(attributes.getValue("id"));
		mapId=Long.parseLong(attributes.getValue("mapId"));
		name=attributes.getValue("name");
		nodeType=Integer.parseInt(attributes.getValue("nodeType"));
		imagePath=attributes.getValue("imagePath");
		describe=attributes.getValue("describe");
	}
	private long id;
	/**
	 * 父级地图
	 */
	private long mapId;
	/**
	 * 地图名
	 */
	private String name;
	/**
	 * 地图节点 0世界1区域2区域子地图
	 */
	private int nodeType;
	/**
	 * 地图图片路径
	 */
	private String imagePath;
	/**
	 * 地图描述信息
	 */
	private String describe;
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public long getMapId() {
		return mapId;
	}
	public void setMapId(long mapId) {
		this.mapId = mapId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getNodeType() {
		return nodeType;
	}
	public void setNodeType(int nodeType) {
		this.nodeType = nodeType;
	}
	public String getDescribe() {
		return describe;
	}
	public void setDescribe(String describe) {
		this.describe = describe;
	}
	public String getImagePath() {
		return imagePath;
	}
	public void setImagePath(String imagePath) {
		this.imagePath = imagePath;
	}
}
