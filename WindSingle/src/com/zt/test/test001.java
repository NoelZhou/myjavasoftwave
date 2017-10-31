package com.zt.test;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.KeyStroke;

import com.zt.common.FileDiretory;

/*
 *模仿实现eclipse中编辑区域的弹出菜单 
 */
public class test001 extends JFrame {

	private static final long serialVersionUID = 1L;
	JPopupMenu popupMenu;
    
    public test001(){
        JMenuItem menuItem;//用作临时变量
        
        popupMenu = new JPopupMenu();
        popupMenu.add(createMenuItem("Undo Typing", null, new ImageIcon("resources\\JPopupMenuDemo\\icon\\undoTyping.png"), KeyStroke.getKeyStroke('Z', InputEvent.CTRL_MASK)));
        popupMenu.add(createMenuItem("Revert File", null, null, null));
        popupMenu.add(createMenuItem("Save", null, new ImageIcon("resources\\JPopupMenuDemo\\icon\\save.png"), KeyStroke.getKeyStroke('S', InputEvent.CTRL_MASK)));
        popupMenu.addSeparator();
        popupMenu.add(createMenuItem("Open Declaration", null, null, KeyStroke.getKeyStroke("F3")));
        popupMenu.add(createMenuItem("Open Type Hierarchy", null, null, KeyStroke.getKeyStroke("F4")));
        popupMenu.add(createMenuItem("Open Call Hierarchy", null, null, KeyStroke.getKeyStroke('H', InputEvent.CTRL_MASK | InputEvent.ALT_MASK)));
        popupMenu.add(createMenuItem("Show in Breakcrumb", null, null, KeyStroke.getKeyStroke('B', InputEvent.SHIFT_MASK | InputEvent.ALT_MASK)));
        popupMenu.add(createMenuItem("Quick Outline", null, null, KeyStroke.getKeyStroke('O', InputEvent.CTRL_MASK)));
        popupMenu.add(createMenuItem("Quick Type Hierarchy", null, null, KeyStroke.getKeyStroke('T', InputEvent.CTRL_MASK)));
        popupMenu.add(createMenuNamedOpenWith());
        popupMenu.add(createMenuNamedShowIn());
        popupMenu.addSeparator();
        menuItem = popupMenu.add(createMenuItem("Cut", null, null, KeyStroke.getKeyStroke('X', InputEvent.CTRL_MASK)));
        menuItem.setEnabled(false);
        menuItem = popupMenu.add(createMenuItem("Copy", null, null, KeyStroke.getKeyStroke('C', InputEvent.CTRL_MASK)));
        menuItem.setEnabled(false);
        popupMenu.add(createMenuItem("Copy Qualified Name", null, null, null));
        popupMenu.add(createMenuItem("Paste", null, null, KeyStroke.getKeyStroke('V', InputEvent.CTRL_MASK)));
        popupMenu.addSeparator();
        popupMenu.add(createMenuItem("Quick Fix", null, null, KeyStroke.getKeyStroke('1', InputEvent.CTRL_MASK)));
        popupMenu.add(createMenuNamedSource());
        popupMenu.add(createMenuNamedRefactor());
        popupMenu.add(createMenuNamedLocalHistory());
        popupMenu.addSeparator();
        popupMenu.add(createMenuNamedReferences());
        popupMenu.add(createMenuNamedDeclarations());
        popupMenu.add(createMenuItem("Add To Snippets", null, new ImageIcon("resources\\JPopupMenuDemo\\icon\\addToSnippets.png"), null));
        popupMenu.addSeparator();
        popupMenu.add(createMenuNamedDebugAs());
        popupMenu.add(createMenuNamedRunAs());
        popupMenu.add(createMenuItem("Validate", null, null, null));
        popupMenu.add(createMenuNamedTeam());
        popupMenu.add(createMenuNamedCompareWith());
        popupMenu.add(createMenuNamedReplaceWith());
        popupMenu.addSeparator();
        popupMenu.add(createMenuItem("Prefrences", null, null, null));
        popupMenu.addSeparator();
        popupMenu.add(createMenuItem("Remove from Context", null, null, KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, InputEvent.CTRL_MASK | InputEvent.ALT_MASK | InputEvent.SHIFT_MASK)));
        
