package com.psi.easymanager.utils;

import java.io.Closeable;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Created by Administrator on 2016-12-13.
 */
public class IOUtils {
  public static void closeCloseables(Closeable... closeables) {
    if (closeables != null) {
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
  }

  public static void closeCloseables(Socket socket, OutputStream outputStream, PrintWriter printWriter) {
    if (outputStream != null) {
      try {
        outputStream.close();
        outputStream = null;
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    if (printWriter != null) {
      printWriter.close();
      printWriter = null;
    }
    if (socket != null) {
      try {
        socket.close();
        socket = null;
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }
}
