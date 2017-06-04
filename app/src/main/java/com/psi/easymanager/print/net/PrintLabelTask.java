package com.psi.easymanager.print.net;

import android.graphics.Bitmap;
import com.gprinter.command.LabelCommand;
import com.psi.easymanager.module.PxFormatInfo;
import com.psi.easymanager.module.PxMethodInfo;
import com.psi.easymanager.module.PxOrderDetails;
import com.psi.easymanager.module.PxProductInfo;
import com.psi.easymanager.module.User;
import com.psi.easymanager.utils.UserUtils;
import java.io.Closeable;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * User: ylw
 * Date: 2016-11-08
 * Time: 17:12
 * 标签打印机任务
 */
public class PrintLabelTask {
  private static final String SIZE = "TSS24.BF2";

  public boolean printLabelOrderDetails(int width, int height, int gap ,String companyCode, OutputStream outputStream, PxOrderDetails details) {
    try {
      setUp(width, height, 4, 5, 0, gap, 0, outputStream);

      addCutter(CUTTER.BATCH, outputStream);
      addSpeel(true, outputStream);

      //addBack(true, outputStream);

      addTear(true, outputStream);
      addReference(0, 0, outputStream);

      //addFormFeed(outputStream);
      //addHome(outputStream);

      //标签初始化
      //sendCommand("SET TEAR ON\n", outputStream);//是否将撕纸位置移动到撕纸处
      //sendCommand("SHIFT 10\n", outputStream);

      clearBuffer(outputStream);

      PxMethodInfo dbMethodInfo = details.getPrintMethod();
      PxProductInfo dbProduct = details.getPrintProd();
      PxFormatInfo dbFormatInfo = details.getPrintFormat();
      SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
      String msg = companyCode + "  " + sdf.format(new Date());
      int y = 20;
      //int widthPos = 20 + (70 - width) * 8;
      int widthPos = 20;
      printFont(widthPos, y, msg, outputStream);
      msg = dbProduct.getName();
      printFont(widthPos, y += 30, 2, 2, msg, outputStream);
      //规格 做法
      boolean hasFormatOrMethod = dbFormatInfo != null || dbMethodInfo != null;
      if (hasFormatOrMethod) {
        StringBuilder sb = new StringBuilder();
        if (dbFormatInfo != null) {
          sb.append("规格:" + dbFormatInfo.getName() + "  ");
        }
        if (dbMethodInfo != null) {
          sb.append("做法:" + dbMethodInfo.getName());
        }
        printFont(widthPos, y += 50, sb.toString(), outputStream);
      }

      User user = UserUtils.getLoginUser();
      msg = "收银员:" + (user == null ? "admin" : user.getName());
      printFont(widthPos, y += hasFormatOrMethod ? 30 : 50, msg, outputStream);

      //addSound(5, 2, outputStream);
      printLabelNum(1, 1, outputStream);
      clearBuffer(outputStream);
      //标签初始化
      sendCommand("SET TEAR ON" + "\r\n", outputStream);//是否将撕纸位置移动到撕纸处
      clearBuffer(outputStream);
      outputStream.flush();
      return true;
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }
  }

  public boolean printLabelBitmap(int width, int height, OutputStream outputStream, Bitmap bitmap) {
    try {
      //高度默认+2
      setUp(width, height + 2, 4, 5, 0, 0, 0, outputStream);
      clearBuffer(outputStream);
      //标签初始化
      sendCommand("SET TEAR ON\n", outputStream);//是否将撕纸位置移动到撕纸处
      sendCommand("SHIFT 10\n", outputStream);

      addBitmap(30, 30, com.gprinter.command.LabelCommand.BITMAP_MODE.OVERWRITE, bitmap.getWidth(),
          bitmap, outputStream);
      printLabelNum(1, 1, outputStream);
      clearBuffer(outputStream);
      outputStream.flush();
      return true;
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }
  }

  /**
   * 在使用含有間隙或黑標的標籤紙時，若不能確定第一張標籤紙是否在正確列印位置時，此指令可將標籤紙向前推送至下一張標籤紙的起點開始列印。
   */
  private void addHome(OutputStream outputStream) throws IOException {
    String str = "HOME\n";
    sendCommand(str.getBytes(), outputStream);
  }

