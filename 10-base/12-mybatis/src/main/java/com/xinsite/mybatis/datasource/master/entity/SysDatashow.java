package com.xinsite.mybatis.datasource.master.entity;

import java.io.Serializable;

/**
 * author name: zhangxiaxin
 * create time: 2019-10-29
 */ 
public class SysDatashow implements Serializable {

	private long id;//主键

	private String dataType;//数据源类型，code:编码表；datasource：系统源

	private String dataKey;//数据源标识

	private long dataId;//数据源表id，编码表是sys_code，系统数据源表都有可能

	private String disabled;//是否可以选择

	private int isshow;//是否显示


	public void setId(long id){
		this.id=id;
	}

	public long getId(){
		return id;
	}

	public void setDataType(String dataType){
		this.dataType=dataType;
	}

	public String getDataType(){
		return dataType;
	}

	public void setDataKey(String dataKey){
		this.dataKey=dataKey;
	}

	public String getDataKey(){
		return dataKey;
	}

	public void setDataId(long dataId){
		this.dataId=dataId;
	}

	public long getDataId(){
		return dataId;
	}

	public void setDisabled(String disabled){
		this.disabled=disabled;
	}

	public String getDisabled(){
		return disabled;
	}

	public void setIsshow(int isshow){
		this.isshow=isshow;
	}

	public int getIsshow(){
		return isshow;
	}

}
