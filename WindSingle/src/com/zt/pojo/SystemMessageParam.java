package com.zt.pojo;
/**
 * 协议
 * @author zhoutao
 * 2017年6月12日10:01:29
 */
public class SystemMessageParam {
	private String name;//参数名称
	private String addr;//地址位
	private int cof;//系数
	private int layouttype;//位置
	private String value;//参数值
	private String unit;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getAddr() {
		return addr;
	}
	public void setAddr(String addr) {
		this.addr = addr;
	}

	public int getCof() {
		return cof;
	}
	public void setCof(int cof) {
		this.cof = cof;
	}
	
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public int getLayouttype() {
		return layouttype;
	}
	public void setLayouttype(int layouttype) {
		this.layouttype = layouttype;
	}
	public String getUnit() {
		return unit;
	}
	public void setUnit(String unit) {
		this.unit = unit;
	}
	
}
