package com.psi.easymanager.utils;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import com.afollestad.materialdialogs.MaterialDialog;
import com.psi.easymanager.R;

/**
 * Created by psi on 2016/3/17.
 * 加载对话框
 */
public class DialogUtils {
  /**
   * 不可取消dialog
   */
  public static MaterialDialog showDialog(Context context, String title, String content) {
    return new MaterialDialog.Builder(context).title(title)
        .content(content)
        .progress(true, 0)
        .progressIndeterminateStyle(false)
        .autoDismiss(false)
        .cancelable(false)
        .canceledOnTouchOutside(false)
        .show();
  }

  public static MaterialDialog showDialog(Context context, String title, String content,
      String posText) {
    return new MaterialDialog.Builder(context).title(title)
        .content(content)
        .progress(true, 0)
        .progressIndeterminateStyle(false)
        .autoDismiss(false)
        .cancelable(false)
        .canceledOnTouchOutside(false)
        .positiveText(posText)
        .show();
  }

  public static MaterialDialog showSimpleDialog(Activity act, String title, String content) {
    //return new MaterialDialog.Builder(act).title(title)
    //    .content(content)
    //    .positiveText("确定")
    //    .negativeText("取消")
    //    .negativeColor(act.getResources().getColor(R.color.primary_text))
    //    .show();
    return showSimpleDialog(act, title, content, "确定", "取消");
  }

  public static MaterialDialog showSimpleDialog(Activity act, String title, String content,
      String posText, String negText) {
    return new MaterialDialog.Builder(act).title(title)
        .content(content)
        .positiveText(posText)
        .negativeText(negText)
        .negativeColor(act.getResources().getColor(R.color.primary_text))
        .show();
  }

  public static MaterialDialog showAddBTDeviceDialog(Activity act) {
    String[] items = new String[] { "58mm", "80mm" };
    return new MaterialDialog.Builder(act).content("添加蓝牙打印机")
        .items(items)
        .itemsCallbackSingleChoice(0, new MaterialDialog.ListCallbackSingleChoice() {
          @Override public boolean onSelection(MaterialDialog dialog, View view, int which,
              CharSequence text) {
            return true;
          }
        })
        .positiveText("确定")
        .negativeText("取消")
        .show();
  }

  public static MaterialDialog showDialog(Context context, String title) {
    return new MaterialDialog.Builder(context).title(title)
        .content("Please Wait")
        .progress(true, 0)
        .progressIndeterminateStyle(false)
        .canceledOnTouchOutside(false)
        .autoDismiss(false)
        .cancelable(false)
        .autoDismiss(false)
        .show();
  }

  public static MaterialDialog showDialogWith(Context context) {
    MaterialDialog dialog = new MaterialDialog.Builder(context).title("正在请求中")
        .content("Please Wait")
        .progress(true, 0)
        .progressIndeterminateStyle(false)
        .build();
    dialog.setCanceledOnTouchOutside(false);
    return dialog;
  }

  /**
   * 检查更新Dialog
   */
  public static MaterialDialog showCheckUpdateDialog(Context context) {
    MaterialDialog chechUpdateDialog = new MaterialDialog.Builder(context).title("检查更新")
        .content("Please Wait")
        .progress(true, 0)
        .progressIndeterminateStyle(false)
        .canceledOnTouchOutside(false)
        .cancelable(false)
        .show();
    return chechUpdateDialog;
  }

  /**
   * 是否下载最新版本dialog
   */
  public static MaterialDialog showDownLoadDialog(Context context, String versionName,
      String updateInfo) {
    return new MaterialDialog.Builder(context).title("发现新版本:" + versionName)
        .content("更新内容:\n" + updateInfo)
        .positiveText("更新")
        .negativeText("取消")
        .cancelable(false)
        .canceledOnTouchOutside(false)
        .autoDismiss(false)
        .show();
  }

  /**
   * 显示 下载进度 dialog
   */
  public static MaterialDialog showDownLoadingDialog(Context context) {
    return new MaterialDialog.Builder(context).title("下载中")
        .content("Please Wait")
        .progress(false, 0, true)
        .positiveText("取消更新")
        .progressIndeterminateStyle(false)
        .canceledOnTouchOutside(false)
        .cancelable(false)
        .show();
  }

