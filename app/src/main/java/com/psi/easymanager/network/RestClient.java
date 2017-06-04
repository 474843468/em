package com.psi.easymanager.network;

import android.app.Activity;
import android.content.Context;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.SyncHttpClient;
import com.loopj.android.http.TextHttpResponseHandler;
import com.psi.easymanager.common.App;
import com.psi.easymanager.config.Constants;
import com.psi.easymanager.dao.dbUtil.DaoServiceUtil;
import com.psi.easymanager.module.Office;
import com.psi.easymanager.module.User;
import com.psi.easymanager.network.req.HttpDataSyncReq;
import com.psi.easymanager.network.req.HttpReq;
import com.psi.easymanager.utils.DigestsUtils;
import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

/**
 * Created by zjq on 2016/1/4.
 */
public abstract class RestClient {
  //@formatter:off
  private  AsyncHttpClient client;
  private static Gson sGson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
  public static final String DEFAULT_ENCODING = "utf-8";
  public static final String SYNC_CLIENT = "1";

  public RestClient() {
    client = new AsyncHttpClient();
    client.setMaxRetriesAndTimeout(1, 5000);
    client.setResponseTimeout(10 * 1000);
    client.setConnectTimeout(5 * 1000);
  }

  public RestClient(int retries, int timeout, int responseValue, int connectValue) {
    client = new AsyncHttpClient();
    client.setMaxRetriesAndTimeout(retries, timeout);
    client.setResponseTimeout(responseValue);
    client.setConnectTimeout(connectValue);
  }
  /**
   * 同步 请求
   * @param mode 模式
   * @param retries 重试次数
   * @param timeout 超时
   * @param responseValue 响应超时
   * @param connectValue 连接超时
   */
  public RestClient(String mode, int retries, int timeout, int responseValue, int connectValue) {
    client = new SyncHttpClient();
    client.setMaxRetriesAndTimeout(retries, timeout);
    client.setResponseTimeout(responseValue);
    client.setConnectTimeout(connectValue);
  }

  /**
   * 其他 上传、检查版本、在线支付...
   */
  public void postOther(Context context, String url, HttpReq req) {
    Office office = DaoServiceUtil.getOfficeService().queryBuilder().unique();
    //cid
    if (office != null){
      req.setCid(office.getObjectId());
    }
    //userId
    if (App.getContext() != null){
     App app = (App) App.getContext();
      User user = app.getUser();
      if (user != null){
        req.setUserId(user.getObjectId());
      }
    }
    String json = RestClient.getGson().toJson(req);
    post(context,url,json);
  }

  /**
   * 初始化、同步 数据
   */
  public void postStoreLogin(Context context, String url, HttpDataSyncReq req) {
    Office office = DaoServiceUtil.getOfficeService().queryBuilder().unique();
    if (office != null){
      req.setCid(office.getObjectId());
    }
    //userId
    if (App.getContext() != null){
      App app = (App) App.getContext();
      User user = app.getUser();
      if (user != null){
        req.setUserId(user.getObjectId());
      }
    }
    String json = RestClient.getGson().toJson(req);
    post(context,url,json);
  }

  /**
   * post
   */
  private void post(final Context context,String url,String json){
    try {
      long timestamp = System.currentTimeMillis();
      String sign = DigestsUtils.md5(json + Constants.SALT + timestamp);
      StringEntity stringEntity = new StringEntity(json, DEFAULT_ENCODING);
      url = url + "?timestamp=" + timestamp + "&sign=" + sign;
      client.post(context, url ,stringEntity,"application/json;charset=utf-8" ,new TextHttpResponseHandler() {
        @Override public void onStart() {
          super.onStart();
          start();
        }

        @Override public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
          if (actIsFinish(context)) return;
          finish();
          failure(responseString, throwable);
        }

        @Override public void onSuccess(int statusCode, Header[] headers, String responseString) {
          if (actIsFinish(context)) return;
          finish();
          success(responseString);
        }

        @Override public void onProgress(long bytesWritten, long totalSize) {
          super.onProgress(bytesWritten, totalSize);
        }

        @Override public void onFinish() {
          super.onFinish();
        }
      });
    } catch (Exception e) {
      e.printStackTrace();
      failure("", null);
    }
  }
  /**
   * Activity 是否已退出
   * isFinishing() Activity finish()中被赋值的
   */
  private boolean actIsFinish(Context context) {
    if (context instanceof Activity){
     Activity act = (Activity) context;
      return act == null || act.isFinishing() || act.isDestroyed();
    }
    return false;
  }

  protected abstract void start();

  protected abstract void finish();

  protected abstract void failure(String responseString, Throwable throwable);

  protected abstract void success(String responseString);

  public static Gson getGson() {
    return sGson;
  }
}
