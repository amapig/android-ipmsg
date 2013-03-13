package com.ipmsg.util;

public class FileSizeUtil
{
      public static String toConvertMB(int size)
      {
    	 if(size>1024*1024)
    	 {
    		 Double dsize=(double)size/(1024*1024);
    		 
    		 
    		 return new java.text.DecimalFormat("#.00").format(dsize)+" MB";
    	 }else if(size>1024)
    	 {
    		 Double dsize=(double)size/(1024);
     
    		 return new java.text.DecimalFormat("#.00").format(dsize)+" KB";
    	 }else
    	 {
    		 return String.valueOf(size)+" B";
    	 }
      }
}
