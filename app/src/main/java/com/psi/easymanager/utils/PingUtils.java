package com.psi.easymanager.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * User: ylw
 * Date: 2016-12-27
 * Time: 13:24
 * ping IP 工具
 */
public class PingUtils {
  private static final int PING_TIMES = 3;
  private static final int TIME_OUT = 2;

  //@formatter:off
  public static boolean isConnect(Runtime r, String ip) {
    BufferedReader in = null;
    //Runtime r = Runtime.getRuntime();  // 将要执行的ping命令,此命令是windows格式的命令
    String pingCommand = "ping "  + " -c " + PING_TIMES + " -w " + TIME_OUT + " " + ip;
    try {   // 执行命令并获取输出
      Process p = r.exec(pingCommand);
      if (p == null) {
        return false;
      }
      in = new BufferedReader(new InputStreamReader(p.getInputStream()));   // 逐行检查输出,计算类似出现=23ms TTL=62字样的次数
      int connectedCount = 0;
      String line = null;
      while ((line = in.readLine()) != null) {
        connectedCount += getCheckResult(line);
      }   // 如果出现类似=23ms TTL=62这样的字样,出现的次数=测试次数则返回真
      return connectedCount > 0;
    } catch (Exception ex) {
      ex.printStackTrace();   // 出现异常则返回假
      return false;
    } finally {
      IOUtils.closeCloseables(in);
    }
  }

  //若line含有=18ms TTL=16字样,说明已经ping通,返回1,否則返回0.
  private static int getCheckResult(String line) {  // System.out.println("控制台输出的结果为:"+line);
    //Pattern pattern = Pattern.compile("(\\d+ms)(\\s+)(ttl=\\d+)", Pattern.CASE_INSENSITIVE);
    //Matcher matcher = pattern.matcher(line);
    //Logger.e(ip+"-----"+line);
    if (null == line) return 0;
    return (line.contains("ms") && line.contains("ttl") )  ? 1 : 0;
  }
}  