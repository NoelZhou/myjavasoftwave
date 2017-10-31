package com.zt.pojo;

public class ErrorDetail {
	private int id;
	private int modbus_type;
	private int error_type;
	public int getError_type() {
		return error_type;
	}
	public void setError_type(int error_type) {
		this.error_type = error_type;
	}
	private String bitid;
	private int addr;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getModbus_type() {
		return modbus_type;
	}
	public void setModbus_type(int modbus_type) {
		this.modbus_type = modbus_type;
	}
	public String getBitid() {
		return bitid;
	}
	public void setBitid(String bitid) {
		this.bitid = bitid;
	}
	public int getAddr() {
		return addr;
	}
	public void setAddr(int addr) {
		this.addr = addr;
	}
	
}
