package com.psi.easymanager.print.constant;

/**
 * Epson打印指令集
 *
 * @author <a href="mailto:iffiff1@hotmail.com">Tyler Chen</a> 新增日期：2008-6-19
 * @author <a href="mailto:iffiff1@hotmail.com">Tyler Chen</a> 修改日期：2008-6-19
 */
public class EpsonPosPrinterCommand {
  //蜂鸣声
  public static final byte[] SOUND = new byte[] { (byte) 27, (byte) 66, (byte) 0, (byte) 0 };
  //修改IP  1F 1B 1F 91 00 49 50 n1~n4(00~FF)
  public static byte[] modifyIp(byte a, byte b, byte c, byte d) {
    byte[] command = new byte[] {
        (byte) 0x1F, (byte) 0x1B, (byte) 0x1F, (byte) 0x91, (byte) 0x00, (byte) 0x49, (byte) 0x50,
        (byte) 0x00, (byte) 0x08, (byte) 0x00, (byte) 0x08// 7-10
    };
    command[7] = a;
    command[8] = b;
    command[9] = c;
    command[10] = d;
    return command;

  }

  //开钱箱
  public static final char[] OPEN_BOX = { 27, 'p', 0, 5, 10 };
  public static final byte[] OPEN_BOX1 = { 27, 'p', 0, 5, 10 };
  //移动打印位置到下一个个制表符打印位置
  public static final byte HT = 0x9;
  //打印并换行
  public static final byte LF = 0x0A;
  //打印并回车
  public static final byte CR = 0x0D;

  //通用的命令头部
  public static final byte ESC = 0x1B;
  public static final byte DLE = 0x10;
  public static final byte GS = 0x1D;
  //退出汉字打印方式
  public static final byte FS = 0x1C;
  public static final byte STX = 0x02;
  public static final byte US = 0x1F;
  public static final byte CAN = 0x18;
  public static final byte CLR = 0x0C;

  /*
   * 打印机状态
   *n=1: 打印机状态
   *n=2: 脱机状态
   *n=3: 错误状态
   *n=4: 纸检测器状态
   *当前状态为n=1打印机状态
   */
  public static final byte[] DLE_EOT_n = new byte[] { DLE, 0x04, 0x01 };
  /*
   * 对打印机的实时请求
   *n=0: 恢复到联机状态
   *n=2: 清除接收和打印缓冲区。并恢复错误
   */
  public static final byte[] DLE_ENQ_n = new byte[] { DLE, 0x05, 0x01 };

