package com.psi.easymanager.widget;

import android.app.Activity;
import android.content.Context;
import android.text.InputType;
import android.util.AttributeSet;
import android.view.WindowManager;
import android.widget.EditText;
import com.orhanobut.logger.Logger;
import java.lang.reflect.Method;

/**
 * User: ylw
 * Date: 2016-12-26
 * Time: 11:15
 * 隐藏软键盘EditText
 */
public class HideSoftInputEditText extends EditText {
  public HideSoftInputEditText(Context context) {
    this(context,null);
  }

  public HideSoftInputEditText(Context context, AttributeSet attrs) {
    this(context, attrs,0);
  }

  public HideSoftInputEditText(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    hideSoftInput();
  }

  private void hideSoftInput() {
    if (android.os.Build.VERSION.SDK_INT <= 10) {
      this.setInputType(InputType.TYPE_NULL);
    } else {

      Activity act = (Activity) this.getContext();
      act.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
      try {
        Class<EditText> cls = EditText.class;
        Method setSoftInputShownOnFocus;
        setSoftInputShownOnFocus = cls.getMethod("setShowSoftInputOnFocus", boolean.class);
        setSoftInputShownOnFocus.setAccessible(true);
        setSoftInputShownOnFocus.invoke(this, false);
      } catch (Exception e) {
        Logger.e(e.toString());
        e.printStackTrace();
      }
    }
  }
}