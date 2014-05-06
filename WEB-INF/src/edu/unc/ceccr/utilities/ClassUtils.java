package edu.unc.ceccr.utilities;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ClassUtils {

    private ClassUtils() {
    }

    //converts any object to a string containing the name and value of each variable.
    public static String varNamesToString(Object o) {
        ArrayList<String> list = new ArrayList<String>();
        ClassUtils.varNamesToString(o, o.getClass(), list);
        return o.getClass().getName().concat(list.toString());
    }

    public static String varValuesToString(Object o) {
        ArrayList<Object> list = new ArrayList<Object>();
        ClassUtils.varValuesToString(o, o.getClass(), list);
        return list.toString();
    }

    private static void varNamesToString(Object o, Class<?> clazz, List<String> list) {
        Field f[] = clazz.getDeclaredFields();
        AccessibleObject.setAccessible(f, true);
        for (int i = 0; i < f.length; i++) {
            try {
                list.add(f[i].getName());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (clazz.getSuperclass().getSuperclass() != null) {
            varNamesToString(o, clazz.getSuperclass(), list);
        }
    }

    private static void varValuesToString(Object o, Class<?> clazz, List<Object> list) {
        Field f[] = clazz.getDeclaredFields();
        AccessibleObject.setAccessible(f, true);
        for (int i = 0; i < f.length; i++) {
            try {
                if (f[i].getType().equals(new String().getClass()) ||
                        f[i].getType().equals(new Date().getClass()) ||
                        f[i].getType().equals(byte.class)) {
                    list.add("'" + f[i].get(o) + "'");
                } else {
                    list.add(f[i].get(o));
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        if (clazz.getSuperclass().getSuperclass() != null) {
            varValuesToString(o, clazz.getSuperclass(), list);
        }
    }


    public static String toString(Object o) {
        ArrayList<String> list = new ArrayList<String>();
        ClassUtils.toString(o, o.getClass(), list);
        return o.getClass().getName().concat(list.toString());
    }

    private static void toString(Object o, Class<?> clazz, List<String> list) {
        Field f[] = clazz.getDeclaredFields();
        AccessibleObject.setAccessible(f, true);
        for (int i = 0; i < f.length; i++) {
            try {
                list.add(f[i].getName() + "=" + f[i].get(o));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        if (clazz.getSuperclass().getSuperclass() != null) {
            toString(o, clazz.getSuperclass(), list);
        }
    }
  /*
  public static Object getObjectFromCsvLine(String csvLine, Class clazz) throws Exception{
      Field f[] = clazz.getDeclaredFields();
      AccessibleObject.setAccessible( f, true );
      String[] fieldValues = csvLine.split(",");

      for(int i = 0; i < f.length; i++){
          Field field = f[i];
          if(field.getClass().equals(String.class)){
              
          }
          else if(field.getClass().equals(Date.class)){
              
          }
          else if(field.getClass().equals(int.class)){
              
          }
          else if(field.getClass().equals(float.class)){
              
          }
      }
      Object arglist[] = new Object[fieldValues.length];
      
      arglist[0] = new Integer(37);
      arglist[1] = new Integer(47);
      Object retobj = ct.newInstance(arglist);
      
      return retobjo;
  }
  */
}
