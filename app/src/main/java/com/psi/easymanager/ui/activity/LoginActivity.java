package com.psi.easymanager.ui.activity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.orhanobut.logger.Logger;
import com.psi.easymanager.BuildConfig;
import com.psi.easymanager.R;
import com.psi.easymanager.common.App;
import com.psi.easymanager.config.Constants;
import com.psi.easymanager.dao.OfficeDao;
import com.psi.easymanager.dao.PxPromotioInfoDao;
import com.psi.easymanager.dao.UserDao;
import com.psi.easymanager.dao.dbUtil.DaoServiceUtil;
import com.psi.easymanager.module.Office;
import com.psi.easymanager.module.PxPromotioInfo;
import com.psi.easymanager.module.User;
import com.psi.easymanager.service.UpLoadErrorService;
import com.psi.easymanager.utils.CryptosUtils;
import com.psi.easymanager.utils.IpAddressUtils;
import com.psi.easymanager.utils.NetUtils;
import com.psi.easymanager.utils.PackageUtils;
import com.psi.easymanager.utils.SPUtils;
import com.psi.easymanager.utils.ToastUtils;
import de.greenrobot.dao.query.QueryBuilder;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * 作者：${ylw} on 2016/5/26 14:47
 */
public class LoginActivity extends AppCompatActivity {
  //商家登录界面
  @Bind(R.id.fl_login_by_store) FrameLayout mFlLoginByStore;
  //用户登录界面
  @Bind(R.id.fl_login_by_user) FrameLayout mFlLoginByUser;
  //用户 用户名
  @Bind(R.id.fl_user_login_name) FrameLayout mFlUserName;
  //用户 密码
  @Bind(R.id.fl_user_login_pwd) FrameLayout mFlUserPwd;
  //商家 用户名
  @Bind(R.id.fl_store_login_name) FrameLayout mFlStoreName;
  //商家 密码
  @Bind(R.id.fl_store_login_pwd) FrameLayout mFlStorePwd;
  //商家 登录入口
  @Bind(R.id.tv_store_login_entrance) TextView mTvStoreLoginEntrance;

  //用户 用户名
  @Bind(R.id.tv_user_login_name) TextView mTvUserName;
  //用户 密码
  @Bind(R.id.tv_user_login_pwd) TextView mTvUserPwd;
  //商家 用户名
  @Bind(R.id.tv_store_login_name) TextView mTvStoreName;
  //商家 密码
  @Bind(R.id.tv_store_login_pwd) TextView mTvStorePwd;

  //版本号
  @Bind(R.id.tv_app_version) TextView mTvVersionName;
  //ip地址
  @Bind(R.id.tv_ip_address) TextView mTvIpAddress;
  //记住密码
  @Bind(R.id.cb_save_pwd) CheckBox mCbSavePwd;
  //存放用户名
  private StringBuilder mSbUserName = new StringBuilder();
  //存放密码
  private StringBuilder mSbUserPwd = new StringBuilder();
  //存放商家编号
  private StringBuilder mSbStoreNo = new StringBuilder();
  //存放初始化密码
  private StringBuilder mSbStorePwd = new StringBuilder();

