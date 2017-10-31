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
import java.util.Vector;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import com.zt.common.FileDiretory;
import com.zt.common.SunModbusTcpbyIp;
import com.zt.custom.MyMouseListener;
import com.zt.custom.MyTableModel;
import com.zt.custom.MyTableModelABC;
import com.zt.frame.AnalsDialog;
import com.zt.frame.ParamSetDialog;
import com.zt.frame.ParamSetDialogAll;
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
	private String[] treeName = { "参数列表" };

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
			if (nodeName.equals("变流器RTC时钟")) {
				popupMenu.add(createMenuItemSpec("参数设置", null));
			} else if (nodeName.equals("变流器历史记录")) {
				popupMenu.add(createMenuItem("故障次数增", null));
				popupMenu.addSeparator();
				popupMenu.add(createMenuItem("故障次数减", null));
			} else {
				popupMenu.add(createMenuItem("参数设置", null));
				popupMenu.addSeparator();
				popupMenu.add(createMenuItem("查询", null));
			}

		}

	}

	public JMenuItem createMenuItemSpec(String name, String mnemonic) {
		// 根据名称和快捷键创建menu并添加到menuBar
		JMenuItem menuItem = new JMenuItem(name);
		menuItem.setActionCommand(name);
		menuItem.setSize(100, 20);
		if (mnemonic != null)
			menuItem.setMnemonic(mnemonic.toCharArray()[0]);
		menuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				ParamSetDialogAll pSetDialogAll = new ParamSetDialogAll("RTC");
				pSetDialogAll.setVisible(true);
			}
		});
		return menuItem;
	}

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
					if (addr >= 419 && addr <= 422) {
						ParamSetDialogAll pSetDialogAll = new ParamSetDialogAll("IP");
						pSetDialogAll.setVisible(true);
					} else {
						ParamSetDialog pSetDialog = new ParamSetDialog(codeStr, num, addr, addrName, value, unit,
								datalimitmin, datalimitmax);
						pSetDialog.setVisible(true);
					}
				}
				if (e.getActionCommand().equals("查询")) {
					sendCode(2);
					;
				}
				if (e.getActionCommand().equals("故障次数增")) {
					sendCode(1);
					;
				}
				if (e.getActionCommand().equals("故障次数减")) {
					sendCode(2);
					;
				}
			}
		});
		return menuItem;
	}

	public void sendCode(int num) {
		String[] ipCom = saveMessage.getIpAndCom();
		int modbusType = saveMessage.getModbustcp();
		String[] code = codeStr.split(",");
		SunModbusTcpbyIp.WriteSunModbusTcpStrAll(ipCom[0], Integer.parseInt(ipCom[1]), modbusType, "0000", code[num]);
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
					if (codeStr.split(",")[0].equals("变流器RTC时钟")||codeStr.split(",")[0].equals("变流器历史记录")) {
						popupMenu.show(table, e.getX(), e.getY());
					}
				}
				if (e.getClickCount() == 2) {
					int row = table.rowAtPoint(e.getPoint());
					int column = table.columnAtPoint(e.getPoint());
					if (table.isCellEditable(row, 3)) {
						table.setRowSelectionInterval(row, row);// 右击选中高亮
						num = (int) table.getValueAt(row, 0);
						addr = Integer.parseInt((String) table.getValueAt(row, 1));
						addrName = (String) table.getValueAt(row, 2);
						value = (String) table.getValueAt(row, 3);
						unit = (String) table.getValueAt(row, 4);
						datalimitmin = (String) table.getValueAt(row, 7);
						datalimitmax = (String) table.getValueAt(row, 8);
						if (addr >= 419 && addr <= 422) {
							ParamSetDialogAll pSetDialogAll = new ParamSetDialogAll("IP");
							pSetDialogAll.setVisible(true);
						} else {
							ParamSetDialog pSetDialog = new ParamSetDialog(codeStr, num, addr, addrName, value, unit,
									datalimitmin, datalimitmax);
							pSetDialog.setVisible(true);
						}
					}
					int addr;
					String name = "";
					String value = "";
					if (table.getModel().getColumnName(1).equals("参数名称")) {
						addr = Integer.parseInt((String) table.getValueAt(row, column + 4));
						name = (String) table.getValueAt(row, 1);
					} else {
						addr = Integer.parseInt((String) table.getValueAt(row, 1));
						name = (String) table.getValueAt(row, 2);
					}
					value = (String) table.getValueAt(row, column);
					
					
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
		int[] arr = { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 };
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
		if (addr == 73 || addr == 133 || addr == 193) {
			analsSpecialAddr(addr, value, valueSpecical, vector, modbusBits, bits, "4");
		} else if (addr == 499) {
			analsSpecialAddr(addr, value, valueSpecical, vector, modbusBits, bits, "416");
		} else if (addr == 553) {
			analsSpecialAddr(addr, value, valueSpecical, vector, modbusBits, bits, "470");
		} else if (addr == 607) {
			analsSpecialAddr(addr, value, valueSpecical, vector, modbusBits, bits, "524");
		} else if (addr == 100 || addr == 160 || addr == 220) {
			analsSpecialAddr(addr, value, valueSpecical, vector, modbusBits, bits, "31");
		} else if (addr == 526) {
			analsSpecialAddr(addr, value, valueSpecical, vector, modbusBits, bits, "443");
		} else if (addr == 580) {
			analsSpecialAddr(addr, value, valueSpecical, vector, modbusBits, bits, "497");
		} else if (addr == 634) {
			analsSpecialAddr(addr, value, valueSpecical, vector, modbusBits, bits, "551");
		} else {
			for (ModbusBit modbusBit : modbusBits) {
				if (modbusBit.getAddr() != null) {
					if (modbusBit.getAddr().equals(String.valueOf(addr))) {
						Vector<Object> list = new Vector<>();
						list.add(modbusBit.getBit_id());
						list.add(modbusBit.getVar1());
						list.add(bits.substring(15 - modbusBit.getBit_id(), 16 - modbusBit.getBit_id()));
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
						vector.addElement(list);
					}
				}
			}
		}

		table.updateUI();
		table.validate();
	}

	public void analsSpecialAddr(int addr, String value, String valueSpecical, Vector<Vector<Object>> vector,
			List<ModbusBit> modbusBits, String bits, String pointId) {
		int tenValueSpecial = Integer.parseInt(valueSpecical.substring(0, valueSpecical.length() - 1), 16);
		if (tenValueSpecial == 51) {
			// 获取84位的bit位置
			for (ModbusBit modbusBit : modbusBits) {
				if (modbusBit.getPoint_id() != null && modbusBit.getPoint_id().equals(pointId)) {
					Vector<Object> list = new Vector<>();
					list.add(modbusBit.getBit_id());
					list.add(modbusBit.getVar1());
					list.add(bits.substring(15 - modbusBit.getBit_id(), 16 - modbusBit.getBit_id()));
					vector.addElement(list);
				}
			}
		} else if (tenValueSpecial == 68) {
			for (ModbusBit modbusBit : modbusBits) {
				if (modbusBit.getAddr() != null && modbusBit.getAddr().equals(String.valueOf(addr))) {
					Vector<Object> list = new Vector<>();
					list.add(modbusBit.getBit_id());
					list.add(modbusBit.getVar1());
					list.add(bits.substring(15 - modbusBit.getBit_id(), 16 - modbusBit.getBit_id()));
					vector.addElement(list);
				}
			}
		} else {
			Vector<Object> list = new Vector<>();
			list.add(0);
			list.add("不解析");
			list.add("0");
			vector.addElement(list);
		}
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

	public void createJTree() {
		// 创建根节点，所有的节点都是挂在根节点下面的
		root = new DefaultMutableTreeNode();
		model = new DefaultTreeModel(root);
		DemoNode cate = new DemoNode(produceImage("arrow_left.png"), treeName[0]);

		cate.add(new DemoNode("系统状态参数"));
		cate.add(new DemoNode("系统参数", ",8CH"));
		cate.add(new DemoNode("柜体参数"));
		cate.add(new DemoNode("变流器历史记录", "85H,86H"));
		cate.add(new DemoNode("变流器RTC时钟", ""));

		root.add(cate);

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
					int[] arr = { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 };
					if (node.getName().equals("柜体参数") || node.getName().equals("变流器历史记录")) {
						table.setModel(new MyTableModelABC(vect));
						DefaultTableColumnModel dcm = (DefaultTableColumnModel) table.getColumnModel();
						DefaultTableCellRenderer dCellRenderer1 = new DefaultTableCellRenderer();
						dCellRenderer1.setForeground(Color.blue);
						DefaultTableCellRenderer dCellRenderer2 = new DefaultTableCellRenderer();
						dCellRenderer2.setForeground(Color.green);
						DefaultTableCellRenderer dCellRenderer3 = new DefaultTableCellRenderer();
						dCellRenderer3.setForeground(Color.red);
						dcm.getColumn(2).setCellRenderer(dCellRenderer1);
						dcm.getColumn(3).setCellRenderer(dCellRenderer2);
						dcm.getColumn(4).setCellRenderer(dCellRenderer3);
						hideAndChangeColumnABC(table, arr);
					} else {
						table.setModel(new MyTableModel(vect));
						hideAndChangeColumn(table, arr);
					}

					sec.setjTableAndNodeName(table, node.getName());
					addPopupMenu(node.getName());
					codeStr = node.getName() + "," + node.getArrayStr();
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
	public void hideAndChangeColumn(JTable table, int[] arr) {
		DefaultTableColumnModel dcm = (DefaultTableColumnModel) table // 获取列模型
				.getColumnModel();
		for (int i = 0; i < arr.length; i++) {
			if (arr[i] == 0)
				columnChange(dcm, i, 60);
			if (arr[i] == 1)
				columnChange(dcm, i, 0);
			if (arr[i] == 3)
				columnChange(dcm, i, 150);
			if (arr[i] == 4)
				columnChange(dcm, i, 150);
			if (arr[i] == 5)
				columnChange(dcm, i, 150);
			if (arr[i] == 6)
				columnChange(dcm, i, 150);
			if (arr[i] == 7)
				columnChange(dcm, i, 0);
			if (arr[i] == 8)
				columnChange(dcm, i, 0);
			if (arr[i] == 9)
				columnChange(dcm, i, 0);

		}
	}

	/**
	 * 隐藏JTable中不需要显示的列
	 * 
	 * @param table
	 *            需要隐藏列的JTable
	 * @param colIndex
	 *            需要隐藏的列的下标（JTable列下标从0开始）
	 */
	public void hideAndChangeColumnABC(JTable table, int[] arr) {
		DefaultTableColumnModel dcm = (DefaultTableColumnModel) table // 获取列模型
				.getColumnModel();
		for (int i = 0; i < arr.length; i++) {
			if (arr[i] == 0)
				columnChange(dcm, i, 60);
			if (arr[i] == 2)
				columnChange(dcm, i, 150);
			if (arr[i] == 3)
				columnChange(dcm, i, 150);
			if (arr[i] == 4)
				columnChange(dcm, i, 150);
			if (arr[i] == 5)
				columnChange(dcm, i, 150);
			if (arr[i] == 6)
				columnChange(dcm, i, 0);
			if (arr[i] == 7)
				columnChange(dcm, i, 0);
			if (arr[i] == 8)
				columnChange(dcm, i, 0);
			if (arr[i] == 9)
				columnChange(dcm, i, 0);

		}
	}

	public void columnChange(DefaultTableColumnModel dcm, int i, int width) {
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
