package com.zt.thread;


import com.zt.panel.SystemMessagePanel;
import com.zt.pojo.SaveMessage;

public class THD_SYS extends Thread {
	 private boolean _run = true;
	private SaveMessage sMessage;
	private SystemMessagePanel sMessagePanel;
	public THD_SYS() {
		this.sMessage = SaveMessage.getInstance();
	}

	public void stopThread(){
		this._run = !_run;
	}
	@Override
	public void run() {
		while (_run) {
			try {
				sleep(1000);
				String strValue =sMessage.getStrValue();
				sMessagePanel.sendSystemMessageParams(sMessage.getSystemMessage(),strValue);
				sMessagePanel.sendSystemImage(strValue);
				sMessagePanel.sendDebugState(strValue);
				sMessagePanel.updateUI();
				sMessagePanel.validate();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public void setSystemPanel(SystemMessagePanel sMessagePanel) {
		this.sMessagePanel = sMessagePanel;
	}
}
