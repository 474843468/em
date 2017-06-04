package com.psi.easymanager.pay.wxpay;

import com.orhanobut.logger.Logger;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

/**
 * 签名算法
 */
public class Signature {
  /**
   * @param o 要参与签名的数据对象
   * @return 签名
   * @throws IllegalAccessException
   */
  public static String getSign(Object o) throws IllegalAccessException {
    ArrayList<String> list = new ArrayList<String>();
    Class<? extends Object> cls = o.getClass();
    Field[] fields = cls.getDeclaredFields();
    for (Field f : fields) {
      f.setAccessible(true);
      if (f.get(o) != null && f.get(o) != "") {
        list.add(f.getName() + "=" + f.get(o) + "&");
      }
    }
    int size = list.size();
    String[] arrayToSort = list.toArray(new String[size]);
    Arrays.sort(arrayToSort, String.CASE_INSENSITIVE_ORDER);
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < size; i++) {
      sb.append(arrayToSort[i]);
    }
    String result = sb.toString();
    result += "key=" + Configure.getKey();
    Logger.i("Sign Before MD5:" + result);
    result = MD5.MD5Encode(result).toUpperCase();
    Logger.i("Sign Result:" + result);
    return result;
  }

  public static String getSign(Map<String, Object> map) {
    ArrayList<String> list = new ArrayList<String>();
    for (Map.Entry<String, Object> entry : map.entrySet()) {
      if (entry.getValue() != "") {
        list.add(entry.getKey() + "=" + entry.getValue() + "&");
      }
    }
    int size = list.size();
    String[] arrayToSort = list.toArray(new String[size]);
    Arrays.sort(arrayToSort, String.CASE_INSENSITIVE_ORDER);
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < size; i++) {
      sb.append(arrayToSort[i]);
    }
    String result = sb.toString();
    result += "key=" + Configure.getKey();
    Logger.i("Sign Before MD5:" + result);
    result = MD5.MD5Encode(result).toUpperCase();
    Logger.i("Sign Result:" + result);
    return result;
  }
}
