package com.zt.pojo;
/**
 * 协议
 * @author zhoutao
 * 2017年6月12日10:01:29
 */
public class ModbusApp {
	private String name;//参数名称
	private String addr;//地址位
	private int cof;//系数
	private String unit;//单位
	private String category;//
	private String sysremark;//是否为16进制
	private int modbus_type;//协议类型
	private int array_type;//数组类型
	private String point_id;
	private String rolecode;//是否可读可写
	private String array_name;//数组名称
	private String value;//参数值
	private String datalimitmin;
	private String datalimitmax;
	
	public String getDatalimitmin() {
		return datalimitmin;
	}
	public void setDatalimitmin(String datalimitmin) {
		this.datalimitmin = datalimitmin;
	}
	public String getDatalimitmax() {
		return datalimitmax;
	}
	public void setDatalimitmax(String datalimitmax) {
		this.datalimitmax = datalimitmax;
	}
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
	public String getUnit() {
		return unit;
	}
	public void setUnit(String unit) {
		this.unit = unit;
	}
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	public String getSysremark() {
		return sysremark;
	}
	public void setSysremark(String sysremark) {
		this.sysremark = sysremark;
	}
	public int getModbus_type() {
		return modbus_type;
	}
	public void setModbus_type(int modbus_type) {
		this.modbus_type = modbus_type;
	}
	public int getArray_type() {
		return array_type;
	}
	public void setArray_type(int array_type) {
		this.array_type = array_type;
	}
	public String getPoint_id() {
		return point_id;
	}
	public void setPoint_id(String point_id) {
		this.point_id = point_id;
	}
	public String getArray_name() {
		return array_name;
	}
	public void setArray_name(String array_name) {
		this.array_name = array_name;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public String getRolecode() {
		return rolecode;
	}
	public void setRolecode(String rolecode) {
		this.rolecode = rolecode;
	}
	
}