  /**
   * 將標籤向前推送至下一張標籤的起始位置
   */
  private void addFormFeed(OutputStream outputStream) throws IOException {
    String str = "FORMFEED";
    sendCommand(str.getBytes(), outputStream);
  }

  /**
   * 此項設定可使印表機列印及裁切結束時不執行回拉的動作，通常置於SET CUTTER功能之後
   */
  private void addBack(boolean on, OutputStream outputStream) throws IOException {
    //SET BACK OFF/ON
    String str = "SET BACK " + (on ? "ON\n" : "OFF\n");
    sendCommand(str.getBytes(), outputStream);
  }

  /**
   * 此設定用來開啟/關閉裁刀裁切的功能，並可定義每印幾張後做一次裁切動作。當印表機電源關閉時，此項設定會被記錄於EEPROM。
   */
  private void addCutter(CUTTER value, OutputStream outputStream) throws IOException {
    //SET CUTTER OFF/BATCH/pieces
    String str = "SET CUTTER " + value.getValue() + "\n";
    sendCommand(str.getBytes(), outputStream);
  }

  /**
   * 自动剥纸
   */
  private void addSpeel(boolean on, OutputStream outputStream) throws IOException {
    String str = "SET PEEL " + (on ? "ON\n" : "OFF\n");
    sendCommand(str.getBytes(), outputStream);
  }

  /**
   * 设置标签尺寸，按照实际尺寸设置
   */
  private void addSize(int width, int height, OutputStream outputStream) throws IOException {
    String str = "SIZE " + width + " mm," + height + " mm" + "\r\n";
    sendCommand(str.getBytes(), outputStream);
  }

  /**
   * 设置标签间隙，按照实际尺寸设置，如果为无间隙纸则设置为0
   */
  private void addGap(int gap, OutputStream outputStream) throws IOException {
    new String();
    String str = "GAP " + gap + " mm," + 0 + " mm" + "\r\n";
    sendCommand(str.getBytes(), outputStream);
  }

  /**
   * 设置打印方向
   */
  private void addDirection(LabelCommand.DIRECTION direction, LabelCommand.MIRROR mirror,
      OutputStream outputStream) throws IOException {
    new String();
    String str = "DIRECTION " + direction.getValue() + ',' + mirror.getValue() + "\r\n";
    sendCommand(str.getBytes(), outputStream);
  }

  /**
   * 设置原点坐标
   */
  private void addReference(int x, int y, OutputStream outputStream) throws IOException {
    new String();
    String str = "REFERENCE " + x + "," + y + "\r\n";
    sendCommand(str.getBytes(), outputStream);
  }

  /**
   * 撕纸模式开启
   */
  private void addTear(boolean on, OutputStream outputStream) throws IOException {
    String str = "SET TEAR " + (on ? "ON\n" : "OFF\n");
    sendCommand(str.getBytes(), outputStream);
  }

  /**
   * 清除缓冲区
   */
  private void addCls(OutputStream outputStream) throws IOException {
    new String();
    String str = "CLS\r\n";
    sendCommand(str.getBytes(), outputStream);
  }

  public void addBitmap(int x, int y, com.gprinter.command.LabelCommand.BITMAP_MODE mode,
      int nWidth, Bitmap b, OutputStream outputStream) throws IOException {
    //Vector command = new Vector(4096, 1024);
    int width = (nWidth + 7) / 8 * 8;
    int height = b.getHeight() * width / b.getWidth();
    Bitmap grayBitmap = com.gprinter.command.GpUtils.toGrayscale(b);
    Bitmap rszBitmap = com.gprinter.command.GpUtils.resizeImage(grayBitmap, width, height);
    byte[] src = com.gprinter.command.GpUtils.bitmapToBWPix(rszBitmap);
    height = src.length / width;
    width /= 8;
    String str = "BITMAP " + x + "," + y + "," + width + "," + height + "," + mode.getValue() + ",";
    //this.addStrToCommand(str);
    sendCommand(str.getBytes("gb2312"), outputStream);
    byte[] codecontent = com.gprinter.command.GpUtils.pixToLabelCmd(src);

    for (int k = 0; k < codecontent.length; ++k) {
      //this.Command.add(Byte.valueOf(codecontent[k]));
      //command.add(Byte.valueOf(codecontent[k]));
      outputStream.write(Byte.valueOf(codecontent[k]));
    }
    //return command;
  }

