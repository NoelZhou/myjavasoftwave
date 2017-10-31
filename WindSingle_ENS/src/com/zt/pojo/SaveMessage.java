package com.zt.pojo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SaveMessage {
	
	private String ipAddr;
	private String com;
	private int modbustcp;
	private String strValue = new String();
	private Map<String, List<ModbusApp>> maps = new HashMap<>();
	private  List<SystemMessageParam> lMessageParams = new ArrayList<SystemMessageParam>();
	private List<ModbusBit> modbusBits = new ArrayList<>();
	private List<ModbusVar> modbusVars = new ArrayList<>();
	private SaveMessage() {}  //饿汉式单例类.在类初始化时，已经自行实例化   
    private static final SaveMessage single = new SaveMessage();  
    //静态工厂方法   
    public static SaveMessage getInstance() {  
        return single;  
    }  
	public void setIpAndCom(String ipAddr, String com) {
		this.ipAddr = ipAddr;
		this.com = com;
	}
	public String[] getIpAndCom(){
		String[] strings = {ipAddr,com};
		return  strings;
	}
	
	public void setNewModbusApp(Map<String, List<ModbusApp>> maps) {
		this.maps = maps;
	}
	public Map<String, List<ModbusApp>> getNewModbusApp(){
		return maps;
	}

	public void setModbustcp(int modbustcp) {
		this.modbustcp = modbustcp;
	}
	public int getModbustcp() {
		return modbustcp;
	}


	public void setStrValue(String strValue) {
		this.strValue = strValue;
	}
	public String getStrValue(){
		return strValue;
	}


	public void setSystemMessage(List<SystemMessageParam> lMessageParams) {
		this.lMessageParams = lMessageParams;
	}
	public List<SystemMessageParam>  getSystemMessage(){
		return lMessageParams;
	}
	public List<ModbusBit> getModbusBits() {
		return modbusBits;
	}
	public void setModbusBits(List<ModbusBit> modbusBits) {
		this.modbusBits = modbusBits;
	}
	public List<ModbusVar> getModbusVars() {
		return modbusVars;
	}
	public void setModbusVars(List<ModbusVar> modbusVars) {
		this.modbusVars = modbusVars;
	}

}
