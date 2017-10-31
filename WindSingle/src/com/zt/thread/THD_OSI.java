package com.zt.thread;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import javax.swing.JButton;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import com.zt.common.PingUtils;
import com.zt.frame.OscilloscopeFrame;

public class THD_OSI extends Thread {
	private XYSeriesCollection xySeriesCollection = new XYSeriesCollection();
	private MyRun runnableA;
	private MyRun runnableB;
	private Socket socketA;
	private InputStream inputStreamA;
	private OutputStream outA;
	private Socket socketB;
	private InputStream inputStreamB;
	private OutputStream outB;
	private int indexA = 0;
	private int indexB = 0;
	private JButton startBtn;// 开始按钮
	private JButton pauseBtn;// 暂停按钮
	private JButton configBtn;// 配置信息按钮
	@SuppressWarnings("unused")
	private String switchFrequency;// 开关频率
	private String gridIp;// 网侧IP
	private String rotIp;// 机侧IP
	private int bugSerial;// 故障编号
	private String bugIp;// 故障录波模式(网侧/机侧)ip
	private String[] stringA;
	private String[] stringB;

	public THD_OSI(OscilloscopeFrame oPanel) {
		this.xySeriesCollection = oPanel.getxySeriesCollection();
	}

	public void stopThread() {
		closeAllSocket(socketA);
		closeAllSocket(socketB);
		if (runnableA != null) {
			runnableA.stopRun();
		}
		if (runnableB != null) {
			runnableB.stopRun();
		}
		if (bugIp != null) {
			flag = false;
		}
		clearAllParams();
	}

	public void clearAllParams() {
		switchFrequency = null;
		gridIp = null;
		rotIp = null;
		bugSerial = 0;
		bugIp = null;
		stringA = null;
		stringB = null;
	}

