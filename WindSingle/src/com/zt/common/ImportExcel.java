package com.zt.common;

import java.io.File;  
import java.io.IOException;  
import javax.swing.JFileChooser;  
import javax.swing.filechooser.FileNameExtensionFilter;  
import jxl.Cell;  
import jxl.Sheet;  
import jxl.Workbook;  
import jxl.read.biff.BiffException;  
 
/*
 * 导入Excel
 * */
public class ImportExcel {  
  
    private Object[][] obj;  
  
    public ImportExcel() throws IOException, BiffException {  
        File excelFile = FileChooser();  
        Workbook workBook = Workbook.getWorkbook(excelFile);  
        // 得到工作簿所有的工作表对象  
        Sheet sheet = workBook.getSheet(0);//默认选择第一个表,可根据需求遍历所有表  
        // 遍历所有行  
        int rowsCount = sheet.getRows();  
        int columnsCount = sheet.getRow(0).length;  
        obj = new Object[rowsCount - 1][columnsCount];  
        for (int i = 1; i < sheet.getRows(); i++) {  
            // 得到所有列，在输出列中的内容  
            Cell[] cells = sheet.getRow(i);  
            for (int j = 0; j < cells.length; j++) {  
                obj[i - 1][j] = cells[j].getContents() + "";  
            }  
        }  
        workBook.close();  
    }  
  
    private File FileChooser() {  
        JFileChooserDemo fc = new JFileChooserDemo("C:");  
        //是否可多选    
        fc.setMultiSelectionEnabled(false);  
        //选择模式，可选择文件和文件夹    
        //fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);    
        fc.setFileSelectionMode(JFileChooser.FILES_ONLY);  
        //fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);    
        //设置是否显示隐藏文件    
        fc.setFileHidingEnabled(true);  
        fc.setAcceptAllFileFilterUsed(false);  
        //设置文件筛选器    
        // fc.setFileFilter(new MyFilter("java"));    
        fc.setFileFilter(new FileNameExtensionFilter("Excel文件(*.xls)", "xls"));  
        int returnValue = fc.showOpenDialog(null);  
        if (returnValue == JFileChooser.APPROVE_OPTION) {  
            File file = fc.getSelectedFile();  
            file = new File(file.getAbsolutePath());  
            return file;  
        }  
        return null;  
    }  
  
    /** 
     * @return the obj 
     */  
    public Object[][] getObj() {  
        return obj;  
    }  
}  
