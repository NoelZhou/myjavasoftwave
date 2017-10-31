package com.zt.common;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
public class ReadModbusXml {
	
	public List<Object> setLists(Class<?> nClass,String xmlpath){
		return setLists(-1, nClass, xmlpath);
	}
	/**
	 * 获取协议数组
	 * @param nClass
	 * @param xmlpath
	 * @return
	 */
	public  List<Object> setLists(int modbustcp,Class<?> nClass,String xmlpath){

		List<Object> lists = new ArrayList<>();
		SAXReader reader = new SAXReader();
		try {
			File file = new File(xmlpath);
			Document document;
			document = reader.read(file);
			Element root = document.getRootElement();
			// 获取解析定义格式
			Element foo;   
			for (Iterator<?> i = root.elementIterator("RECORD"); i.hasNext();) {   
		      foo = (Element) i.next();
		      Object c = nClass.newInstance();
		      try {
		    	  Field[] field = nClass.getDeclaredFields(); // 获取实体类的所有属性，返回Field数组
		            for (int j = 0; j < field.length; j++) { // 遍历所有属性
		                String name = field[j].getName(); // 获取属性的名字
		                String upName = name.substring(0, 1).toUpperCase() + name.substring(1); // 将属性的首字符大写，方便构造get，set方法
		                String type = field[j].getGenericType().toString(); // 获取属性的类型
		                if (type.equals("class java.lang.String")) { // 如果type是类类型，则前面包含"class "，后面跟类名
		                     Method   m = nClass.getMethod("set"+upName,String.class);
		                       m.invoke(c, foo.elementText(name));
		                }
		                if (type.equals("int")) {
		                	Method   m = nClass.getMethod("set"+upName,int.class);
		                        m.invoke(c, Integer.parseInt(confValueChange(name,foo.elementText(name))));
		                }
		            }
		        } catch (NoSuchMethodException e) {
		            e.printStackTrace();
		        } catch (SecurityException e) {
		            e.printStackTrace();
		        } catch (IllegalAccessException e) {
		            e.printStackTrace();
		        } catch (IllegalArgumentException e) {
		            e.printStackTrace();
		        } catch (InvocationTargetException e) {
		            e.printStackTrace();
		        }
		      
		      lists.add(c);
			}   
		} catch (Exception e) {
			e.printStackTrace();
		}
		return  lists;
	}

	public String confValueChange(String conf, String value){
		if(conf.equals("cof")){
			if(value.indexOf("/")!=-1||value.indexOf(".")!=-1){
				return "1";
			}
		}
		return value;
	}
}