  private int mCapsLock = LOWER;
  private static final int UPPER = 0;
  public static final int LOWER = 1;
  private static final int STORE_LOGIN = 11;
  private static final int USER_LOGIN = 12;
  private int mInputFocusMark = 0;//输入标记
  private static final int MARK_USER_NAME = 1;//用户名标识
  private static final int MARK_USER_PWD = 2;//用户密码标识
  private static final int MARK_STORE_NO = 3;//店家编号标识
  private static final int MARK_STORE_PWD = 4;//初始化密码标识
  private static final int STORE_REQUEST = 5;//店家登陆请求
  private static final int UPDATE_DATA_REQUEST = 6;//更新数据请求
  private static final int RESULT_CODE_FAIL = 13;//结果失败
  private static final int RESULT_CODE_SUCCESS = 14;//结果成功

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_login);
    ButterKnife.bind(this);
    //给按钮设置@字符
    ((Button) findViewById(R.id.btn_special)).setText("@");
    //初始化版本号
    initAppVersion();
    //初始化按钮背景
    initBtnBg();
    //初始化Ip
    initIpAddress();
    //初始化记住密码
    initRememberPwd();
    //开启上传error服务
    initErrorUpLoad();
  }

  private void initErrorUpLoad() {
    if (!NetUtils.isConnected(this)) return;
    File errorLogDir = PackageUtils.getErrorLogDir(this);
    File crash = new File(errorLogDir, Constants.LOG_NAME);
    User user = DaoServiceUtil.getUserService()
        .queryBuilder()
        .where(UserDao.Properties.DelFlag.eq("0"))
        .where(UserDao.Properties.LoginName.eq("admin"))
        .unique();
    //存在error
    if (crash.exists() && user != null) {
      Intent intent = new Intent();
      intent.putExtra("user", user);
      intent.putExtra("crash", crash);
      intent.setClass(LoginActivity.this, UpLoadErrorService.class);
      startService(intent);
    }
  }

  /**
   * 初始化版本号
   */
  private void initAppVersion() {
    String localVersionName = new PackageUtils(App.getContext()).getLocalVersionName();
    mTvVersionName.setText("v" + localVersionName);
  }

  /**
   * 初始化按钮背景
   */
  private void initBtnBg() {
    mFlUserName.setBackgroundResource((R.drawable.bg_login_fl_unsel));
    mFlUserPwd.setBackgroundResource((R.drawable.bg_login_fl_unsel));
    mFlStoreName.setBackgroundResource((R.drawable.bg_login_fl_unsel));
    mFlStorePwd.setBackgroundResource((R.drawable.bg_login_fl_unsel));
  }

  /**
   * 初始化Ip地址
   */
  private void initIpAddress() {
    mTvIpAddress.setText("本机IP地址:" + IpAddressUtils.getLocalIpAddress());
  }

  /**
   * 初始化记住密码
   */
  private void initRememberPwd() {
    String storeNum = (String) SPUtils.get(this, Constants.SAVE_STORE_NUM, "");
    String storePwd = (String) SPUtils.get(this, Constants.SAVE_STORE_PWD, "");
    if (!storeNum.isEmpty() && !storePwd.isEmpty()) {
      mTvStoreName.setText(storeNum);
      mTvStorePwd.setText(storePwd);

      mSbStoreNo.append(storeNum);
      mSbStorePwd.append(storePwd);
      //商家登录变成店家编号
      Office office = DaoServiceUtil.getOfficeDao().queryBuilder().unique();
      if (office == null) return;
      String name = office.getName().isEmpty() ? storeNum : "店铺：" + office.getName();
      mTvStoreLoginEntrance.setText(name);
    }

    //回显用户信息
    if ((boolean) SPUtils.get(this, Constants.REMEMBER_PWD, false)) {
      String remLoginUserName = (String) SPUtils.get(this, Constants.SAVE_LOGIN_NAME, "");
      String remLoginUserPwd = (String) SPUtils.get(this, Constants.SAVE_LOGIN_PWD, "");
      mSbUserPwd = new StringBuilder();
      mSbUserPwd.append(remLoginUserPwd);
      mTvUserName.setText(remLoginUserName);
      mTvUserPwd.setText(remLoginUserPwd);
      mCbSavePwd.setChecked(true);
    }
  }

  /**
   * 获取商家数据
   */
  @OnClick(R.id.btn_confirm_store_login) public void confirmStoreLogin() {
    final String storeNum = mSbStoreNo.toString().trim();
    final String storePwd = mSbStorePwd.toString().trim();
    if (storeNum.isEmpty() || storePwd.isEmpty()) {
      ToastUtils.showShort(App.getContext(), "店家编号或初始化密码不能为空!");
      return;
    }
    //禁止登陆两个店家
    String spStoreNum = (String) SPUtils.get(LoginActivity.this, Constants.SAVE_STORE_NUM, "");
    String spStorePwd = (String) SPUtils.get(LoginActivity.this, Constants.SAVE_STORE_PWD, "");
    List<User> userList = DaoServiceUtil.getUserService().queryBuilder().list();
    //无网提示
    if (!NetUtils.isConnected(this)) {
      ToastUtils.showShort(App.getContext(), "暂无网络，无法更新最新数据");
      return;
    }
    if (!spStoreNum.isEmpty() && !spStorePwd.isEmpty() && userList != null && userList.size() > 0) {
      syncData(STORE_LOGIN);
    } else {
      initData(storeNum, storePwd);
    }
  }

  /**
   * 初始化数据
   */
  private void initData(String storeNum, String storePwd) {
    //跳转初始化数据activity
    Intent intent = new Intent();
    Bundle bundle = new Bundle();
    bundle.putString("storePwd", storePwd);
    bundle.putString("storeNum", storeNum);
    bundle.putBoolean("isUpdate", false);
    intent.putExtra("bundle", bundle);
    intent.setClass(LoginActivity.this, SyncActivity.class);
    startActivityForResult(intent, STORE_REQUEST);
  }

  /**
   * 分析结果
   */
  @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    //店家登陆初始化请求
    if (requestCode == STORE_REQUEST) {
      switch (resultCode) {
        case RESULT_CODE_FAIL:
          break;
        case RESULT_CODE_SUCCESS:
          //切换至用户登录页面
          mFlLoginByStore.setVisibility(View.GONE);
          mFlLoginByUser.setVisibility(View.VISIBLE);
          Office office = DaoServiceUtil.getOfficeDao().queryBuilder().unique();
          if (office != null) {
            String name = office.getName().isEmpty() ? "店铺：" : "店铺：" + office.getName();
            mTvStoreLoginEntrance.setText(name);
          }
          break;
      }
    } else if (requestCode == UPDATE_DATA_REQUEST) {//数据更新请求

    }
  }

  /**
   * 商店登录入口点击
   */
  @OnClick(R.id.rl_store_login_entrance) public void storeLoginEntrance() {
    mFlLoginByUser.setVisibility(View.GONE);
    mFlLoginByStore.setVisibility(View.VISIBLE);
    mInputFocusMark = MARK_STORE_NO;
    initBtnBg();
    mFlStoreName.setBackgroundResource((R.drawable.bg_login_fl_sel));
  }

  /**
   * 取消商家登录点击
   */
  @OnClick(R.id.btn_cancel_store_login) public void cancelStoreLogin() {
    mFlLoginByUser.setVisibility(View.VISIBLE);
    mFlLoginByStore.setVisibility(View.GONE);
    mInputFocusMark = MARK_USER_NAME;
    mFlUserName.setBackgroundResource((R.drawable.bg_login_fl_sel));
  }

  /**
   * 用户 用户名点击
   */
  @OnClick(R.id.fl_user_login_name) public void userNameClick() {
    initBtnBg();
    mFlUserName.setBackgroundResource((R.drawable.bg_login_fl_sel));
    mInputFocusMark = MARK_USER_NAME;
  }

  /**
   * 用户 密码点击
   */
  @OnClick(R.id.fl_user_login_pwd) public void userPwdClick() {
    initBtnBg();
    mFlUserPwd.setBackgroundResource((R.drawable.bg_login_fl_sel));
    mInputFocusMark = MARK_USER_PWD;
  }

  /**
   * 商家 用户名点击
   */
  @OnClick(R.id.fl_store_login_name) public void storeNameClick() {
    initBtnBg();
    mFlStoreName.setBackgroundResource((R.drawable.bg_login_fl_sel));
    mInputFocusMark = MARK_STORE_NO;
  }

  /**
   * 商家 密码点击
   */
  @OnClick(R.id.fl_store_login_pwd) public void storePwdClick() {
    initBtnBg();
    mFlStorePwd.setBackgroundResource((R.drawable.bg_login_fl_sel));
    mInputFocusMark = MARK_STORE_PWD;
  }

  /**
   * 登录到MainActivity
   */
  @OnClick(R.id.btn_user_login) public void goToMainActivity() {
    //有效的user
    List<User> userList = DaoServiceUtil.getUserService()
        .queryBuilder()
        .where(UserDao.Properties.DelFlag.eq("0"))
        .list();
    if (userList == null || userList.size() == 0) {//店家未登录or 店家未分配账号
      ToastUtils.showShort(App.getContext(), "无有效用户");
      return;
    }
    String userName = mTvUserName.getText().toString().trim();
    String userPwd = mSbUserPwd.toString().trim();
    if (userName.isEmpty() || userPwd.isEmpty()) {
      ToastUtils.showShort(App.getContext(), "用户名和密码不能为空!");
      return;
    }
    QueryBuilder<User> userQueryBuilder = DaoServiceUtil.getUserService().queryBuilder();
    userQueryBuilder.where(UserDao.Properties.LoginName.eq(userName.trim()));
    User resultUser = userQueryBuilder.unique();
    if (resultUser == null) {
      ToastUtils.showShort(App.getContext(), "用户名或密码不对，请重新输入!");
      return;
    }
    //匹配用户名和密码
    String dbPassword = resultUser.getPassword();
    boolean isRight = CryptosUtils.validatePassword(userPwd.trim().toString(), dbPassword);
    if (!isRight) {
      ToastUtils.showShort(App.getContext(), "用户名或密码不正确，请重新输入!");
      return;
    }
    SPUtils.put(LoginActivity.this, Constants.REMEMBER_PWD, mCbSavePwd.isChecked());
    if (mCbSavePwd.isChecked()) {//保存密码
      SPUtils.put(LoginActivity.this, Constants.SAVE_LOGIN_NAME, userName);
      SPUtils.put(LoginActivity.this, Constants.SAVE_LOGIN_PWD, userPwd);
    }
    //设置群组id和密码
    String companyCode = resultUser.getCompanyCode();
    if (companyCode != null) {
      Office office = DaoServiceUtil.getOfficeService()
          .queryBuilder()
          .where(OfficeDao.Properties.Code.eq(companyCode))
          .unique();
      if (office == null) {
        ToastUtils.showShort(App.getContext(), "公司为空");
        return;
      }
      resultUser.setGroupId(office.getGroupId());
      resultUser.setChatPwd(office.getInitPassword());
    }
    App app = (App) App.getContext();
    //添加用户
    app.setUser(resultUser);
    //存储 login user ObjId
    SPUtils.put(this, Constants.LOGIN_USER_OBJID, resultUser.getObjectId());
    //无网提示
    if (!NetUtils.isConnected(this)) {
      ToastUtils.showShort(App.getContext(), "暂无网络，无法更新最新数据");
      turnToMain();
      return;
    }
    //判断是不是从数据更新界面跳过来的
    boolean fromDataUpdate = app.isFromDataUpdate();
    if (!fromDataUpdate) {
      app.setFromDataUpdate(false);
      //同步数据
      syncData(USER_LOGIN);
    } else {
      //重置false
      app.setFromDataUpdate(false);
      turnToMain();
    }
  }

  /**
   * 同步数据
   */
  //@formatter:on
  private void syncData(int from) {
    User user = DaoServiceUtil.getUserService()
        .queryBuilder()
        .where(UserDao.Properties.LoginName.eq("admin"))
        .unique();
    if (user == null || user.getInitPassword() == null) {
      ToastUtils.showLong(App.getContext(), "用户信息有误");
      turnToMain();
      return;
    }
    String initPwd = user.getInitPassword();
    if (("").equals(initPwd)) {
      ToastUtils.showLong(App.getContext(), "用户信息有误");
      turnToMain();
      return;
    }
    String companyCode = (String) SPUtils.get(this, Constants.SAVE_STORE_NUM, "");
    if ("".equals(companyCode.trim())) {
      ToastUtils.showShort(App.getContext(), "信息有误，无法更新");
      return;
    }
    Intent intent = new Intent();
    intent.setClass(LoginActivity.this, SyncActivity.class);
    Bundle bundle = new Bundle();
    bundle.putString("initPwd", initPwd);
    bundle.putString("companyCode", companyCode);
    //更新数据
    bundle.putInt("fromLogin", from);
    bundle.putBoolean("fromDataUpDateActivity", false);
    bundle.putBoolean("isUpdate", true);
    bundle.putBoolean("isLogin", true);
    intent.putExtra("bundle", bundle);
    startActivityForResult(intent, UPDATE_DATA_REQUEST);
  }

  /**
   * 跳转至主界面
   */
  private void turnToMain() {
    //跳转
    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
    startActivity(intent);
    finish();
  }

  /**
   * 屏蔽返回键
   */
  @Override public boolean onKeyDown(int keyCode, KeyEvent event) {
    return keyCode == KeyEvent.KEYCODE_BACK || super.onKeyDown(keyCode, event);
  }

  /**
   * 键盘点击
   */
  @OnClick({
      R.id.btn_0, R.id.btn_1, R.id.btn_2, R.id.btn_3, R.id.btn_4, R.id.btn_5, R.id.btn_6,
      R.id.btn_7, R.id.btn_8, R.id.btn_9, R.id.btn_a, R.id.btn_b, R.id.btn_c, R.id.btn_d,
      R.id.btn_e, R.id.btn_f, R.id.btn_g, R.id.btn_h, R.id.btn_i, R.id.btn_j, R.id.btn_k,
      R.id.btn_l, R.id.btn_m, R.id.btn_n, R.id.btn_o, R.id.btn_p, R.id.btn_q, R.id.btn_r,
      R.id.btn_s, R.id.btn_t, R.id.btn_u, R.id.btn_v, R.id.btn_w, R.id.btn_x, R.id.btn_y,
      R.id.btn_z, R.id.btn_slash, R.id.btn_point, R.id.btn_special
  }) public void keyboardClick(Button button) {
    kbdInput(button);
  }

  /**
   * 大小写切换
   */
  @OnClick(R.id.btn_capslock) public void switchCaspsLock(Button button) {
    if (mCapsLock == UPPER) {//当前大写
      mCapsLock = LOWER;//改为小写
      ((Button) findViewById(R.id.btn_a)).setText("a");
      ((Button) findViewById(R.id.btn_b)).setText("b");
      ((Button) findViewById(R.id.btn_c)).setText("c");
      ((Button) findViewById(R.id.btn_d)).setText("d");
      ((Button) findViewById(R.id.btn_e)).setText("e");
      ((Button) findViewById(R.id.btn_f)).setText("f");
      ((Button) findViewById(R.id.btn_g)).setText("g");
      ((Button) findViewById(R.id.btn_h)).setText("h");
      ((Button) findViewById(R.id.btn_i)).setText("i");
      ((Button) findViewById(R.id.btn_j)).setText("j");
      ((Button) findViewById(R.id.btn_k)).setText("k");
      ((Button) findViewById(R.id.btn_l)).setText("l");
      ((Button) findViewById(R.id.btn_m)).setText("m");
      ((Button) findViewById(R.id.btn_n)).setText("n");
      ((Button) findViewById(R.id.btn_o)).setText("o");
      ((Button) findViewById(R.id.btn_p)).setText("p");
      ((Button) findViewById(R.id.btn_q)).setText("q");
      ((Button) findViewById(R.id.btn_r)).setText("r");
      ((Button) findViewById(R.id.btn_s)).setText("s");
      ((Button) findViewById(R.id.btn_t)).setText("t");
      ((Button) findViewById(R.id.btn_u)).setText("u");
      ((Button) findViewById(R.id.btn_v)).setText("v");
      ((Button) findViewById(R.id.btn_w)).setText("w");
      ((Button) findViewById(R.id.btn_x)).setText("x");
      ((Button) findViewById(R.id.btn_y)).setText("y");
      ((Button) findViewById(R.id.btn_z)).setText("z");
    } else {//当前小写
      mCapsLock = UPPER;//改为大写
      ((Button) findViewById(R.id.btn_a)).setText("A");
      ((Button) findViewById(R.id.btn_b)).setText("B");
      ((Button) findViewById(R.id.btn_c)).setText("C");
      ((Button) findViewById(R.id.btn_d)).setText("D");
      ((Button) findViewById(R.id.btn_e)).setText("E");
      ((Button) findViewById(R.id.btn_f)).setText("F");
      ((Button) findViewById(R.id.btn_g)).setText("G");
      ((Button) findViewById(R.id.btn_h)).setText("H");
      ((Button) findViewById(R.id.btn_i)).setText("I");
      ((Button) findViewById(R.id.btn_j)).setText("J");
      ((Button) findViewById(R.id.btn_k)).setText("K");
      ((Button) findViewById(R.id.btn_l)).setText("L");
      ((Button) findViewById(R.id.btn_m)).setText("M");
      ((Button) findViewById(R.id.btn_n)).setText("N");
      ((Button) findViewById(R.id.btn_o)).setText("O");
      ((Button) findViewById(R.id.btn_p)).setText("P");
      ((Button) findViewById(R.id.btn_q)).setText("Q");
      ((Button) findViewById(R.id.btn_r)).setText("R");
      ((Button) findViewById(R.id.btn_s)).setText("S");
      ((Button) findViewById(R.id.btn_t)).setText("T");
      ((Button) findViewById(R.id.btn_u)).setText("U");
      ((Button) findViewById(R.id.btn_v)).setText("V");
      ((Button) findViewById(R.id.btn_w)).setText("W");
      ((Button) findViewById(R.id.btn_x)).setText("X");
      ((Button) findViewById(R.id.btn_y)).setText("Y");
      ((Button) findViewById(R.id.btn_z)).setText("Z");
    }
  }

  /**
   * 删除
   */
  @OnClick(R.id.btn_del) public void buttonDel(Button button) {
    //用户 名称
    if (mInputFocusMark == MARK_USER_NAME) {
      if (mSbUserName.length() - 1 >= 0) {
        mSbUserName.delete(mSbUserName.length() - 1, mSbUserName.length());
        mTvUserName.setText(mSbUserName.toString().trim());
      }
    }
    //用户 密码
    if (mInputFocusMark == MARK_USER_PWD) {
      if (mSbUserPwd.length() - 1 >= 0) {
        mSbUserPwd.delete(mSbUserPwd.length() - 1, mSbUserPwd.length());
        mTvUserPwd.setText(mSbUserPwd.toString().trim());
      }
    }
    //商家 编码
    if (mInputFocusMark == MARK_STORE_NO) {
      if (mSbStoreNo.length() - 1 >= 0) {
        mSbStoreNo.delete(mSbStoreNo.length() - 1, mSbStoreNo.length());
        mTvStoreName.setText(mSbStoreNo.toString().trim());
      }
    }
    //商家 密码
    if (mInputFocusMark == MARK_STORE_PWD) {
      if (mSbStorePwd.length() - 1 >= 0) {
        mSbStorePwd.delete(mSbStorePwd.length() - 1, mSbStorePwd.length());
        mTvStorePwd.setText(mSbStorePwd.toString().trim());
      }
    }
  }

  /**
   * 清空
   */
  @OnClick(R.id.btn_clear) public void clearInput(Button button) {
    //用户 名称
    if (mInputFocusMark == MARK_USER_NAME) {
      mSbUserName = new StringBuilder();
      mTvUserName.setText(mSbUserName.toString().trim());
    }
    //用户 密码
    if (mInputFocusMark == MARK_USER_PWD) {
      mSbUserPwd = new StringBuilder();
      mTvUserPwd.setText(mSbUserPwd.toString().trim());
    }
    //商家 编码
    if (mInputFocusMark == MARK_STORE_NO) {
      mSbStoreNo = new StringBuilder();
      mTvStoreName.setText(mSbStoreNo.toString().trim());
    }
    //商家 密码
    if (mInputFocusMark == MARK_STORE_PWD) {
      mSbStorePwd = new StringBuilder();
      mTvStorePwd.setText(mSbStorePwd.toString().trim());
    }
  }

  /**
   * 键盘输入
   */
  public void kbdInput(Button button) {
    //用户 名称
    if (mInputFocusMark == MARK_USER_NAME) {
      mSbUserName.append(button.getText().toString().trim());
      mTvUserName.setText(mSbUserName.toString().trim());
    }
    //用户 密码
    if (mInputFocusMark == MARK_USER_PWD) {
      if (!mSbUserPwd.toString().trim().equals("") && mSbUserPwd.length() > 10) {
        ToastUtils.showShort(App.getContext(), "输入过长");
        return;
      }
      mSbUserPwd.append(button.getText().toString().trim());
      mTvUserPwd.setText(mSbUserPwd.toString().trim());
    }
    //商家 编码
    if (mInputFocusMark == MARK_STORE_NO) {
      mSbStoreNo.append(button.getText().toString().trim());
      mTvStoreName.setText(mSbStoreNo.toString().trim());
    }
    //商家 密码
    if (mInputFocusMark == MARK_STORE_PWD) {
      if (!mSbStorePwd.toString().trim().equals("") && mSbStorePwd.length() > 10) {
        ToastUtils.showShort(App.getContext(), "输入过长");
        return;
      }
      mSbStorePwd.append(button.getText().toString().trim());
      mTvStorePwd.setText(mSbStorePwd.toString().trim());
    }
  }

  @Override protected void onDestroy() {
    super.onDestroy();
    ButterKnife.unbind(this);
  }
}