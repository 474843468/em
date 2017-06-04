package com.psi.easymanager.print.net;

import com.psi.easymanager.common.App;
import com.psi.easymanager.event.PrintLabelQueueEvent;
import com.psi.easymanager.module.PxOrderDetails;
import com.psi.easymanager.print.PrintEventManager;
import java.util.List;

/**
 * User: ylw
 * Date: 2016-11-22
 * Time: 13:43
 * 网口标签打印管理
 */
public class PrintLabelTaskManager {
  public static void printLabelTask(List<PxOrderDetails> detailsList) {
    App app = (App) App.getContext();
    if (!app.isOpenLabel()) return;
    if (detailsList == null || detailsList.isEmpty()) return;
    PrintEventManager.getManager().postPrintEvent(PrintEventManager.PRINT_LABEL_TASK,new PrintLabelQueueEvent(detailsList));
  }
}  