package com.wuxiadou.android.model;

import java.util.List;

import org.xml.sax.Attributes;

public class Persona {
	public Persona()
	{
		
	}
	public Persona(Attributes attributes)
	{
		id=Long.parseLong(attributes.getValue("id"));
		name=attributes.getValue("name");
		level=attributes.getValue("level");
		exp=attributes.getValue("exp");
		describe=attributes.getValue("describe");
		roleType=Integer.parseInt(attributes.getValue("roleType"));
		guildId=Long.parseLong(attributes.getValue("guildId"));
		//List<Skill> 
		
	}
	
	private long id;
	/**
	 * 名字
	 */
	private String name;
	/**
	 * 图片位置
	 */
	private String imagePath;
	/**
	 * 等级
	 */
	private String level;
	/**
	 * 经验 
	 */
	private String exp;
	/**
	 * 描述 
	 */
	private String describe;
	/**
	 * 所属门派
	 */
	private long guildId;
	/**
	 * 角色类型
	 */
	private int roleType;
	/**
	 * 拥有技能
	 */
	private List<Skill> skills;
	/**
	 * 战斗力
	 */

}
