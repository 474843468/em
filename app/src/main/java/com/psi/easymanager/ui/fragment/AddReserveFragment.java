package com.psi.easymanager.ui.fragment;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.InputType;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.orhanobut.logger.Logger;
import com.psi.easymanager.R;
import com.psi.easymanager.common.App;
import com.psi.easymanager.config.Constants;
import com.psi.easymanager.dao.PxTableInfoDao;
import com.psi.easymanager.dao.dbUtil.DaoServiceUtil;
import com.psi.easymanager.event.CancelReserveEvent;
import com.psi.easymanager.event.ReserveDetailEvent;
import com.psi.easymanager.module.PxOrderInfo;
import com.psi.easymanager.module.PxTableInfo;
import com.psi.easymanager.module.TableOrderRel;
import com.psi.easymanager.ui.activity.ReserveManagerActivity;
import com.psi.easymanager.utils.RegExpUtils;
import com.psi.easymanager.utils.ToastUtils;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.greenrobot.eventbus.EventBus;

/**
 * User: ylw
 * Date: 2016-09-12
 * Time: 14:35
 * 新加预定
 */
public class AddReserveFragment extends BaseFragment
    implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {
  @Bind(R.id.tv_link_man) TextView mTvLinkMan;//联系人
  @Bind(R.id.tv_contact_phone) TextView mTvContactPhone;//联系电话
  @Bind(R.id.tv_dining_date) TextView mTvDiningDate;//日期
  @Bind(R.id.tv_dining_time) TextView mTvDiningTime;//时间
  @Bind(R.id.tv_people_num) TextView mTvPeopleNum;//人数
  //@Bind(R.id.tv_table_require) TextView mTvTableRequire;//桌位类型要求
  @Bind(R.id.tv_arrange_table) TextView mTvArrangeTable;//安排桌位
  //@Bind(R.id.tv_customer_message) TextView mTvCustomerMsg;//顾客留言

  private ReserveManagerActivity mAct;
  private FragmentManager mFm;

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    mAct = (ReserveManagerActivity) getActivity();
    mFm = mAct.getSupportFragmentManager();
  }

  @Nullable @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_add_reserve, null);
    ButterKnife.bind(this, view);
    return view;
  }

  public static AddReserveFragment newInstance(String param) {
    Bundle bundle = new Bundle();
    bundle.putString("param", param);
    AddReserveFragment fragment = new AddReserveFragment();
    fragment.setArguments(bundle);
    return fragment;
  }

  /**
   * 联系人
   */
  //@formatter:off
  @OnClick(R.id.rl_link_man) public void rlLinkMan(RelativeLayout rl) {
    new MaterialDialog.Builder(mAct).title("联系人")
        .content("请输入联系人名称")
        .inputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PERSON_NAME | InputType.TYPE_TEXT_FLAG_CAP_WORDS)
        .positiveText("确认")
        .negativeText("取消")
        .negativeColor(mAct.getResources().getColor(R.color.primary_text))
        .alwaysCallInputCallback()
        .input("请输入联系人名称", "", false, new MaterialDialog.InputCallback() {
          @Override public void onInput(MaterialDialog dialog, CharSequence charSequence) {
            dialog.getActionButton(DialogAction.POSITIVE).setEnabled(RegExpUtils.matchName(charSequence.toString().trim()));
          }
        })
        .onPositive(new MaterialDialog.SingleButtonCallback() {
          @Override public void onClick(MaterialDialog dialog, DialogAction dialogAction) {
            mTvLinkMan.setText(dialog.getInputEditText().getText().toString().trim());
          }
        })
        .show();
  }

  /**
   * 联系电话
   */
  @OnClick(R.id.rl_contact_phone) public void rlContactPhone(RelativeLayout rl) {
    new MaterialDialog.Builder(mAct).title("联系人电话")
        .content("请输入联系人电话")
        .inputType(InputType.TYPE_CLASS_NUMBER)
        .positiveText("确认")
        .negativeText("取消")
        .inputMaxLength(11)
        .negativeColor(mAct.getResources().getColor(R.color.primary_text))
        .alwaysCallInputCallback()
        .input("请输入联系人电话", "", false, new MaterialDialog.InputCallback() {
          @Override public void onInput(MaterialDialog dialog, CharSequence charSequence) {
            dialog.getActionButton(DialogAction.POSITIVE)
                .setEnabled(RegExpUtils.match11Number(charSequence.toString().trim()));
          }
        })
        .onPositive(new MaterialDialog.SingleButtonCallback() {
          @Override public void onClick(MaterialDialog dialog, DialogAction dialogAction) {
           mTvContactPhone.setText(dialog.getInputEditText().getText().toString().trim());
          }
        })
        .show();
  }

  /**
   * 用餐日期
   */
  @OnClick(R.id.rl_dining_date) public void rlDiningDate(RelativeLayout rl) {
    Calendar now = Calendar.getInstance();
    DatePickerDialog datePickerDialog = DatePickerDialog.newInstance(this, now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH));
    //datePickerDialog.setMinDate(now);
    datePickerDialog.show(mAct.getFragmentManager(), "DatePickerDialog");
  }

  @Override
  public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
    String time = year + "-" + (monthOfYear + 1) + "-" + dayOfMonth;
    mTvDiningDate.setText(time);
  }
  /**
   * 用餐时间
   */
  @OnClick(R.id.rl_dining_time) public void rlDiningTime(RelativeLayout rl) {
    Calendar now = Calendar.getInstance();
    TimePickerDialog timePickerDialog = TimePickerDialog.newInstance(this, now.get(Calendar.HOUR_OF_DAY), now.get(Calendar.MINUTE), now.get(Calendar.SECOND), true);
    timePickerDialog.show(mAct.getFragmentManager(), "TimePickerDialog");
  }

  @Override public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute, int second) {
      mTvDiningTime.setText(hourOfDay +(minute < 10 ? ":0":":" ) +minute);
  }
  /**
   * 人数
   */
  @OnClick(R.id.rl_people_num) public void rlPeopleNum(RelativeLayout rl) {
    new MaterialDialog.Builder(mAct).title("顾客人数")
        .content("请输入顾客人数")
        .inputType(InputType.TYPE_CLASS_NUMBER)
        .positiveText("确认")
        .negativeText("取消")
        .negativeColor(mAct.getResources().getColor(R.color.primary_text))
        .alwaysCallInputCallback()
        .input("请输入顾客人数", "", false, new MaterialDialog.InputCallback() {
          @Override public void onInput(MaterialDialog dialog, CharSequence charSequence) {
            dialog.getActionButton(DialogAction.POSITIVE)
                .setEnabled(RegExpUtils.matchPeoPleNum(charSequence.toString().trim()));
          }
        })
        .onPositive(new MaterialDialog.SingleButtonCallback() {
          @Override public void onClick(MaterialDialog dialog, DialogAction dialogAction) {
            mTvPeopleNum.setText(dialog.getInputEditText().getText().toString().trim());
          }
        })
        .show();
  }

  /**
   * 安排桌位
   */
  @OnClick(R.id.rl_arrange_table) public void rlArrangeTable(RelativeLayout rl) {
    //显示所有桌台信息
    List<PxTableInfo> tableInfoList = DaoServiceUtil.getTableInfoService()
        .queryBuilder()
        .where(PxTableInfoDao.Properties.DelFlag.eq("0"))
        .list();
    Map<String,PxTableInfo> tableInfoMap = new HashMap<>();
    final List<String> tableNameList = new ArrayList<>();
    for (PxTableInfo tableInfo : tableInfoList) {
      tableInfoMap.put(tableInfo.getName(),tableInfo);
      tableNameList.add(tableInfo.getName());
    }
    new MaterialDialog.Builder(mAct).title("选择桌位")
        .positiveText("确定")
        .negativeText("取消")
        .items(tableNameList)
        .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
          @Override public boolean onSelection(MaterialDialog dialog, View itemView, int which,
              CharSequence text) {
            return true;
          }
        })
        .onPositive(new MaterialDialog.SingleButtonCallback() {
          @Override
          public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
            saveTableInfo(dialog,tableNameList);
          }
        })
        .show();

  }
  /**
   * 保存桌台信息
   */
  private void saveTableInfo(MaterialDialog dialog, List<String> tableNameList) {
    int selectedIndex = dialog.getSelectedIndex();
    if (selectedIndex == -1){
      mTvArrangeTable.setText("");
    }else{
      String tableName = tableNameList.get(selectedIndex);
      mTvArrangeTable.setText(tableName);
    }
  }

  ///**
  // * 顾客留言
  // */
  //@OnClick({R.id.rl_customer_message,R.id.tv_customer_message}) public void rlCustomerMessage() {
  //  String preMsg = mTvCustomerMsg.getText().toString().trim();
  //  new MaterialDialog.Builder(mAct).title("留言")
  //      .content("请输入顾客留言")
  //      .positiveText("确认")
  //      .negativeText("取消")
  //      .negativeColor(mAct.getResources().getColor(R.color.primary_text))
  //      .alwaysCallInputCallback()
  //      .input("请输入顾客留言", preMsg, true, new MaterialDialog.InputCallback() {
  //        @Override public void onInput(MaterialDialog dialog, CharSequence charSequence) {
  //          //dialog.getActionButton(DialogAction.POSITIVE).setEnabled(RegExpUtils.matchPeoPleNum(charSequence.toString().trim()));
  //        }
  //      })
  //      .onPositive(new MaterialDialog.SingleButtonCallback() {
  //        @Override public void onClick(MaterialDialog dialog, DialogAction dialogAction) {
  //          String input = dialog.getInputEditText().getText().toString().trim();
  //          if (TextUtils.isEmpty(input)) {
  //            mTvCustomerMsg.setText("");
  //          }else {
  //            mTvCustomerMsg.setText("  " + input);
  //          }
  //        }
  //      })
  //      .show();
  //}


  @Override public void onHiddenChanged(boolean hidden) {
    super.onHiddenChanged(hidden);
    if (!hidden && mAct != null) {
      resetBasicInfo();
    }
  }

  private void resetBasicInfo() {
    mTvLinkMan.setText("");
    mTvContactPhone.setText("");
    mTvDiningDate.setText("");
    mTvDiningTime.setText("");
    mTvPeopleNum.setText("");
    mTvArrangeTable.setText("");
  }
  /**
   * 取消
   */
  @OnClick(R.id.ibtn_cancel) public void cancel(ImageButton iBtn) {
    FragmentTransaction transaction = mFm.beginTransaction();
    transaction.hide(this);
    transaction.commit();
    EventBus.getDefault().post(new CancelReserveEvent());
    //mAct.receiveCancelEvent(null);
  }
  /**
   * 确定
   */
  @OnClick(R.id.ibtn_confirm) public void confirm(ImageButton iBtn) {
    String linkMan = mTvLinkMan.getText().toString();
    String contactPhone = mTvContactPhone.getText().toString();
    String diningDate = mTvDiningDate.getText().toString();
    String diningTime = mTvDiningTime.getText().toString();
    String peopleNum = mTvPeopleNum.getText().toString();
    String tableName = mTvArrangeTable.getText().toString();
    if (TextUtils.isEmpty(linkMan)) {
      ToastUtils.showShort(App.getContext(), "请输入联系人");
      return;
    }
    if (TextUtils.isEmpty(contactPhone)) {
      ToastUtils.showShort(App.getContext(), "请输入联系人电话");
      return;
    }
    if (TextUtils.isEmpty(diningDate)) {
      ToastUtils.showShort(App.getContext(), "请输入用餐日期");
      return;
    }
    if (TextUtils.isEmpty(diningTime)) {
      ToastUtils.showShort(App.getContext(), "请输入用餐时间");
      return;
    }
    if (TextUtils.isEmpty(peopleNum)){
      ToastUtils.showShort(App.getContext(),"请输入人数");
      return;
    }
    PxTableInfo tableInfo = null;
    if (tableName != null) {
      tableInfo = DaoServiceUtil.getTableInfoService()
          .queryBuilder()
          .where(PxTableInfoDao.Properties.DelFlag.eq("0"))
          .where(PxTableInfoDao.Properties.Name.eq(tableName))
          .unique();
    }
    //转化时间 存数据库
    String dateString = diningDate + " "+(diningTime + ":00");
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    Date reserveDate = null;
    try {
      reserveDate = sdf.parse(dateString);
    } catch (ParseException e) {
      e.printStackTrace();
    }
    if (reserveDate == null || ((new Date().getTime())) > reserveDate.getTime()) {
      ToastUtils.showShort(App.getContext(), "预定时间小于当前时间");
      return;
    }
    //创建一条预订记录
    SQLiteDatabase db = DaoServiceUtil.getUserDao().getDatabase();
    db.beginTransaction();
    PxOrderInfo reserveOrder = null;
    try {
        reserveOrder = new PxOrderInfo();
        reserveOrder.setIsReserveOrder(PxOrderInfo.IS_REVERSE_ORDER_TRUE);
        reserveOrder.setLinkMan(linkMan);
        reserveOrder.setContactPhone(contactPhone);
        reserveOrder.setDiningTime(reserveDate);
        reserveOrder.setActualPeopleNumber(Integer.valueOf(peopleNum));
        reserveOrder.setType(PxOrderInfo.TYPE_DEFAULT);
        //默认预定
        reserveOrder.setReserveState(PxOrderInfo.RESERVE_STATE_RESERVE);
        DaoServiceUtil.getOrderInfoService().save(reserveOrder);
        //建立桌台和订单连接
        if (tableInfo != null) {
          TableOrderRel tableOrderRel = new TableOrderRel();
          tableOrderRel.setDbOrder(reserveOrder);
          tableOrderRel.setDbTable(tableInfo);
          DaoServiceUtil.getTableOrderRelService().save(tableOrderRel);
        }
        db.setTransactionSuccessful();
    }catch (Exception e){
      e.printStackTrace();
      Logger.e(e.toString());
    }finally {
      db.endTransaction();
    }
    //左边 替换为预订单详情Fragment
    Fragment reserveDetailFragment = mFm.findFragmentByTag(Constants.RESERVE_DETAIL);
    FragmentTransaction transaction = mFm.beginTransaction();
    transaction.hide(this);
    if (reserveDetailFragment == null) {
      reserveDetailFragment = ReserveDetailFragment.newInstance("param");
      transaction.add(R.id.fl_left, reserveDetailFragment, Constants.RESERVE_DETAIL);
    } else {
      transaction.show(reserveDetailFragment);
    }
    transaction.commit();
    //发送ReserveManagerActivity 新增Event
    EventBus.getDefault().post(new ReserveDetailEvent(reserveOrder, true));
    //发送ReserveDetailFragment 展示新增的详情Event
    EventBus.getDefault().postSticky(new ReserveDetailEvent(reserveOrder));
  }

  @Override public void onDetach() {
    ButterKnife.unbind(this);
    super.onDetach();
  }
}