  /**
   * 继续查询还是
   */
  public static MaterialDialog showCheckResultOrManualConfirm(Context context, String title,
      String content) {
    return new MaterialDialog.Builder(context).title(title)
        .content(content)
        .positiveText("等待查询")
        .negativeText("稍后查询")
        .negativeColor(context.getResources().getColor(R.color.primary_text))
        .cancelable(false)
        .autoDismiss(false)
        .canceledOnTouchOutside(false)
        .show();
  }

  /**
   * 支付宝支付结果
   */
  public static MaterialDialog showManualConfirm(Activity act, String[] items, String title) {
    return new MaterialDialog.Builder(act).title(title)
        .content("响应超时，请仔细核对用户手机结果提示!")
        .items(items)
        .itemsCallbackSingleChoice(1, new MaterialDialog.ListCallbackSingleChoice() {
          @Override public boolean onSelection(MaterialDialog dialog, View itemView, int which,
              CharSequence text) {
            return true;
          }
        })
        .positiveText("确认")
        .cancelable(false)
        .autoDismiss(false)
        .canceledOnTouchOutside(false)
        .show();
  }

  /**
   *
   */
  public static MaterialDialog showContinueQuery(Activity act, String title, String positive,
      String negative) {
    return new MaterialDialog.Builder(act).title(title)
        .content("服务器响应超时,请继续查询或稍后查询?")
        .positiveText(positive)
        .negativeText(negative)
        .cancelable(false)
        .autoDismiss(false)
        .canceledOnTouchOutside(false)
        .show();
  }

  /**
   * 单选 dialog
   */
  public static MaterialDialog showListDialog(Activity act, String title, String[] items) {
    return new MaterialDialog.Builder(act).title(title)
        .items(items)
        .itemsCallbackSingleChoice(0, new MaterialDialog.ListCallbackSingleChoice() {
          @Override public boolean onSelection(MaterialDialog dialog, View view, int which,
              CharSequence text) {
            return true;
          }
        })
        .positiveText("确定")
        .negativeText("取消")
        .cancelable(false)
        .autoDismiss(false)
        .canceledOnTouchOutside(false)
        .show();
  }

  /**
   * 关闭dialog
   */
  public static void dismissDialog(MaterialDialog dialog) {
    if (dialog != null) {
      if (dialog.isShowing()) {
        dialog.dismiss();
      }
      dialog = null;
    }
  }

  /**
   * vip支付等待
   */
  public static MaterialDialog showVipDialog(Context context, String title, String content) {
    return new MaterialDialog.Builder(context).title(title)
        .content(content)
        .positiveText("确定")
        .autoDismiss(false)
        .cancelable(false)
        .canceledOnTouchOutside(false)
        .show();
  }

  /**
   * 继续查询还是(翼支付)
   */
  public static MaterialDialog showCheckResultOrManualConfirmWithBestPay(Context context,
      String title, String content) {
    return new MaterialDialog.Builder(context).title(title)
        .content(content)
        .positiveText("继续查询")
        .negativeText("关闭")
        .negativeColor(context.getResources().getColor(R.color.primary_text))
        .cancelable(false)
        .autoDismiss(false)
        .canceledOnTouchOutside(false)
        .show();
  }

  /**
   * 继续查询还是
   */
  public static MaterialDialog showCheckResultOrManualConfirmWithBest(Context context, String title,
      String content) {
    return new MaterialDialog.Builder(context).title(title)
        .content(content)
        .positiveText("继续查询")
        .negativeText("稍后查询")
        .negativeColor(context.getResources().getColor(R.color.primary_text))
        .cancelable(false)
        .autoDismiss(false)
        .canceledOnTouchOutside(false)
        .show();
  }

  /**
   * 继续查询还是(微信)
   */
  public static MaterialDialog showCheckResultOrManualConfirmWithWeiXin(Context context,
      String title, String content) {
    return new MaterialDialog.Builder(context).title(title)
        .content(content)
        .positiveText("继续查询")
        .negativeText("关闭")
        .negativeColor(context.getResources().getColor(R.color.primary_text))
        .cancelable(false)
        .autoDismiss(false)
        .canceledOnTouchOutside(false)
        .show();
  }
}