	public void closeAllSocket(Socket socket) {
		try {
			if (socket != null) {
				socket.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	Future<?> future;

	@Override
	public void run() {
		if (bugIp != null) {
			getBugDatas();
		} else {
			getDatas();
		}
	}

	/**
	 * 示波器
	 */
	public void getDatas() {
		// 创建一个线程池
		ExecutorService pool = Executors.newFixedThreadPool(2);
		runnableA = createRunable(0);
		runnableB = createRunable(1);
		// 创建多个有返回值的任务
		pool.submit(runnableA);
		pool.submit(runnableB);
	}

	private boolean flag = true;

	public void getBugDatas() {
		List<String> listS = new ArrayList<>();
		String[] string = new String[2];
		string[0] = "00";
		string[1] = String.valueOf(bugSerial);
		if (PingUtils.ping(bugIp, 1, 1000)) {
			socketA = sendCmd(socketA, outA, bugIp, string, "A9");// 发送指令
		}else {
			socketA = null;
		}
		if(socketA!=null){
			try {
				inputStreamA = socketA.getInputStream();
				boolean flagOnly = true;
				out: while (flagOnly) {
					byte[] recDataA = new byte[66];
					inputStreamA.read(recDataA);
					if (recDataA[1] == 0&&recDataA[2]!=-1) {
						String strings = getshortstr(recDataA);
						listS.add(strings);
					} else {
						for (String strs : listS) {
							AnimateBugSeries(strs);
							if (!flag) {
								socketA.close();
								break out;
							}
						}
						flagOnly = false;
						startBtn.setEnabled(true);
						pauseBtn.setEnabled(false);
						configBtn.setEnabled(true);
					}
				}
			} catch (Exception e) {

			}
		}else {
			startBtn.setEnabled(true);
			pauseBtn.setEnabled(false);
			configBtn.setEnabled(true);
		}
		
	}

	interface MyRun extends Runnable {
		public abstract void stopRun();
	}

	public MyRun createRunable(final int type) {
		MyRun runnable = new MyRun() {
			private boolean _run = true;

			public void run() {
				if (type == 0) {
					if (PingUtils.ping(gridIp, 1, 1000)) {
						socketA = sendCmd(socketA, outA, gridIp, stringA, "A8");// 发送指令
					} else {
						System.out.println("socketA通讯异常");
						return;
					}
				}
				if (type == 1) {
					if (PingUtils.ping(rotIp, 1, 1000)) {
						socketB = sendCmd(socketB, outB, rotIp, stringB, "A8");// 发送指令
					} else {
						System.out.println("socketB通讯异常");
						return;
					}
				}
				while (_run) {
					try {
						if (type == 0) {
							System.out.println("开启A线程");
							byte[] recDataA = new byte[66];
							inputStreamA = socketA.getInputStream();
							inputStreamA.read(recDataA);
							indexA = AnimateSeries(getDataArray(getshortstr(recDataA)), 0, 8, indexA);
						}
						if (type == 1) {
							System.out.println("开启B线程");
							byte[] recDataB = new byte[66];
							inputStreamB = socketB.getInputStream();
							inputStreamB.read(recDataB);
							indexB = AnimateSeries(getDataArray(getshortstr(recDataB)), 8, 16, indexB);
						}
					} catch (IOException e) {
						System.out.println(e.getMessage());
					}
				}
			}

			@Override
			public void stopRun() {
				_run = !_run;
			}
		};
		return runnable;
	}

	public MyRun createBugRunnable(final int type,final List<String> lStrings, final List<?> lSeries){
		MyRun bugRun = new MyRun() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				for(int i=0;i<lStrings.size();i++){
					for(int j=type;j<lSeries.size()+type;j++){
						XYSeries xSeries = (XYSeries) lSeries.get(j);
						xSeries.add(i, Integer.valueOf(lStrings.get(i).split(",")[type]));
					}
					
				}
			}
			
			@Override
			public void stopRun() {
				// TODO Auto-generated method stub
				
			}
		};
		return bugRun;
	}
	private int AnimateSeries(String[] dd, int start, int end, int index) {
		List<?> lSeries = this.xySeriesCollection.getSeries();
		for (int i = 0; i < dd.length; i++) {
			for (int j = start; j < end; j++) {
				XYSeries xSeries = (XYSeries) lSeries.get(j);
				xSeries.add(index, Integer.valueOf(dd[i].split(",")[j - start]));
			}
			index++;
		}
		try {
			sleep(2);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return index;
	}

	public void AnimateBugSeries(String string) {
		List<?> lSeries = this.xySeriesCollection.getSeries();
		for (int i = 0; i < lSeries.size(); i++) {
			XYSeries xSeries = (XYSeries) lSeries.get(i);
			xSeries.add(indexA, Integer.valueOf(string.split(",")[i]));
		}
		indexA++;
	}

	public Socket sendCmd(Socket socket, OutputStream out, String ip, String[] waveformCodes, String code) {
		byte cmd[] = new byte[waveformCodes.length + 2];
		cmd[0] = Integer.valueOf(code, 16).byteValue();
		cmd[1] = Integer.valueOf("00", 16).byteValue();
		for (int i = 0; i < waveformCodes.length; i++) {
			// 示波器的编号为十进制的，之前已经进行进制转换了
			if (waveformCodes[i] != null) {
				cmd[i + 2] = Integer.valueOf(waveformCodes[i], 10).byteValue();
			}
		}
		try {
			socket = new Socket(ip, 503);
			out = socket.getOutputStream();
			out.write(cmd);// 发送指令
		} catch (IOException e) {
			System.out.println(e.getMessage());
			socket = null;
		}
		return socket;
	}

	public static String getshortstr(byte[] newmodbusstr) {
		byte[] array = Arrays.copyOfRange(newmodbusstr, 2, newmodbusstr.length);
		int len = array.length / 2;
		short[] ArrayData = new short[len];
		byte[] arraytmp = new byte[2];
		for (short i = 0; i <= len - 1; i++) {
			arraytmp[0] = array[2 * i];
			arraytmp[1] = array[2 * i + 1];
			ArrayData[i] = byte4ToInt(arraytmp, 0);
		}
		String bString = "";
		for (int i = 0; i < ArrayData.length; i++) {
			short str = ArrayData[i];
			if (i == ArrayData.length - 1) {
				bString += str;
			} else {
				bString += str + ",";
			}
		}

		return bString;
	}

	public static short byte4ToInt(byte[] bytes, int off) {
		int b0 = bytes[off] & 0xFF;
		int b1 = bytes[off + 1] & 0xFF;
		short ii = (short) ((b0 << 8) | b1);
		return ii;
	}

	public static String[] getDataArray(String str) {
		if (str.equals("")) {
			return null;
		}
		String[] allArrs = str.split(",");
		String[] resArrs = new String[4];
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < 4; i++) {

			for (int j = 0; j < 8; j++) {
				sb.append(allArrs[i * 8 + j]).append(",");
			}
			resArrs[i] = sb.substring(0, sb.length() - 1);
			sb.delete(0, sb.length());
		}
		return resArrs;
	}

	public void receiveFrequencyAndIp(String switchFrequency, String gridIp, String rotIp, String[] stringA,
			String[] stringB) {
		this.gridIp = gridIp;
		this.rotIp = rotIp;
		this.switchFrequency = switchFrequency;
		this.stringA = stringA;
		this.stringB = stringB;
	}

	public void receiveBugSerialAndIp(int bugSerial, String bugIp) {
		this.bugIp = bugIp;
		this.bugSerial = bugSerial;
	}

	public void receiveButtons(JButton startBtn, JButton pauseBtn, JButton configBtn) {
		this.configBtn = configBtn;
		this.startBtn = startBtn;
		this.pauseBtn = pauseBtn;
	}
}