  /**
   * 使用條碼機內建條碼列印
   * point = 1 / 8 mm
   */
  //@formatter:off
  private void barCode(int x, int y, String type, int height, int human_readable, int rotation,
      int narrow, int wide, String code, OutputStream outputStream) throws IOException {
    String message = "";
    String barcode = "BARCODE ";
    String position = x + "," + y;
    String mode = "\"" + type + "\"";
    String height_value = "" + height;
    String human_value = "" + human_readable;
    String rota = "" + rotation;
    String narrow_value = "" + narrow;
    String wide_value = "" + wide;
    String string_value = "\"" + code + "\"";
    message = barcode + position + " ," + mode + " ," + height_value + " ," + human_value + " ," + rota + " ," + narrow_value + " ," + wide_value + " ," + string_value + "\n";
    byte[] msgBuffer = message.getBytes();
    outputStream.write(msgBuffer);
  }
  private void addSound(int level, int interval,OutputStream outputStream) throws IOException{
    String str = "SOUND " + level + "," + interval + "\r\n";
    sendCommand(str.getBytes("gb2312"),outputStream);
  }
  /**
   * 送內建指令到條碼印表機
   */
  private void sendCommand(String msg, OutputStream outputStream) throws IOException {
    sendCommand(msg.getBytes(), outputStream);
  }

  private void sendCommand(byte[] message, OutputStream outputStream) throws IOException {
    outputStream.write(message);
  }
  /**
   * status
   */
  //@formatter:on
  public String getStatus(OutputStream outputStream, InputStream inputStream) throws IOException {
    byte[] readBuf = new byte[1024];
    boolean printvalue = false;
    String printbatch = "";
    String stringbatch = "0";
    String message = "~HS";
    byte[] batcharray = new byte[] {
        (byte) 48, (byte) 48, (byte) 48, (byte) 48, (byte) 48, (byte) 48, (byte) 48, (byte) 48
    };
    byte[] msgBuffer = message.getBytes();
    outputStream.write(msgBuffer);

    try {
      Thread.sleep(1000L);
    } catch (InterruptedException var8) {
      var8.printStackTrace();
    }

    int i;
    while (inputStream.available() > 50) {
      readBuf = new byte[1024];
      i = inputStream.read(readBuf);
    }

    if (readBuf[0] == 2) {
      System.arraycopy(readBuf, 55, batcharray, 0, 8);

      for (i = 0; i <= 7; ++i) {
        if (batcharray[i] == 44) {
          batcharray = new byte[] {
              (byte) 57, (byte) 57, (byte) 57, (byte) 57, (byte) 57, (byte) 57, (byte) 57, (byte) 57
          };
        }
      }
      stringbatch = new String(batcharray);
      int var11 = Integer.parseInt(stringbatch);
      printbatch = Integer.toString(var11);
      if (printbatch == "99999999") {
        printbatch = "";
      }
    }
    outputStream.flush();
    return printbatch;
  }

  /**
   * 關閉指定的電腦端輸出埠
   */
  private void closeport(Socket socket) throws IOException {
    if (socket != null && !socket.isClosed()) {
      socket.close();
    }
  }

  /**
   * 傳送txt檔案至印表機
   */
  private void sendFile(String filename, OutputStream outputStream) {
    FileInputStream fis = null;
    try {
      fis = new FileInputStream("/sdcard/Download/" + filename);
      byte[] data = new byte[fis.available()];
      while (fis.read(data) != -1) {
      }
      outputStream.write(data);
      fis.close();
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      closeCloseable(fis);
    }
  }

  /**
   * 列印標籤內容
   *
   * @param set 列印張數sets
   * @param copy 每張標籤需重覆列印的張數
   */
  private void printLabelNum(int set, int copy, OutputStream outputStream) throws IOException {
    String message = "";
    message = "PRINT " + set + ", " + copy + "\n";
    byte[] msgBuffer = message.getBytes();
    outputStream.write(msgBuffer);
  }

