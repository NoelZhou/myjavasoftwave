package com.zt.thread;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableColumnModel;

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
			while (boolRun) {
				try {
					refleshTable(sMessage.getNewModbusApp(), sMessage.getModbustcp(), name, vect, jTable);
					sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	public void refleshTable(Map<String, List<ModbusApp>> maps, int modbustcp, String name, Vector<List<Object>> vect,
			JTable jTable) {
		if (name == null) {
			name = "系统状态参数";
		}
		vect.removeAllElements();
		List<Vector<Object>> mLists = new ArrayList<>();
		int num = 1;
		if (name.equals("柜体参数")) {
			List<ModbusApp> modbusA = maps.get("A相参数");
			List<ModbusApp> modbusB = maps.get("B相参数");
			List<ModbusApp> modbusC = maps.get("C相参数");
			for (int i = 0; i < modbusA.size(); i++) {
				ModbusApp modbusAppA = modbusA.get(i);
				ModbusApp modbusAppB = modbusB.get(i);
				ModbusApp modbusAppC = modbusC.get(i);
				if (modbusAppA.getRolecode().equals("W")) {
					boolean1 = true;
				} else {
					boolean1 = false;
				}
				if (!modbusAppA.getName().equals("")&&!modbusAppA.getName().equals("备用")) {
					Vector<Object> list = new Vector<Object>();
					list.add(0, num);
					list.add(1, modbusAppA.getName());
					list.add(2, modbusAppA.getValue());
					list.add(3, modbusAppB.getValue());
					list.add(4, modbusAppC.getValue());
					list.add(5, modbusAppA.getUnit());
					list.add(6, modbusAppA.getAddr());
					list.add(7, modbusAppB.getAddr());
					list.add(8, modbusAppC.getAddr());
					list.add(9, modbusAppA.getCof());
					num++;
					mLists.add(list);
				}
			}
		}
		if (name.equals("变流器历史记录")) {
			List<ModbusApp> modbusHistory = maps.get("变流器历史记录");
			List<ModbusApp> modbusHistoryA = new ArrayList<ModbusApp>();
			List<ModbusApp> modbusHistoryB = new ArrayList<ModbusApp>();
			List<ModbusApp> modbusHistoryC = new ArrayList<ModbusApp>();
			for (int i = 0; i < modbusHistory.size(); i++) {
				if(i<=5){
					modbusHistoryA.add(modbusHistory.get(i));
					modbusHistoryB.add(modbusHistory.get(i));
					modbusHistoryC.add(modbusHistory.get(i));
				}else if (i<=59) {
					modbusHistoryA.add(modbusHistory.get(i));
				} else if (i<=113) {
					modbusHistoryB.add(modbusHistory.get(i));
				}else if(i<=167) {
					modbusHistoryC.add(modbusHistory.get(i));
				}else {
					modbusHistoryA.add(modbusHistory.get(i));
					modbusHistoryB.add(modbusHistory.get(i));
					modbusHistoryC.add(modbusHistory.get(i));
				}
			}
			for (int i = 0; i < modbusHistoryA.size(); i++) {
				ModbusApp modbusAppA = modbusHistoryA.get(i);
				if (modbusAppA.getRolecode().equals("W")) {
					boolean1 = true;
				} else {
					boolean1 = false;
				}
				if (!modbusAppA.getName().equals("")&&!modbusAppA.getName().equals("备用")) {
					Vector<Object> list = new Vector<Object>();
					list.add(0, num);
					list.add(1, modbusAppA.getName());
					list.add(2, modbusAppA.getValue());
					list.add(3, modbusHistoryB.get(i).getValue());
					list.add(4, modbusHistoryC.get(i).getValue());
					list.add(5, modbusAppA.getUnit());
					list.add(6, modbusAppA.getAddr());
					list.add(7, modbusHistoryB.get(i).getAddr());
					list.add(8, modbusHistoryC.get(i).getAddr());
					list.add(9, modbusAppA.getCof());
					num++;
					mLists.add(list);
				}
			}
		}
		if (name.equals("变流器RTC时钟")) {
			List<ModbusApp> modbusSET = maps.get("变流器RTC时钟设置");
//			List<ModbusApp> modbusDIS = maps.get("变流器RTC时钟显示");
			List<ModbusApp> modbusAll = new ArrayList<ModbusApp>();
			modbusAll.addAll(modbusSET);
//			modbusAll.addAll(modbusDIS);
			for (int i = 0; i < modbusAll.size(); i++) {
				ModbusApp modbusApp = modbusAll.get(i);
				if (modbusApp.getRolecode().equals("W")) {
					boolean1 = true;
				} else {
					boolean1 = false;
				}
				if (!modbusApp.getName().equals("")&&!modbusApp.getName().equals("备用")) {
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
		
		for (String key : maps.keySet()) {
			if (!key.equals("变流器历史记录")&&name.equals(key)) {
					for (ModbusApp modbusApp : maps.get(key)) {
						if (modbusApp.getRolecode().equals("W")) {
							boolean1 = true;
						} else {
							boolean1 = false;
						}
						if (!modbusApp.getName().equals("")&&!modbusApp.getName().equals("备用")) {
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
		vect.add(vect.size(), ltemp);
		jTable.validate();
		jTable.updateUI();
		if (name.equals("柜体参数")||name.equals("变流器历史记录")) {
			changeFontColor(jTable);
		}
	}

	public void changeFontColor(JTable jTable2){
		DefaultTableColumnModel dcm = (DefaultTableColumnModel) jTable2.getColumnModel();
		DefaultTableCellRenderer dCellRenderer1 = new DefaultTableCellRenderer();
		dCellRenderer1.setForeground(Color.blue);
		DefaultTableCellRenderer dCellRenderer2 = new DefaultTableCellRenderer();
		dCellRenderer2.setForeground(Color.green);
		DefaultTableCellRenderer dCellRenderer3 = new DefaultTableCellRenderer();
		dCellRenderer3.setForeground(Color.red);
		dcm.getColumn(2).setCellRenderer(dCellRenderer1);
		dcm.getColumn(3).setCellRenderer(dCellRenderer2);
		dcm.getColumn(4).setCellRenderer(dCellRenderer3);
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
