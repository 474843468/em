package com.psi.easymanager.print.net.event;

import com.psi.easymanager.event.PrintLabelQueueEvent;
import com.psi.easymanager.event.PrintQueueEvent;

/**
 * 作者：${ylw} on 2016-12-12 17:58
 */
public interface IPrintEvent {

  void receivePrintTask(PrintQueueEvent event);

  void receiveLabelPrintTask(PrintLabelQueueEvent event);

  void receiveOpenCashBoxEvent();

  void switchLabelPrint(boolean isOpen);
}
