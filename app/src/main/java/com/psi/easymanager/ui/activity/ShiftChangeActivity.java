package com.psi.easymanager.ui.activity;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.afollestad.materialdialogs.MaterialDialog;
import com.psi.easymanager.R;
import com.psi.easymanager.common.App;
import com.psi.easymanager.dao.PxOrderInfoDao;
import com.psi.easymanager.dao.dbUtil.DaoServiceUtil;
import com.psi.easymanager.module.PxOrderInfo;
import com.psi.easymanager.module.User;
import com.psi.easymanager.utils.ToastUtils;
import com.psi.easymanager.widget.SwipeBackLayout;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by dorado on 2016/7/29.
 */
public class ShiftChangeActivity extends BaseActivity
    implements TimePickerDialog.OnTimeSetListener, DatePickerDialog.OnDateSetListener {
  @Bind(R.id.swipe_back) SwipeBackLayout mSwipeBack;
  @Bind(R.id.tv_date) TextView mTvDate;
  @Bind(R.id.tv_time) TextView mTvTime;
  @Bind(R.id.tv_cashier) TextView mTvCashier;
  @Bind(R.id.tv_area) TextView mTvArea;

  public static final int AREA_ALL = 0;//全部
  public static final int AREA_TABLE = 1;//桌位单
  public static final int AREA_RETAIL = 2;//零售单
  public static final int AREA_HALL = 3;//大厅
  public static final int AREA_PARLOR = 4;//包厢

  public static final int CASHIER_CURRENT = 0;//当前收银员
  public static final int CASHIER_ALL = 1;//所有收银员

  private int mYear;//年
  private int mMonthOfYear;//月
  private int mDayOfMonth;//日
  private int mHourOfDay = -1;//时
  private int mMinute = -1;//分
  private int mArea = AREA_ALL;
  private int mCashier = CASHIER_CURRENT;

  @Override protected int provideContentViewId() {
    return R.layout.activity_shift_change;
  }

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    ButterKnife.bind(this);
    mSwipeBack.setCallBack(new SwipeBackLayout.CallBack() {
      @Override public void onFinish() {
        finish();
      }
    });

    App app = (App) App.getContext();
    if (app != null) {
      User user = app.getUser();
      if (user != null) {
        mTvCashier.setText(user.getName());
      }
    }
    //当前日期和时间
    Calendar calendar = Calendar.getInstance();
    mYear = calendar.get(Calendar.YEAR);
    mMonthOfYear = calendar.get(Calendar.MONTH);
    mDayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
    mTvDate.setText(mYear + "-" + (mMonthOfYear + 1) + "-" + mDayOfMonth);
    mHourOfDay = calendar.get(Calendar.HOUR_OF_DAY);
    mMinute = calendar.get(Calendar.MINUTE);
    mTvTime.setText(mHourOfDay + ":" + mMinute);
    //取消冻结
    cancelFreeze();
  }

  /**
   * 取消冻结
   */
  private void cancelFreeze() {
    SQLiteDatabase db = DaoServiceUtil.getUserDao().getDatabase();
    db.beginTransaction();
    try {
      List<PxOrderInfo> freezeOrderList = DaoServiceUtil.getOrderInfoService()
          .queryBuilder()
          .where(PxOrderInfoDao.Properties.ShiftChangeType.eq(PxOrderInfo.SHIFT_CHANGE_FREEZE))
          .where(PxOrderInfoDao.Properties.IsReserveOrder.eq(PxOrderInfo.IS_REVERSE_ORDER_FALSE))
          .list();
      for (PxOrderInfo orderInfo : freezeOrderList) {
        orderInfo.setShiftChangeType(PxOrderInfo.SHIFT_CHANGE_UNHAND);
      }
      //List<PxRechargeRecord> freezeRechargeList = DaoServiceUtil.getRechargeRecordService()
      //    .queryBuilder()
      //    .where(PxRechargeRecordDao.Properties.ShiftChangeType.eq(
      //        PxRechargeRecord.SHIFT_CHANGE_FREEZE))
      //    .list();
      //for (PxRechargeRecord rechargeRecord : freezeRechargeList) {
      //    rechargeRecord.setShiftChangeType(PxRechargeRecord.SHIFT_CHANGE_UNHAND);
      //}
      DaoServiceUtil.getOrderInfoService().saveOrUpdate(freezeOrderList);
      //DaoServiceUtil.getRechargeRecordService().saveOrUpdate(freezeRechargeList);
      db.setTransactionSuccessful();
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      db.endTransaction();
    }
  }

  /**
   * 日期选择
   */
  @OnClick(R.id.rl_begin_date) public void selectDate() {
    Calendar now = Calendar.getInstance();
    DatePickerDialog datePickerDialog = DatePickerDialog.newInstance(ShiftChangeActivity.this, now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH));
    datePickerDialog.show(getFragmentManager(), "DatePickerDialog");
  }

  /**
   * 时间选择
   */
  @OnClick(R.id.rl_begin_time) public void selectTime() {
    Calendar now = Calendar.getInstance();
    TimePickerDialog timePickerDialog =
        TimePickerDialog.newInstance(ShiftChangeActivity.this, now.get(Calendar.HOUR_OF_DAY),
            now.get(Calendar.MINUTE), now.get(Calendar.SECOND), true);
    timePickerDialog.show(getFragmentManager(), "TimePickerDialog");
  }

  /**
   * 区域选择
   */
  @OnClick(R.id.rl_area) public void selectArea() {
    new MaterialDialog.Builder(this).title("选择区域")
        .items(R.array.shift_change_area_selections)
        .itemsCallbackSingleChoice(0, new MaterialDialog.ListCallbackSingleChoice() {
          @Override public boolean onSelection(MaterialDialog dialog, View view, int which,
              CharSequence text) {
            mArea = which;
            if (which == AREA_ALL) {
              mTvArea.setText("全部");
            } else if (which == AREA_TABLE) {
              mTvArea.setText("桌位单");
            } else if (which == AREA_RETAIL) {
              mTvArea.setText("零售单");
            }
            return true;
          }
        })
        .positiveText("确定")
        .show();
  }

  /**
   * 时间回调
   */
  @Override public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute, int second) {
    mHourOfDay = hourOfDay;
    mMinute = minute;
    mTvTime.setText(hourOfDay  + (minute < 10 ? ":0":":" ) + minute);
  }

  /**
   * 时期回调
   */
  @Override public void onDateSet(DatePickerDialog view, int year, int monthOfYear,
      int dayOfMonth) {
    mYear = year;
    mMonthOfYear = monthOfYear;
    mDayOfMonth = dayOfMonth;
    mTvDate.setText(year + "-" + (monthOfYear + 1) + "-" + dayOfMonth);
  }

  /**
   * 确定
   */
  @OnClick(R.id.btn_confirm) public void confirm() {
    if (mYear == 0 && mMonthOfYear == 0 && mDayOfMonth == 0) {
      ToastUtils.showShort(this, "请选择日期");
      return;
    }

    if (mHourOfDay == -1 && mMinute == -1) {
      ToastUtils.showShort(this, "请选择时间");
      return;
    }

    Intent intent = new Intent(this, ShiftChangeFunctionsActivity.class);
    intent.putExtra(ShiftChangeFunctionsActivity.KEY_AREA, mArea);
    intent.putExtra(ShiftChangeFunctionsActivity.KEY_USER, mCashier);
    Calendar instance = Calendar.getInstance();
    instance.set(Calendar.YEAR, mYear);
    instance.set(Calendar.MONTH, mMonthOfYear);
    instance.set(Calendar.DAY_OF_MONTH, mDayOfMonth);
    instance.set(Calendar.HOUR_OF_DAY, mHourOfDay);
    instance.set(Calendar.MINUTE, mMinute);
    Date date = instance.getTime();
    intent.putExtra(ShiftChangeFunctionsActivity.KEY_DATE, date);
    startActivity(intent);
  }

  /**
   * 日结
   */
  @OnClick(R.id.btn_day_report) public void dayReport() {
    Intent intent = new Intent(this, DayReportActivity.class);
    startActivity(intent);
  }

  @Override protected void onDestroy() {
    super.onDestroy();
    ButterKnife.unbind(this);
  }
}
