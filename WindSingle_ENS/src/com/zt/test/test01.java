package com.zt.test;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Date;
import com.zt.pojo.ModbusApp;

public class test01 {
	
	public static void main(String[] args) {
		ModbusApp mp = new ModbusApp();
		Field[] field = mp.getClass().getDeclaredFields(); // 获取实体类的所有属性，返回Field数组
        try {
            for (int j = 0; j < field.length; j++) { // 遍历所有属性
                String name = field[j].getName(); // 获取属性的名字
                name = name.substring(0, 1).toUpperCase() + name.substring(1); // 将属性的首字符大写，方便构造get，set方法
                String type = field[j].getGenericType().toString(); // 获取属性的类型
                if (type.equals("class java.lang.String")) { // 如果type是类类型，则前面包含"class "，后面跟类名
                    Method m = mp.getClass().getMethod("get" + name);
                    String value = (String) m.invoke(mp); // 调用getter方法获取属性值
                    if (value == null) {
                        m = mp.getClass().getMethod("set"+name,String.class);
                        m.invoke(mp, "");
                    }
                }
                if (type.equals("class java.lang.Integer")) {
                    Method m = mp.getClass().getMethod("get" + name);
                    Integer value = (Integer) m.invoke(mp);
                    if (value == null) {
                        m = mp.getClass().getMethod("set"+name,Integer.class);
                        m.invoke(mp, 0);
                    }
                }
                if (type.equals("class java.lang.Boolean")) {
                    Method m = mp.getClass().getMethod("get" + name);
                    Boolean value = (Boolean) m.invoke(mp);
                    if (value == null) {
                        m = mp.getClass().getMethod("set"+name,Boolean.class);
                        m.invoke(mp, false);
                    }
                }
                if (type.equals("class java.util.Date")) {
                    Method m = mp.getClass().getMethod("get" + name);
                    Date value = (Date) m.invoke(mp);
                    if (value == null) {
                        m = mp.getClass().getMethod("set"+name,Date.class);
                        m.invoke(mp, new Date());
                    }
                }// 如果有需要,可以仿照上面继续进行扩充,再增加对其它类型的判断
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
        System.err.println(mp);
	}
}
