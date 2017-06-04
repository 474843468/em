package com.psi.easymanager.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.internal.MDButton;
import com.psi.easymanager.R;
import com.psi.easymanager.common.App;
import com.psi.easymanager.config.Constants;
import com.psi.easymanager.dao.PxOrderInfoDao;
import com.psi.easymanager.dao.UserDao;
import com.psi.easymanager.dao.dbUtil.DaoServiceUtil;
import com.psi.easymanager.event.UploadOrderEvent;
import com.psi.easymanager.module.PxOrderInfo;
import com.psi.easymanager.module.User;
import com.psi.easymanager.service.PrintQueueService;
import com.psi.easymanager.upload.UpLoadOperationLog;
import com.psi.easymanager.upload.UpLoadOrder;
import com.psi.easymanager.utils.DialogUtils;
import com.psi.easymanager.utils.NetUtils;
import com.psi.easymanager.utils.SPUtils;
import com.psi.easymanager.utils.ServiceWorkUtil;
import com.psi.easymanager.utils.ToastUtils;
import com.psi.easymanager.widget.SwipeBackLayout;
import java.text.SimpleDateFormat;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by zjq on 2016/5/27.
 * 数据更新
 */
public class DataUpdateActivity extends BaseActivity {
  @Bind(R.id.swipe_back) SwipeBackLayout mSwipeBackView;
  @Bind(R.id.tv_last_update_time) TextView mTvLastUpdateTime;//上次更新时间
  @Bind(R.id.tv_last_upload_time) TextView mTvLastUploadTime;//上次上传时间
  @Bind(R.id.tv_upload_count) TextView mTvUploadCount;//待上传订单数
  private MaterialDialog mUploadDialog;
  private UIHandler mUiHandler;

