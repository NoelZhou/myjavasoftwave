package com.zt.custom;
import java.awt.Component;

import javax.swing.JCheckBox;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.table.TableCellRenderer;

public class CWCheckBoxRenderer extends JCheckBox implements TableCellRenderer {
  //~ Static fields/initializers -------------------------------------------------------------------------------------

  private static final long serialVersionUID = 1L;

  //~ Instance fields ------------------------------------------------------------------------------------------------

  Border border = new EmptyBorder(1, 2, 1, 2);

  //~ Constructors ---------------------------------------------------------------------------------------------------

  public CWCheckBoxRenderer() {
    super();
    setOpaque(true);
    setHorizontalAlignment(SwingConstants.CENTER);
  }

  //~ Methods --------------------------------------------------------------------------------------------------------

  @Override public Component getTableCellRendererComponent(
    JTable  table,
    Object  value,
    boolean isSelected,
    boolean hasFocus,
    int     row,
    int     column) {
	  setEnabled(true);
	  if (isSelected) {
          setForeground(table.getSelectionForeground());
          super.setBackground(table.getSelectionBackground());
      } else {
          setForeground(table.getForeground());
          setBackground(table.getBackground());
      }
      this.setSelected((Boolean)value);
    return this;
  }
} // end class CWCheckBoxRenderer