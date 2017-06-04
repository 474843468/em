package com.psi.easymanager.pay.vip;

import com.orhanobut.logger.Logger;
import com.psi.easymanager.common.App;
import com.psi.easymanager.config.URLConstants;
import com.psi.easymanager.dao.dbUtil.DaoServiceUtil;
import com.psi.easymanager.event.VipLoginEvent;
import com.psi.easymanager.module.Office;
import com.psi.easymanager.module.PxPaymentMode;
import com.psi.easymanager.module.PxVipCardInfo;
import com.psi.easymanager.module.PxVipInfo;
import com.psi.easymanager.module.User;
import com.psi.easymanager.network.RestClient;
import com.psi.easymanager.network.req.HttpIdCardReq;
import com.psi.easymanager.network.req.HttpVipLoginReq;
import com.psi.easymanager.network.resp.HttpIdCardResp;
import com.psi.easymanager.network.resp.HttpVipPayLoginResp;
import com.psi.easymanager.utils.ToastUtils;
import org.greenrobot.eventbus.EventBus;

/**
 * Created by Administrator on 2016-10-22.
 */
public class VipLogin {
  public static void vipLogin(String mobile, final PxPaymentMode paymentMode) {
    //初始化 信息
    final App app = (App) App.getContext();
    User user = app.getUser();
    Office office = DaoServiceUtil.getOfficeService().queryBuilder().unique();
    if (user == null) {
      EventBus.getDefault().post(new VipLoginEvent(false));
      return;
    }

    String companyCode = user.getCompanyCode();
    String cid = office.getObjectId();
    String userId = user.getObjectId();

    HttpVipLoginReq req = new HttpVipLoginReq();
    req.setCompanyCode(companyCode);
    req.setUserId(userId);
    req.setCompanyId(cid);
    req.setMobile(mobile);
    req.setCardNo(mobile);
    new RestClient(1,1000,5000,3000) {
      @Override protected void success(String responseString) {
        Logger.json(responseString);
        HttpVipPayLoginResp resp = getGson().fromJson(responseString, HttpVipPayLoginResp.class);
        if (resp.getStatusCode() == 1000) {
          PxVipInfo vipInfo = resp.getVipInfo();
          if (vipInfo == null) {
            EventBus.getDefault().post(new VipLoginEvent(false));
            ToastUtils.showShort(app, resp.getMsg()+"");
          } else { //登陆成功
            EventBus.getDefault().post(new VipLoginEvent(true,vipInfo,null,paymentMode));
          }
        } else {
          ToastUtils.showShort(app, resp.getMsg()+"");
          EventBus.getDefault().post(new VipLoginEvent(false));
        }
      }

      @Override protected void failure(String responseString, Throwable throwable) {
        EventBus.getDefault().post(new VipLoginEvent(false));
        ToastUtils.showShort(app,"登录失败!");
        Logger.i("vip login request failure" + responseString);
        //登陆 不用处理服务器响应超时
      }

      @Override protected void start() {

      }

      @Override protected void finish() {

      }
    }.postOther(app, URLConstants.VIP_LOGIN, req);
  }

  /**
   *  会员卡登录
   */
  public static void vipCardLogin(String idcardNum, final PxPaymentMode paymentMode) {
    //初始化 信息
    final App app = (App) App.getContext();
    User user = app.getUser();
    if (user == null) {
      EventBus.getDefault().post(new VipLoginEvent(false));
      return;
    }

    String companyCode = user.getCompanyCode();
    Office office = DaoServiceUtil.getOfficeService().queryBuilder().unique();
    String cid = office.getObjectId();
    String userId = user.getObjectId();

    HttpIdCardReq req = new HttpIdCardReq();
    req.setCompanyCode(companyCode);
    req.setUserId(userId);
    req.setCid(cid);
    req.setIdCardNum(idcardNum);
    Logger.v(req.toString());
    new RestClient(1,1000,5000,3000) {
      @Override protected void success(String responseString) {
        Logger.json(responseString);
        HttpIdCardResp resp = getGson().fromJson(responseString, HttpIdCardResp.class);
        if (resp.getStatusCode() == 1000) {
          PxVipCardInfo cardInfo = resp.getCardInfo();
          if (cardInfo == null) {
            EventBus.getDefault().post(new VipLoginEvent(false));
            ToastUtils.showShort(app, resp.getMsg()+"");
          } else { //登陆成功
            EventBus.getDefault().post(new VipLoginEvent(true,null,cardInfo,paymentMode));
          }
        } else {
          Logger.v("resp.getStatusCode()!= 1000");
          ToastUtils.showShort(app, resp.getMsg()+"");
          EventBus.getDefault().post(new VipLoginEvent(false));
        }
      }

      @Override protected void failure(String responseString, Throwable throwable) {
        EventBus.getDefault().post(new VipLoginEvent(false));
        ToastUtils.showShort(app,"登录失败!");
        Logger.v("vip login request failure" + responseString);
        //登陆 不用处理服务器响应超时
      }

      @Override protected void start() {

      }

      @Override protected void finish() {

      }
    }.postOther(app, URLConstants.VIP_CARD_LOGIN, req);
  }
}