  /**
   * n = 1 m = 0, 1 1 ≤ t ≤ 8 m Connector pin m = 0: Drawer kick-out connector
   * pin 2. m = 1: Drawer kick-out connector pin 5. pulse ON time is [t × 100
   * ms] and the OFF time is [t × 100 ms].
   */
  public static final byte[] DLE_DC4_n_m_t = new byte[] { DLE, 0x14, 0x01, 0x00, 0x01 };
  //允许、禁止用户自定义字符
  public static final byte[] ESC_SELECT_DEF_CHAR = new byte[] { ESC, '%', 0x00 };
  public static final byte[] ESC_CANCEL_DEF_CHAR = new byte[] { ESC, '%', 0x01 };
  //
  //设置取消下划线
  public static final byte[] ESC_UNDER_LINE_OFF = new byte[] { ESC, '-', 0x00 };
  public static final byte[] ESC_UNDER_LINE_ON = new byte[] { ESC, '-', 0x01 };
  //设定1/6英寸换行量
  public static final byte[] ESC_DEFAULT_LINE_SP = new byte[] { ESC, '2' };
  //设备设置
  public static final byte[] ESC_ENABLE_PRINTER = new byte[] { ESC, '=', 0x01 };
  //初始化打印机
  public static final byte[] ESC_INIT = new byte[] { ESC, '@' };
  //消除所有的水平制表符
  public static final byte[] ESC_HT_RESET = new byte[] { ESC, 'D', };
  //设置、取消着重模式
  public static final byte[] ESC_EM_OFF = new byte[] { ESC, 'E', 0x00 };
  public static final byte[] ESC_EM_ON = new byte[] { ESC, 'E', 0x01 };
  //设置、取消重叠模式
  public static final byte[] ESC_BLOD_OFF = new byte[] { ESC, 'G', 0x00 };
  public static final byte[] ESC_BLOD_ON = new byte[] { ESC, 'G', 0x01 };
  //选择中文字符集
  public static final byte[] ESC_CHARSET_CHINESS = new byte[] { ESC, 'R', 15 };
  //布局样式，靠左、居中、靠右
  public static final byte[] ESC_ALIGN_LEFT = new byte[] { ESC, 'a', 0x00 };
  public static final byte[] ESC_ALIGN_CENTER = new byte[] { ESC, 'a', 0x01 };
  public static final byte[] ESC_ALIGN_RIGHT = new byte[] { ESC, 'a', 0x02 };
  //输出纸尽传感器
  public static final byte[] ESC_PAPER_END_SENSOR_DISABLE_ALL = new byte[] { ESC, 'c', '3', 0x00 };
  public static final byte[] ESC_PAPER_END_SENSOR_ENABLE_ALL = new byte[] { ESC, 'c', '3', 0x0F };
  public static final byte[] ESC_PAPER_END_SENSOR_ENABLE_NEAR = new byte[] { ESC, 'c', '3', 0x01 };
  public static final byte[] ESC_PAPER_END_SENSOR_ENABLE_ROLL = new byte[] { ESC, 'c', '3', 0x04 };
  //设定缺纸时停止打印
  public static final byte[] ESC_STOP_PRINT_SENSOR_DISABLE = new byte[] { ESC, 'c', '4', 0x00 };
  public static final byte[] ESC_STOP_PRINT_SENSOR_ANABLE = new byte[] { ESC, 'c', '4', 0x01 };
  //允许、禁止走纸按键
  public static final byte[] ESC_PANEL_BUTTON_DISABLE = new byte[] { ESC, 'c', '5', 0x00 };
  public static final byte[] ESC_PANEL_BUTTON_ENABLE = new byte[] { ESC, 'c', '5', 0x01 };
  //设置、取消倒向打印模式
  public static final byte[] ESC_UPSIDE_OFF = new byte[] { ESC, '{', 0x00 };
  public static final byte[] ESC_UPSIDE_ON = new byte[] { ESC, '{', 0x01 };
  //切纸
  public static final byte[] ESC_CUT_PAPER = new byte[] { GS, 'V', 0x00 };
  public static final byte[] ESC_CUT_MODE = new byte[] { GS, 'V', 0x00 };
  //状态传送
  public static final byte[] ESC_TRANSMIT_PAPER_STATUS = new byte[] { GS, 'r', 0x01 };
  public static final byte[] ESC_TRANSMIT_DRAWER_STATUS = new byte[] { GS, 'r', 0x02 };
  //设置、取消下划线
  public static final byte[] ESC_UNDERLINE_OFF = new byte[] { FS, '-', 0x00 };
  public static final byte[] ESC_UNDERLINE_ON = new byte[] { FS, '-', 0x01 };
  //退出、进入汉字打印方式
  public static final byte[] ESC_CN_MODE_OFF = new byte[] { FS, '.' };
  public static final byte[] ESC_CN_MODE = new byte[] { FS, '&' };
  //设定、取消四倍角汉字模式
  public static final byte[] ESC_CN_SIZE_QUADRUPLE_OFF = new byte[] { FS, 'W', 0x00 };
  public static final byte[] ESC_CN_SIZE_QUADRUPLE_ON = new byte[] { FS, 'W', 0x01 };

  public static final byte[] ESC_OPEN_DRAWER = new byte[] { STX, 'M' };
  public static final byte[] ESC_OPEN_DRAWER_US = new byte[] { US, 'M' };

  public static final byte[] ESC_DRAWER_RATE_9600 = new byte[] { STX, 'B', 0x00 };
  public static final byte[] ESC_DRAWER_RATE_2400 = new byte[] { STX, 'B', 0x02 };

  /*
   * 选择打印模式
   * @param fontB: 粗体模式。
   * @param both: 西文字符 （半宽）字体A (6 ×12)，汉字字符 （全宽）字体A （12×12）。
   * @param doubleWidth: 倍宽模式。
   * @param doubleHeight: 倍高模式。
   * @param underLine: 下划线模式。
   */
  public static byte[] setPrintMode(boolean fontB, boolean both, boolean doubleWidth,
      boolean doubleHeight, boolean underLine) {
    int n = 0;
    if (fontB) {
      n |= 1;
    }
    if (both) {
      n |= 1 << 3;
    }
    if (doubleHeight) {
      n |= 1 << 4;
    }
    if (doubleWidth) {
      n |= 1 << 5;
    }
    if (underLine) {
      n |= 1 << 7;
    }
    return new byte[] { ESC, '!', (byte) n };
  }

  //设置字符间距
  public static byte[] setCharSpacing(int n) {
    n = (n > -1 || n < 256 ? n : 0);
    return new byte[] { ESC, ' ', (byte) n };
  }

  //设置行距(n/144英寸换行量)
  public static byte[] setLineSpacing(int n) {
    n = (n > -1 || n < 256 ? n : 24);
    return new byte[] { ESC, '3', (byte) n };
  }