        JPanel panel = new JPanel();
        panel.setComponentPopupMenu(popupMenu);
        add(panel);
    }
    
    public static JMenu createMenuNamedReplaceWith() {
        /*
         * * 创建名称为ReplaceWith的menu
         */
        JMenu menu = createMenu("Replace With", null);    
        menu.add(createMenuItem("Local History", null, null, null));
        menu.add(createMenuItem("Previous From History", null, null, null));
        
        return menu;
    }
    
    public static JMenu createMenuNamedCompareWith() {
        /*
         * * 创建名称为CompareWith的menu
         */
        JMenu menu = createMenu("Compare With", null);    
        JMenuItem menuItem = menu.add(createMenuItem("Each Other", null, null, null));
        menuItem.setEnabled(false);
        menu.add(createMenuItem("Local History", null, null, null));
        
        return menu;
    }
    
    public static JMenu createMenuNamedTeam() {
        /*
         * * 创建名称为Team的menu
         */
        JMenu menu = createMenu("Team", null);    
        menu.add(createMenuItem("Apply Patch", null, null, null));
        menu.add(createMenuItem("Show Local History", null, null, null));
        JMenuItem menuItem= menu.add(createMenuItem("Share Project", null, null, null));
        menuItem.setEnabled(false);
        return menu;
    }
    
    public static JMenu createMenuNamedRunAs() {
        /*
         * * 创建名称为RunAs的menu
         */
        JMenu menu = createMenu("Run As", null);
        menu.add(createMenuItem("Java Application", null, new ImageIcon("resources\\JPopupMenuDemo\\icon\\javaApplication.png"),KeyStroke.getKeyStroke('X', InputEvent.ALT_MASK | InputEvent.SHIFT_MASK)));
        menu.addSeparator();
        menu.add(createMenuItem("Debug Configurations", null, null, null));
        
        return menu;
    }
    
    public static JMenu createMenuNamedDebugAs() {
        /*
         * * 创建名称为DebugAs的menu
         */
        JMenu menu = createMenu("Debug As", null);    
        menu.add(createMenuItem("Java Application", null, new ImageIcon("resources\\JPopupMenuDemo\\icon\\javaApplication.png"),KeyStroke.getKeyStroke('D', InputEvent.ALT_MASK | InputEvent.SHIFT_MASK)));
        menu.addSeparator();
        menu.add(createMenuItem("Debug Configurations", null, null, null));
        
        return menu;
    }
    
    public static JMenu createMenuNamedDeclarations() {
        /*
         * * 创建名称为Declarations的menu
         */
        JMenu menu = createMenu("Declarations", null);    
        menu.add(createMenuItem("workSpace", null, new ImageIcon("resources\\JPopupMenuDemo\\icon\\declarationsIcon.png"),KeyStroke.getKeyStroke('G', InputEvent.CTRL_MASK | InputEvent.SHIFT_MASK)));
        menu.add(createMenuItem("Project", null, new ImageIcon("resources\\JPopupMenuDemo\\icon\\declarationsIcon.png"),null));
        menu.add(createMenuItem("Hierarchy", null, new ImageIcon("resources\\JPopupMenuDemo\\icon\\declarationsIcon.png"),null));
        menu.addSeparator();
        menu.add(createMenuItem("WorkingSet", null, new ImageIcon("resources\\JPopupMenuDemo\\icon\\declarationsIcon.png"),null));

        return menu;
    }
    
    public static JMenu createMenuNamedReferences() {
        /*
         * * 创建名称为References的menu
         */
        JMenu menu = createMenu("References", null);    
        menu.add(createMenuItem("workSpace", null, new ImageIcon("resources\\JPopupMenuDemo\\icon\\ReferencesIcon.png"),KeyStroke.getKeyStroke('G', InputEvent.CTRL_MASK | InputEvent.SHIFT_MASK)));
        menu.add(createMenuItem("Project", null, new ImageIcon("resources\\JPopupMenuDemo\\icon\\ReferencesIcon.png"),null));
        menu.add(createMenuItem("Hierarchy", null, new ImageIcon("resources\\JPopupMenuDemo\\icon\\ReferencesIcon.png"),null));
        menu.addSeparator();
        menu.add(createMenuItem("WorkingSet", null, new ImageIcon("resources\\JPopupMenuDemo\\icon\\ReferencesIcon.png"),null));

        return menu;
    }
    
    public static JMenu createMenuNamedLocalHistory() {
        /*
         * * 创建名称为LocalHistory的menu
         */
        JMenu menu = createMenu("Local History", null);    
        menu.add(createMenuItem("Compare With", null, null,null));
        menu.add(createMenuItem("Replace With Previous", null, null,null));
        menu.add(createMenuItem("Replace With", null, null,null));
        menu.add(createMenuItem("Restore From", null, null,null));

        return menu;
    }
    
    public static JMenu createMenuNamedRefactor() {
        /*
         * * 创建名称为Refactor的menu
         */
        JMenu menu = createMenu("Refactor", null);    
        menu.add(createMenuItem("Move", null, null,KeyStroke.getKeyStroke('V', InputEvent.SHIFT_MASK | InputEvent.ALT_MASK)));
        menu.addSeparator();
        menu.add(createMenuItem("Change Method Signature", null, null,KeyStroke.getKeyStroke('C', InputEvent.SHIFT_MASK | InputEvent.ALT_MASK)));
        menu.addSeparator();
        menu.add(createMenuItem("Extract Interface", null, null,null));
        menu.add(createMenuItem("Extract Superclass", null, null,null));
        menu.add(createMenuItem("Use Suppertype where Possible", null, null,null));
        menu.add(createMenuItem("Pull Up", null, null,null));
        menu.add(createMenuItem("Push Down", null, null,null));
        menu.addSeparator();
        menu.add(createMenuItem("Extract Class", null, null,null));
        menu.add(createMenuItem("Introduce Parameter Object", null, null,null));
        menu.addSeparator();
        menu.add(createMenuItem("Infer Generic Type Arguments", null, null,null));

        return menu;
    }
    
    public static JMenu createMenuNamedSource() {
        /*
         * * 创建名称为Source的menu
         */
        JMenu menu = createMenu("Source", null);    
        menu.add(createMenuItem("Toggle Comment", null, null,KeyStroke.getKeyStroke('/', InputEvent.CTRL_MASK)));
        menu.add(createMenuItem("Remove Bolck Comment", null, null,KeyStroke.getKeyStroke('\\', InputEvent.CTRL_MASK | InputEvent.SHIFT_MASK)));
        menu.add(createMenuItem("Generate Element Comment", null, null,KeyStroke.getKeyStroke('J', InputEvent.SHIFT_MASK | InputEvent.ALT_MASK)));
        menu.addSeparator();
        menu.add(createMenuItem("Corrent Indentation", null, null, KeyStroke.getKeyStroke('I', InputEvent.CTRL_MASK)));
        menu.add(createMenuItem("Format Element", null, null,null));
        menu.add(createMenuItem("Format ", null, null,KeyStroke.getKeyStroke('F', InputEvent.SHIFT_MASK | InputEvent.ALT_MASK)));
        menu.addSeparator();
        menu.add(createMenuItem("Add Import", null, null,KeyStroke.getKeyStroke('M', InputEvent.CTRL_MASK | InputEvent.SHIFT_MASK)));
        menu.add(createMenuItem("Organize Imports", null, null,KeyStroke.getKeyStroke('O', InputEvent.CTRL_MASK | InputEvent.SHIFT_MASK)));
        menu.add(createMenuItem("Sort Members", null, null,null));
        menu.add(createMenuItem("Clean Up", null, null,null));
        menu.addSeparator();
        menu.add(createMenuItem("Override/Implement Methods", null, null,null));
        menu.add(createMenuItem("Generate Getters and Setters", null, null,null));
        menu.add(createMenuItem("Generate Delegate Methods", null, null,null));
        menu.add(createMenuItem("Generate hashCode() and equals()", null, null,null));
        menu.add(createMenuItem("Generate toString()", null, null,null));
        menu.add(createMenuItem("Generate Constructor using Fields", null, null,null));
        menu.add(createMenuItem("Generate Constructor from Superclass", null, null,null));
        menu.addSeparator();
        menu.add(createMenuItem("Externalize Strings", null, null,null));

        return menu;
    }
    
    public static JMenu createMenuNamedShowIn() {
        /*
         * * 创建名称为ShowIn的menu
         */
        JMenu menu = createMenu("Show In", null);    
        menu.add(createMenuItem("Git Repositories", null, new ImageIcon("resources\\JPopupMenuDemo\\icon\\gitRepositories.png"), null));
        menu.add(createMenuItem("Package Explorer", null, new ImageIcon("resources\\JPopupMenuDemo\\icon\\packageExplorer.png"), null));
        menu.add(createMenuItem("History", null, new ImageIcon("resources\\JPopupMenuDemo\\icon\\history.png"), null));
        menu.add(createMenuItem("Navigator", null, new ImageIcon("resources\\JPopupMenuDemo\\icon\\navigator.png"), null));
        menu.add(createMenuItem("Project Explorer", null, new ImageIcon("resources\\JPopupMenuDemo\\icon\\projectExplorer.png"), null));
        menu.add(createMenuItem("Outline", null, new ImageIcon("resources\\JPopupMenuDemo\\icon\\outline.png"), null));
        menu.add(createMenuItem("System Explorer", null, null, null));
        menu.add(createMenuItem("Properties", null, new ImageIcon("resources\\JPopupMenuDemo\\icon\\Properties.png"), null));
        return menu;
    }
    
    public static JMenu createMenuNamedOpenWith() {
        /*
         * * 创建名称为OpenWith的menu
         */
        JMenu menu = createMenu("Open With", null);        
        menu.add(createMenuItem("Java Editor", null, new ImageIcon("resources\\JPopupMenuDemo\\icon\\JavaEditor.png"), null));
        menu.add(createMenuItem("Text Editor", null, new ImageIcon("resources\\JPopupMenuDemo\\icon\\textEditor.png"), null));
        menu.add(createMenuItem("Window Builder Editor", null, new ImageIcon("resources\\JPopupMenuDemo\\icon\\windowBuilderEditor.png"), null));
        menu.addSeparator();
        menu.add(createMenuItem("System Editor", null, new ImageIcon("resources\\JPopupMenuDemo\\icon\\systemEditor.png"), null));
        menu.add(createMenuItem("In-Place Editor", null, new ImageIcon("resources\\JPopupMenuDemo\\icon\\inPlaceEditor.png"), null));
        menu.add(createMenuItem("Default Editor", null, null, null));
        menu.addSeparator();
        menu.add(createMenuItem("Other", null, null, null));
        return menu;
    }
    
    public static JMenu createMenu(String name, String mnemonic) {
        /*
         * 根据名称和快捷键创建menu并添加到menuBar
         */
        JMenu menu = new JMenu(name);
        if (mnemonic != null)
            menu.setMnemonic(mnemonic.toCharArray()[0]);
        return menu;
    }

    public static JMenuItem createMenuItem(String name, String mnemonic, Icon icon, KeyStroke keyStroke) {
        /*
         * 根据名称和快捷键创建menu并添加到menuBar
         */
        JMenuItem menuItem = new JMenuItem(name, icon);
        if (mnemonic != null)
            menuItem.setMnemonic(mnemonic.toCharArray()[0]);
        if (keyStroke != null)
            menuItem.setAccelerator(keyStroke);
        return menuItem;
    }
    
    public static void main(String[] args) {
        // TODO Auto-generated method stub
        // 创建窗体
        test001 frame = new test001();
        //指定标题
        frame.setTitle("JPopupMenuDemo");
        //指定icon
        frame.setIconImage(new ImageIcon(FileDiretory.getCurrentDir()+"/images/Set.png").getImage());
        // 关闭窗体后退出程序
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // 自动适配所有控件大小
        frame.setSize(frame.getPreferredSize().width, 200);
        //frame.pack();
        // 设置窗体位置在屏幕中央
        frame.setLocationRelativeTo(null);
        // 显示窗体
        frame.setVisible(true);
    }

}