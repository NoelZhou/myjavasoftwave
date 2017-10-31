package com.zt.panel;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import com.zt.common.FileDiretory;
import com.zt.common.SunModbusTcpbyIp;
import com.zt.custom.MyMouseListener;
import com.zt.custom.MyTableModel;
import com.zt.frame.AnalsDialog;
import com.zt.frame.ParamSetDialog;
import com.zt.pojo.ModbusApp;
import com.zt.pojo.ModbusBit;
import com.zt.pojo.ModbusVar;
import com.zt.pojo.SaveMessage;
import com.zt.source.DemoNode;
import com.zt.source.DemoRenderer;
import com.zt.source.DemoTreeUI;
import com.zt.thread.THD_SEC;

public class ParaMonitor extends JPanel {
	private static final long serialVersionUID = 1L;
	private JTable table;
	private JScrollPane jPane;
	private Vector<Vector<Object>> cells = new Vector<Vector<Object>>();
	private THD_SEC sec;
	private Vector<List<Object>> vect;
	private JTree tree;
	private DefaultTreeModel model;
	private JPopupMenu popupMenu;
	private Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize(); // 得到屏幕的尺寸
	private SaveMessage saveMessage = SaveMessage.getInstance();
	private int addr;
	private String addrName;
	private String value;
	private String unit;
	private String datalimitmin;
	private String datalimitmax;
	private String codeStr;
	private int num;
	/** 调用reload方法更新jtree界面 */
	private DefaultMutableTreeNode root;
	/** 根节点 */
	private Border inBorder = BorderFactory.createLineBorder(Color.LIGHT_GRAY);
	private String[] treeName = { "网侧", "机侧", "外围接口" };
	// private String[][] nodeName = { { "网侧监控参数", "网侧EEPROM参数", "谐波参数显示" }, {
	// "机侧监控参数", "机侧EEPROM参数" },
	// { "控制参数", "系统状态参数", "温度监控参数", "中间变量监控", "其他监控参数", "变流器历史记录", "系统参数",
	// "变流器RTC时钟显示","变流器RTC时钟设置", "变流器序列号", "功率曲线参数" } };

	public ParaMonitor() {
		setUIFont();
		createUI();
		createJTree();
		initListener();
		// addButton();
		addPopupMenu(null);
	}

