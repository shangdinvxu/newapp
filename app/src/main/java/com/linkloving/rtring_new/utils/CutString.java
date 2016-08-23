package com.linkloving.rtring_new.utils;
import android.content.ClipboardManager;
import android.content.Context;
import android.util.Log;

import java.io.UnsupportedEncodingException;

/**
 * 
 * @author zkx
 *
 */
public class CutString {
    private static final String TAG = CutString.class.getSimpleName();
	/**
	 * 判断是否是一个中文汉字  
	 * @param c 字符
	 * @return  true表示是 中文汉字 ，false表示是 英文字母 或 数字
	 * @throws UnsupportedEncodingException 使用了JAVA不支持的编码格式
	 */
	public static boolean isChineseChar(char c)
			throws UnsupportedEncodingException {
	    // 如果字节数大于1，是汉字   
        // 以这种方式区别英文字母和中文汉字并不是十分严谨，但在这个题目中，这样判断已经足够了 
		return String.valueOf(c).getBytes("utf-8").length>1;
	}
	
   public static byte[] stringtobyte(String orignal, int size)   
           throws UnsupportedEncodingException {   
       // 原始字符不为null，也不是空字符串   
       if (orignal != null && !"".equals(orignal)) {   
           // 将原始字符串转换为utf-8编码格式   
           orignal = new String(orignal.getBytes(), "utf-8");   
//           // 要截取的字节数大于0，且小于原始字符串的字节数   
           if (size > 0 && size < orignal.getBytes("utf-8").length) {   
               char c;   
               int k =0;
               for (int i = 0; i < orignal.length(); i++) {   
                   c = orignal.charAt(i);
                   if (CutString.isChineseChar(c)) {   
                       // 遇到中文汉字，截取字节总数减1   
                      if(k+3>size){
                    	  orignal=orignal.substring(0,i-1);
                    	  break; 
                      }
                      else
                    	  k+=3;
                   }else{
                	   //遇到的是英文或者数字
                	   if(k+1>size){
                     	  orignal=orignal.substring(0,i);
                     	  Log.i(TAG , orignal);
                     	  break; 
                       }
                	   else
                		   k++;
                   }
               }   
           }   
       }   
       return orignal.getBytes("utf-8");   
   }
   
   /** 
   * 实现文本复制功能 
   * add by zkx
   * @param content 
   */  
   public static void copy(String content, Context context)  
   {  
   // 得到剪贴板管理器  
	   ClipboardManager cmb = (ClipboardManager)context.getSystemService(Context.CLIPBOARD_SERVICE);  
	   cmb.setText(content.trim());  
   }  
}
