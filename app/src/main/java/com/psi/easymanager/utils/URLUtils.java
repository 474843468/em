/**
 * URL接入签名算法类
 */
package com.psi.easymanager.utils;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

public class URLUtils {
		
		/** 
	    *  
	    * 方法用途: 对所有传入参数按照字段名的 ASCII 码从小到大排序（字典序），并且生成url参数串<br> 
	    * 实现步骤: <br> 
	    *  
	    * @param paraMap   要排序的Map对象 
	    * @param secretkey   签名密钥 
	    * @param urlEncode   是否需要URLENCODE 
	    * @return 
	    */  
	   public static String formatUrlMap(Map<String, String> paraMap, boolean urlEncode,String secretkey){  
	   
	       String buff = "";  
	       Map<String, String> tmpMap = paraMap;  
	       try {  
	     
	           List<Map.Entry<String, String>> infoIds = new ArrayList<Map.Entry<String, String>>(tmpMap.entrySet());  
	           // 对所有传入参数按照字段名的 ASCII 码从小到大排序（字典序）  
	           Collections.sort(infoIds, new Comparator<Map.Entry<String, String>>(){  
	               @Override  
	               public int compare(Map.Entry<String, String> o1, Map.Entry<String, String> o2){
	                   return (o1.getKey()).toString().compareTo(o2.getKey());  
	               }  
	           });  
	           // 构造URL 键值对的格式  
	           StringBuilder buf = new StringBuilder();  
	           for (Map.Entry<String, String> item : infoIds) {    
	               if (StringUtils.isNotBlank(item.getKey())){   
	              
	                   String key = item.getKey();  
	                   String val = item.getValue(); 
	                   //如果参数值为空或者空字符串，则不参与签名
	                   if(StringUtils.isBlank(val)){
	                	   continue;
	                   }
	                   if (urlEncode){  
	                       val = URLEncoder.encode(val, "utf-8");  
	                   } 
	                   buf.append(key + "=" + val);  
	                   buf.append("&");  
	               }  
	  
	           } 
	           buff = buf.append("key=" + secretkey).toString();
	       } catch (Exception e)  {   
	          return null;  
	       }  
	       return buff;  
	   }  
	
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub  
        //字典序列排序  
        Map<String,String> paraMap = new HashMap<String,String>();  
        paraMap.put("total_fee","200");  
        paraMap.put("appid", "wxd678efh567hg6787");  
        paraMap.put("id", null);
        paraMap.put("idss", "");
        paraMap.put("idsss", "123");
        paraMap.put("body", "腾讯充值中心-QQ会员充值");  
        paraMap.put("out_trade_no","20150806125346");  
        String url = formatUrlMap(paraMap, true,"IPW20161228WZTESTGOOD");  
        System.out.println(DigestsUtils.md5(url).toUpperCase());
        System.out.println(url);  

	}

}
