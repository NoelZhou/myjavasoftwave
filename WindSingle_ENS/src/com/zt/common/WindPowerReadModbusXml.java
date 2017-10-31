package com.zt.common;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import com.zt.pojo.ModbusApp;
import com.zt.pojo.ModbusBit;
import com.zt.pojo.ModbusVar;

public class WindPowerReadModbusXml {
	private static List<String> appliststr_sk = new ArrayList<String>();
	private static List<String> bitliststr_sk = new ArrayList<String>();
	private Map<String, List<ModbusApp>> maps = new HashMap<String, List<ModbusApp>>();
	private List<ModbusBit> modbusBits = new ArrayList<ModbusBit>();
	private List<ModbusVar> modbusVars = new ArrayList<ModbusVar>();
	private List<ModbusApp> modbusApps = new ArrayList<ModbusApp>();
	private String rolecode = "R";

	public Map<String, List<ModbusApp>> getAppMaps() {
		return maps;
	}
	public List<ModbusBit> getModbusBits() {
		return modbusBits;
	}

	public List<ModbusVar> getModbusVars() {
		return modbusVars;
	}

	public void readModbusXmlall(String filePath, int modbustcp) {
		try {
			File root = new File(filePath);
			File[] files = root.listFiles();
			for (File file : files) {
				if (file.isDirectory()) {
					readModbusXmlall(file.getAbsolutePath(), modbustcp);
//					System.out.println("显示" + filePath + "下所有子目录及其文件" + file.getAbsolutePath());
				} else {
//					System.out.println("显示" + filePath + "下所有子目录" + file.getAbsolutePath());
					String fileNames = file.getAbsolutePath();
					String[] s = fileNames.split("\\\\");
					String readOrwrite = s[s.length - 2];
					if (readOrwrite.equals("通讯协议定义控制参数数组xml解析文件--读写参数")) {
						rolecode = "W";
					} else {
						rolecode = "R";
					}
					setlist(file.getAbsolutePath(), modbustcp);
				}
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void setlist(String xmlpath, int modbustcp) {
		SAXReader reader = new SAXReader();
		try {
			File file = new File(xmlpath);
			Document document;
			document = reader.read(file);
			Element root = document.getRootElement();
			@SuppressWarnings("unchecked")
			List<Element> childElements = root.elements();
			// 获取解析定义格式
			getList(childElements);
			getModbusApp(modbustcp);
			getModbusBitAndVar(modbustcp);
			appliststr_sk = new ArrayList<String>();
			bitliststr_sk = new ArrayList<String>();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void getList(List<Element> childElements) {
		try {
			// 计算节点中的属性值
			// 计算几点数
			for (Element child : childElements) {
				String listr = "";
				// 未知属性名情况下
				@SuppressWarnings("unchecked")
				List<Attribute> attributeList = child.attributes();
				// 主节点中的属性值
				for (int i = 0; i < attributeList.size(); i++) {
					if (i == attributeList.size() - 1) {
						listr += attributeList.get(i).getValue();
					} else {
						listr += attributeList.get(i).getValue() + ",";
					}
				}
				@SuppressWarnings("unchecked")
				List<Element> elementList = child.elements();
				if (elementList.size() == 0 && child.getQName().getName().equals("list")) {
					listr += "," + child.getText();
					listr += ",自定义值";
				}
				if (child.getQName().getName().equals("point") || child.getQName().getName().equals("device")) {
					appliststr_sk.add(listr);
				} else {
					bitliststr_sk.add(listr);
				}
				// 主节点下面的，应数据库片接处理，
				if (elementList.size() != 0) {
					getList(elementList);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void getModbusApp(int modbustcp) {
		String rolecodeONE = rolecode;
		String array_name = "";
		modbusApps = new ArrayList<ModbusApp>();
		for (int i = 0; i < appliststr_sk.size(); i++) {
			String strone = appliststr_sk.get(i);
			String[] str = strone.split(",");
			if (str.length <= 2) {
				array_name = str[1];
			}
			if (str.length > 2) {
				ModbusApp modbusApp = new ModbusApp();
				if (rolecodeONE.equals("W")) {
					modbusApp.setAddr(str[2]);
					modbusApp.setCategory(str[12]);
					modbusApp.setCof(Integer.parseInt(confValueChange(str[7])));
					modbusApp.setModbus_type(modbustcp);
					modbusApp.setName(str[11]);
					modbusApp.setPoint_id(str[0]);
					modbusApp.setRolecode(rolecodeONE);
					modbusApp.setSysremark(str[10]);
					modbusApp.setUnit(str[8]);
					modbusApp.setDatalimitmin(str[5]);
					modbusApp.setDatalimitmax(str[6]);
				} else if (rolecodeONE.equals("R")) {
					modbusApp.setAddr(str[2]);
					modbusApp.setCategory(str[10]);
					modbusApp.setCof(Integer.parseInt(confValueChange(str[5])));
					modbusApp.setModbus_type(modbustcp);
					modbusApp.setName(str[9]);
					modbusApp.setPoint_id(str[0]);
					modbusApp.setRolecode(rolecodeONE);
					modbusApp.setSysremark(str[8]);
					modbusApp.setUnit(str[6]);
				}
				modbusApps.add(modbusApp);
			}
		}
		maps.put(array_name, modbusApps);
	}

	public String confValueChange(String value) {
		if (value.indexOf("/") != -1 || value.indexOf(".") != -1) {
			return "1";
		}
		return value;
	}

	public void getModbusBitAndVar(int modbustcptype) {
		if (bitliststr_sk.size() == 0) {
			return;
		}
		String[] headstrparame = bitliststr_sk.get(0).split(",");
		String point_id = headstrparame[0];
		String addr = headstrparame[1];
		for (int i = 1; i < bitliststr_sk.size(); i++) {
			if (bitliststr_sk.get(i).split(",").length == 2) {
				headstrparame = bitliststr_sk.get(i).split(",");
				point_id = headstrparame[0];
				addr = headstrparame[1];
			} else if (bitliststr_sk.get(i).split(",").length == 1) {
				if ((bitliststr_sk.get(i).equals("84") && modbustcptype == 0)
						|| (bitliststr_sk.get(i).equals("54") && modbustcptype == 0)
						|| (bitliststr_sk.get(i).equals("54") && modbustcptype == 1)
						|| (bitliststr_sk.get(i).equals("114") && modbustcptype == 1)) {
					point_id = bitliststr_sk.get(i);
					addr = null;
				} else if ((bitliststr_sk.get(i).equals("233") && modbustcptype == 0)
						|| (bitliststr_sk.get(i).equals("73") && modbustcptype == 0)
						|| (bitliststr_sk.get(i).equals("73") && modbustcptype == 1)
						|| (bitliststr_sk.get(i).equals("233") && modbustcptype == 1)) {
					point_id = null;
					addr = bitliststr_sk.get(i);
				}
			} else if (bitliststr_sk.get(i).contains("bit")) {
				ModbusBit modbusBit = new ModbusBit();
				modbusBit.setBit_id(Integer.parseInt(bitliststr_sk.get(i).split(",")[0]));
				modbusBit.setAddr(addr);
				modbusBit.setVar0(bitliststr_sk.get(i).split(",")[1]);
				modbusBit.setVar1(bitliststr_sk.get(i).split(",")[2]);
				modbusBit.setPoint_id(point_id);
				modbusBits.add(modbusBit);
			} else {
				ModbusVar modbusVar = new ModbusVar();
				modbusVar.setVar(Integer.parseInt(bitliststr_sk.get(i).split(",")[0]));
				modbusVar.setName(bitliststr_sk.get(i).split(",")[1]);
				modbusVar.setPoint_id(point_id);
				modbusVar.setAddr(addr);
				modbusVars.add(modbusVar);
			}
		}

	}

}