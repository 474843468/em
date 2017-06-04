package com.psi.easymanager.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.internal.MDButton;
import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.FileAsyncHttpResponseHandler;
import com.orhanobut.logger.Logger;
import com.psi.easymanager.R;
import com.psi.easymanager.common.App;
import com.psi.easymanager.config.URLConstants;
import com.psi.easymanager.dao.UserDao;
import com.psi.easymanager.dao.dbUtil.DaoServiceUtil;
import com.psi.easymanager.module.User;
import com.psi.easymanager.network.RestClient;
import com.psi.easymanager.network.req.HttpCheckUpdateReq;
import com.psi.easymanager.network.resp.HttpCheckUpdateResp;
import com.psi.easymanager.ui.activity.MoreActivity;
import com.psi.easymanager.utils.NetUtils;
import com.psi.easymanager.utils.PackageUtils;
import com.psi.easymanager.utils.DialogUtils;
import com.psi.easymanager.utils.ToastUtils;
import cz.msebera.android.httpclient.Header;
import java.io.File;

/**
 * Created by zjq on 2016/3/17.
 * 更多-关于逸掌柜
 */
public class AboutFireFragment extends BaseFragment {
  @Bind(R.id.tv_bg_address) TextView mTvBgAdress;

  private static final String ABOUT_FIRE_FRAGMENT_PARAM = "param";
  private String mParam;//MainActivity参数
  private MoreActivity mAct;

  public static AboutFireFragment newInstance(String mParam) {
    Bundle bundle = new Bundle();
    bundle.putString("param", mParam);
    AboutFireFragment fragment = new AboutFireFragment();
    fragment.setArguments(bundle);
    return fragment;
  }

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    if (getArguments() != null) {
      mParam = getArguments().getString(ABOUT_FIRE_FRAGMENT_PARAM);
    }
    mAct = (MoreActivity) getActivity();
  }

  @Nullable @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_about_fire, null);
    ButterKnife.bind(this, view);
    String bgAdress = "<a href='http://server.yizhanggui.cc/a/login'>http://server.yizhanggui.cc/</a>";
    mTvBgAdress.setText(Html.fromHtml(bgAdress));
    mTvBgAdress.setMovementMethod(LinkMovementMethod.getInstance());
    return view;
  }

  @Override public void onActivityCreated(@Nullable Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
  }

  /**
   * 检查更新版本
   */
  @OnClick(R.id.btn_check_update) public void checkUpdate(Button btn) {
    if (!NetUtils.isConnected(mAct)) {
      ToastUtils.showShort(App.getContext(), "暂无网络，稍后重试");
      return;
    }

    HttpCheckUpdateReq req = new HttpCheckUpdateReq();
    PackageUtils packageUtils = new PackageUtils(mAct);
    int localVersionCode = packageUtils.getLocalVersionCode();
    String localVersionName = packageUtils.getLocalVersionName();
    req.setVersionName(localVersionName);
    req.setVersionCode(localVersionCode);
    req.setType("2");
    User user = DaoServiceUtil.getUserService()
        .queryBuilder()
        .where(UserDao.Properties.LoginName.eq("admin"))
        .unique();
    if (user == null) {
      ToastUtils.showShort(mAct, "无用户,请初始化数据");
      return;
    }
    req.setCompanyCode(user.getCompanyCode());

    //显示检查更新dialog
    final MaterialDialog checkUpdateDialog = DialogUtils.showCheckUpdateDialog(mAct);

    new RestClient(0, 1000, 3000, 3000) {
      @Override protected void start() {

      }

      @Override protected void finish() {

      }

      @Override protected void failure(String responseString, Throwable throwable) {
        Logger.i("---" + responseString);
        ToastUtils.showShort(App.getContext(), "更新失败，请检查网络");

        DialogUtils.dismissDialog(checkUpdateDialog);
      }

      @Override protected void success(String responseString) {
        Logger.i("---" + responseString);
        DialogUtils.dismissDialog(checkUpdateDialog);

        Gson gson = new Gson();
        HttpCheckUpdateResp checkUpdateResp = gson.fromJson(responseString, HttpCheckUpdateResp.class);
        if (checkUpdateResp.getStatusCode() == 1) {
          //检查版本匹配
          checkVersion(checkUpdateResp);
        } else {
          ToastUtils.showShort(App.getContext(), "错误:" + checkUpdateResp.getMsg());
        }
      }
    }.postOther(mAct, URLConstants.VERSION_UPDATE, req);

  }

  /**
   * 判断是否是新的版本
   */
  private void checkVersion(final HttpCheckUpdateResp checkUpdateResp) {
    //获取当前版本
    int localVersionCode = new PackageUtils(mAct).getLocalVersionCode();
    if (localVersionCode < checkUpdateResp.getVersionCode()) {
      final String url = checkUpdateResp.getUrl();
      final MaterialDialog downLoadDialog =
          DialogUtils.showDownLoadDialog(mAct, checkUpdateResp.getVersionNumber(),
              checkUpdateResp.getUpdateInfo());
      MDButton positiveBtn = downLoadDialog.getActionButton(DialogAction.POSITIVE);
      MDButton negBtn = downLoadDialog.getActionButton(DialogAction.NEGATIVE);
      positiveBtn.setOnClickListener(new View.OnClickListener() {
        @Override public void onClick(View v) {

          downLoadNewVersion(url);
          DialogUtils.dismissDialog(downLoadDialog);
        }
      });

      negBtn.setOnClickListener(new View.OnClickListener() {
        @Override public void onClick(View v) {
          DialogUtils.dismissDialog(downLoadDialog);
        }
      });
    } else {
      ToastUtils.showShort(App.getContext(), "已经是最新版本");
    }
  }

  /**
   * 下载新版本
   */
  private void downLoadNewVersion(String url) {

    //下载中的dialog
    final MaterialDialog loadingDialog = DialogUtils.showDownLoadingDialog(mAct);
    //下载 路径
    File file = PackageUtils.getDownloadFile(mAct);
    final AsyncHttpClient client = new AsyncHttpClient();
    client.get(mAct, url, new FileAsyncHttpResponseHandler(file) {
      @Override
      public void onFailure(int statusCode, Header[] headers, Throwable throwable, File file) {
        ToastUtils.showShort(App.getContext(), "下载失败，请检查网络");
        DialogUtils.dismissDialog(loadingDialog);
      }

      @Override public void onProgress(long bytesWritten, long totalSize) {
        super.onProgress(bytesWritten, totalSize);
        loadingDialog.setMaxProgress((int) totalSize);
        loadingDialog.setProgress((int) bytesWritten);
      }

      @Override public void onSuccess(int statusCode, Header[] headers, File file) {
        DialogUtils.dismissDialog(loadingDialog);
        PackageUtils.installApk(mAct, file);
      }
    });
    //取消更新
    loadingDialog.getActionButton(DialogAction.POSITIVE)
        .setOnClickListener(new View.OnClickListener() {
          @Override public void onClick(View v) {
            client.cancelAllRequests(true);
            client.removeAllHeaders();
            DialogUtils.dismissDialog(loadingDialog);
          }
        });
  }

  /**
   * 重置注入
   */
  @Override public void onDestroyView() {
    super.onDestroyView();
    ButterKnife.unbind(this);
  }
}