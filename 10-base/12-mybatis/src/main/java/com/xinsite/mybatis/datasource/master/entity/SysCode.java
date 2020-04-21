package com.xinsite.mybatis.datasource.master.entity;

import java.io.Serializable;

/**
 * author name: 系统管理员
 * create time: 2020-02-21 02:05
 * object name: 编码表
 */ 
public class SysCode implements Serializable {

	private int id;              //主键

	private int pid;             //父结点Id

	private int codetypeId;      //编码类型Id

	private String text;         //编码名称

	private String value;        //编码值

	private String expanded;     //默认展开，true、false

	private String remark;       //备注

	private int serialcode;      //排序号

	private int isdel;           //是否删除，0：未删除；1：删除

	private int issys;           //是否系统编码，1：不可删除


	//设置_主键
	public void setId(int id){
		this.id=id;
	}

	//获取_主键
	public int getId(){
		return id;
	}

	//设置_父结点Id
	public void setPid(int pid){
		this.pid=pid;
	}

	//获取_父结点Id
	public int getPid(){
		return pid;
	}

	//设置_编码类型Id
	public void setCodetypeId(int codetypeId){
		this.codetypeId=codetypeId;
	}

	//获取_编码类型Id
	public int getCodetypeId(){
		return codetypeId;
	}

	//设置_编码名称
	public void setText(String text){
		this.text=text;
	}

	//获取_编码名称
	public String getText(){
		return text;
	}

	//设置_编码值
	public void setValue(String value){
		this.value=value;
	}

	//获取_编码值
	public String getValue(){
		return value;
	}

	//设置_默认展开，true、false
	public void setExpanded(String expanded){
		this.expanded=expanded;
	}

	//获取_默认展开，true、false
	public String getExpanded(){
		return expanded;
	}

	//设置_备注
	public void setRemark(String remark){
		this.remark=remark;
	}

	//获取_备注
	public String getRemark(){
		return remark;
	}

	//设置_排序号
	public void setSerialcode(int serialcode){
		this.serialcode=serialcode;
	}

	//获取_排序号
	public int getSerialcode(){
		return serialcode;
	}

	//设置_是否删除，0：未删除；1：删除
	public void setIsdel(int isdel){
		this.isdel=isdel;
	}

	//获取_是否删除，0：未删除；1：删除
	public int getIsdel(){
		return isdel;
	}

	//设置_是否系统编码，1：不可删除
	public void setIssys(int issys){
		this.issys=issys;
	}

	//获取_是否系统编码，1：不可删除
	public int getIssys(){
		return issys;
	}

}
