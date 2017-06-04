package com.psi.easymanager.pay.wxpay;

import java.util.Random;

/**
 * 获取一定长度的随机字符串
 * 这边应用时获取到32为随机字符串
 */
public class RandomStringGenerator {

  /**
   * @param length 指定字符串长度
   * @return 一定长度的字符串
   */
  public static String getRandomStringByLength(int length) {
    String base = "abcdefghijklmnopqrstuvwxyz0123456789";
    Random random = new Random();
    StringBuffer sb = new StringBuffer();
    for (int i = 0; i < length; i++) {
      int number = random.nextInt(base.length());
      sb.append(base.charAt(number));
    }
    return sb.toString();
  }
}
