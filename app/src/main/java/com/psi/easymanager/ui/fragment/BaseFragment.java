package com.psi.easymanager.ui.fragment;

import android.support.v4.app.Fragment;
import com.afollestad.materialdialogs.MaterialDialog;
import com.orhanobut.logger.Logger;
import com.psi.easymanager.BuildConfig;
import com.psi.easymanager.common.App;
import com.squareup.leakcanary.RefWatcher;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by dorado on 2016/9/21.
 */
public class BaseFragment extends Fragment {

  private List<MaterialDialog> list = new ArrayList<MaterialDialog>();

  @Override public void onDestroy() {
    super.onDestroy();
    if (BuildConfig.DEBUG) {
      RefWatcher refWatcher = App.getRefWatcher(getActivity());
      refWatcher.watch(this);
    }
  }
  @Override public void onDestroyView() {
    super.onDestroyView();
    dismissDialog();
  }
  public void addDialog(MaterialDialog dialog) {
    list.add(dialog);
  }
  protected void dismissDialog() {
    if (list.size() != 0) {
      for (MaterialDialog dialog : list) {
        if (dialog.isShowing()) {
          Logger.v("---dialog.dismiss();");
          dialog.dismiss();
        }
      }
    }
  }
}
