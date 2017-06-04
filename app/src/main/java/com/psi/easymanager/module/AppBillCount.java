package com.psi.easymanager.module;

import android.support.v4.util.Pair;
import java.util.List;

/**
 * Created by psi on 2016/6/8.
 * 用于账单汇总打印
 */
public class AppBillCount {
  private String totalReceivable = "";//应收
  private String totalChange = "";//找零
  private String totalTail = "";//抹零
  private String payPrivilege;//支付类优惠
  private List<Pair<String, String>> everyReceived;//各项实收

  public String getPayPrivilege() {
    return payPrivilege;
  }

  public void setPayPrivilege(String payPrivilege) {
    this.payPrivilege = payPrivilege;
  }

  public String getTotalReceivable() {
    return totalReceivable;
  }

  public void setTotalReceivable(String totalReceivable) {
    this.totalReceivable = totalReceivable;
  }

  public String getTotalChange() {
    return totalChange;
  }

  public void setTotalChange(String totalChange) {
    this.totalChange = totalChange;
  }

  public String getTotalTail() {
    return totalTail;
  }

  public void setTotalTail(String totalTail) {
    this.totalTail = totalTail;
  }

  public List<Pair<String, String>> getEveryReceived() {
    return everyReceived;
  }

  public void setEveryReceived(List<Pair<String, String>> everyReceived) {
    this.everyReceived = everyReceived;
  }
}
