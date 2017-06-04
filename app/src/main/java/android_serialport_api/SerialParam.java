package android_serialport_api;

/**
 * Created by Administrator on 2016-12-21.
 */

public class SerialParam {
  public int baudrate;
  public String device;
  public int openFlag;

  public SerialParam(int baud, String dev, int flag) {
    this.baudrate = baud;
    this.device = dev;
    this.openFlag = flag;
  }
}
