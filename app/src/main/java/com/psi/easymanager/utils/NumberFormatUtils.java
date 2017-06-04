package com.psi.easymanager.utils;

import java.math.BigDecimal;

/**
 * Created by zjq on 2015/10/10.
 * 当浮点型数据位数超过10位之后，数据变成科学计数法显示。用此方法可以使其正常显示。
 */
public class NumberFormatUtils {

  public static String formatFloatNumber(double value) {
    if (value != 0.0) {
      boolean b = value < 1;
      java.text.DecimalFormat df = new java.text.DecimalFormat("########.00");
      return b ? String.valueOf(new Double(df.format(value))) : df.format(value);
    } else {
      return "0.0";
    }
  }

  public static String formatFloatNumber(Double value) {
    if (value != null) {
      if (value.doubleValue() != 0.0) {
        boolean b = value < 1;
        java.text.DecimalFormat df = new java.text.DecimalFormat("########.00");
        return b ? String.valueOf(new Double(df.format(value.doubleValue())))
            : df.format(value.doubleValue());
      } else {
        return "0.0";
      }
    }
    return "";
  }

  public static Double formatFloatAndParse(Double value) {
    return Double.parseDouble(formatFloatNumber(value));
  }

  public static double formatFloatAndParse(double value) {
    return Double.parseDouble(formatFloatNumber(value));
  }
//  public static String formatFloatNumber(double value) {
//  BigDecimal decimal = new BigDecimal(value);
//  return String.valueOf(decimal.setScale(2, BigDecimal.ROUND_HALF_UP));
//}
//
//  public static String formatFloatNumber(Double value) {
//    BigDecimal decimal = new BigDecimal(value);
//    return String.valueOf(decimal.setScale(2, BigDecimal.ROUND_HALF_UP));
//  }
  /**
   * 提供精确的加法运算
   * @param v1 被加数
   * @param v2 加数
   * @return 两个参数的和
   */
  public static Double add(double v1, double v2)
  {
    BigDecimal b1 = new BigDecimal(Double.toString(v1));
    BigDecimal b2 = new BigDecimal(Double.toString(v2));
    return b1.add(b2).doubleValue();
  }
  /**
   * 提供精确的减法运算
   * @param v1 被减数
   * @param v2 减数
   * @return 两个参数的差
   */
  public static Double subtract(double v1, double v2)
  {
    BigDecimal b1 = new BigDecimal(Double.toString(v1));
    BigDecimal b2 = new BigDecimal(Double.toString(v2));
    return b1.subtract(b2).doubleValue();
  }

}
