package com.zt.frame;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import com.zt.common.FileDiretory;
import com.zt.panel.Debug.ControlPanel;
import com.zt.panel.Debug.ParamGivenPanel;
import com.zt.panel.Debug.StatePanel;
import com.zt.panel.Debug.WindParamGivenPanel;
import com.zt.thread.THD_DEBUG;

public class DebugFrame extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private THD_DEBUG tDebug;
	private ControlPanel controlPanel;
	private StatePanel statePanel;
	public static volatile DebugFrame dFrame;
	public static DebugFrame getdFrame() {
		// 对象实例化时与否判断（不使用同步代码块，instance不等于null时，直接返回对象，提高运行效率）
        if (dFrame == null) {
            //同步代码块（对象未初始化时，使用同步代码块，保证多线程访问时对象在第一次创建后，不再重复被创建）
            synchronized (DebugFrame.class) {
                //未初始化，则初始instance变量
                if (dFrame == null) {
                    dFrame = new DebugFrame();
                    dFrame.createUI();
//                    dFrame.setVisible(true);
                }   
            }   
        }   
        return dFrame;   
	}
	private DebugFrame() {
		
	}
	public void createUI(){
		dFrame.setUIFont();
		dFrame.setTitle("控制面板");
		dFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		dFrame.setResizable(false);
		dFrame.setIconImage(new ImageIcon(FileDiretory.getCurrentDir()+"/images/SC.png").getImage());
		dFrame.setBounds(100, 100,750, 450);
		dFrame.contentPane = new JPanel();
		dFrame.contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		dFrame.contentPane.setLayout(new BorderLayout());
		dFrame.setContentPane(contentPane);
		dFrame.contentPane.add(buildJTabbedPane(), BorderLayout.CENTER);
		dFrame.addWindowListener(new WindowAdapter() {
			public void windowClosed(WindowEvent e) {
				tDebug.stopThread();//关闭窗口时，停止线程
			}
		});
	}
	
	public JTabbedPane buildJTabbedPane(){
		  JTabbedPane tab = new JTabbedPane(JTabbedPane.TOP); 
		  ParamGivenPanel paramGivenPanel = new ParamGivenPanel(this.getWidth(),this.getHeight());
		  WindParamGivenPanel windParamGivenPanel = new WindParamGivenPanel(this.getWidth(),this.getHeight());
		  statePanel = new StatePanel(this.getWidth(),this.getHeight());
		  controlPanel = new ControlPanel(this.getWidth(),this.getHeight());
		  tab.add(paramGivenPanel,"参数给定");
		  tab.add(windParamGivenPanel,"风场模式参数给定");
		  tab.add(statePanel,"状态控制");
		  tab.add(controlPanel,"控制模式");
		return tab;
	}
	
	public void setUIFont()
	{
		Font f = new Font("宋体",Font.PLAIN,14);
		String   names[]={ "Label", "CheckBox", "PopupMenu","MenuItem", "CheckBoxMenuItem",
				"JRadioButtonMenuItem","ComboBox", "Button", "Tree", "ScrollPane",
				"TabbedPane", "EditorPane", "TitledBorder", "Menu", "TextArea",
				"OptionPane", "MenuBar", "ToolBar", "ToggleButton", "ToolTip",
				"ProgressBar", "TableHeader", "Panel", "List", "ColorChooser",
				"PasswordField","TextField", "Table", "Label", "Viewport",
				"RadioButtonMenuItem","RadioButton", "DesktopPane", "InternalFrame"
		}; 
		for (String item : names) {
			 UIManager.put(item+ ".font",f); 
		}
	}
	public void setDebugThread(THD_DEBUG tDebug) {
		this.tDebug = tDebug;
	}
	
	public ControlPanel getControlPanel(){
		return controlPanel;
	}
	public StatePanel getStatePane(){
		return statePanel;
	}
}
