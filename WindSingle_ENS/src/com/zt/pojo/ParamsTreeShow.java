package com.zt.pojo;

/**
 * 选择节点树
 * @author zhoutao
 * 2017年6月12日10:08:07
 */
public class ParamsTreeShow {
	private int id;//id
	private String name;//树名
	private int parent_type;//父节点
	private int modbustcp_type;//协议类型
	private String array_type;//数组类型
	private String code;//指令
	private String fw_code;//默认指令
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getParent_type() {
		return parent_type;
	}
	public void setParent_type(int parent_type) {
		this.parent_type = parent_type;
	}
	public int getModbustcp_type() {
		return modbustcp_type;
	}
	public void setModbustcp_type(int modbustcp_type) {
		this.modbustcp_type = modbustcp_type;
	}
	public String getArray_type() {
		return array_type;
	}
	public void setArray_type(String array_type) {
		this.array_type = array_type;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getFw_code() {
		return fw_code;
	}
	public void setFw_code(String fw_code) {
		this.fw_code = fw_code;
	}
}