  /**
   * 使用條碼機內建文字列印
   */
  //@formatter:off
  private void printFont(int x, int y,int x_multiplication,int y_multiplication, String string, OutputStream outputStream) throws IOException {
    //printFont(20,y,SIZE,0,1,1,msg,outputStream);
    String size = SIZE;
    int rotation = 0;
    String message = "";
    String text = "TEXT ";
    String position = x + "," + y;
    String size_value = "\"" + size + "\"";
    String rota = String.valueOf(rotation);
    String x_value = String.valueOf(x_multiplication);
    String y_value = String.valueOf(y_multiplication);
    String string_value = "\"" + string + "\"";
    message = text + position + "," + size_value + "," + rota + "," + x_value + "," + y_value + "," + string_value + "\n";
    sendCommand(message.getBytes("gb2312"), outputStream);
  }
  /**
   * 使用條碼機內建文字列印
   */
  //@formatter:off
  private void printFont(int x, int y, String string, OutputStream outputStream) throws IOException {
    printFont(x,y,1,1,string,outputStream);
  }
  //@formatter:on

  /**
   * 清除
   */
  private void clearBuffer(OutputStream outputStream) throws IOException {
    String message = "CLS\n";
    byte[] msgBuffer = message.getBytes();
    outputStream.write(msgBuffer);
  }

  /**
   * @param width 寬度
   * @param height 高度
   * @param speed 列印速度
   * @param density 字体黑度
   * @param sensor  感應器類別
   * @param sensor_distance 间隔距离
   * @param sensor_offset 偏移距离
   */
  private void setUp(int width, int height, int speed, int density, int sensor, int sensor_distance,
      int sensor_offset, OutputStream outputStream) throws IOException {
    //setUp(width, height, 4, 7, 0, 0, 0, outputStream);
    String message = "";
    String size = "SIZE " + width + " mm" + ", " + height + " mm";
    String speed_value = "SPEED " + speed;
    String density_value = "DENSITY " + density;
    String sensor_value = "";
    //String str = "GAP " + gap + " mm," + 2 + " mm" + "\r\n";
    if (sensor == 0) {
      sensor_value = "GAP " + sensor_distance + " mm" + ", " + sensor_offset + " mm";
    } else if (sensor == 1) {
      sensor_value = "BLINE " + sensor_distance + " mm" + ", " + sensor_offset + " mm";
    }
    message = size + "\n" + speed_value + "\n" + density_value + "\n" + sensor_value + "\n";
    byte[] msgBuffer = message.getBytes();
    outputStream.write(msgBuffer);
  }

  /**
   * 下載單色BMP 格式圖檔至印表
   */
  private void downLoadTTF(String fileName, OutputStream outputStream) {
    downLoad(fileName, outputStream);
  }

  /**
   * 下載單色BMP 格式圖檔至印表
   */
  private void downLoadBmp(String fileName, OutputStream outputStream) {
    downLoad(fileName, outputStream);
  }

  /**
   * 下載單色PCX 格式圖檔至印表
   */
  private void downLoadPcx(String fileName, OutputStream outputStream) {
    downLoad(fileName, outputStream);
  }

  private void downLoad(String fileName, OutputStream outputStream) {
    FileInputStream fis = null;
    try {
      fis = new FileInputStream("/sdcard/Download/" + fileName);
      byte[] data = new byte[fis.available()];
      String download = "DOWNLOAD F,\"" + fileName + "\"," + data.length + ",";
      byte[] download_head = download.getBytes();
      while (fis.read(data) != -1) {
      }
      outputStream.write(download_head);
      outputStream.write(data);
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      closeCloseable(fis);
    }
  }

  private void closeCloseable(Closeable... closeables) {
    if (closeables == null) return;
    for (Closeable closeable : closeables) {
      if (closeable != null) {
        try {
          closeable.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }
  }

  //@formatter:on
  enum CUTTER {
    OFF("OFF"),//关闭
    BATCH("BATCH"),//設定在列印結束時才執行裁切動作
    PIECES("PIECES");//設定每印幾張後做一次裁切動做0<= pieces <=65535
    private String value;

    CUTTER(String value) {
      this.value = value;
    }

    public String getValue() {
      return value;
    }
  }
}  