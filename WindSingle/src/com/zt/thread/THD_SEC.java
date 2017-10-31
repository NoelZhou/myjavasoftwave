package com.zt.thread;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import javax.swing.JTable;
import com.zt.pojo.ModbusApp;
import com.zt.pojo.SaveMessage;

public class THD_SEC extends Thread {
	private boolean _run = true;
	private JTable jTable;
	private Vector<List<Object>> vect;
	private String name;
	private SaveMessage sMessage;
	private boolean boolean1;
	private boolean boolRun = true;
	

	public boolean getBoolRun() {
		return boolRun;
	}

	public void setBoolRun(boolean boolean1) {
		this.boolRun = boolean1;
	}

	public THD_SEC() {
		this.sMessage = SaveMessage.getInstance();
	}

	public void stopThread() {
		this._run = !_run;
	}

	@Override
	public void run() {
		while (_run) {
			while(boolRun){
				try {
					refleshTable(sMessage.getNewModbusApp(), sMessage.getModbustcp(),name, vect, jTable);
					sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	public void refleshTable(Map<String, List<ModbusApp>> maps, int modbustcp,String name,Vector<List<Object>> vect,
			JTable jTable) {
		if(name==null){
			name="网侧监控参数";
		}
		vect.removeAllElements();
		List<Vector<Object>> mLists = new ArrayList<>();
		int num = 1;
		for (String key : maps.keySet()) {
			if (name.equals(key)) {
				for (ModbusApp modbusApp : maps.get(key)) {
					if (modbusApp.getRolecode().equals("W")) {
						boolean1= true;
					}else{
						boolean1 = false;
					}
					if (!modbusApp.getName().equals("")) {
						Vector<Object> list = new Vector<Object>();
						list.add(num);
						list.add(modbusApp.getAddr());
						list.add(modbusApp.getName());
						list.add(modbusApp.getValue());
						list.add(modbusApp.getUnit());
						list.add(modbusApp.getSysremark());
						list.add(modbusApp.getCategory());
						
						list.add(modbusApp.getDatalimitmin());
						list.add(modbusApp.getDatalimitmax());
						list.add(modbusApp.getCof());
						num++;
						mLists.add(list);
					}
				}
			}
		}
		for (int i = 0; i < mLists.size(); i++) {
			vect.addElement(mLists.get(i));
		}
		List<Object> ltemp = new ArrayList<>();
		ltemp.add(boolean1);
		vect.add(vect.size(),ltemp);
		jTable.validate();
		jTable.updateUI();
	}

	public void setjTableAndNodeName(JTable table, String name) {
		this.jTable = table;
		this.name = name;
	}

	public void setjTableAndNodeArray(JTable table) {
		this.jTable = table;
	}

	public void setJtableAndVect(JTable table, Vector<List<Object>> vect2) {
		this.jTable = table;
		this.vect = vect2;
	}
}