	private void addPopupMenu(String nodeName) {
		popupMenu = new JPopupMenu();
		popupMenu.removeAll();
		if (nodeName != null) {
			if(nodeName.equals("变流器序列号")||nodeName.equals("变流器RTC时钟设置")||nodeName.equals("功率曲线参数")){
				popupMenu.add(createMenuItem("参数设置", null));
			}else if(nodeName.equals("变流器历史记录")){
				popupMenu.add(createMenuItem("故障次数增", null));
				popupMenu.addSeparator();
				popupMenu.add(createMenuItem("故障次数减", null));
			}else {
				popupMenu.add(createMenuItem("参数设置", null));
				popupMenu.addSeparator();
				popupMenu.add(createMenuItem("查询", null));
			}
			
		}

	}
	private  int index = 0;
	public JMenuItem createMenuItem(String name, String mnemonic) {
		// 根据名称和快捷键创建menu并添加到menuBar
		JMenuItem menuItem = new JMenuItem(name);
		menuItem.setActionCommand(name);
		menuItem.setSize(100, 20);
		if (mnemonic != null)
			menuItem.setMnemonic(mnemonic.toCharArray()[0]);
		menuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (e.getActionCommand().equals("参数设置")) {
					ParamSetDialog pSetDialog = new ParamSetDialog(codeStr,num, addr, addrName, value, unit, datalimitmin,
							datalimitmax);
					pSetDialog.setVisible(true);
				}
				if (e.getActionCommand().equals("查询")) {
					sendCode(2);;
				}
				if (e.getActionCommand().equals("故障次数增")) {
					sendCode(1);;
				}
				if (e.getActionCommand().equals("故障次数减")) {
					sendCode(2);;
				}
				if(e.getActionCommand().equals("添加参数到记录模式中")){
					String param = addr+"-"+addrName;
					List<String> strings= saveMessage.getParamsForRecordModel();
					for (String string : strings) {
						if(param.equals(string)){
							Toolkit.getDefaultToolkit().beep();
			                JOptionPane.showMessageDialog(null, "重复添加，请重新添加！", "警告", JOptionPane.WARNING_MESSAGE);
							return;
						}
					}
					if(strings.size()==12){
						Toolkit.getDefaultToolkit().beep();
		                JOptionPane.showMessageDialog(null, "最多添加12个参数通道！", "警告", JOptionPane.WARNING_MESSAGE);
		                return;
					}
					strings.add(index, param);
					saveMessage.setParamsForRecordModel(strings);
					index++;
				}
			}
		});
		return menuItem;
	}

	public void sendCode(int num){
		String[] ipCom = saveMessage.getIpAndCom();
		int modbusType = saveMessage.getModbustcp();
		String[] code = codeStr.split(",");
		SunModbusTcpbyIp.WriteSunModbusTcpStrAll(ipCom[0], Integer.parseInt(ipCom[1]), modbusType, "0000",
				code[num]);
	}
	
	public void createUI() {
		vect = new Vector<List<Object>>();
		setLayout(null);
		MyTableModel myTableModel = new MyTableModel(vect);
		table = new JTable(myTableModel);
		table.setRowHeight(30);
		table.addMouseListener(new MyMouseListener() {
			@Override
			public void mouseClicked(MouseEvent e) {
				// TODO Auto-generated method stub
				if (e.isMetaDown()) {
					int row = table.rowAtPoint(e.getPoint());
					if (table.isCellEditable(row, 3)||codeStr.split(",")[0].equals("变流器历史记录")) {
						table.setRowSelectionInterval(row, row);// 右击选中高亮
						num = (int) table.getValueAt(row, 0);
						addr = Integer.parseInt((String) table.getValueAt(row, 1));
						addrName = (String) table.getValueAt(row, 2);
						value = (String) table.getValueAt(row, 3);
						unit = (String) table.getValueAt(row, 4);
						datalimitmin = (String) table.getValueAt(row, 7);
						datalimitmax = (String) table.getValueAt(row, 8);
						popupMenu.show(table, e.getX(), e.getY());
					}else {
						table.setRowSelectionInterval(row, row);// 右击选中高亮
						addr = Integer.parseInt((String) table.getValueAt(row, 1));
						addrName = (String) table.getValueAt(row, 2);
						popupMenu = new JPopupMenu();
						popupMenu.removeAll();
						popupMenu.add(createMenuItem("添加参数到记录模式中", null));
						popupMenu.show(table, e.getX(), e.getY());
					}

				}
				if (e.getClickCount() == 2) {
					int row = table.rowAtPoint(e.getPoint());
					int addr = Integer.parseInt((String) table.getValueAt(row, 1));
					String name = (String) table.getValueAt(row, 2);
					String value = (String) table.getValueAt(row, 3);
					String valueSpecial = "";
					if (row > 0) {
						valueSpecial = (String) table.getValueAt(row - 1, 3);// 只有是特殊地址时，才使用
					}
					if (value.indexOf("H") != -1) {
						AnalsDialog analsDialog = new AnalsDialog(name);
						analsDialog.setVisible(true);
						analsicsD(addr, value, valueSpecial, analsDialog.getVect(), analsDialog.getTable());

					}
				}
			}
		});
		int[] arr = {0,1,2,3,4,5,6,7,8,9};
		hideAndChangeColumn(table, arr);
		jPane = new JScrollPane(table);
		jPane.setBounds(300, 0, (int) screenSize.getWidth() - 300, 600);
		jPane.getViewport().setBackground(Color.white);
		jPane.setAutoscrolls(true);
		jPane.setBorder(BorderFactory.createLoweredBevelBorder());
		add(jPane);
		addDataChangeListener();
	}

	public void analsicsD(int addr, String value, String valueSpecical, Vector<Vector<Object>> vector, JTable table) {
		vector.removeAllElements();
		SaveMessage saveMessage = SaveMessage.getInstance();
		List<ModbusBit> modbusBits = saveMessage.getModbusBits();
		List<ModbusVar> modbusVars = saveMessage.getModbusVars();
		int tenValue = Integer.parseInt(value.substring(0, value.length() - 1), 16);
		String bits = getbits(tenValue);
		if (addr == 233) {
			int tenValueSpecial = Integer.parseInt(valueSpecical.substring(0, valueSpecical.length() - 1), 16);
			if (tenValueSpecial == 51) {
				// 获取84位的bit位置
				for (ModbusBit modbusBit : modbusBits) {
					if (modbusBit.getPoint_id()!=null&&modbusBit.getPoint_id().equals("84")) {
						Vector<Object> list = new Vector<>();
						list.add(modbusBit.getBit_id());
						list.add(modbusBit.getVar1());
						list.add(bits.substring(15 - modbusBit.getBit_id(), 16 - modbusBit.getBit_id()));
						list.add("233");
						vector.addElement(list);
					}
				}
			} else if (tenValueSpecial == 68) {
				for (ModbusBit modbusBit : modbusBits) {
					if (modbusBit.getAddr()!=null&&modbusBit.getAddr().equals(String.valueOf(addr))) {
						Vector<Object> list = new Vector<>();
						list.add(modbusBit.getBit_id());
						list.add(modbusBit.getVar1());
						list.add(bits.substring(15 - modbusBit.getBit_id(), 16 - modbusBit.getBit_id()));
						list.add("233");
						vector.addElement(list);
					}
				}
			} else {
				Vector<Object> list = new Vector<>();
				list.add(0);
				list.add("不解析");
				list.add("0");
				list.add("233");
				vector.addElement(list);
			}
		} else if (addr == 73) {
			int tenValueSpecial = Integer.parseInt(valueSpecical.substring(0, valueSpecical.length() - 1), 16);
			if (tenValueSpecial == 51) {
				// 获取84位的bit位置
				for (ModbusBit modbusBit : modbusBits) {
					if (modbusBit.getPoint_id()!=null&&modbusBit.getPoint_id().equals("54")) {
						Vector<Object> list = new Vector<>();
						list.add(modbusBit.getBit_id());
						list.add(modbusBit.getVar1());
						list.add(bits.substring(15 - modbusBit.getBit_id(), 16 - modbusBit.getBit_id()));
						list.add("73");
						vector.addElement(list);
					}
				}
			} else if (tenValueSpecial == 68) {
				for (ModbusBit modbusBit : modbusBits) {
					if (modbusBit.getAddr()!=null&&modbusBit.getAddr().equals(String.valueOf(addr))) {
						Vector<Object> list = new Vector<>();
						list.add(modbusBit.getBit_id());
						list.add(modbusBit.getVar1());
						list.add(bits.substring(15 - modbusBit.getBit_id(), 16 - modbusBit.getBit_id()));
						list.add("73");
						vector.addElement(list);
					}
				}
			} else {
				Vector<Object> list = new Vector<>();
				list.add(0);
				list.add("不解析");
				list.add("0");
				list.add("73");
				vector.addElement(list);
			}
		} else {
			for (ModbusBit modbusBit : modbusBits) {
				if (modbusBit.getAddr() != null) {
					if (modbusBit.getAddr().equals(String.valueOf(addr))) {
						Vector<Object> list = new Vector<>();
						list.add(modbusBit.getBit_id());
						list.add(modbusBit.getVar1());
						list.add(bits.substring(15 - modbusBit.getBit_id(), 16 - modbusBit.getBit_id()));
						list.add(modbusBit.getAddr());
						vector.addElement(list);
					}
				}
			}
			for (ModbusVar modbusVar : modbusVars) {
				if (modbusVar.getAddr() != null) {
					if (modbusVar.getAddr().equals(String.valueOf(addr)) && modbusVar.getVar() == tenValue) {
						Vector<Object> list = new Vector<>();
						list.add(modbusVar.getVar());
						list.add(modbusVar.getName());
						list.add("1");
						list.add(modbusVar.getAddr());
						vector.addElement(list);
					}
				}
			}
		}

		table.updateUI();
		table.validate();

	}

	/**
	 * * bit位
	 * 
	 * @param begin
	 * @param end
	 * @param shortvalue
	 */
	public String getbits(int shortvalue) {
		short sv = (short) shortvalue;
		String bstr = Integer.toBinaryString(sv);
		bstr = bstr.length() < 16 ? "0000000000000000".substring(bstr.length()) + bstr
				: bstr.substring(bstr.length() - 16, bstr.length());// 二进制16位补零
		return bstr;
	}

	/*
	 * private JButton defaultSetBtn; private JButton checkBtn; public void
	 * addButton() { defaultSetBtn = new JButton("设置");
	 * defaultSetBtn.setBounds(750, 20, 100, 30);
	 * defaultSetBtn.addActionListener(clickBtn(false));
	 * 
	 * checkBtn = new JButton("查询"); checkBtn.setBounds(900, 20, 100, 30);
	 * checkBtn.addActionListener(clickBtn(true));
	 * 
	 * add(defaultSetBtn); add(checkBtn); }
	 */
	/*
	 * public ActionListener clickBtn(final boolean boolRun) { ActionListener
	 * aListener = new ActionListener() {
	 * 
	 * @Override public void actionPerformed(ActionEvent e) { // TODO
	 * Auto-generated method stub JButton jButton = (JButton) e.getSource();
	 * if(jButton==checkBtn){ checkBtn.setBackground(Color.orange);
	 * defaultSetBtn.setBackground(new Color(238, 238, 238)); }else{
	 * checkBtn.setBackground(new Color(238, 238, 238));
	 * defaultSetBtn.setBackground(Color.orange); } sec.setBoolRun(boolRun); }
	 * }; return aListener; }
	 */

	public void createJTree() {
		// List<Object> lObjects = new
		// ReadModbusXml().setLists(ParamsTreeShow.class,
		// GlobalUntils.PATH_PARAM_SHOW);
		Map<String, List<ModbusApp>> maps = SaveMessage.getInstance().getNewModbusApp();
		// 创建根节点，所有的节点都是挂在根节点下面的
		root = new DefaultMutableTreeNode();
		model = new DefaultTreeModel(root);
		for (int i = 0; i < 3; i++) {
			DemoNode cate = new DemoNode(produceImage("arrow_left.png"), treeName[i]);
			for (String key : maps.keySet()) {
				if (i == 0 && key.indexOf("网侧") != -1) {
					DemoNode node;
					if (key.equals("网侧FLASH参数")) {
						node = new DemoNode(key, "89H,9BH");
					} else {
						node = new DemoNode(key);
					}
					cate.add(node);
				} else if (i == 1 && key.indexOf("机侧") != -1) {
					DemoNode node;
					if (key.equals("机侧FLASH参数")) {
						node = new DemoNode(key, "8AH,9CH");
					} else {
						node = new DemoNode(key);
					}
					cate.add(node);
				} else if (i == 2 && key.indexOf("网侧") == -1 && key.indexOf("机侧") == -1) {
					DemoNode node;
					if (key.equals("双馈电机参数")) {
						node = new DemoNode(key, "A1H,9FH");
					} else if (key.equals("功率曲线参数")) {
						node = new DemoNode(key, "97H");
					} else if (key.equals("系统参数")) {
						node = new DemoNode(key, "8CH,A7H");
					} else if (key.equals("变流器序列号")) {
						node = new DemoNode(key, "92H");
					} else if (key.equals("变流器RTC时钟设置")) {
						node = new DemoNode(key, "8DH");
					} else if (key.equals("变流器历史记录")) {
						node = new DemoNode(key, "85H,86H");
					} else {
						node = new DemoNode(key);
					}
					cate.add(node);
				}
			}
			root.add(cate);
		}

		tree = new JTree(model);
		// tree.setOpaque(false);
		tree.setBounds(0, 0, 300, 600);
		// 设置根节点不可见
		tree.setRootVisible(false);
		// 设置点击1次展开节点
		tree.setToggleClickCount(1);
		// 设置UI
		tree.setUI(new DemoTreeUI());
		// 设置节点渲染器
		tree.setCellRenderer(new DemoRenderer());
		// 去掉节点前面的线条
		// 此功能也可以放到DemoTreeUI里面实现
		tree.putClientProperty("JTree.lineStyle", "None");
		add(tree);
	}

	private void initListener() {
		tree.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				TreePath path = tree.getSelectionPath();
				if (null != path) {
					Object object = path.getLastPathComponent();
					DemoNode node = (DemoNode) object;
					// 二级节点（分组）咱就不管了
					if (node.getLevel() == 2) {
						node.nodeContent.setBorder(inBorder);
						node.nodeContent.setBackground(Color.ORANGE);
						// 去掉选中之外其他所有节点的特效
						for (int i = 0; i < root.getChildCount(); i++) {
							DemoNode cate = (DemoNode) root.getChildAt(i);
							// 三级节点
							for (int j = 0; j < cate.getChildCount(); j++) {
								DemoNode buddy = (DemoNode) cate.getChildAt(j);
								if (node != buddy) {
									buddy.nodeContent.setBorder(null);
									buddy.nodeContent.setBackground(null);
								}
								model.reload(buddy);
							}
						}
						model.reload(node);
					}
				}
			}
		});

		tree.addMouseMotionListener(new MouseMotionAdapter() {
			// 没有使用moseEnter事件，是因为此方法里面没有可以获取当前节点的API
			// 大家若是知道有更好的方法，可以告诉我一下，感激不尽
			// 还有，大家看到如下方法，循环太多，如果节点过多的话，效率就不行了，但是事件只能加到jtree上，如果能加到我重绘的UI上就好了。。。
			@Override
			public void mouseMoved(MouseEvent e) {
				TreePath path = tree.getPathForLocation(e.getX(), e.getY());
				if (null != path) {
					Object object = path.getLastPathComponent();
					DemoNode node = (DemoNode) object;
					// 不管三七二十一，先把自己的特效加上再说
					// 这里因为是鼠标移动事件，触发太快了，所以要判断是否在当前节点上移动鼠标
					if (node.getLevel() == 1 && node.cateContent.getBorder() != inBorder) {
						node.cateContent.setBorder(inBorder);
					}
					if (node.getLevel() == 2 && node.nodeContent.getBorder() != inBorder) {
						node.nodeContent.setBorder(inBorder);
					}
					model.reload(node);

					// 去掉选中之外其他所有节点的特效
					for (int i = 0; i < root.getChildCount(); i++) {
						DemoNode cate = (DemoNode) root.getChildAt(i);
						if (node != cate && cate.cateContent.getBackground() != Color.ORANGE) {
							cate.cateContent.setBorder(null);
						}
						model.reload(cate);
						// 三级节点
						for (int j = 0; j < cate.getChildCount(); j++) {
							DemoNode buddy = (DemoNode) cate.getChildAt(j);
							if (node != buddy && buddy.nodeContent.getBackground() != Color.ORANGE) {
								buddy.nodeContent.setBorder(null);
							}
							model.reload(buddy);
						}
					}
				}
			}
		});
		tree.addTreeSelectionListener(new TreeSelectionListener() {

			@Override
			public void valueChanged(TreeSelectionEvent e) {
				Object object = e.getPath().getLastPathComponent();
				DemoNode node = (DemoNode) object;
				if (node.getLevel() != 1) {
					// table.removeAll();
					sec.setjTableAndNodeName(table, node.getName());
					addPopupMenu(node.getName());
					codeStr = node.getName()+","+node.getArrayStr();
				}

			}
		});
	}

	/**
	 * 获取图片
	 * 
	 * @param name
	 *            图片名称
	 * @return
	 */
	private ImageIcon produceImage(String name) {
		ImageIcon backImage = new ImageIcon(FileDiretory.getCurrentDir() + "/image/" + name);
		return backImage;
	}

	/**
	 * 隐藏JTable中不需要显示的列
	 * 
	 * @param table
	 *            需要隐藏列的JTable
	 * @param colIndex
	 *            需要隐藏的列的下标（JTable列下标从0开始）
	 */
	public void  hideAndChangeColumn(JTable table,int[] arr){
		DefaultTableColumnModel dcm = (DefaultTableColumnModel) table // 获取列模型
				.getColumnModel();
		for(int i=0;i<arr.length;i++){
			if(arr[i]==0) columnChange(dcm, i, 60);
			if(arr[i]==1) columnChange(dcm, i, 100);
//			if(arr[i]==2) columnChange(dcm, i, 200);
			if(arr[i]==3) columnChange(dcm, i, 100);
			if(arr[i]==4) columnChange(dcm, i, 60);
			if(arr[i]==5) columnChange(dcm, i, 150);
			if(arr[i]==6) columnChange(dcm, i, 150);
			if(arr[i]==7) columnChange(dcm, i, 0);
			if(arr[i]==8) columnChange(dcm, i, 0);
			if(arr[i]==9) columnChange(dcm, i, 0);
			
		}
		}
		public void columnChange(DefaultTableColumnModel dcm,int i,int width){
			dcm.getColumn(i).setPreferredWidth(width);
			dcm.getColumn(i).setMinWidth(width);
			dcm.getColumn(i).setMaxWidth(width);
		}

	public void setUIFont() {
		Font f = new Font("宋体", Font.PLAIN, 14);
		String names[] = { "Label", "CheckBox", "PopupMenu", "MenuItem", "CheckBoxMenuItem", "JRadioButtonMenuItem",
				"ComboBox", "Button", "Tree", "ScrollPane", "TabbedPane", "EditorPane", "TitledBorder", "Menu",
				"TextArea", "OptionPane", "MenuBar", "ToolBar", "ToggleButton", "ToolTip", "ProgressBar", "TableHeader",
				"Panel", "List", "ColorChooser", "PasswordField", "TextField", "Table", "Label", "Viewport",
				"RadioButtonMenuItem", "RadioButton", "DesktopPane", "InternalFrame" };
		for (String item : names) {
			UIManager.put(item + ".font", f);
		}
	}

	public JTable getTable() {
		return table;
	}

	public void setTable(JTable table) {
		this.table = table;
	}

	public Vector<List<Object>> getVect() {
		return vect;
	}

	public void setVect(Vector<List<Object>> vect) {
		this.vect = vect;
	}

	public void setTsec(THD_SEC sec2) {
		this.sec = sec2;
	}

	private void addDataChangeListener() {
		// 检测单元格数据变更
		Action action = new AbstractAction() {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				TableCellListener tcl = (TableCellListener) e.getSource();
				int row = tcl.getRow();
				int col = tcl.getColumn();
				Object oldValue = tcl.getOldValue();
				Object newValue = tcl.getNewValue();
				if (col == 3) {
					Vector<Object> list = new Vector<Object>();
					list.add(table.getValueAt(row, 1));
					list.add(table.getValueAt(row, 2));
					list.add(table.getValueAt(row, 3));
					cells.addElement(list);
					System.out.println(list);
				}
				System.out.printf("cell changed at [%d,%d] : %s -> %s%n", row, col, oldValue, newValue);
			}
		};
		@SuppressWarnings("unused")
		TableCellListener tcl1 = new TableCellListener(table, action);
		System.out.printf("cell changed%n");
	}

	/*
	 * public void sendCode(){ String modbusstr = ""; for (Vector<Object> vector
	 * : cells) { String addrO = (String) vector.get(0); String addrHex =
	 * gethex4(Integer.toHexString(Integer.parseInt(addrO))); Object paramValue
	 * = vector.get(2); String vString = String.valueOf(paramValue); modbusstr =
	 * SunModbusTcpbyIp.WriteSunModbusTcpStrAll(ip, port, 1, addrHex,vString); }
	 * cells.removeAllElements(); //参数指令有返回值 if(modbusstr.contains(",")){
	 * System.out.println(11); SunModbusTcpbyIp.WriteSunModbusTcpStrAll(ip,
	 * port, 1, "0000","54"); } }
	 */
	/**
	 * 字符组成16进制的字符
	 * 
	 * @param HexString
	 * @return
	 */
	public static String gethex4(String HexString) {
		String hx = "";
		if (HexString.length() < 2) {
			hx = "000" + HexString;
		} else if (HexString.length() < 3) {
			hx = "00" + HexString;
		} else if (HexString.length() < 4) {
			hx = "0" + HexString;
			;
		} else if (HexString.length() > 4) {
			hx = HexString.substring(4, HexString.length());
		} else {
			hx = HexString;
		}
		return hx;
	}
}
