package com.psi.easymanager.ui.activity;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.internal.MDButton;
import com.daimajia.numberprogressbar.NumberProgressBar;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.FileAsyncHttpResponseHandler;
import com.orhanobut.logger.Logger;
import com.psi.easymanager.R;
import com.psi.easymanager.common.App;
import com.psi.easymanager.config.Constants;
import com.psi.easymanager.config.URLConstants;
import com.psi.easymanager.dao.EPaymentInfoDao;
import com.psi.easymanager.dao.PdConfigRelDao;
import com.psi.easymanager.dao.PrintDetailsCollectDao;
import com.psi.easymanager.dao.PrintDetailsDao;
import com.psi.easymanager.dao.PxExtraDetailsDao;
import com.psi.easymanager.dao.PxOrderDetailsDao;
import com.psi.easymanager.dao.PxOrderInfoDao;
import com.psi.easymanager.dao.PxPayInfoDao;
import com.psi.easymanager.dao.PxTableAlterationDao;
import com.psi.easymanager.dao.TableOrderRelDao;
import com.psi.easymanager.dao.UserDao;
import com.psi.easymanager.dao.data.DataLoader;
import com.psi.easymanager.dao.dbUtil.DaoServiceUtil;
import com.psi.easymanager.module.EPaymentInfo;
import com.psi.easymanager.module.PdConfigRel;
import com.psi.easymanager.module.PrintDetails;
import com.psi.easymanager.module.PrintDetailsCollect;
import com.psi.easymanager.module.PxExtraDetails;
import com.psi.easymanager.module.PxOrderDetails;
import com.psi.easymanager.module.PxOrderInfo;
import com.psi.easymanager.module.PxPayInfo;
import com.psi.easymanager.module.PxTableAlteration;
import com.psi.easymanager.module.TableOrderRel;
import com.psi.easymanager.module.User;
import com.psi.easymanager.network.RestClient;
import com.psi.easymanager.network.req.HttpCheckUpdateReq;
import com.psi.easymanager.network.req.HttpDataSyncReq;
import com.psi.easymanager.network.resp.HttpCheckUpdateResp;
import com.psi.easymanager.network.resp.HttpDataSyncResp;
import com.psi.easymanager.utils.DialogUtils;
import com.psi.easymanager.utils.PackageUtils;
import com.psi.easymanager.utils.SPUtils;
import com.psi.easymanager.utils.ToastUtils;
import cz.msebera.android.httpclient.Header;
import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class SyncActivity extends BaseActivity {
  @Bind(R.id.tv_data_size) TextView mTvDataSize;
  @Bind(R.id.number_progress_bar) NumberProgressBar mPb;
  @Bind(R.id.progress_view) View mProgressView;
  @Bind(R.id.tv_refresh_title) TextView mTvRefreshTitle;
  private static final int DATA_SIZE = 0;//数据大小
  private static final int UPDATE_PROGRESS = 1;//更新进度
  private static final int RESULT_CODE_FAIL = 13;//结果嘛
  private static final int RESULT_CODE_SUCCESS = 14;//结果嘛
  private static final int STORE_REQUEST = 5;//店家登陆请求
  private static final int DELETE_ORDER_OVER = 6;//删除订单结束
  private Timer mTimer;//更新progressbar
  private boolean mIsUpdate;
  private static final int STORE_LOGIN = 11;
  private static final int USER_LOGIN = 12;
  private boolean mFromDataUpDateActivity;
  private int mFromLogin;
  private int mDataSize;//更新数据量
  private MyHandler mHandler;
  private Bundle mBundle;

  @Override protected int provideContentViewId() {
    return R.layout.activity_sync;
  }

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    ButterKnife.bind(this);
    initView();
    mHandler = new MyHandler();
    Intent intent = getIntent();
    mBundle = intent.getBundleExtra("bundle");
    //是否是更新数据
    mIsUpdate = mBundle.getBoolean("isUpdate");
    if (mIsUpdate) {
      //删除72小时外的已结订单和撤销订单
      new Thread() {
        @Override public void run() {
          super.run();
          deleteOrderInfo();
          mHandler.sendEmptyMessage(DELETE_ORDER_OVER);
        }
      }.start();
    } else {
      //初始化数据
      String storeNum = mBundle.getString("storeNum");
      String storePwd = mBundle.getString("storePwd");
      //初始化数据
      initData(storeNum, storePwd);
    }
  }

  /**
   * 更新数据或检查版本
   */
  private void syncDataOrCheckVersion() {
    boolean isLogin = mBundle.getBoolean("isLogin");
    String initPwd = mBundle.getString("initPwd");
    String companyCode = mBundle.getString("companyCode");
    mFromDataUpDateActivity = mBundle.getBoolean("fromDataUpDateActivity");
    mFromLogin = mBundle.getInt("fromLogin");
    if (!isLogin) {
      //更新数据
      syncData(initPwd, companyCode, mFromLogin);
    } else {//来自登陆界面的数据更新 需要做版本检测 然后再更新数据
      checkVersion(initPwd, companyCode, mFromLogin);
    }
  }

  /**
   * init view
   */
  private void initView() {
    mProgressView.setVisibility(View.VISIBLE);
    mTvRefreshTitle.setText("正在更新");
  }

  /**
   * 检查版本
   */
  private void checkVersion(final String initPwd, final String companyCode, final int fromLogin) {
    HttpCheckUpdateReq req = new HttpCheckUpdateReq();
    PackageUtils packageUtils = new PackageUtils(this);
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
      ToastUtils.showShort(App.getContext(), "无用户,请初始化数据");
      return;
    }
    req.setCompanyCode(user.getCompanyCode());

    new RestClient(0, 1000, 3000, 3000) {
      @Override protected void start() {

      }

      @Override protected void finish() {

      }

      @Override protected void failure(String responseString, Throwable throwable) {
        //更新数据
        syncData(initPwd, companyCode, fromLogin);
      }

      @Override protected void success(String responseString) {
        Logger.e(responseString);
        Gson gson = new Gson();
        final HttpCheckUpdateResp resp = gson.fromJson(responseString, HttpCheckUpdateResp.class);
        if (resp.getStatusCode() == 1) {
          //获取当前版本
          int localVersionCode = new PackageUtils(SyncActivity.this).getLocalVersionCode();
          if (localVersionCode < resp.getVersionCode()) {
            final String url = resp.getUrl();
            final MaterialDialog downLoadDialog =
                DialogUtils.showDownLoadDialog(SyncActivity.this, resp.getVersionNumber(),
                    resp.getUpdateInfo());
            MDButton positiveBtn = downLoadDialog.getActionButton(DialogAction.POSITIVE);
            MDButton negativeBtn = downLoadDialog.getActionButton(DialogAction.NEGATIVE);
            positiveBtn.setOnClickListener(new View.OnClickListener() {
              @Override public void onClick(View v) {
                downLoadNewVersion(url, initPwd, companyCode, fromLogin, resp.getForceUpdate());
                DialogUtils.dismissDialog(downLoadDialog);
              }
            });
            if (HttpCheckUpdateResp.FORCE_UPDATE_TRUE.equals(resp.getForceUpdate())) {
              negativeBtn.setVisibility(View.GONE);
            }
            negativeBtn.setOnClickListener(new View.OnClickListener() {
              @Override public void onClick(View v) {
                //然后更新数据
                syncData(initPwd, companyCode, fromLogin);
                DialogUtils.dismissDialog(downLoadDialog);
              }
            });
          } else {
            //然后更新数据
            syncData(initPwd, companyCode, fromLogin);
          }
        } else {
          //然后更新数据
          syncData(initPwd, companyCode, fromLogin);
        }
      }
    }.postOther(SyncActivity.this, URLConstants.VERSION_UPDATE, req);
  }

  /**
   * 下载新版本
   */
  private void downLoadNewVersion(String url, final String initPwd, final String companyCode,
      final int fromLogin, String forceUpdate) {
    //下载中的dialog
    final MaterialDialog loadingDialog = DialogUtils.showDownLoadingDialog(SyncActivity.this);
    if (HttpCheckUpdateResp.FORCE_UPDATE_TRUE.equals(forceUpdate)) {
      MDButton positiveBtn = loadingDialog.getActionButton(DialogAction.POSITIVE);
      positiveBtn.setVisibility(View.GONE);
    }
    //下载 路径
    File file = PackageUtils.getDownloadFile(SyncActivity.this);
    final AsyncHttpClient client = new AsyncHttpClient();
    client.get(SyncActivity.this, url, new FileAsyncHttpResponseHandler(file) {
      @Override
      public void onFailure(int statusCode, Header[] headers, Throwable throwable, File file) {
        if (SyncActivity.this.isFinishing() || SyncActivity.this.isDestroyed()) return;
        ToastUtils.showShort(App.getContext(), "下载失败，请检查网络");
        DialogUtils.dismissDialog(loadingDialog);
        //然后更新数据
        syncData(initPwd, companyCode, fromLogin);
      }

      @Override public void onProgress(long bytesWritten, long totalSize) {
        super.onProgress(bytesWritten, totalSize);
        if (SyncActivity.this.isFinishing() || SyncActivity.this.isDestroyed()) return;
        loadingDialog.setMaxProgress((int) totalSize);
        loadingDialog.setProgress((int) bytesWritten);
      }

      @Override public void onSuccess(int statusCode, Header[] headers, File file) {
        if (SyncActivity.this.isFinishing() || SyncActivity.this.isDestroyed()) return;//Activity异常关闭
        DialogUtils.dismissDialog(loadingDialog);
        PackageUtils.installApk(SyncActivity.this, file);
      }
    });
    //取消更新
    loadingDialog.getActionButton(DialogAction.POSITIVE)
        .setOnClickListener(new View.OnClickListener() {
          @Override public void onClick(View v) {
            client.cancelAllRequests(true);
            client.removeAllHeaders();
            //然后更新数据
            syncData(initPwd, companyCode, fromLogin);
            DialogUtils.dismissDialog(loadingDialog);
          }
        });
  }

  /**
   * 删除
   */
  //@formatter:on
  private void deleteOrderInfo() {
    SQLiteDatabase db = DaoServiceUtil.getOfficeDao().getDatabase();
    db.beginTransaction();
    try {
      //3天前
      Date dateAfter = new Date(System.currentTimeMillis() - 3 * 24 * 60 * 60 * 1000);
      //已下单未反结账的list
      List<PxOrderInfo> orderList = DaoServiceUtil.getOrderInfoService()
          .queryBuilder()
          .where(PxOrderInfoDao.Properties.Status.eq(PxOrderInfo.STATUS_FINISH))
          .where(PxOrderInfoDao.Properties.StartTime.lt(dateAfter))
          .where(PxOrderInfoDao.Properties.IsUpload.eq(true))
          .whereOr(PxOrderInfoDao.Properties.IsReversed.eq(PxOrderInfo.REVERSE_FALSE),
              PxOrderInfoDao.Properties.IsReversed.isNull())
          .list();
      delDetailsList(orderList);
      //已下单反结账的list
      List<PxOrderInfo> orderInfoReverseList = DaoServiceUtil.getOrderInfoService()
          .queryBuilder()
          .where(PxOrderInfoDao.Properties.Status.eq(PxOrderInfo.STATUS_FINISH))
          .where(PxOrderInfoDao.Properties.StartTime.lt(dateAfter))
          .where(PxOrderInfoDao.Properties.IsReversed.eq(PxOrderInfo.REVERSE_TRUE))
          .where(PxOrderInfoDao.Properties.IsUploadReverse.eq(true))
          .list();
      delDetailsList(orderInfoReverseList);
      //撤销的未反结账list
      List<PxOrderInfo> cancelList = DaoServiceUtil.getOrderInfoService()
          .queryBuilder()
          .where(PxOrderInfoDao.Properties.Status.eq(PxOrderInfo.STATUS_CANCEL))
          .where(PxOrderInfoDao.Properties.StartTime.lt(dateAfter))
          .whereOr(PxOrderInfoDao.Properties.IsReversed.eq(PxOrderInfo.REVERSE_FALSE),
              PxOrderInfoDao.Properties.IsReversed.isNull())
          .list();
      delDetailsList(cancelList);
      //撤销的反结账的list
      List<PxOrderInfo> cancelReverseList = DaoServiceUtil.getOrderInfoService()
          .queryBuilder()
          .where(PxOrderInfoDao.Properties.Status.eq(PxOrderInfo.STATUS_CANCEL))
          .where(PxOrderInfoDao.Properties.StartTime.lt(dateAfter))
          .where(PxOrderInfoDao.Properties.IsReversed.eq(PxOrderInfo.REVERSE_TRUE))
          .where(PxOrderInfoDao.Properties.IsUploadReverse.eq(true))
          .list();
      delDetailsList(cancelReverseList);
      //预定的List
      //条件:预订单、预定类型、预定时间、订单状态
      List<PxOrderInfo> reserveOrderList = DaoServiceUtil.getOrderInfoService()
          .queryBuilder()
          .where(PxOrderInfoDao.Properties.IsReserveOrder.eq(PxOrderInfo.IS_REVERSE_ORDER_TRUE))
          .where(PxOrderInfoDao.Properties.ReserveState.eq(PxOrderInfo.RESERVE_STATE_RESERVE))
          .where(PxOrderInfoDao.Properties.StartTime.lt(dateAfter))
          .where(PxOrderInfoDao.Properties.Status.isNull())
          .list();
      //直接删除
      if (reserveOrderList != null && reserveOrderList.size() > 0) {
        DaoServiceUtil.getOrderInfoService().delete(reserveOrderList);
      }
      db.setTransactionSuccessful();
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      db.endTransaction();
    }
  }

  private void delDetailsList(List<PxOrderInfo> orderInfoList) {
    if (orderInfoList == null) return;
    for (PxOrderInfo orderInfo : orderInfoList) {
      //附加费list
      List<PxExtraDetails> extraDetailsList = DaoServiceUtil.getExtraDetailsService()
          .queryBuilder()
          .where(PxExtraDetailsDao.Properties.PxOrderInfoId.eq(orderInfo.getId()))
          .list();
      if (!extraDetailsList.isEmpty()) {
        DaoServiceUtil.getExtraDetailsService().delete(extraDetailsList);
      }
      //订单详情list
      List<PxOrderDetails> dbOrderDetailsList = DaoServiceUtil.getOrderDetailsService()
          .queryBuilder()
          .where(PxOrderDetailsDao.Properties.PxOrderInfoId.eq(orderInfo.getId()))
          .list();
      DaoServiceUtil.getOrderDetailsService().delete(dbOrderDetailsList);
      //PrintDetails
      List<PrintDetails> printDetailses = DaoServiceUtil.getPrintDetailsService()
          .queryBuilder()
          .where(PrintDetailsDao.Properties.PxOrderInfoId.eq(orderInfo.getId()))
          .list();
      if (!printDetailses.isEmpty()) {
        DaoServiceUtil.getPrintDetailsService().delete(printDetailses);
      }
      //PdCollect
      List<PrintDetailsCollect> collectList = DaoServiceUtil.getPdCollectService()
          .queryBuilder()
          .where(PrintDetailsCollectDao.Properties.PxOrderInfoId.eq(orderInfo.getId()))
          .list();
      if (!collectList.isEmpty()) {
        DaoServiceUtil.getPdCollectService().delete(collectList);
      }
      //PdConfigRel
      List<PdConfigRel> pdConfigRels = DaoServiceUtil.getPdConfigRelService()
          .queryBuilder()
          .where(PdConfigRelDao.Properties.PxOrderInfoId.eq(orderInfo.getId()))
          .list();
      if (!pdConfigRels.isEmpty()) {
        DaoServiceUtil.getPdConfigRelService().delete(pdConfigRels);
      }
      //支付信息list
      List<PxPayInfo> dbPayInfoList = DaoServiceUtil.getPayInfoService()
          .queryBuilder()
          .where(PxPayInfoDao.Properties.PxOrderInfoId.eq(orderInfo.getId()))
          .list();
      if (!dbPayInfoList.isEmpty()) {
        DaoServiceUtil.getPayInfoService().delete(dbPayInfoList);
      }
      //桌台变更信息
      List<PxTableAlteration> tableAlterationList = DaoServiceUtil.getTableAlterationService()
          .queryBuilder()
          .where(PxTableAlterationDao.Properties.PxOrderInfoId.eq(orderInfo.getId()))
          .list();
      if (!tableAlterationList.isEmpty()) {
        DaoServiceUtil.getTableAlterationService().delete(tableAlterationList);
      }
      //电子支付信息
      List<EPaymentInfo> ePaymentInfoList = DaoServiceUtil.getEPaymentInfoService()
          .queryBuilder()
          .where(EPaymentInfoDao.Properties.OrderInfoId.eq(orderInfo.getId()))
          .list();
      if (!ePaymentInfoList.isEmpty()) {
        DaoServiceUtil.getEPaymentInfoService().delete(ePaymentInfoList);
      }
      //订单桌台关系
      List<TableOrderRel> tableOrderRelList = DaoServiceUtil.getTableOrderRelService()
          .queryBuilder()
          .where(TableOrderRelDao.Properties.PxOrderInfoId.eq(orderInfo.getId()))
          .list();
      if (!tableOrderRelList.isEmpty()) {
        DaoServiceUtil.getTableOrderRelService().delete(tableOrderRelList);
      }
      //Order
      DaoServiceUtil.getOrderInfoService().delete(orderInfo);
    }
  }

  /**
   * 更新数据
   */
  private void syncData(String initPwd, String companyCode, final int fromLogin) {
    //发送请求
    HttpDataSyncReq req = new HttpDataSyncReq();
    req.setInitPassword(initPwd);
    req.setPassword(initPwd);
    req.setLoginName("admin");
    req.setType("0");
    req.setInstall("1");
    req.setCompanyCode(companyCode);
    String localVersionName = new PackageUtils(this).getLocalVersionName();
    req.setVersionName(localVersionName);
    new RestClient(0, 1000, 10000, 5000) {
      @Override protected void start() {
      }

      @Override protected void finish() {

      }

      @Override protected void failure(String responseString, Throwable throwable) {
        returnSyncFailRes(fromLogin);
      }

      @Override protected void success(String responseString) {
        Log.i("Fire", " " + responseString);
        //保存数据到本地数据库
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
        final HttpDataSyncResp syncResp = gson.fromJson(responseString, HttpDataSyncResp.class);
        if (syncResp == null) {
          returnSyncFailRes(fromLogin);
          return;
        }
        if (syncResp.getStatusCode() == 0) {
          ToastUtils.showShort(App.getContext(), "请求错误:" + syncResp.getMsg());
          returnSyncFailRes(fromLogin);
          return;
        }
        if (syncResp.getDataList() == null || syncResp.getDataList().size() == 0) {
          returnSyncFailRes(fromLogin);
        } else {
          //dataList 的数量
          Message msg = Message.obtain();
          msg.what = DATA_SIZE;
          mDataSize = syncResp.getDataList().size();
          msg.obj = mDataSize;
          mHandler.sendMessage(msg);
          new Thread() {
            @Override public void run() {
              super.run();
              //储存数据
              DataLoader.getInstance().saveData(syncResp.getDataList());
              //保存上次更新时间
              SPUtils.put(SyncActivity.this, Constants.LAST_UPDATE_TIME,
                  System.currentTimeMillis());
              //更新进度u
              updateProgress();
            }
          }.start();
        }
      }
    }.postStoreLogin(SyncActivity.this, URLConstants.DATA_SYNC, req);
  }

  /**
   * 初始化数据
   */
  private void initData(final String storeNum, final String storePwd) {
    //发送请求
    HttpDataSyncReq req = new HttpDataSyncReq();
    req.setInitPassword(storePwd);
    req.setLoginName("admin");
    req.setType("0");
    req.setInstall("0");
    req.setCompanyCode(storeNum.toUpperCase());
    String localVersionName = new PackageUtils(this).getLocalVersionName();
    req.setVersionName(localVersionName);
    Constants.INIT_STORE_NUM = storeNum;
    Constants.INIT_STORE_PSD = storePwd;
    new RestClient() {
      @Override protected void start() {
      }

      @Override protected void finish() {
      }

      @Override protected void failure(String responseString, Throwable throwable) {
        Logger.i("fail:" + responseString);
        ToastUtils.showShort(App.getContext(), "网络错误，请重试");
        returnInitFailResult();
      }

      @Override protected void success(String responseString) {
        Logger.i(responseString);
        final HttpDataSyncResp syncResp =
            new Gson().fromJson(responseString, HttpDataSyncResp.class);
        if (syncResp == null) {
          returnInitFailResult();
          return;
        }
        if (syncResp.getStatusCode() != 1) {
          ToastUtils.showShort(App.getContext(), "请求错误:" + syncResp.getMsg());
          returnInitFailResult();
          return;
        }
        if (syncResp.getDataList() == null || syncResp.getDataList().size() == 0) {
          ToastUtils.showShort(App.getContext(), "服务器无最新数据");
          returnInitFailResult();
        } else {
          //dataList 的数量
          Message msg = Message.obtain();
          msg.what = DATA_SIZE;
          mDataSize = syncResp.getDataList().size();
          msg.obj = mDataSize;
          mHandler.sendMessage(msg);
          //店家登陆成功 保存店家信息
          SPUtils.put(SyncActivity.this, Constants.SAVE_STORE_NUM, storeNum);
          SPUtils.put(SyncActivity.this, Constants.SAVE_STORE_PWD, storePwd);
          //SPUtils.put(SyncActivity.this, Constants.OLD_VERSION_CODE, nowVersionCode);

          new Thread(new Runnable() {
            @Override public void run() {
              //储存数据
              DataLoader.getInstance().saveData(syncResp.getDataList());
              //更新进度条
              updateProgress();
            }
          }).start();
        }
      }
    }.postStoreLogin(SyncActivity.this, URLConstants.DATA_SYNC, req);
  }

  /**
   * 返回 更新数据 失败结果
   */
  private void returnSyncFailRes(int from) {
    if (mFromDataUpDateActivity) {
      turnToLogin();
    } else {
      //用户登陆的更新
      if (from == USER_LOGIN) {
        turnToMain();
      } else {
        turnToLogin();
      }
    }
  }

  /**
   * 返回 更新数据 成功结果
   */
  private void returnSyncSuccRes() {
    if (mFromLogin == USER_LOGIN) {
      turnToMain();
    } else {
      turnToLogin();
    }
  }

  /**
   * 跳登陆界面
   */
  private void turnToLogin() {
    //跳转
    Intent intent = new Intent(SyncActivity.this, LoginActivity.class);
    startActivity(intent);
    SyncActivity.this.finish();
  }

  /**
   * 跳转至主界面
   */
  private void turnToMain() {
    //跳转
    Intent intent = new Intent(SyncActivity.this, MainActivity.class);
    startActivity(intent);
    SyncActivity.this.finish();
  }

  /**
   * 初始化数据返回失败结果
   */
  private void returnInitFailResult() {
    Intent intent = new Intent();
    intent.setClass(this, LoginActivity.class);
    setResult(RESULT_CODE_FAIL, intent);
    SyncActivity.this.finish();
  }

  /**
   * 初始化数据 返回成功的结果
   */
  private void returnInitSuccRes() {
    Intent intent = new Intent();
    intent.setClass(SyncActivity.this, LoginActivity.class);
    setResult(RESULT_CODE_SUCCESS, intent);
    SyncActivity.this.finish();
  }

  /**
   * 更新进度条，关闭蒙层
   */
  int i = 0;

  private void updateProgress() {
    int timeUnit = 100;//毫秒值
    if (mDataSize < 50) {
      timeUnit = 200;
    } else {
      timeUnit = 500;
    }
    mTimer = new Timer();
    mTimer.schedule(new TimerTask() {
      @Override public void run() {
        mHandler.sendEmptyMessage(UPDATE_PROGRESS);
        i = i + 1;
        if (i == 4) {
          if (!mIsUpdate) {
            returnInitSuccRes();
          } else {
            returnSyncSuccRes();
          }
        }
      }
    }, timeUnit, timeUnit);
  }

  /**
   * 主线程更新
   */
  class MyHandler extends Handler {
    @Override public void handleMessage(Message msg) {
      switch (msg.what) {
        case DELETE_ORDER_OVER:
          syncDataOrCheckVersion();
          break;
        case UPDATE_PROGRESS:
          if (mPb != null) {
            mPb.incrementProgressBy(25);
          }
          break;
        case DATA_SIZE:
          int size = (int) msg.obj;
          mTvDataSize.setText("更新" + size + "条数据");
          break;
      }
    }
  }

  @Override protected void onDestroy() {
    super.onDestroy();
    if (mTimer != null) {
      mTimer.cancel();
    }
    if (mHandler != null) {
      mHandler.removeCallbacksAndMessages(null);
      mHandler = null;
    }
    ButterKnife.unbind(this);
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