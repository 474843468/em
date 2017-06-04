package com.psi.easymanager.ui.activity;

import android.os.Bundle;
import android.widget.FrameLayout;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.psi.easymanager.R;
import com.psi.easymanager.common.App;
import com.psi.easymanager.dao.dbUtil.DbCore;

/**
 * Created by dorado on 2016/6/19.
 */
public class SplashActivity extends BaseActivity {
  @Bind(R.id.content) FrameLayout mContent;

  @Override protected int provideContentViewId() {
    return R.layout.activity_splash;
  }

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    ButterKnife.bind(this);
    //跳转
    mContent.postDelayed(new Runnable() {
      @Override public void run() {
        openActivity(LoginActivity.class);
        SplashActivity.this.finish();
      }
    }, 2000);
  }

  @Override protected void onDestroy() {
    super.onDestroy();
    ButterKnife.unbind(this);
  }
}
