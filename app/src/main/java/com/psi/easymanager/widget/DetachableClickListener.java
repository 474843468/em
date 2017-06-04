package com.psi.easymanager.widget;

import android.app.Dialog;
import android.view.View;
import android.view.ViewTreeObserver;

/**
 * User: ylw
 * Date: 2017-03-03
 * Time: 12:04
 * dialog 内存泄漏
 * Dalvik VM
 * http://www.jcodecraeer.com/a/anzhuokaifa/androidkaifa/2015/0911/3441.html
 */
public final class DetachableClickListener implements View.OnClickListener {

  public static DetachableClickListener wrap(View.OnClickListener delegate) {
    return new DetachableClickListener(delegate);
  }

  private View.OnClickListener delegateOrNull;

  private DetachableClickListener(View.OnClickListener delegate) {
    this.delegateOrNull = delegate;
  }


  public void clearOnDetach(Dialog dialog) {
    dialog.getWindow()
        .getDecorView()
        .getViewTreeObserver()
        .addOnWindowAttachListener(new ViewTreeObserver.OnWindowAttachListener() {
          @Override public void onWindowAttached() {
          }

          @Override public void onWindowDetached() {
            delegateOrNull = null;
          }
        });
  }

  @Override public void onClick(View v) {
    if (delegateOrNull != null) {
      delegateOrNull.onClick(v);
    }
  }
}
