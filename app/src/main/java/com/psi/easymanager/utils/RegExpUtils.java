package com.psi.easymanager.utils;

import android.graphics.drawable.Drawable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 正则表达式工具类
 * Created by zjq on 15/4/1.
 */
public class RegExpUtils {
  /**
   * 手机号:目前全国有27种手机号段。
   * 移动有16个号段：134、135、136、137、138、139、147、150、151、152、157、158、159、182、187、188。其中147、157、188是3G号段，其他都是2G号段。
   * 联通有7种号段：130、131、132、155、156、185、186。其中186是3G（WCDMA）号段，其余为2G号段。
   * 电信有4个号段：133、153、180、189。其中189是3G号段（CDMA2000），133号段主要用作无线网卡号。
   * 150、151、152、153、155、156、157、158、159 九个;
   * 130、131、132、133、134、135、136、137、138、139 十个;
   * 180、182、185、186、187、188、189 七个;
   * 13、15、18三个号段共30个号段，154、181、183、184暂时没有，加上147共27个。
   */
  private static final String MATCH_PHONE_REGEX =
      "^((13\\d{9}$)|(15[0,1,2,3,5,6,7,8,9]\\d{8}$)|(18[0,2,3,5,6,7,8,9]\\d{8}$)|(147\\d{8})$)";

  private static final String MATCH_11_NUMBER = "^1\\d{10}$";

  /**
   * 浮点数
   */
  private static final String MATCH_FLOAT_NUM = "[0-9]+\\.?[0-9]*";

  /**
   * 两位小数
   */
  private static final String MATCH_TWO_DECIMAL_PLACES = "^(([0-9]+\\d*)|([0-9]+\\d*\\.\\d{1,2}))$";

  private static final String MATCH_NUMBER = "[0-9]+";

  private RegExpUtils() {
  }

  ///**
  // * 匹配输入电话号码
  // */
  //public static boolean matchPhone(String phone) {
  //  return Pattern.matches(MATCH_PHONE_REGEX, phone);
  //}

  /**
   * 匹配11位数字,以1打头
   */
  public static boolean match11Number(String number) {
    return Pattern.matches(MATCH_11_NUMBER, number);
  }

  /**
   * 匹配输入人数
   */
  public static boolean matchPeoPleNum(String num) {
    try {
      int inputNum = Integer.valueOf(num);
      if (inputNum < 0 || inputNum > 1000) {
        return false;
      }
      return true;
    } catch (Exception e) {
      return false;
    }
  }

  /**
   * 检查输入数量
   */
  public static boolean matchinpuNum(String input) {
    try {
      Integer integer = Integer.valueOf(input);
      if (integer > 0 && integer < 1000) {
        return true;
      }
      return false;
    } catch (Exception e) {

    }
    return false;
  }

  /**
   * 匹配输入名
   */
  public static boolean matchName(String name) {
    ///^([\u4e00-\u9fa5]){2,7}$/
    try {
      //String regex = "^[a-zA-Z0-9\u4E00-\u9FA5]+$";
      String regex = "^[\\u4E00-\\u9FA5\\uF900-\\uFA2D\\w]{2,8}$";
      Pattern pattern = Pattern.compile(regex);
      Matcher matcher = pattern.matcher(name);
      return matcher.matches();
    } catch (Exception e) {
      return false;
    }
  }

  /**
   * 匹配输入金额
   */
  public static boolean matchMoney(String money) {
    try {
      if (money.length() > 6) return false;
      String start = money.substring(0, 1);
      if (start == null || start.isEmpty() || start.equals("0")) {
        return false;
      } else {
        return true;
      }
    } catch (Exception e) {
      return false;
    }
  }

  /**
   * 判断是否为合法IP
   */
  public static boolean isBoolIp(String ipAddress) {
    String ip = "^(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|[1-9])\\."
        + "(00?\\d|1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\."
        + "(00?\\d|1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\."
        + "(00?\\d|1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)$";
    Pattern pattern = Pattern.compile(ip);
    Matcher matcher = pattern.matcher(ipAddress);
    return matcher.matches();
  }

  /**
   * 匹配浮点数
   */
  public static boolean matchFloatNum(String str) {
    return Pattern.matches(MATCH_FLOAT_NUM, str);
  }

  /**
   * 匹配两位小数
   */
  public static boolean match2DecimalPlaces(String str) {
    return Pattern.matches(MATCH_TWO_DECIMAL_PLACES, str);
  }

  /**
   * 匹配整数
   */
  public static boolean matchNumber(String str){
    return Pattern.matches(MATCH_NUMBER,str);
  }

  public static boolean equals(Drawable a, Drawable b) {
    return a.getConstantState().equals(b.getConstantState());
  }
}
