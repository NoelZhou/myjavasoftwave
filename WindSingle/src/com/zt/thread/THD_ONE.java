package com.zt.thread;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.zt.common.GlobalUntils;
import com.zt.common.PingUtils;
import com.zt.common.ReadNativeXml;
import com.zt.common.SunModbusTcpbyIp;
import com.zt.common.WindPowerReadModbusXml;
import com.zt.frame.TheWholeFrame;
import com.zt.pojo.ModbusApp;
import com.zt.pojo.SaveMessage;
import com.zt.pojo.SystemMessageParam;

public class THD_ONE extends Thread {

	private String ipAddr;
	private String com;
	private String strValue;
	private int modbustcp;
	private SaveMessage sMessage;
	private Map<String, List<ModbusApp>> maps;
	private List<SystemMessageParam> lMessageParams = new ArrayList<SystemMessageParam>();
	private TheWholeFrame theWholeFrame;

	public THD_ONE() {
		this.sMessage = SaveMessage.getInstance();
	}

	public void setIpAndCom(String ipAddr, String com) {
		this.ipAddr = ipAddr;
		this.com = com;
	}

	public void setModbusTcp(int modbustcp) {
		this.modbustcp = modbustcp;
	}

	@Override
	public void run() {
		sendBasicMessageToSave();// 保存ip，com，协议类型到saveMessage中
		// mApps=new ReadModbusXml().setLists(modbustcp,ModbusApp.class,
		// GlobalUntils.PATH_APP);
		maps = readXmlListStringAll(modbustcp);
		readxml();
		while (true) {
			try {
				if (PingUtils.ping(ipAddr, 1, 1000)) {
					if(modbustcp==2){
						strValue = new SunModbusTcpbyIp().ReadSunModbusTcpStrAll(ipAddr, Integer.parseInt(com), modbustcp,
								12788, 100, 1202);
					}else {
						strValue = new SunModbusTcpbyIp().ReadSunModbusTcpStrAll(ipAddr, Integer.parseInt(com), modbustcp,
								0, 687, 687);
					}
				} else {
					strValue = "connect";
				}
				if (strValue.equals("sendcodeerror") || strValue.equals("connect")) {
					theWholeFrame.sendStateAndTimeNow("通讯失败 . . .", Color.RED, "1970-1-1", "00:00:00");
					FaultDataApps();
					sMessage.setNewModbusApp(maps);
					sMessage.setStrValue(strValue);
					receiveSystemMess(strValue);
					sMessage.setSystemMessage(lMessageParams);
					THD_ONE.sleep(1000);
				} else {
					dataApps(strValue);/*协议地址位赋值*/
					theWholeFrame.sendStateAndTimeNow("通讯中 . . .", Color.black, time1, time2);
					sMessage.setNewModbusApp(maps);
					sMessage.setStrValue(strValue);
					receiveSystemMess(strValue);
					sMessage.setSystemMessage(lMessageParams);
					THD_ONE.sleep(1000);
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	private void FaultDataApps() {
		String tempStr = "";
		for(String key:maps.keySet()){
			List<ModbusApp> modbusApps = maps.get(key);
			for (ModbusApp modbusApp : modbusApps) {
				String value = "0";
				if (modbusApp.getSysremark().equals("Hex")) {
					if (Integer.parseInt(value) < 0) {
						value = (Integer.toHexString(Integer.parseInt(value)).toUpperCase()).substring(4, 8) + "H";
					} else {
						value = Integer.toHexString(Integer.parseInt(value)).toUpperCase() + "H";
					}
				}
				modbusApp.setValue(value);
				tempStr +="0,";
			}
			}
		strValue = tempStr;
	}

	public void receiveSystemMess(String value) {
		String[] dataArray = value.split(",");
		for (SystemMessageParam sMessageParam : lMessageParams) {
			if(modbustcp==2){
				sMessageParam.setValue(dataArray[Integer.parseInt(sMessageParam.getAddr())-12788]);
			}else {
				sMessageParam.setValue(dataArray[Integer.parseInt(sMessageParam.getAddr())]);
			}
		}
	}

	private String time1 = "1970-1-1";
	private String time2 = "00:00:00";
	public void dataApps(String dataStr) {
		String[] dataArray = dataStr.split(",");
		time1 = dataArray[666] + "-" + dataArray[667] + "-" + dataArray[668];
		time2 = dataArray[669] + ":" + dataArray[670] + ":" + dataArray[671];
		for(String key:maps.keySet()){
			List<ModbusApp> modbusApps = maps.get(key);
			for (ModbusApp modbusApp : modbusApps) {
				modbusApp.setValue(null);
				String value="";
				if(modbustcp==2){
					value = dataArray[Integer.parseInt(modbusApp.getAddr())-12788];
				}else {
					value = dataArray[Integer.parseInt(modbusApp.getAddr())];
				}
				
				// 查询系数cof
				int cof = modbusApp.getCof();
				if (modbusApp.getSysremark().equals("Hex")) {
					if (Integer.parseInt(value) < 0) {
						value = (Integer.toHexString(Integer.parseInt(value)).toUpperCase()).substring(4, 8) + "H";

					} else {
						value = Integer.toHexString(Integer.parseInt(value)).toUpperCase() + "H";
					}
				}
				if (cof != 1) {
					float va = (float) Integer.parseInt(value) / cof;
					value = va + "";
				}
				modbusApp.setValue(value);
			}
		}
	}

	public void readxml() {
		List<String> lStrings = new ReadNativeXml().setlist(GlobalUntils.PATH_SYS_PARAM);
		for (int i = 0; i < lStrings.size(); i++) {
			String strone = lStrings.get(i);
			String[] str = strone.split(",");
			if (str.length > 2) {
				SystemMessageParam sParam = new SystemMessageParam();
				sParam.setAddr(str[2]);
				sParam.setCof(Integer.parseInt(str[5]));
				sParam.setLayouttype(Integer.parseInt(str[8]));
				sParam.setName(str[10]);
				lMessageParams.add(sParam);
			}
		}
	}

	public Map<String, List<ModbusApp>> readXmlListStringAll(int modbustcp) {
		String xmlpath = "";
		if (modbustcp == 0) {
			xmlpath = GlobalUntils.PATH_DF;
		}
		if (modbustcp == 1) {
			xmlpath = GlobalUntils.PATH_KFP;
		}
		if (modbustcp == 2) {
			xmlpath = GlobalUntils.PATH_SEAWIND;
		}
		WindPowerReadModbusXml windPowerReadModbusXml = new WindPowerReadModbusXml();
		windPowerReadModbusXml.readModbusXmlall(xmlpath, modbustcp);
		sMessage.setModbusBits(windPowerReadModbusXml.getModbusBits());//保存bit位
		sMessage.setModbusVars(windPowerReadModbusXml.getModbusVars());//保存var
		return windPowerReadModbusXml.getAppMaps();
	}

	public void sendBasicMessageToSave() {
		sMessage.setIpAndCom(ipAddr, com);
		sMessage.setModbustcp(modbustcp);
	}

	public void setTheWholwFrame(TheWholeFrame theWholeFrame) {
		this.theWholeFrame = theWholeFrame;

	}
}