  //取消用户自定义的编码
  public static byte[] cancelUserDefineCharacters(int offset) {
    if (offset < 0 || (offset + 31) > 126) {
      return new byte[0];
    }
    return new byte[] { ESC, '?', (byte) (31 + offset) };
  }

  //初始化打印机，清除以前留下的程序
  public static byte[] setHT() {
    // TODO
    return new byte[] { ESC, 'D' };
  }

  /*
   * 打印并进纸
   * @param n: 打印输出打印缓冲区的数据，并进纸n个垂直点距，一个垂直点距为0.33mm。
   */
  public static byte[] printAndFeedPaper(int n) {
    n = (n > 255 ? 255 : n);
    n = (n < 0 ? 0 : n);
    return new byte[] { ESC, 'J', (byte) n };
  }

  /*
   * 打印并进纸n行
   * @param n:  打印打印缓冲区中的数据并进纸n字符行。
   */
  public static byte[] printAndFeedLines(int n) {
    n = (n > 255 ? 255 : n);
    n = (n < 0 ? 0 : n);
    return new byte[] { ESC, 'd', (byte) n };
  }

  //产生钱箱驱动脉冲
  public static byte[] generatePulse(int onTime, int offTime) {
    int t2 = 255 * 2;
    int t5 = 255 * 5;
    offTime = (offTime < onTime ? onTime : offTime);
    offTime = (offTime > t5 ? t5 : offTime);
    int m = (offTime > t2 ? 1 : 0);
    int ot1 = (m == 1 ? onTime / 5 : onTime / 2);
    int ot2 = (m == 1 ? offTime / 5 : offTime / 2);
    return new byte[] { ESC, 'p', (byte) m, (byte) ot1, (byte) ot2 };
  }

  /*
   * 选择字符集
   * @param n:  n=0选择7*9字符集，n=1选择7*7字符集
   */
  public static byte[] selectCharacterCodeTable(int n) {
    return new byte[] { ESC, 't', (byte) n };
  }

  //打印位图
  public static byte[] printNvBitImage(int n, int m) {
    return new byte[] { ESC, 'p', (byte) n, (byte) m };
  }

  //打印测试
  public static byte[] testPrint(int paper, int pattern) {
    paper = (paper == 0 || paper == 1 || paper == 2 || paper == 48 || paper == 49 || paper == 50)
        ? paper : 0;
    pattern = (pattern == 1 || pattern == 2 || pattern == 3 || pattern == 49 || pattern == 50
        || pattern == 51) ? pattern : 1;
    return new byte[] { ESC, '(', 'A', 0x02, 0x00, (byte) paper, (byte) pattern };
  }

  //走纸到切纸位置
  public static byte[] setCutMode(int n) {
    n = n % 256;
    int m = 66;
    return new byte[] { GS, 'V', (byte) m, (byte) n };
  }

  //汉字综合选择，设置打印字体模式，倍宽、倍高
  public static byte[] setMultiByteCharMode(boolean doubleWidth, boolean doubleHeight,
      boolean underLine) {
    int n = 0;
    if (doubleWidth) {
      n |= 1 << 2;
    }
    if (doubleHeight) {
      n |= 1 << 3;
    }
    if (underLine) {
      n |= 1 << 7;
    }
    return new byte[] { FS, '!', (byte) n };
  }

  //局部切割
  public static final byte[] ESC_FONT_A = new byte[] { ESC, 'M', 0x00 };
  public static final byte[] ESC_FONT_B = new byte[] { ESC, 'M', 0x01 };

  public static byte[] getFontA() {
    return new byte[] { ESC, 'M', 0x00 };
  }

  public static byte[] getFontB() {
    return new byte[] { ESC, 'M', 0x01 };
  }

  //选择打印颜色 ,反色
  public static byte[] getColorDefault() {
    return new byte[] { ESC, 'r', 0x00 };
  }

  public static byte[] getColorRed() {
    return new byte[] { ESC, 'r', 0x01 };
  }

  public static byte[] setDisplayRate(char n) {
    return new byte[] { STX, 'B', (byte) n };
  }

  public static byte[] sendDisplayData(String data) {
    if (data == null || data.length() == 0) {
      return new byte[0];
    }
    byte[] bytes = data.getBytes();
    int len = bytes.length + 4;
    byte[] bs = new byte[len];
    bs[0] = ESC;
    bs[1] = 'Q';
    bs[2] = 'A';
    bs[len - 1] = CR;
    for (int i = 0; i < bytes.length; i++) {
      bs[i + 3] = bytes[i];
    }
    return bs;
  }

  public static byte[] setDisplayState(char n) {
    return new byte[] { ESC, 's', (byte) n };
  }

  public static void main(String[] args) throws Exception {

  }
}