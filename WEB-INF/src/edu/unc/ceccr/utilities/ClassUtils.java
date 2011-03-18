package edu.unc.ceccr.utilities;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;

import java.util.ArrayList;
import java.util.List;

public class ClassUtils {

  private ClassUtils() {}

  //converts any object to a string containing the name and value of each variable.
  public static String varNamesToString( Object o ) {
    ArrayList list = new ArrayList();
    ClassUtils.varNamesToString( o, o.getClass(), list );
    return o.getClass().getName().concat( list.toString() );
  }

  public static String varValuesToString( Object o ) {
    ArrayList list = new ArrayList();
    ClassUtils.varValuesToString( o, o.getClass(), list );
    return list.toString();
  }
  
  private static void varNamesToString( Object o, Class clazz, List list ) {
    Field f[] = clazz.getDeclaredFields();
    AccessibleObject.setAccessible( f, true );
    for ( int i = 0; i < f.length; i++ ) {
      try {
    	list.add(f[i].getName());
      }
      catch ( Exception e ) { e.printStackTrace(); }
      }
      if ( clazz.getSuperclass().getSuperclass() != null ) {
    	  varNamesToString( o, clazz.getSuperclass(), list );
      }
  }
  
  private static void varValuesToString( Object o, Class clazz, List list ) {
	    Field f[] = clazz.getDeclaredFields();
	    AccessibleObject.setAccessible( f, true );
	    for ( int i = 0; i < f.length; i++ ) {
	      try {
	    	  if(f[i].getClass().equals(new String().getClass())){
	    		  list.add("'" + f[i].get(o) + "'");
	    	  }
	    	  else{
	            list.add(f[i].get(o));
	    	  }
	      }
	      catch ( IllegalAccessException e ) { e.printStackTrace(); }
	      }
	      if ( clazz.getSuperclass().getSuperclass() != null ) {
	    	  varValuesToString( o, clazz.getSuperclass(), list );
	      }
	  }
  
  
  
  public static String toString( Object o ) {
    ArrayList list = new ArrayList();
    ClassUtils.toString( o, o.getClass(), list );
    return o.getClass().getName().concat( list.toString() );
  }

  private static void toString( Object o, Class clazz, List list ) {
    Field f[] = clazz.getDeclaredFields();
    AccessibleObject.setAccessible( f, true );
    for ( int i = 0; i < f.length; i++ ) {
      try {
        list.add( f[i].getName() + "=" + f[i].get(o) );
      }
      catch ( IllegalAccessException e ) { e.printStackTrace(); }
      }
      if ( clazz.getSuperclass().getSuperclass() != null ) {
         toString( o, clazz.getSuperclass(), list );
      }
  }
}
