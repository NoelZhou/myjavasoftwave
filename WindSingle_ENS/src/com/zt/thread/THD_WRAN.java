package com.zt.thread;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import com.zt.common.GlobalUntils;
import com.zt.common.ReadModbusXml;
import com.zt.frame.SysSubFrame.WarnFrame;
import com.zt.pojo.ErrorDetail;
import com.zt.pojo.ModbusBit;
import com.zt.pojo.SaveMessage;

public class THD_WRAN extends Thread {
	private boolean _run = true;
	private SaveMessage sMessage;
	private WarnFrame warnFrame;
	private List<List<Object>> lists;

	public THD_WRAN() {
		this.sMessage = SaveMessage.getInstance();
	}

	public void stopThread() {
		this._run = !_run;
	}

	@Override
	public void run() {
		List<ModbusBit> lBits = sMessage.getModbusBits();
		List<Object> objects = new ReadModbusXml().setLists(ErrorDetail.class, GlobalUntils.PATH_ERROR);
		while (_run) {
			try {
				sleep(1000);
				analysisState(sMessage.getStrValue().split(","), lBits, objects);
				warnFrame.sendWarnMessage(lists);
				
				warnFrame.validate();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public void analysisState(String[] values, List<ModbusBit> lBits, List<Object> objects) {
		lists = new ArrayList<>();
		int defalutAddr = 34;
		for (Object object : objects) {
			ErrorDetail eDetail = (ErrorDetail) object;
			if (eDetail.getModbus_type() == sMessage.getModbustcp() && eDetail.getAddr() == defalutAddr) {
				String bitStr = getbits(Integer.parseInt(values[defalutAddr]));
				String[] strings = eDetail.getBitid().split(",");
				for (int i = 0; i < strings.length; i++) {
					List<Object> list = new Vector<Object>();
					for (ModbusBit modbusBit : lBits) {
						String addr = modbusBit.getAddr();
						if (addr != null) {
							int bitNum = Integer.parseInt(strings[i]);
							if (addr.equals(String.valueOf(defalutAddr)) && modbusBit.getBit_id() == bitNum) {
								list.add(i + 1);
								list.add(modbusBit.getVar1());
								list.add(bitStr.substring(15-bitNum, 16-bitNum));
							}
						}
					}
					lists.add(list);
				}
			}
		}
	}

	public ModbusBit getModbusBit(List<ModbusBit> lBits, ErrorDetail eDetail) {
		for (ModbusBit modbusBit : lBits) {
			if (modbusBit.getAddr().equals(String.valueOf(eDetail.getAddr()))) {
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

	public void setWarnFrame(WarnFrame warnFrame) {
		this.warnFrame = warnFrame;

	}

}
