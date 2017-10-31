package com.zt.pojo;

import java.io.Serializable;
/**
 * 模式控制
 * @author zhoutao
 *2017年6月12日10:10:37
 */
public class ControlModelParam implements Serializable {
	private static final long serialVersionUID = 1L;
	private int id;
	private String name;
	private String bit_id;
	private String code;
	private int modbus_type;
	private int model_type;
	public int getId() {
		return id;
	}
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return super.toString();
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
	public String getBit_id() {
		return bit_id;
	}
	public void setBit_id(String bit_id) {
		this.bit_id = bit_id;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public int getModbus_type() {
		return modbus_type;
	}
	public void setModbus_type(int modbus_type) {
		this.modbus_type = modbus_type;
	}
	public int getModel_type() {
		return model_type;
	}
	public void setModel_type(int model_type) {
		this.model_type = model_type;
	}
}
