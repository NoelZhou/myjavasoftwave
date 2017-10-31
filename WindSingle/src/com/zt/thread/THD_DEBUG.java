package com.zt.thread;


import com.zt.panel.Debug.ControlPanel;
import com.zt.panel.Debug.StatePanel;
import com.zt.pojo.SaveMessage;

public class THD_DEBUG extends Thread {
	private boolean _run = true;
	private SaveMessage sMessage;
	private ControlPanel controlPanel;
	private StatePanel statePanel;

	public THD_DEBUG() {
		this.sMessage = SaveMessage.getInstance();
	}

	public void stopThread() {
		this._run = !_run;
	}

	@Override
	public void run() {
		while (_run) {
			try {
				sleep(1000);
				refleshControlPanel();
				refleshStatePanel();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public void refleshControlPanel(){
		controlPanel.functionModel(controlPanel.getPanel1());
		String[] strings = sMessage.getStrValue().split(",");
		controlPanel.sendRunModelState(Integer.parseInt(strings[23]));
	}
	
	public void refleshStatePanel(){
		statePanel.StateModel(statePanel.getPanel1());
	}

	public void setControlAndSatePanel(ControlPanel controlPanel, StatePanel statePanel) {
		this.controlPanel = controlPanel;
		this.statePanel = statePanel;
	}

}
