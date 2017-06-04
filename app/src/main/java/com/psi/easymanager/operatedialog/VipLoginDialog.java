package com.psi.easymanager.operatedialog;

import android.app.Activity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.afollestad.materialdialogs.MaterialDialog;
import com.psi.easymanager.R;
import com.psi.easymanager.event.ShowProgressEvent;
import com.psi.easymanager.module.PxPaymentMode;
import com.psi.easymanager.pay.vip.VipLogin;
import com.psi.easymanager.ui.fragment.CheckOutFragment;
import com.psi.easymanager.utils.RegExpUtils;
import com.psi.easymanager.utils.ToastUtils;
import org.greenrobot.eventbus.EventBus;

/**
 * Created by lj on 217-1-6.
 */
public class VipLoginDialog implements View.OnClickListener {
  private EditText mEtVipInfo;
  private boolean isSelected = true;
  private StringBuilder sb = new StringBuilder();

  public void showVipLoginDialog(CheckOutFragment cof, final Activity act,
      final PxPaymentMode mPaymentMode) {

    View view = View.inflate(act, R.layout.layout_vip_login_dialog, null);
    final MaterialDialog dialog = new MaterialDialog.Builder(act).customView(view, true).build();
    // View view = dialog.getCustomView();
    mEtVipInfo = (EditText) view.findViewById(R.id.et_phone_vip_login);
    //登录
    Button mBtnTemp = (Button) view.findViewById(R.id.btn_temp);
    TextView mBtnVipLogin = (TextView) view.findViewById(R.id.btn_vip_login_in);

    mBtnTemp.setOnClickListener(this);
    //刷实体卡
    final Button mBtnVipcardLogin = (Button) view.findViewById(R.id.btn_membership_card_vip_login);
    // final LinearLayout mLlKeyboard = (LinearLayout) view.findViewById(R.id.ll_keyboard);
    final TextView tvTitle = (TextView) view.findViewById(R.id.tv_vip_login_title);
    //关闭
    //ImageButton mBtnDismissDialog = (ImageButton) mDialog.findViewById(R.id.btn_dismiss);
    //扫码
    // ImageButton mBtnwxLogin = (ImageButton) mDialog.findViewById(R.id.ib_wx_vip_login);
    TextView tv1 = (TextView) view.findViewById(R.id.btn_vip_1);
    TextView tv2 = (TextView) view.findViewById(R.id.btn_vip_2);
    TextView tv3 = (TextView) view.findViewById(R.id.btn_vip_3);
    TextView tv4 = (TextView) view.findViewById(R.id.btn_vip_4);
    TextView tv5 = (TextView) view.findViewById(R.id.btn_vip_5);
    TextView tv6 = (TextView) view.findViewById(R.id.btn_vip_6);
    TextView tv7 = (TextView) view.findViewById(R.id.btn_vip_7);
    TextView tv8 = (TextView) view.findViewById(R.id.btn_vip_8);
    TextView tv9 = (TextView) view.findViewById(R.id.btn_vip_9);
    TextView tv = (TextView) view.findViewById(R.id.btn_vip_0);
    TextView tvDel = (TextView) view.findViewById(R.id.btn_vip_del);
    TextView tvEmpty = (TextView) view.findViewById(R.id.btn_vip_empty);
    tv1.setOnClickListener(this);
    tv2.setOnClickListener(this);
    tv3.setOnClickListener(this);
    tv4.setOnClickListener(this);
    tv5.setOnClickListener(this);
    tv6.setOnClickListener(this);
    tv7.setOnClickListener(this);
    tv8.setOnClickListener(this);
    tv9.setOnClickListener(this);
    tv.setOnClickListener(this);
    tvDel.setOnClickListener(this);
    tvEmpty.setOnClickListener(this);
    //登录
    mBtnVipLogin.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        if (isSelected) {
          String vipCardNumber = mEtVipInfo.getText().toString().trim();
          if (TextUtils.isEmpty(vipCardNumber)) {
            ToastUtils.showShort(null, "请刷实体卡");
          } else {
            dialog.dismiss();
            //开启蒙层
            EventBus.getDefault().post(new ShowProgressEvent());
            VipLogin.vipCardLogin(vipCardNumber, mPaymentMode);
          }
        } else {
          String vipPhone = mEtVipInfo.getText().toString().trim();
          if (!RegExpUtils.match11Number(vipPhone)) {
            ToastUtils.showShort(null, "您输入的电话号码错误");
          } else {
            dialog.dismiss();
            //开启蒙层
            EventBus.getDefault().post(new ShowProgressEvent());
            VipLogin.vipLogin(vipPhone, mPaymentMode);
          }
        }
      }
    });
    //切换登录方式
    mBtnVipcardLogin.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        mBtnVipcardLogin.setSelected(!isSelected);//false
        if (mBtnVipcardLogin.isSelected()) {//true
          isSelected = true;
          //TransformationMethod cipherText =  PasswordTransformationMethod.getInstance();
          //etVipInfo.setTransformationMethod(cipherText);
          sb = new StringBuilder("");
          mEtVipInfo.setText(sb.toString());
          mEtVipInfo.setHint("请刷实体卡");
          tvTitle.setText("实体卡");
          mBtnVipcardLogin.setText("手机号");
        } else {
          //HideReturnsTransformationMethod text = HideReturnsTransformationMethod.getInstance();
          //etVipInfo.setTransformationMethod(text);
          isSelected = false;
          sb = new StringBuilder("");
          mEtVipInfo.setText(sb.toString());
          mEtVipInfo.setHint("请输入手机号");
          tvTitle.setText("手机号");
          mBtnVipcardLogin.setText("实体卡");
        }
      }
    });

    mEtVipInfo.setHint("请刷实体卡");
    mEtVipInfo.setInputType(0);
    dialog.show();
    cof.addDialog(dialog);
  }

  private void showNumber(String text) {
    if (isSelected == true) {
      if (!(sb.length() < 16)) {
        return;
      }
       return;
    } else if (!(sb.length() < 11)) {
      return;
    }
    sb.append(text);
    mEtVipInfo.setText(sb.toString());
  }

  @Override public void onClick(View v) {
    switch (v.getId()) {
      case R.id.btn_vip_1:
      case R.id.btn_vip_2:
      case R.id.btn_vip_3:
      case R.id.btn_vip_0:
      case R.id.btn_vip_4:
      case R.id.btn_vip_5:
      case R.id.btn_vip_6:
      case R.id.btn_vip_7:
      case R.id.btn_vip_8:
      case R.id.btn_vip_9:
        TextView view = (TextView) v;
        showNumber(view.getText().toString());
        break;
      case R.id.btn_vip_del:
        if (sb.length() >= 2) {
          sb.delete(sb.length() - 1, sb.length());
        } else if (sb.length() <= 1) {
          sb = new StringBuilder("");
        }
        mEtVipInfo.setText(sb.toString());
        break;
      case R.id.btn_vip_empty:
        sb = new StringBuilder("");
        mEtVipInfo.setText(sb.toString());
        break;
      case R.id.btn_temp:
        break;
    }
  }
}
