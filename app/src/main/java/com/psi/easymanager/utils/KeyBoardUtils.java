package com.psi.easymanager.utils;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import com.psi.easymanager.R;
import com.psi.easymanager.common.App;

/**
 * User: ylw
 * Date: 2016-12-13
 * Time: 15:27
 * FIXME
 */
public class KeyBoardUtils {
  private KeyBoardUtils() {
    throw new UnsupportedOperationException("u can't instantiate me...");
  }

  /**
   * 避免输入法面板遮挡
   * <p>在manifest.xml中activity中设置</p>
   * <p>android:windowSoftInputMode="adjustPan"</p>
   */

  /**
   * 动态隐藏软键盘
   */
  public static void hideSoftInput(EditText et) {
    InputMethodManager imm = (InputMethodManager) App.getContext().getSystemService(Activity.INPUT_METHOD_SERVICE);
    if (imm.hideSoftInputFromWindow(et.getWindowToken(), 0)) {
      imm.hideSoftInputFromWindow(et.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }
  }

  /**
   * 动态隐藏软键盘
   */
  public static void hideSoftInput(Activity act) {
    InputMethodManager imm = (InputMethodManager) App.getContext().getSystemService(Activity.INPUT_METHOD_SERVICE);
    View view = act.getCurrentFocus();
    if (view.getId() == R.id.et_pay_third_paycode){
      if (imm.hideSoftInputFromWindow(view.getWindowToken(), 0)) {
        imm.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
      }
    }
  }
  /**
   * 点击屏幕空白区域隐藏软键盘
   * <p>根据EditText所在坐标和用户点击的坐标相对比，来判断是否隐藏键盘</p>
   * <p>需重写dispatchTouchEvent</p>
   * <p>参照以下注释代码</p>
   */
  public static void clickBlankArea2HideSoftInput() {
        /*
        @Override
        public boolean dispatchTouchEvent(MotionEvent ev) {
            if (ev.getAction() == MotionEvent.ACTION_DOWN) {
                View v = getCurrentFocus();
                if (isShouldHideKeyboard(v, ev)) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                }
            }
            return super.dispatchTouchEvent(ev);
        }

        // 根据EditText所在坐标和用户点击的坐标相对比，来判断是否隐藏键盘
        private boolean isShouldHideKeyboard(View v, MotionEvent event) {
            if (v != null && (v instanceof EditText)) {
                int[] l = {0, 0};
                v.getLocationInWindow(l);
                int left = l[0],
                        top = l[1],
                        bottom = top + v.getHeight(),
                        right = left + v.getWidth();
                return !(event.getX() > left && event.getX() < right
                        && event.getY() > top && event.getY() < bottom);
            }
            return false;
        }
        */
  }

  /**
   * 动态显示软键盘
   *
   * @param edit 输入框
   */
  public static void showSoftInput(Context context, EditText edit) {
    edit.setFocusable(true);
    edit.setFocusableInTouchMode(true);
    edit.requestFocus();
    InputMethodManager imm =
        (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
    imm.showSoftInput(edit, 0);
  }

  /**
   * 切换键盘显示与否状态
   */
  public static void toggleSoftInput(Context context) {
    InputMethodManager imm =
        (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
  }
}  