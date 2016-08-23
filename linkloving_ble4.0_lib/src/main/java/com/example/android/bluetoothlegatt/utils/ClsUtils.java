package com.example.android.bluetoothlegatt.utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import android.bluetooth.BluetoothDevice;
import android.util.Log;
/**
 * 
 * @author cherry Zhang
 *
 */
public class ClsUtils {
	
	
	
	static public void savelog(String processedStr){
		String fullFilename = "D:/test.txt";
	      try {
	          File newTextFile = new File(fullFilename);
	          FileWriter fw;
	          fw = new FileWriter(newTextFile);
	          fw.write(processedStr);
	          fw.close();
	      } catch (IOException e) {
	          // TODO Auto-generated catch block
	          e.printStackTrace();
	      }
	}
	/**
	 * 
	 */
	
	
	
	/**
	 * 使用反射机制对BluetoothDevice枚举其所有方法和常量
	 * @param clsShow
	 * @author cherry Zhang
	 */
	static public void printAllInform(Class clsShow) {  
		
		   try {
		       // 取得所有方法    
		       Method[] hideMethod = clsShow.getMethods();    
		       int i = 0;    
		        for (; i < hideMethod.length; i++) {    
		            Log.e("method name", hideMethod[i].getName());    
		       }    
		        // 取得所有常量    
		        Field[] allFields = clsShow.getFields();    
		        for (i = 0; i < allFields.length; i++) {    
		            Log.e("Field name", allFields[i].getName());    
		        }    
		    } catch (SecurityException e) {    
		        // throw new RuntimeException(e.getMessage());    
		        e.printStackTrace();    
		    } catch (IllegalArgumentException e) {    
		        // throw new RuntimeException(e.getMessage());    
		        e.printStackTrace();    
		    } catch (Exception e) {    
	            // TODO Auto-generated catch block    
		        e.printStackTrace();    
		    }    
		}    

	
	/** 与设备配对   参考源码：platform/packages/apps/Settings.git     
	  * /Settings/src/com/android/settings/bluetooth/CachedBluetoothDevice.java    
      */   
	static public boolean createBond(Class<?> btClass, BluetoothDevice btDevice)           
			throws Exception    {       
		Method createBondMethod = btClass.getMethod("createBond");        
	    Boolean returnValue = (Boolean) createBondMethod.invoke(btDevice);        
	    return returnValue.booleanValue();   
	    }
	
	
	/** 与设备解除配对   参考源码：platform/packages/apps/Settings.git    
	  * /Settings/src/com/android/settings/bluetooth/CachedBluetoothDevice.java     
	  */
	static public boolean removeBond(Class<?> btClass,BluetoothDevice btDevice) 
			throws Exception {  
	    Method removeBondMethod = btClass.getMethod("removeBond");  
	    Boolean returnValue = (Boolean) removeBondMethod.invoke(btDevice);  
	    return returnValue.booleanValue();  
	}
	
	static public boolean setPin(Class<?> btClass, BluetoothDevice btDevice, String str) 
			throws Exception    {       
		try{            
			Method removeBondMethod = btClass.getDeclaredMethod("setPin", new Class[] {byte[].class});
			Boolean returnValue = (Boolean) removeBondMethod.invoke(btDevice, new Object[] {str.getBytes()});           
			Log.e("returnValue", "" + returnValue);       
			}       
		       catch (SecurityException e)        
		       {            
		    	   // throw new RuntimeException(e.getMessage());       
		    	   e.printStackTrace();        }       
		       catch (IllegalArgumentException e)       
		       {
		    	   // throw new RuntimeException(e.getMessage()); 
		    	   e.printStackTrace();        }       
		       catch (Exception e)       
		       {
		    	   // TODO Auto-generated catch block           
		    	   e.printStackTrace();        }        
		        return true;     
		        }
	
		
	
}
