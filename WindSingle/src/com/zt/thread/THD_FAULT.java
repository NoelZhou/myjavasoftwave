package com.zt.thread;


import java.util.List;
import java.util.Vector;
import javax.swing.JTable;

import com.zt.common.GlobalUntils;
import com.zt.common.ReadModbusXml;
import com.zt.frame.SysSubFrame.DFrame;
import com.zt.pojo.ErrorDetail;
import com.zt.pojo.ModbusBit;
import com.zt.pojo.SaveMessage;

public class THD_FAULT extends Thread {
	 private boolean _run = true;
	private SaveMessage sMessage;
	private JTable jTable;
	private Vector<List<Object>> vect;
	private String actionCommand;
	private DFrame dFrame;
	public THD_FAULT() {
		this.sMessage = SaveMessage.getInstance();
	}

	public void stopThread(){
		this._run = !_run;
	}
	@Override
	public void run() {
		List<ModbusBit> lBits = sMessage.getModbusBits();
		List<Object> objects = new ReadModbusXml().setLists(ErrorDetail.class, GlobalUntils.PATH_ERROR);
		while (_run) {
			try {
				sleep(1000);
				analysisState(sMessage.getStrValue().split(","),lBits,objects);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	public void analysisState(String[] values,List<ModbusBit> lBits,List<Object> objects){
		vect.removeAllElements();
		Vector<List<Object>> vLists = new Vector<List<Object>>();
		if(actionCommand==null){
			actionCommand="74";
		}
		for (Object object : objects) {
			ErrorDetail eDetail = (ErrorDetail)object;
			if(eDetail.getModbus_type()==sMessage.getModbustcp()){
				String bitStr = getbits(Integer.parseInt(values[eDetail.getAddr()]));
//				if(eDetail.getAddr()==Integer.parseInt(actionCommand)){
					if(eDetail.getBitid()==""){
						for(int i=15;i>=0;i--){
							Vector<Object> list = new Vector<Object>();
							for (ModbusBit modbusBit : lBits) {
								String addr = modbusBit.getAddr();
								if(addr!=null){
									if(addr.equals(String.valueOf(eDetail.getAddr()))&&modbusBit.getBit_id()==15-i){
										list.add(16-i);
										list.add(addr);
										list.add(modbusBit.getVar1());
										list.add(bitStr.substring(i, i+1));
									}
								}
							}
							vLists.addElement(list);
						}
					}else{
						String[] strings = eDetail.getBitid().split(",");
						for(int i=0;i<strings.length;i++){
							List<Object> list = new Vector<Object>();
							for (ModbusBit modbusBit : lBits) {
								String addr = modbusBit.getAddr();
								if(addr!=null){
									int bitNum = Integer.parseInt(strings[i]);
									if(addr.equals(String.valueOf(eDetail.getAddr()))&&modbusBit.getBit_id()==bitNum){
										list.add(i+1);
										list.add(addr);
										list.add(modbusBit.getVar1());
										list.add(bitStr.substring(bitNum, bitNum+1));
									}
								}
							}
							vLists.addElement(list);
						}
					}
				}
//			}
		}
		dFrame.sendButtonState(vLists);
		for(int i=0;i<vLists.size();i++){
			List<Object> list = vLists.get(i);
			if(actionCommand.equals(list.get(1))){
				vect.addElement(list);
			}
		}
		jTable.validate();
		jTable.updateUI();
	}
	
	public ModbusBit getModbusBit(List<ModbusBit> lBits,ErrorDetail eDetail){
		for (ModbusBit modbusBit : lBits) {
			if(modbusBit.getAddr().equals(String.valueOf(eDetail.getAddr()))){
				return modbusBit;
			}
		}
		return null;
	}
	
	public String getbits(int shortvalue) {
		short sv = (short) shortvalue;
		String bstr = Integer.toBinaryString(sv);
		bstr = bstr.length() < 16 ? "0000000000000000".substring(bstr.length()) + bstr
				: bstr.substring(bstr.length() - 16, bstr.length());// 二进制16位补零
		return bstr;
	}
	
	public void setTableAndVect(JTable table, Vector<List<Object>> vect) {
		this.jTable = table;
		this.vect = vect;
	}

	public void setButtonCommandTag(String actionCommand) {
		this.actionCommand = actionCommand;
	}

	public void setDFrame(DFrame dFrame) {
		this.dFrame = dFrame;
	}
}