  @Override protected int provideContentViewId() {
    return R.layout.activity_data_update;
  }

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    ButterKnife.bind(this);
    EventBus.getDefault().register(this);
    //初始化滑动关闭
    initSwipeBack();
    initView();
    mUiHandler = new UIHandler();
  }

  @Override protected void onDestroy() {
    super.onDestroy();
    EventBus.getDefault().unregister(this);
    ButterKnife.unbind(this);
  }

  /**
   * initView 上次操作时间
   */
  private void initView() {
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    long lastUploadTime = (long) SPUtils.get(this, Constants.LAST_UPLOAD_TIME, 1l);
    long lastUpdateTime = (long) SPUtils.get(this, Constants.LAST_UPDATE_TIME, 1l);
    String upLoadTime = sdf.format(lastUploadTime);
    String upDateTime = sdf.format(lastUpdateTime);
    mTvLastUploadTime.setText((lastUploadTime == 1l) ? "上次上传于: 无上传记录" : "上次上传于:" + upLoadTime);
    mTvLastUpdateTime.setText((lastUpdateTime == 1l) ? "上次更新于: 无更新记录" : "上次更新于:" + upDateTime);
    initOrderCount();
  }

  public void initOrderCount() {
    //已完结的订单
    long finishCount = DaoServiceUtil.getOrderInfoService()
        .queryBuilder()
        .where(PxOrderInfoDao.Properties.Status.eq(PxOrderInfo.STATUS_FINISH))
        .where(PxOrderInfoDao.Properties.IsUpload.eq(false))
        .buildCount()
        .forCurrentThread()
        .count();
    //1.反结账后撤销的(仅针对之前上传成功的)2.反结账后完结的
    long reverseCount = DaoServiceUtil.getOrderInfoService()
        .queryBuilder()
        .whereOr(PxOrderInfoDao.Properties.Status.eq(PxOrderInfo.STATUS_CANCEL),
            PxOrderInfoDao.Properties.Status.eq(PxOrderInfo.STATUS_FINISH))
        .where(PxOrderInfoDao.Properties.IsReversed.eq(PxOrderInfo.REVERSE_TRUE))
        .where(PxOrderInfoDao.Properties.IsUpload.eq(true))
        .where(PxOrderInfoDao.Properties.IsUploadReverse.eq(false))
        .buildCount()
        .forCurrentThread()
        .count();
    mTvUploadCount.setText((finishCount + reverseCount) + "个订单未上传");
  }

  /**
   * 初始化滑动关闭
   */
  private void initSwipeBack() {
    mSwipeBackView.setCallBack(new SwipeBackLayout.CallBack() {
      @Override public void onFinish() {
        finish();
      }
    });
  }
  private static final int SYNC_DATA = 100;
  private class UIHandler extends Handler{
    @Override public void handleMessage(Message msg) {
      super.handleMessage(msg);
      if (msg.what == SYNC_DATA){
        syncData();
      }
    }
  }
  /**
   * 设置更新数据
   */
  @OnClick(R.id.btn_data_update) public void dataUpdate(Button button) {
    //对话框
    final MaterialDialog dialog = new MaterialDialog.Builder(DataUpdateActivity.this).title("警告")
        .content("确认要从后台更新数据到这台设备吗?")
        .positiveText("确定")
        .negativeText("取消")
        .negativeColor(getResources().getColor(R.color.primary_text))
        .show();
    MDButton posBtn = dialog.getActionButton(DialogAction.POSITIVE);
    MDButton negBtn = dialog.getActionButton(DialogAction.NEGATIVE);
    posBtn.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        //syncData();
        mUiHandler.sendMessage(mUiHandler.obtainMessage(SYNC_DATA));
        DialogUtils.dismissDialog(dialog);
      }
    });

    negBtn.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        DialogUtils.dismissDialog(dialog);
      }
    });
  }

  /**
   * 上传营业数据
   */
  @OnClick(R.id.btn_data_upload) public void upLoadBusiness(Button btn) {
    final MaterialDialog dialog = new MaterialDialog.Builder(this).title("上传营业数据")
        .content("确定要上传营业数据到后台吗?")
        .positiveText("确定")
        .negativeText("取消")
        .negativeColor(getResources().getColor(R.color.primary_text))
        .show();
    MDButton posBtn = dialog.getActionButton(DialogAction.POSITIVE);
    MDButton negBtn = dialog.getActionButton(DialogAction.NEGATIVE);
    posBtn.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        //上传
        upLoadData();
        DialogUtils.dismissDialog(dialog);
      }
    });
    negBtn.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        DialogUtils.dismissDialog(dialog);
      }
    });
  }

  /**
   * 上传营业数据 自定义商品
   */
  private void upLoadData() {
    if (!NetUtils.isConnected(DataUpdateActivity.this)) {
      ToastUtils.showShort(App.getContext(), "请检查网络");
      return;
    }
    UpLoadOrder upLoadOrder = UpLoadOrder.getInstance();
    if (upLoadOrder.isUploading()){
      ToastUtils.showShort(DataUpdateActivity.this,"正在上传中...");
      return;
    }
    //显示上传Dialog
    mUploadDialog = DialogUtils.showDialog(DataUpdateActivity.this, "上传中...");
    //上传订单
    upLoadOrder.upLoadOrderInfo();
    //upLoadOrder.closePool();

    //上传自定义商品
    //UpLoadCustomProduct upLoadCustomProduct = UpLoadCustomProduct.getInstance();
    //upLoadCustomProduct.upLoadProdList();
    //upLoadCustomProduct.closePool();

    //上传操作记录
    UpLoadOperationLog upLoadOperationLog = UpLoadOperationLog.getInstance();
    upLoadOperationLog.upload();
    //upLoadOperationLog.closePool();
  }

  /**
   * 接收上传结果的event
   */
  @Subscribe(threadMode = ThreadMode.MAIN) public void receiveUploadResult(UploadOrderEvent event) {
    ToastUtils.showShort(App.getContext(), event.getMsg());
    if (event.getResult() == UpLoadOrder.ORDER_SUCCESS) {
      //保存上次上传时间
      SPUtils.put(this, Constants.LAST_UPLOAD_TIME, System.currentTimeMillis());
      //更新状态
      initView();
      //更新计数
      initOrderCount();
    }
    DialogUtils.dismissDialog(mUploadDialog);
  }

  /**
   * 同步数据
   */
  //@formatter:on
  private void syncData() {
    //存在内存中
    if (App.getContext() == null) {
      ToastUtils.showShort(this, "App有异常发生，请重新启动App");
      return;
    }
    //无网提示
    if (!NetUtils.isConnected(App.getContext())) {
      ToastUtils.showShort(App.getContext(), "暂无网络，无法更新最新数据");
      return;
    }
    User user = DaoServiceUtil.getUserService()
        .queryBuilder()
        .where(UserDao.Properties.LoginName.eq("admin"))
        .unique();
    if (user == null || user.getInitPassword() == null) {
      ToastUtils.showLong(App.getContext(), "用户信息有误");
      return;
    }
    String initPwd = user.getInitPassword();
    String companyCode = (String) SPUtils.get(this, Constants.SAVE_STORE_NUM, "");
    if (TextUtils.isEmpty(initPwd)) {
      ToastUtils.showLong(App.getContext(), "用户信息有误");
      return;
    }
    if (TextUtils.isEmpty(companyCode)) {
      ToastUtils.showShort(App.getContext(), "用户信息有误");
      return;
    }
    //更新状态
    initView();

    App app = (App) App.getContext();
    app.setFromDataUpdate(true);
    Intent intent = new Intent();
    intent.setClass(DataUpdateActivity.this, SyncActivity.class);
    Bundle bundle = new Bundle();
    bundle.putString("initPwd", initPwd);
    bundle.putString("companyCode", companyCode);
    //更新数据
    bundle.putBoolean("fromDataUpDateActivity", true);
    bundle.putBoolean("isUpdate", true);
    intent.putExtra("bundle", bundle);
    //关闭打印服务
    String serviceName = getPackageName() + ".service.PrintQueueService";
    if (ServiceWorkUtil.isServiceWork(this, serviceName)) {
      stopService(new Intent(this, PrintQueueService.class));
    }
    startActivity(intent);
    finish();
  }

  /**
   * 屏蔽返回键
   */
  @Override public boolean onKeyDown(int keyCode, KeyEvent event) {
    if (keyCode == KeyEvent.KEYCODE_BACK) {
      return true;
    }
    return super.onKeyDown(keyCode, event);
  }
}
