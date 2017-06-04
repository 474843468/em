package com.psi.easymanager.service;

import android.app.Service;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.util.Pair;
import android.text.TextUtils;
import android.util.SparseArray;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.orhanobut.logger.Logger;
import com.psi.easymanager.chat.AppConfirmOrderDetails;
import com.psi.easymanager.chat.AppDetailsCollection;
import com.psi.easymanager.chat.AppMoveTableItem;
import com.psi.easymanager.chat.AppTableStatus;
import com.psi.easymanager.chat.FireMessage;
import com.psi.easymanager.chat.chatutils.XMPPConnectUtils;
import com.psi.easymanager.chat.req.AddProdReq;
import com.psi.easymanager.chat.req.AllTableReq;
import com.psi.easymanager.chat.req.CancelOrderReq;
import com.psi.easymanager.chat.req.ConfirmOrderReq;
import com.psi.easymanager.chat.req.ModifyBillReq;
import com.psi.easymanager.chat.req.RefundProdReq;
import com.psi.easymanager.chat.req.ServingProdReq;
import com.psi.easymanager.chat.req.TableStatusReq;
import com.psi.easymanager.chat.resp.AllTableResp;
import com.psi.easymanager.chat.resp.MoveTableResp;
import com.psi.easymanager.chat.resp.OrderInfoResp;
import com.psi.easymanager.chat.resp.SimpleResp;
import com.psi.easymanager.chat.resp.TableStatusResp;
import com.psi.easymanager.common.App;
import com.psi.easymanager.dao.PxExtraChargeDao;
import com.psi.easymanager.dao.PxFormatInfoDao;
import com.psi.easymanager.dao.PxMethodInfoDao;
import com.psi.easymanager.dao.PxOptReasonDao;
import com.psi.easymanager.dao.PxOrderDetailsDao;
import com.psi.easymanager.dao.PxOrderInfoDao;
import com.psi.easymanager.dao.PxOrderNumDao;
import com.psi.easymanager.dao.PxPayInfoDao;
import com.psi.easymanager.dao.PxProductFormatRelDao;
import com.psi.easymanager.dao.PxProductInfoDao;
import com.psi.easymanager.dao.PxProductMethodRefDao;
import com.psi.easymanager.dao.PxPromotioInfoDao;
import com.psi.easymanager.dao.PxTableAreaDao;
import com.psi.easymanager.dao.PxTableExtraRelDao;
import com.psi.easymanager.dao.PxTableInfoDao;
import com.psi.easymanager.dao.SmackUUIDRecordDao;
import com.psi.easymanager.dao.TableOrderRelDao;
import com.psi.easymanager.dao.UserDao;
import com.psi.easymanager.dao.dbUtil.DaoServiceUtil;
import com.psi.easymanager.event.ConfirmStartBillEvent;
import com.psi.easymanager.event.FindBillRefreshStatusEvent;
import com.psi.easymanager.event.RefreshCashBillListEvent;
import com.psi.easymanager.event.RevokeOrFinishBillEvent;
import com.psi.easymanager.event.UpdateProdInfoListStatusEvent;
import com.psi.easymanager.module.Office;
import com.psi.easymanager.module.PrintDetailsCollect;
import com.psi.easymanager.module.PxExtraCharge;
import com.psi.easymanager.module.PxExtraDetails;
import com.psi.easymanager.module.PxFormatInfo;
import com.psi.easymanager.module.PxMethodInfo;
import com.psi.easymanager.module.PxOperationLog;
import com.psi.easymanager.module.PxOptReason;
import com.psi.easymanager.module.PxOrderDetails;
import com.psi.easymanager.module.PxOrderInfo;
import com.psi.easymanager.module.PxOrderNum;
import com.psi.easymanager.module.PxPayInfo;
import com.psi.easymanager.module.PxProductCategory;
import com.psi.easymanager.module.PxProductFormatRel;
import com.psi.easymanager.module.PxProductInfo;
import com.psi.easymanager.module.PxProductMethodRef;
import com.psi.easymanager.module.PxPromotioDetails;
import com.psi.easymanager.module.PxPromotioInfo;
import com.psi.easymanager.module.PxTableAlteration;
import com.psi.easymanager.module.PxTableArea;
import com.psi.easymanager.module.PxTableExtraRel;
import com.psi.easymanager.module.PxTableInfo;
import com.psi.easymanager.module.SmackUUIDRecord;
import com.psi.easymanager.module.TableOrderRel;
import com.psi.easymanager.module.User;
import com.psi.easymanager.print.MakePrintDetails;
import com.psi.easymanager.print.net.PrintTaskManager;
import com.psi.easymanager.utils.PromotioDetailsHelp;
import de.greenrobot.dao.query.Join;
import de.greenrobot.dao.query.QueryBuilder;
import de.greenrobot.dao.query.WhereCondition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.greenrobot.eventbus.EventBus;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.chat.ChatManager;
import org.jivesoftware.smack.chat.ChatManagerListener;
import org.jivesoftware.smack.chat.ChatMessageListener;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;

import static com.psi.easymanager.dao.dbUtil.DaoServiceUtil.getTableInfoService;

/**
 * Created by dorado on 2016/6/24.
 */
public class ChatSmackService extends Service {
  private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
  //订单日期用 sdf
  private SimpleDateFormat mSdfDate = new SimpleDateFormat("yyyyMMdd");
  //序列号用 sdf
  private SimpleDateFormat mSdfOrderReq = new SimpleDateFormat("yyyyMMddHHmmss");
  //单一线程 处理数据库操作
  private static ExecutorService sDbEngine = Executors.newSingleThreadExecutor();

  @Nullable @Override public IBinder onBind(Intent intent) {
    return null;
  }

  @Override public int onStartCommand(Intent intent, int flags, int startId) {
    getChatMessage();
    return super.onStartCommand(intent, flags, startId);
  }

  @Override public void onDestroy() {
    super.onDestroy();
    XMPPTCPConnection connection = XMPPConnectUtils.getConnection();
    if (connection != null) {
      ChatManager chatManager = ChatManager.getInstanceFor(connection);
      Set<ChatManagerListener> listenerSet = chatManager.getChatListeners();
      for (ChatManagerListener listener : listenerSet) {
        chatManager.removeChatListener(listener);
      }
    }
  }

  //@formatter:off
  private void getChatMessage() {
    XMPPTCPConnection connection = XMPPConnectUtils.getConnection();
    if (connection == null) {
      //ChatLogin
      startService(new Intent(this, ChatLoginService.class));
      stopSelf();
      return;
    }
    ChatManager chatManager = ChatManager.getInstanceFor(connection);
    chatManager.addChatListener(new ChatManagerListener() {
      @Override public void chatCreated(Chat chat, boolean createdLocally) {
        if (!createdLocally) {
          chat.addMessageListener(new MyMsgListener());
        }
      }
    });
  }

  class MyMsgListener implements ChatMessageListener {
    @Override public void processMessage(Chat chat, Message message) {
      String body = message.getBody();
      Logger.i(body);
      final FireMessage fireMessage = new Gson().fromJson(body, FireMessage.class);
      //处理请求
      sDbEngine.execute(new OperateReqRunnable(chat, fireMessage));
    }
  }
  //@formatter:off

  /**
   * 处理请求
   */
  //@formatter:on
  class OperateReqRunnable implements Runnable {

    private Chat mChat;
    private FireMessage mFireMessage;

    public OperateReqRunnable(Chat chat, FireMessage fireMessage) {
      mChat = chat;
      mFireMessage = fireMessage;
    }

    @Override public void run() {
      //校验UUID
      String uuid = mFireMessage.getUUID().toString();
      SmackUUIDRecord uuidRecord = DaoServiceUtil.getSmackUUIDRecordService()
          .queryBuilder()
          .where(SmackUUIDRecordDao.Properties.Uuid.eq(uuid))
          .unique();
      if (uuidRecord != null) return;

      //数据库事务
      SQLiteDatabase db = DaoServiceUtil.getUserDao().getDatabase();
      db.beginTransaction();
      try {
        //保存UUID
        SmackUUIDRecord smackUUIDRecord = new SmackUUIDRecord();
        smackUUIDRecord.setUuid(uuid);
        smackUUIDRecord.setOperateTime(new Date());
        DaoServiceUtil.getSmackUUIDRecordService().save(smackUUIDRecord);

        switch (mFireMessage.getOperateType()) {
          case FireMessage.HALL_LIST_REQ://请求大厅列表
            operateHallListReq(mChat, mFireMessage);
            break;
          case FireMessage.PARLOR_LIST_REQ://请求包间列表
            operateParlorListReq(mChat, mFireMessage);
            break;
          case FireMessage.CONFIRM_ORDER_REQ://确认下单
            operateConfirmOrder(mChat, mFireMessage);
            break;
          case FireMessage.ORDER_INFO_REQ://订单信息
            operateOrderInfo(mChat, mFireMessage);
            break;
          case FireMessage.CANCEL_ORDER_REQ://撤单信息
            operateCancelOrder(mChat, mFireMessage);
            break;
          case FireMessage.MOVE_TABLE_REQ://移动桌台信息
            operateMoveTable(mChat, mFireMessage);
            break;
          case FireMessage.MODIFY_BILL_REQ://改单信息
            operateModifyBill(mChat, mFireMessage);
            break;
          case FireMessage.ADD_PROD_REQ://加菜
            operateAddProd(mChat, mFireMessage);
            break;
          case FireMessage.REFUND_PROD_REQ://退菜
            operateRefundProd(mChat, mFireMessage);
            break;
          case FireMessage.SERVING_PROD_REQ://划菜
            operateServingTag(mChat, mFireMessage);
            break;
          case FireMessage.ALL_EMPTY_TABLE_REQ://开单所有空桌台
            operateAllEmptyTable(mChat, mFireMessage);
            break;
          case FireMessage.ALL_OCCUPIED_TABLE_REQ://改单所有已占用桌台
            operateAllOccupiedTable(mChat, mFireMessage);
            break;
        }
        db.setTransactionSuccessful();
      } catch (Exception e) {
        Logger.e(e.toString());
      } finally {
        db.endTransaction();
      }
    }
  }

  /**
   * 请求大厅列表
   */
  //@formatter:off
  private void operateHallListReq(Chat chat, FireMessage fireMessage) {
    String data = fireMessage.getData();
    TableStatusReq req = new Gson().fromJson(data, TableStatusReq.class);
    List<String> tableIdList = req.getTableIdList();
    List<AppTableStatus> tableStatusList = addOrderToTableStatusList(tableIdList);
    TableStatusResp tableStatusResp = new TableStatusResp();
    tableStatusResp.setTableStatusList(tableStatusList);
    String s = new Gson().toJson(tableStatusResp);
    FireMessage fireMessageResp = new FireMessage();
    fireMessageResp.setOperateType(FireMessage.HALL_LIST_RESP);
    fireMessageResp.setData(s);
    fireMessageResp.setUUID(fireMessage.getUUID());
    //发送消息
    try {
      chat.sendMessage(new Gson().toJson(fireMessageResp));
    } catch (SmackException.NotConnectedException e) {
      e.printStackTrace();
    }
  }

  /**
   * 请求包间列表
   */
  //@formatter:off
  private void operateParlorListReq(Chat chat, FireMessage fireMessage) {
    String data = fireMessage.getData();
    TableStatusReq req = new Gson().fromJson(data, TableStatusReq.class);
    List<String> tableIdList = req.getTableIdList();
    List<AppTableStatus> tableStatusList = addOrderToTableStatusList(tableIdList);
    TableStatusResp tableStatusResp = new TableStatusResp();
    tableStatusResp.setTableStatusList(tableStatusList);
    String s = new Gson().toJson(tableStatusResp);
    FireMessage fireMessageResp = new FireMessage();
    fireMessageResp.setOperateType(FireMessage.PARLOR_LIST_RESP);
    fireMessageResp.setUUID(fireMessage.getUUID());
    fireMessageResp.setData(s);
    //发送消息
    try {
      chat.sendMessage(new Gson().toJson(fireMessageResp));
    } catch (SmackException.NotConnectedException e) {
      e.printStackTrace();
    }
  }

  /**
   * 将订单加入桌台状态列表
   */
  private List<AppTableStatus> addOrderToTableStatusList(List<String> tableIdList) {
    List<AppTableStatus> tableStatusList = new ArrayList<AppTableStatus>();
    for (String tableId : tableIdList) {
      PxTableInfo tableInfo = getTableInfoService()
          .queryBuilder()
          .where(PxTableInfoDao.Properties.ObjectId.eq(tableId))
          .unique();
      if (tableInfo != null) {
        AppTableStatus appTableStatus = new AppTableStatus();
        appTableStatus.setTableId(tableInfo.getObjectId());
        appTableStatus.setStatus(tableInfo.getStatus());
        if (tableInfo.getStatus().equals(PxTableInfo.STATUS_OCCUPIED)) {
          //优先返回未结过账的
          QueryBuilder<TableOrderRel> qbUnFinish = DaoServiceUtil
              .getTableOrderRelService()
              .queryBuilder()
              .where(TableOrderRelDao.Properties.PxTableInfoId.eq(tableInfo.getId()))
              .where(TableOrderRelDao.Properties.OrderEndTime.isNull());
          Join<TableOrderRel,PxOrderInfo> joinUnFinish = qbUnFinish .join(TableOrderRelDao.Properties.PxOrderInfoId,PxOrderInfo.class);
          joinUnFinish.where(PxOrderInfoDao.Properties.Status.eq(PxOrderInfo.STATUS_UNFINISH));
          List<TableOrderRel> unFinishedRelList = qbUnFinish.list();
          if (unFinishedRelList != null && unFinishedRelList.size() != 0){
            PxOrderInfo dbOrder = unFinishedRelList.get(0).getDbOrder();
            appTableStatus.setActualPeopleNumber(dbOrder.getActualPeopleNumber().intValue());
            appTableStatus.setDuration(new Date().getTime() - dbOrder.getStartTime().getTime());
            tableStatusList.add(appTableStatus);
            continue;
          }

          //结果账的结账时间倒序取第一个
          QueryBuilder<TableOrderRel> queryBuilder = DaoServiceUtil
              .getTableOrderRelService()
              .queryBuilder()
              .where(TableOrderRelDao.Properties.PxTableInfoId.eq(tableInfo.getId()))
              .orderDesc(TableOrderRelDao.Properties.OrderEndTime);
          Join<TableOrderRel,PxOrderInfo> join = queryBuilder.join(TableOrderRelDao.Properties.PxOrderInfoId,PxOrderInfo.class);
          join.where(PxOrderInfoDao.Properties.Status.eq(PxOrderInfo.STATUS_UNFINISH));
          List<TableOrderRel> relList = queryBuilder.list();
          if (relList != null && relList.size() != 0){
            PxOrderInfo dbOrder = relList.get(0).getDbOrder();
            appTableStatus.setActualPeopleNumber(dbOrder.getActualPeopleNumber().intValue());
            appTableStatus.setDuration(new Date().getTime() - dbOrder.getStartTime().getTime());
            tableStatusList.add(appTableStatus);
          }
        } else {
          tableStatusList.add(appTableStatus);
        }
      }
    }
    return tableStatusList;
  }

  //@formatter:on

  /**
   * 将订单信息 加入已占用的桌台
   */
  private List<PxTableInfo> addOrderToOccupiedTableList(String like) {
    List<PxTableInfo> tableInfoList = null;
    if (like == null || like.trim().isEmpty()) {
      tableInfoList = getTableInfoService().queryBuilder()
          .where(PxTableInfoDao.Properties.DelFlag.eq("0"))
          .where(PxTableInfoDao.Properties.Status.eq(PxTableInfo.STATUS_OCCUPIED))
          .list();
    } else {
      tableInfoList = getTableInfoService().queryBuilder()
          .where(PxTableInfoDao.Properties.DelFlag.eq("0"))
          .where(PxTableInfoDao.Properties.Name.like("%" + like + "%"))
          .where(PxTableInfoDao.Properties.Status.eq(PxTableInfo.STATUS_OCCUPIED))
          .list();
    }

    for (PxTableInfo tableInfo : tableInfoList) {
      //优先返回未结过账的
      QueryBuilder<TableOrderRel> qbUnFinish = DaoServiceUtil.getTableOrderRelService()
          .queryBuilder()
          .where(TableOrderRelDao.Properties.PxTableInfoId.eq(tableInfo.getId()))
          .where(TableOrderRelDao.Properties.OrderEndTime.isNull());
      Join<TableOrderRel, PxOrderInfo> joinUnFinish =
          qbUnFinish.join(TableOrderRelDao.Properties.PxOrderInfoId, PxOrderInfo.class);
      joinUnFinish.where(PxOrderInfoDao.Properties.Status.eq(PxOrderInfo.STATUS_UNFINISH));
      List<TableOrderRel> unFinishedRelList = qbUnFinish.list();
      if (unFinishedRelList != null && unFinishedRelList.size() != 0) {
        PxOrderInfo dbOrder = unFinishedRelList.get(0).getDbOrder();
        tableInfo.setActualPeopleNumber(dbOrder.getActualPeopleNumber().intValue());
        tableInfo.setDuration(new Date().getTime() - dbOrder.getStartTime().getTime());
        continue;
      }

      //结果账的结账时间倒序取第一个
      QueryBuilder<TableOrderRel> queryBuilder = DaoServiceUtil.getTableOrderRelService()
          .queryBuilder()
          .where(TableOrderRelDao.Properties.PxTableInfoId.eq(tableInfo.getId()))
          .orderDesc(TableOrderRelDao.Properties.OrderEndTime);
      Join<TableOrderRel, PxOrderInfo> join =
          queryBuilder.join(TableOrderRelDao.Properties.PxOrderInfoId, PxOrderInfo.class);
      join.where(PxOrderInfoDao.Properties.Status.eq(PxOrderInfo.STATUS_UNFINISH));
      List<TableOrderRel> relList = queryBuilder.list();
      if (relList != null && relList.size() != 0) {
        PxOrderInfo dbOrder = relList.get(0).getDbOrder();
        tableInfo.setActualPeopleNumber(dbOrder.getActualPeopleNumber().intValue());
        tableInfo.setDuration(new Date().getTime() - dbOrder.getStartTime().getTime());
      }
    }
    return tableInfoList;
  }

  /**
   * 确认下单
   */
  //@formatter:off
  private void operateConfirmOrder(Chat chat, FireMessage fireMessage) {
    String data = fireMessage.getData();
    ConfirmOrderReq confirmOrderReq = new Gson().fromJson(data, ConfirmOrderReq.class);
    String waiterId = confirmOrderReq.getWaiterId();

    //促销计划
    String promotioInfoId = confirmOrderReq.getPromotioInfoId();
    PxPromotioInfo promotioInfo = null;
    if (!TextUtils.isEmpty(promotioInfoId)) {
      promotioInfo = DaoServiceUtil.getPromotionInfoService()
          .queryBuilder()
          .where(PxPromotioInfoDao.Properties.ObjectId.eq(promotioInfoId))
          .unique();
      //检查促销计划
      boolean valid = PromotioDetailsHelp.isValid(promotioInfo);
      if (!valid) {
        SimpleResp confirmOrderResp = new SimpleResp();
        confirmOrderResp.setResult(SimpleResp.FAILURE);
        confirmOrderResp.setDes("促销计划无效,请同步数据!");
        String failureData = new Gson().toJson(confirmOrderResp);
        sendMessage(chat, FireMessage.CONFIRM_ORDER_RESP, failureData, fireMessage.getUUID());
        return;
      }
    }

    User waiter = DaoServiceUtil.getUserService()
        .queryBuilder()
        .where(UserDao.Properties.ObjectId.eq(waiterId))
        .where(UserDao.Properties.DelFlag.eq("0"))
        .unique();
    if (waiterId == null || waiter == null) {
      SimpleResp confirmOrderResp = new SimpleResp();
      confirmOrderResp.setResult(SimpleResp.FAILURE);
      confirmOrderResp.setDes("该服务生已删除");
      String failureData = new Gson().toJson(confirmOrderResp);
      FireMessage messageFailure = new FireMessage();
      messageFailure.setOperateType(FireMessage.CONFIRM_ORDER_RESP);
      messageFailure.setData(failureData);
      messageFailure.setUUID(fireMessage.getUUID());
      //发送消息
      try {
        chat.sendMessage(new Gson().toJson(messageFailure));
      } catch (SmackException.NotConnectedException e) {
        e.printStackTrace();
      }
      return;
    }
    String tableId = confirmOrderReq.getTableId();
    PxTableInfo tableInfo = getTableInfoService()
        .queryBuilder()
        .where(PxTableInfoDao.Properties.ObjectId.eq(tableId))
        .where(PxTableInfoDao.Properties.DelFlag.eq("0"))
        .unique();
    //桌台已删除
    if (tableInfo == null) {
      SimpleResp confirmOrderResp = new SimpleResp();
      confirmOrderResp.setResult(SimpleResp.FAILURE);
      confirmOrderResp.setDes("收银机已删除该桌台");
      String failureData = new Gson().toJson(confirmOrderResp);
      FireMessage messageFailure = new FireMessage();
      messageFailure.setOperateType(FireMessage.CONFIRM_ORDER_RESP);
      messageFailure.setData(failureData);
      messageFailure.setUUID(fireMessage.getUUID());
      //发送消息
      try {
        chat.sendMessage(new Gson().toJson(messageFailure));
      } catch (SmackException.NotConnectedException e) {
        e.printStackTrace();
      }
      return;
    }
    //桌台已占用
    if (tableInfo.getStatus().equals(PxTableInfo.STATUS_OCCUPIED)) {
      SimpleResp confirmOrderResp = new SimpleResp();
      confirmOrderResp.setResult(SimpleResp.FAILURE);
      confirmOrderResp.setDes("该桌台已经被占用");
      String failureData = new Gson().toJson(confirmOrderResp);
      FireMessage messageFailure = new FireMessage();
      messageFailure.setOperateType(FireMessage.CONFIRM_ORDER_RESP);
      messageFailure.setData(failureData);
      messageFailure.setUUID(fireMessage.getUUID());
      //发送消息
      try {
        chat.sendMessage(new Gson().toJson(messageFailure));
      } catch (SmackException.NotConnectedException e) {
        e.printStackTrace();
      }
      return;
    }

    //商品信息
    List<AppConfirmOrderDetails> confirmOrderDetailsList = confirmOrderReq.getConfirmOrderDetailsList();
    for (AppConfirmOrderDetails confirmOrderDetails : confirmOrderDetailsList) {
      //商品id
      String prodId = confirmOrderDetails.getProdId();
      PxProductInfo productInfo = DaoServiceUtil.getProductInfoService()
          .queryBuilder()
          .where(PxProductInfoDao.Properties.ObjectId.eq(prodId))
          .where(PxProductInfoDao.Properties.DelFlag.eq("0"))
          .whereOr(PxProductInfoDao.Properties.Shelf.isNull(), PxProductInfoDao.Properties.Shelf.eq(PxProductInfo.SHELF_PUT_AWAY))
          .unique();

      //商品检测
      if (productInfo == null){
        SimpleResp confirmOrderResp = new SimpleResp();
        confirmOrderResp.setResult(SimpleResp.FAILURE);
        confirmOrderResp.setDes("商品与收银端不同步,请先更新数据");
        String failureData = new Gson().toJson(confirmOrderResp);
        FireMessage messageFailure = new FireMessage();
        messageFailure.setOperateType(FireMessage.CONFIRM_ORDER_RESP);
        messageFailure.setData(failureData);
        messageFailure.setUUID(fireMessage.getUUID());
        //发送消息
        try {
          chat.sendMessage(new Gson().toJson(messageFailure));
        } catch (SmackException.NotConnectedException e) {
          e.printStackTrace();
        }
        return;
      }

      //分类检测
      if (productInfo != null) {
        PxProductCategory dbCategory = productInfo.getDbCategory();
        if (dbCategory == null || "1".equals(dbCategory.getDelFlag()) || "1".equals(dbCategory.getShelf())) {
          SimpleResp confirmOrderResp = new SimpleResp();
          confirmOrderResp.setResult(SimpleResp.FAILURE);
          confirmOrderResp.setDes("分类与收银端不同步,请先更新数据");
          String failureData = new Gson().toJson(confirmOrderResp);
          FireMessage messageFailure = new FireMessage();
          messageFailure.setOperateType(FireMessage.CONFIRM_ORDER_RESP);
          messageFailure.setData(failureData);
          messageFailure.setUUID(fireMessage.getUUID());
          //发送消息
          try {
            chat.sendMessage(new Gson().toJson(messageFailure));
          } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
          }
          return;
        }
      }


      //规格id
      String formatId = confirmOrderDetails.getFormatId();
      PxFormatInfo formatInfo = null;
      if (formatId != null) {
        formatInfo = DaoServiceUtil.getFormatInfoService()
            .queryBuilder()
            .where(PxFormatInfoDao.Properties.ObjectId.eq(formatId))
            .where(PxFormatInfoDao.Properties.DelFlag.eq("0"))
            .unique();
      }
      //规格rel
      PxProductFormatRel formatRel = null;
      if (productInfo != null && formatInfo != null){
       formatRel = DaoServiceUtil.getProductFormatRelService()
            .queryBuilder()
            .where(PxProductFormatRelDao.Properties.PxFormatInfoId.eq(formatInfo.getId()))
            .where(PxProductFormatRelDao.Properties.PxProductInfoId.eq(productInfo.getId()))
            .where(PxProductFormatRelDao.Properties.DelFlag.eq("0"))
            .unique();
      }
      //做法id
      String methodId = confirmOrderDetails.getMethodId();
      PxMethodInfo methodInfo = null;
      if (methodId != null) {
        methodInfo = DaoServiceUtil.getMethodInfoService()
            .queryBuilder()
            .where(PxMethodInfoDao.Properties.ObjectId.eq(methodId))
            .where(PxMethodInfoDao.Properties.DelFlag.eq("0"))
            .unique();
      }
      //做法rel
      PxProductMethodRef methodRef = null;
      if (productInfo != null && methodInfo != null){
        methodRef = DaoServiceUtil.getProductMethodRelService()
            .queryBuilder()
            .where(PxProductMethodRefDao.Properties.PxMethodInfoId.eq(methodInfo.getId()))
            .where(PxProductMethodRefDao.Properties.PxProductInfoId.eq(productInfo.getId()))
            .where(PxProductMethodRefDao.Properties.DelFlag.eq("0"))
            .unique();
      }


      //查询可用的规格引用关系
      QueryBuilder<PxProductFormatRel> formatRelQb = DaoServiceUtil.getProductFormatRelService()
          .queryBuilder()
          .where(PxProductFormatRelDao.Properties.DelFlag.eq("0"))
          .where(PxProductFormatRelDao.Properties.PxProductInfoId.eq(productInfo.getId()));
      Join<PxProductFormatRel, PxFormatInfo> formatJoin = formatRelQb.join(PxProductFormatRelDao.Properties.PxFormatInfoId, PxFormatInfo.class);
      formatJoin.where(PxFormatInfoDao.Properties.DelFlag.eq("0"));
      long countFormat = formatRelQb.count();
      if (formatId == null && countFormat != 0) {
        SimpleResp confirmOrderResp = new SimpleResp();
        confirmOrderResp.setResult(SimpleResp.FAILURE);
        confirmOrderResp.setDes(productInfo.getName() + "已设置规格，请先更新数据");
        String failureData = new Gson().toJson(confirmOrderResp);
        FireMessage messageFailure = new FireMessage();
        messageFailure.setOperateType(FireMessage.CONFIRM_ORDER_RESP);
        messageFailure.setData(failureData);
        messageFailure.setUUID(fireMessage.getUUID());
        //发送消息
        try {
          chat.sendMessage(new Gson().toJson(messageFailure));
        } catch (SmackException.NotConnectedException e) {
          e.printStackTrace();
        }
        return;
      }

      //商品规格做法 检测
      if ((formatId != null && (formatInfo == null || formatRel == null)) || (methodId != null && (methodInfo == null || methodRef == null))) {
        SimpleResp confirmOrderResp = new SimpleResp();
        confirmOrderResp.setResult(SimpleResp.FAILURE);
        confirmOrderResp.setDes(productInfo.getName() + "规格或做法与收银端不同步,请先更新数据");
        String failureData = new Gson().toJson(confirmOrderResp);
        FireMessage messageFailure = new FireMessage();
        messageFailure.setOperateType(FireMessage.CONFIRM_ORDER_RESP);
        messageFailure.setData(failureData);
        messageFailure.setUUID(fireMessage.getUUID());
        //发送消息
        try {
          chat.sendMessage(new Gson().toJson(messageFailure));
        } catch (SmackException.NotConnectedException e) {
          e.printStackTrace();
        }
        return;
      }

      //商品沽清检测
      if (formatInfo != null) {
        PxProductFormatRel rel = DaoServiceUtil.getProductFormatRelService()
            .queryBuilder()
            .where(PxProductFormatRelDao.Properties.PxProductInfoId.eq(productInfo.getId()))
            .where(PxProductFormatRelDao.Properties.PxFormatInfoId.eq(formatInfo.getId()))
            .unique();
        if (rel != null && PxProductFormatRel.STATUS_STOP_SALE.equals(rel.getStatus())) {
          SimpleResp confirmOrderResp = new SimpleResp();
          confirmOrderResp.setResult(SimpleResp.FAILURE);
          confirmOrderResp.setDes(
              rel.getDbProduct().getName() + rel.getDbFormat().getName() + "已沽清,不能添加");
          String failureData = new Gson().toJson(confirmOrderResp);
          FireMessage messageFailure = new FireMessage();
          messageFailure.setOperateType(FireMessage.CONFIRM_ORDER_RESP);
          messageFailure.setData(failureData);
          messageFailure.setUUID(fireMessage.getUUID());
          //发送消息
          try {
            chat.sendMessage(new Gson().toJson(messageFailure));
          } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
          }
          return;
        }
      }

      if (productInfo.getStatus().equals(PxProductInfo.STATUS_STOP_SALE)) {
        SimpleResp confirmOrderResp = new SimpleResp();
        confirmOrderResp.setResult(SimpleResp.FAILURE);
        confirmOrderResp.setDes(productInfo.getName() + "已沽清,不能添加");
        String failureData = new Gson().toJson(confirmOrderResp);
        FireMessage messageFailure = new FireMessage();
        messageFailure.setOperateType(FireMessage.CONFIRM_ORDER_RESP);
        messageFailure.setData(failureData);
        messageFailure.setUUID(fireMessage.getUUID());
        //发送消息
        try {
          chat.sendMessage(new Gson().toJson(messageFailure));
        } catch (SmackException.NotConnectedException e) {
          e.printStackTrace();
        }
        return;
      }

      //规格余量检测
      if (formatInfo != null) {
        PxProductFormatRel rel = DaoServiceUtil.getProductFormatRelService()
            .queryBuilder()
            .where(PxProductFormatRelDao.Properties.PxProductInfoId.eq(productInfo.getId()))
            .where(PxProductFormatRelDao.Properties.PxFormatInfoId.eq(formatInfo.getId()))
            .unique();
        if (PxProductFormatRel.STATUS_ON_SALE.equals(rel.getStatus())) {
          if (productInfo.getMultipleUnit().equals(PxProductInfo.IS_TWO_UNIT_TURE)) {
            if (rel.getStock() != null && rel.getStock() < confirmOrderDetails.getMultNum()) {
              SimpleResp confirmOrderResp = new SimpleResp();
              confirmOrderResp.setResult(SimpleResp.FAILURE);
              confirmOrderResp.setDes(
                  rel.getDbProduct().getName() + rel.getDbFormat().getName() + "剩余数量不足,不能添加");
              String failureData = new Gson().toJson(confirmOrderResp);
              FireMessage messageFailure = new FireMessage();
              messageFailure.setOperateType(FireMessage.CONFIRM_ORDER_RESP);
              messageFailure.setData(failureData);
              messageFailure.setUUID(fireMessage.getUUID());
              //发送消息
              try {
                chat.sendMessage(new Gson().toJson(messageFailure));
              } catch (SmackException.NotConnectedException e) {
                e.printStackTrace();
              }
              return;
            }
          } else {
            if (rel.getStock() != null && rel.getStock() < confirmOrderDetails.getNum()) {
              SimpleResp confirmOrderResp = new SimpleResp();
              confirmOrderResp.setResult(SimpleResp.FAILURE);
              confirmOrderResp.setDes(
                  rel.getDbProduct().getName() + rel.getDbFormat().getName() + "剩余数量不足,不能添加");
              String failureData = new Gson().toJson(confirmOrderResp);
              FireMessage messageFailure = new FireMessage();
              messageFailure.setOperateType(FireMessage.CONFIRM_ORDER_RESP);
              messageFailure.setData(failureData);
              messageFailure.setUUID(fireMessage.getUUID());
              //发送消息
              try {
                chat.sendMessage(new Gson().toJson(messageFailure));
              } catch (SmackException.NotConnectedException e) {
                e.printStackTrace();
              }
              return;
            }
          }
        }
      }
      //商品余量检测
      else {
        if (productInfo.getStatus().equals(PxProductInfo.STATUS_ON_SALE)) {
          if (productInfo.getMultipleUnit().equals(PxProductInfo.IS_TWO_UNIT_TURE)
              && productInfo.getOverPlus() != null
              && productInfo.getOverPlus() < confirmOrderDetails.getMultNum()) {
            SimpleResp confirmOrderResp = new SimpleResp();
            confirmOrderResp.setResult(SimpleResp.FAILURE);
            confirmOrderResp.setDes(productInfo.getName() + "剩余数量不足,不能添加");
            String failureData = new Gson().toJson(confirmOrderResp);
            FireMessage messageFailure = new FireMessage();
            messageFailure.setOperateType(FireMessage.CONFIRM_ORDER_RESP);
            messageFailure.setData(failureData);
            messageFailure.setUUID(fireMessage.getUUID());
            //发送消息
            try {
              chat.sendMessage(new Gson().toJson(messageFailure));
            } catch (SmackException.NotConnectedException e) {
              e.printStackTrace();
            }
            return;
          } else if (productInfo.getMultipleUnit().equals(PxProductInfo.IS_TWO_UNIT_FALSE)
              && productInfo.getOverPlus() != null
              && productInfo.getOverPlus() < confirmOrderDetails.getNum()) {
            SimpleResp confirmOrderResp = new SimpleResp();
            confirmOrderResp.setResult(SimpleResp.FAILURE);
            confirmOrderResp.setDes(productInfo.getName() + "剩余数量不足,不能添加");
            String failureData = new Gson().toJson(confirmOrderResp);
            FireMessage messageFailure = new FireMessage();
            messageFailure.setOperateType(FireMessage.CONFIRM_ORDER_RESP);
            messageFailure.setData(failureData);
            messageFailure.setUUID(fireMessage.getUUID());
            //发送消息
            try {
              chat.sendMessage(new Gson().toJson(messageFailure));
            } catch (SmackException.NotConnectedException e) {
              e.printStackTrace();
            }
            return;
          }
        }
      }
    }
    //开单
    PxOrderInfo orderInfo = new PxOrderInfo();
    //type
    orderInfo.setType(PxOrderInfo.TYPE_DEFAULT);
    //reverse
    orderInfo.setIsReserveOrder(PxOrderInfo.IS_REVERSE_ORDER_FALSE);
    //是否锁定状态 默认不锁定
    orderInfo.setIsLock(false);
    //开始时间
    orderInfo.setStartTime(new Date());
    //未完成
    orderInfo.setStatus(PxOrderInfo.STATUS_UNFINISH);
    //抹零金额
    orderInfo.setTailMoney((double) 0);
    //优惠金额
    orderInfo.setDiscountPrice((double) 0);
    //支付类优惠
    orderInfo.setPayPrivilege((double) 0);
    //是否刷卡
    orderInfo.setUseVipCard(PxOrderInfo.USE_VIP_CARD_FALSE);
    //服务生
    orderInfo.setDbWaiter(waiter);
    //收银员
    if (((App) (App.getContext())).getUser() == null) return;
    orderInfo.setDbUser(((App) App.getContext()).getUser());
    //储存
    DaoServiceUtil.getOrderInfoService().save(orderInfo);
    //桌台状态
    tableInfo.setStatus(PxTableInfo.STATUS_OCCUPIED);
    //人数
    orderInfo.setActualPeopleNumber(confirmOrderReq.getPeopleNum());
    getTableInfoService().saveOrUpdate(tableInfo);

    //订单和桌台关联
    TableOrderRel tableOrderRel = new TableOrderRel();
    tableOrderRel.setDbOrder(orderInfo);
    tableOrderRel.setDbTable(tableInfo);
    DaoServiceUtil.getTableOrderRelService().saveOrUpdate(tableOrderRel);

    //实收
    orderInfo.setRealPrice((double) 0);
    //应收
    orderInfo.setAccountReceivable((double) 0);
    //总的找零
    orderInfo.setTotalChange((double) 0);
    //订单类型
    orderInfo.setOrderInfoType(PxOrderInfo.ORDER_INFO_TYPE_TABLE);
    //备注
    if (confirmOrderReq.getRemarks() == null || confirmOrderReq.getRemarks().equals("")) {
      orderInfo.setRemarks("");
    } else {
      orderInfo.setRemarks(confirmOrderReq.getRemarks());
    }
    //促销计划
    if (promotioInfo != null){
      orderInfo.setDbPromotioInfo(promotioInfo);
    }
    DaoServiceUtil.getOrderInfoService().saveOrUpdate(orderInfo);

    //查询可用的附加费引用关系
    QueryBuilder<PxTableExtraRel> extraRelQb = DaoServiceUtil.getTableExtraRelService()
        .queryBuilder()
        .where(PxTableExtraRelDao.Properties.DelFlag.eq("0"))
        .where(PxTableExtraRelDao.Properties.PxTableInfoId.eq(tableInfo.getId()));
    Join<PxTableExtraRel, PxExtraCharge> extraJoin =
        extraRelQb.join(PxTableExtraRelDao.Properties.PxExtraChargeId, PxExtraCharge.class);
    extraJoin.where(PxExtraChargeDao.Properties.DelFlag.eq("0"));
    PxTableExtraRel rel = extraRelQb.unique();
    //如果不为空，表示有可用的附加费
    if (rel != null) {
      //附加费信息
      PxExtraCharge extraCharge = rel.getDbExtraCharge();
      //新建附加费详情
      PxExtraDetails pxExtraDetails = new PxExtraDetails();
      //开始时间
      pxExtraDetails.setStartTime(new Date());
      //价格
      pxExtraDetails.setPrice((double) 0);
      //订单
      pxExtraDetails.setDbOrder(orderInfo);
      //附加费名
      pxExtraDetails.setExtraName(extraCharge.getName());
      //桌台名
      pxExtraDetails.setTableName(tableInfo.getName());
      //已付款
      pxExtraDetails.setPayPrice((double) 0);
      //已打印
      pxExtraDetails.setIsPrinted(false);
      //储存
      DaoServiceUtil.getExtraDetailsService().save(pxExtraDetails);
      //订单更新当前附加费详情
      orderInfo.setDbCurrentExtra(pxExtraDetails);
    }
    //订单号
    PxOrderNum orderNum = DaoServiceUtil.getOrderNumService()
        .queryBuilder()
        .where(PxOrderNumDao.Properties.Date.eq(mSdfDate.format(new Date())))
        .unique();
    if (orderNum == null) {
      PxOrderNum pxOrderNum = new PxOrderNum();
      pxOrderNum.setDate(mSdfDate.format(new Date()));
      pxOrderNum.setNum(1);
      DaoServiceUtil.getOrderNumService().saveOrUpdate(pxOrderNum);
      //获取OrderReqNum
      String orderReqNum = createOrderReqNum(pxOrderNum.getNum());
      orderInfo.setOrderNo(orderReqNum);
      orderInfo.setOrderReqNo(orderReqNum);
    } else {
      orderNum.setNum(orderNum.getNum() + 1);
      DaoServiceUtil.getOrderNumService().saveOrUpdate(orderNum);
      //获取OrderReqNum
      String orderReqNum = createOrderReqNum(orderNum.getNum());
      orderInfo.setOrderNo(orderReqNum);
      orderInfo.setOrderReqNo(orderReqNum);
    }
    //更新订单
    DaoServiceUtil.getOrderInfoService().saveOrUpdate(orderInfo);

    //下单时间
    Date orderTime = new Date();
    //Map
    SparseArray<PrintDetailsCollect> collectArray = new SparseArray<>();

    //存放打印机IP
    List<Long> printIdList = new ArrayList<>();
    //存储DetailsList
    for (AppConfirmOrderDetails confirmOrderDetails : confirmOrderDetailsList) {
      //商品id
      String prodId = confirmOrderDetails.getProdId();
      PxProductInfo productInfo = DaoServiceUtil.getProductInfoService()
          .queryBuilder()
          .where(PxProductInfoDao.Properties.ObjectId.eq(prodId))
          .unique();
      //规格id
      String formatId = confirmOrderDetails.getFormatId();
      PxFormatInfo formatInfo = null;
      PxProductFormatRel formatRel = null;
      if (formatId != null) {
        formatInfo = DaoServiceUtil.getFormatInfoService()
            .queryBuilder()
            .where(PxFormatInfoDao.Properties.ObjectId.eq(formatId))
            .unique();
         //规格价格
        if (formatInfo != null){
          formatRel  = DaoServiceUtil.getProductFormatRelService()
            .queryBuilder()
            .where(PxProductFormatRelDao.Properties.DelFlag.eq("0"))
            .where(PxProductFormatRelDao.Properties.PxFormatInfoId.eq(formatInfo.getId()))
            .where(PxProductFormatRelDao.Properties.PxProductInfoId.eq(productInfo.getId()))
            .unique();
          }
      }
      //做法id
      String methodId = confirmOrderDetails.getMethodId();
      PxMethodInfo methodInfo = null;
      if (methodId != null) {
        methodInfo = DaoServiceUtil.getMethodInfoService()
            .queryBuilder()
            .where(PxMethodInfoDao.Properties.ObjectId.eq(methodId))
            .unique();
      }
      //促销计划
      PxPromotioDetails validPromotioDetails = PromotioDetailsHelp.getValidPromotioDetails(promotioInfo,formatInfo,productInfo);
      double unitPrice = formatRel == null ? productInfo.getPrice() : formatRel.getPrice();
      double unitVipPrice = formatRel == null ? productInfo.getVipPrice() : formatRel.getVipPrice();
      if (validPromotioDetails != null){
        unitPrice = validPromotioDetails.getPromotionalPrice();
        unitVipPrice = validPromotioDetails.getPromotionalPrice();
      }

      //数量
      int num = confirmOrderDetails.getNum();
      //多单位数量
      double multNum = confirmOrderDetails.getMultNum();
      //延迟
      boolean isDelay = confirmOrderDetails.getIsDelay() == 1;

      //新建Details
      PxOrderDetails details = new PxOrderDetails();
      //默认不清空
      details.setIsClear(false);
      //数量
      details.setNum((double) num);
      //多单位数量
      details.setMultipleUnitNumber(multNum);
      //订单
      details.setDbOrder(orderInfo);
      //折扣率
      details.setCurrentDiscRate();
      //单价(是否使用促销价)
      details.setUnitPrice(unitPrice);
      //会员单价
      details.setUnitVipPrice(unitVipPrice);
      //价格
      if (productInfo.getMultipleUnit().equals(PxProductInfo.IS_TWO_UNIT_TURE)) {
        details.setPrice(details.getUnitPrice() * details.getMultipleUnitNumber());
        details.setVipPrice(details.getUnitVipPrice() * details.getMultipleUnitNumber());
      } else {
        details.setPrice(details.getUnitPrice() * details.getNum());
        details.setVipPrice(details.getUnitVipPrice() * details.getNum());
      }
      //商品
      details.setDbProduct(productInfo);
      //下单状态
      details.setOrderStatus(PxOrderDetails.ORDER_STATUS_ORDER);
      //商品状态
      if (isDelay) {
        details.setStatus(PxOrderDetails.STATUS_DELAY);
      } else {
        details.setStatus(PxOrderDetails.STATUS_ORIDINARY);
      }
      //已上菜
      details.setIsServing(false);
      //折扣率
      details.setCurrentDiscRate();
      //规格信息
      details.setDbFormatInfo(formatInfo);
      //做法
      details.setDbMethodInfo(methodInfo);
      //套餐details
      details.setIsComboDetails(PxOrderDetails.IS_COMBO_FALSE);
      //套餐类details
      details.setInCombo(PxOrderDetails.IN_COMBO_FALSE);
      //赠品
      details.setIsGift(PxOrderDetails.GIFT_FALSE);
      //备注
      if (confirmOrderDetails.getRemarks() == null || confirmOrderDetails.getRemarks().equals("")) {
        details.setRemarks("");
      } else {
        details.setRemarks(confirmOrderDetails.getRemarks());
      }
      //已上菜
      details.setIsServing(false);
      //objId
      details.setObjectId(UUID.randomUUID().toString().replace("-","") + System.currentTimeMillis());
      //储存
      DaoServiceUtil.getOrderDetailsService().save(details);
      //生成printDetails
      MakePrintDetails.makePrintDetails(details, orderTime, collectArray, details.getNum(), details.getMultipleUnitNumber(), orderInfo, false, printIdList);
      //修改剩余数量
      if (formatInfo != null) {
        PxProductFormatRel productFormatRel = DaoServiceUtil.getProductFormatRelService()
            .queryBuilder()
            .where(PxProductFormatRelDao.Properties.PxProductInfoId.eq(productInfo.getId()))
            .where(PxProductFormatRelDao.Properties.PxFormatInfoId.eq(formatInfo.getId()))
            .unique();
        if (productFormatRel != null) {
          if (PxProductFormatRel.STATUS_ON_SALE.equals(productFormatRel.getStatus())) {
            if (productInfo.getMultipleUnit().equals(PxProductInfo.IS_TWO_UNIT_TURE)) {
              if (productFormatRel.getStock() != null) {
                productFormatRel.setStock(productFormatRel.getStock() - multNum);
              } else {
                productFormatRel.setStock(null);
              }
            } else {
              if (productFormatRel.getStock() != null) {
                productFormatRel.setStock(productFormatRel.getStock() - num);
              } else {
                productFormatRel.setStock(null);
              }
            }
            if (productFormatRel.getStock() != null && productFormatRel.getStock() == 0.0) {
              productFormatRel.setStatus(PxProductFormatRel.STATUS_STOP_SALE);
            }
            //储存
            DaoServiceUtil.getProductFormatRelService().saveOrUpdate(productFormatRel);
          }
        }
      } else {
        if (productInfo.getStatus().equals(PxProductInfo.STATUS_ON_SALE)
            && productInfo.getOverPlus() != null && productInfo.getOverPlus() != 0) {
          if (productInfo.getMultipleUnit().equals(PxProductInfo.IS_TWO_UNIT_TURE)) {
            productInfo.setOverPlus(productInfo.getOverPlus() - multNum);
          } else {
            productInfo.setOverPlus(productInfo.getOverPlus() - num);
          }
          if (productInfo.getOverPlus() == 0) {
            productInfo.setStatus(PxProductInfo.STATUS_STOP_SALE);
          }
          DaoServiceUtil.getProductInfoService().saveOrUpdate(productInfo);
        }
      }

      //修改商品销量
      if (productInfo.getSaleNum() == null) {
        productInfo.setSaleNum(confirmOrderDetails.getNum());
      } else {
        productInfo.setSaleNum(productInfo.getSaleNum() + confirmOrderDetails.getNum());
      }
      DaoServiceUtil.getProductInfoService().saveOrUpdate(productInfo);
    }

    //@formatter:off
    //发送成功消息
    SimpleResp confirmOrderResp = new SimpleResp();
    confirmOrderResp.setResult(SimpleResp.SUCCESS);
    confirmOrderResp.setDes("下单成功");
    String successData = new Gson().toJson(confirmOrderResp);
    FireMessage messageSuccess = new FireMessage();
    messageSuccess.setOperateType(FireMessage.CONFIRM_ORDER_RESP);
    messageSuccess.setData(successData);
    messageSuccess.setUUID(fireMessage.getUUID());
    //发送消息
    try {
      chat.sendMessage(new Gson().toJson(messageSuccess));
    } catch (SmackException.NotConnectedException e) {
      e.printStackTrace();
    }
    //由客单页面接收
    EventBus.getDefault().post(new ConfirmStartBillEvent().setOrderInfo(orderInfo).setFromWaiter(true));
    //找单页面接收
    EventBus.getDefault().postSticky(new FindBillRefreshStatusEvent());
    //由菜单列表接收,更新菜品状态
    EventBus.getDefault().post(new UpdateProdInfoListStatusEvent().setFromWaiter(true));

    //后厨打印
    //printCollect(collectArray,false,printIdList);
     PrintTaskManager.printKitchenTask(collectArray,printIdList,false);
  }

  /**
   * 生成reqNum
   */
  //@formatter:off
  private String createOrderReqNum(int orderNum) {
    //公司编码
    int companyHashCode = ((App) App.getContext()).getUser().getCompanyCode().hashCode();
    //公司编码求绝对值
    int absHashCode = Math.abs(Integer.valueOf(companyHashCode));
    //公司编码绝对值的String表示
    String strHashCode = String.valueOf(absHashCode);
    //当前日期
    String strDate = mSdfOrderReq.format(new Date());
    //当前序号
    String strSer = String.format("%06d", orderNum);
    String strOrderNum = new StringBuilder().append(strHashCode).append(strDate).append(strSer).toString();
    return strOrderNum;
  }

  /**
   * 请求订单信息
   */
  //@formatter:off
  private void operateOrderInfo(Chat chat, FireMessage fireMessage) {
    String tableId = fireMessage.getTableId();
    PxTableInfo tableInfo = getTableInfoService()
        .queryBuilder()
        .where(PxTableInfoDao.Properties.ObjectId.eq(tableId))
        .where(PxTableInfoDao.Properties.DelFlag.eq("0"))
        .unique();
    if (tableInfo == null) {
      OrderInfoResp resp = new OrderInfoResp();
      resp.setResult(OrderInfoResp.FAILURE);
      resp.setDes("收银端无此桌台");
      String successData = new Gson().toJson(resp);
      FireMessage messageSuccess = new FireMessage();
      messageSuccess.setOperateType(FireMessage.ORDER_INFO_RESP);
      messageSuccess.setData(successData);
      messageSuccess.setUUID(fireMessage.getUUID());
      //发送消息
      try {
        chat.sendMessage(new Gson().toJson(messageSuccess));
      } catch (SmackException.NotConnectedException e) {
        e.printStackTrace();
      }
      return;
    }
    if (tableInfo.getStatus().equals(PxTableInfo.STATUS_EMPTY)) {
      OrderInfoResp resp = new OrderInfoResp();
      resp.setResult(OrderInfoResp.FAILURE);
      resp.setDes("此桌为空闲状态");
      String successData = new Gson().toJson(resp);
      FireMessage messageSuccess = new FireMessage();
      messageSuccess.setOperateType(FireMessage.ORDER_INFO_RESP);
      messageSuccess.setData(successData);
      messageSuccess.setUUID(fireMessage.getUUID());
      //发送消息
      try {
        chat.sendMessage(new Gson().toJson(messageSuccess));
      } catch (SmackException.NotConnectedException e) {
        e.printStackTrace();
      }
      return;
    }
    OrderInfoResp resp = new OrderInfoResp();

    //状态
    resp.setResult(OrderInfoResp.SUCCESS);
    //当前订单
    PxOrderInfo currentOrder = findOrder(tableInfo);
    PxPromotioInfo promotioInfo = currentOrder.getDbPromotioById();
    //table
    resp.setTableId(tableInfo.getObjectId());
    //人数
    resp.setPeopleNum(currentOrder.getActualPeopleNumber());
    //桌名
    resp.setTableName(tableInfo.getName());
    //订单id
    resp.setOrderId(currentOrder.getId());
    //开单时间
    resp.setStartTime(sdf.format(currentOrder.getStartTime()));
    //流水号
    resp.setOrderReqNum(currentOrder.getOrderReqNo());
    //备注
    resp.setRemarks(currentOrder.getRemarks());
    //促销计划
    if (promotioInfo != null){
      resp.setPromotioInfoId(promotioInfo.getObjectId());
    }
    //查询商品
    List<PxOrderDetails> detailsList = DaoServiceUtil.getOrderDetailsService()
        .queryBuilder()
        .where(PxOrderDetailsDao.Properties.PxOrderInfoId.eq(currentOrder.getId()))
        .whereOr(PxOrderDetailsDao.Properties.OrderStatus.eq(PxOrderDetails.ORDER_STATUS_ORDER),PxOrderDetailsDao.Properties.OrderStatus.eq(PxOrderDetails.ORDER_STATUS_REFUND))
        .whereOr(PxOrderDetailsDao.Properties.InCombo.eq(PxOrderDetails.IN_COMBO_FALSE), PxOrderDetailsDao.Properties.InCombo.isNull())
       .list();
    ArrayList<AppDetailsCollection> appDetailsCollectionList = new ArrayList<AppDetailsCollection>();
    for (PxOrderDetails details : detailsList) {
      AppDetailsCollection appDetailsCollection = new AppDetailsCollection();
      PxProductInfo dbProduct = details.getDbProduct();


      //商品名
      appDetailsCollection.setProdName(dbProduct.getName());
      //商品id
      appDetailsCollection.setProdObjId(dbProduct.getObjectId());
      //数量
      appDetailsCollection.setNum(details.getNum().intValue());
      appDetailsCollection.setUnit(dbProduct.getUnit());
      //是否为多单位
      if (details.getDbProduct().getMultipleUnit().equals(PxProductInfo.IS_TWO_UNIT_TURE)) {
        appDetailsCollection.setIsMultipleUnit(AppDetailsCollection.IS_TWO_UNIT_TURE);
        //多单位数量
        appDetailsCollection.setMultipleNum(details.getMultipleUnitNumber());
        appDetailsCollection.setUnit(dbProduct.getUnit());
        appDetailsCollection.setOrderUnit(dbProduct.getOrderUnit());
      } else {
        appDetailsCollection.setIsMultipleUnit(AppDetailsCollection.IS_TWO_UNIT_FALSE);
      }
      //规格
      if (details.getDbFormatInfo() != null) {
        appDetailsCollection.setFormatName(details.getDbFormatInfo().getName());
        appDetailsCollection.setFormatObjId(details.getDbFormatInfo().getObjectId());
      }
      //做法
      if (details.getDbMethodInfo() != null) {
        appDetailsCollection.setMethodName(details.getDbMethodInfo().getName());
        appDetailsCollection.setMethodObjId(details.getDbMethodInfo().getObjectId());
      }
      //折扣率
      appDetailsCollection.setDiscRate(details.getDiscountRate());
      //下单状态
      appDetailsCollection.setOrderStatus(details.getOrderStatus());
      //状态
      appDetailsCollection.setStatus(details.getStatus());
      //标记已上/未上
      if (details.getIsServing() != null){
       appDetailsCollection.setIsServing(details.getIsServing());
      }
      //赠品
      appDetailsCollection.setIsGift(details.getIsGift());
      //备注
      appDetailsCollection.setRemarks(details.getRemarks());
      //套餐商品
      appDetailsCollection.setIsComboDetails(details.getIsComboDetails());
      //ObjId
      appDetailsCollection.setObjectId(details.getObjectId());

      //添加到list
      appDetailsCollectionList.add(appDetailsCollection);
    }
    resp.setDetailsList(appDetailsCollectionList);
    //发送消息
    FireMessage messageSuccess = new FireMessage();
    messageSuccess.setOperateType(FireMessage.ORDER_INFO_RESP);
    messageSuccess.setData(new Gson().toJson(resp));
    messageSuccess.setUUID(fireMessage.getUUID());
    //发送消息
    try {
      chat.sendMessage(new Gson().toJson(messageSuccess));
    } catch (SmackException.NotConnectedException e) {
      e.printStackTrace();
    }
  }

  /**
   * 查找订单
   */
  private PxOrderInfo findOrder(PxTableInfo tableInfo) {
        QueryBuilder<TableOrderRel> queryBuilder = DaoServiceUtil
            .getTableOrderRelService()
            .queryBuilder()
            .where(TableOrderRelDao.Properties.PxTableInfoId.eq(tableInfo.getId()))
            .orderDesc(TableOrderRelDao.Properties.OrderEndTime);
        Join<TableOrderRel,PxOrderInfo> join = queryBuilder.join(TableOrderRelDao.Properties.PxOrderInfoId,PxOrderInfo.class);
        join.where(PxOrderInfoDao.Properties.Status.eq(PxOrderInfo.STATUS_UNFINISH));
        List<TableOrderRel> relList = queryBuilder.list();
        if (relList != null && relList.size() != 0){
          PxOrderInfo dbOrder = relList.get(0).getDbOrder();
          return dbOrder;
        }
    return null;
  }


  /**
   * 请求撤单
   */
  private void operateCancelOrder(Chat chat, FireMessage fireMessage) {
    String data = fireMessage.getData();
    CancelOrderReq req = new Gson().fromJson(data, CancelOrderReq.class);
    //服务生
    String waiterId = req.getWaiterId();
    if (waiterId == null) {
      SimpleResp failureResp = new SimpleResp();
      failureResp.setDes("请升级逸掌柜服务生版本");
      failureResp.setResult(SimpleResp.FAILURE);
      String failureData = new Gson().toJson(failureResp);
      FireMessage messageFailure = new FireMessage();
      messageFailure.setOperateType(FireMessage.CANCEL_ORDER_RESP);
      messageFailure.setData(failureData);
      messageFailure.setUUID(fireMessage.getUUID());
      //发送消息
      try {
        chat.sendMessage(new Gson().toJson(messageFailure));
      } catch (SmackException.NotConnectedException e) {
        e.printStackTrace();
      }
      return;
    }
    User waiter = DaoServiceUtil.getUserService()
        .queryBuilder()
        .where(UserDao.Properties.DelFlag.eq("0"))
        .where(UserDao.Properties.ObjectId.eq(waiterId))
        .unique();
    if (waiter == null) {
      SimpleResp failureResp = new SimpleResp();
      failureResp.setDes("收银端无此服务生，请同步数据");
      failureResp.setResult(SimpleResp.FAILURE);
      String failureData = new Gson().toJson(failureResp);
      FireMessage messageFailure = new FireMessage();
      messageFailure.setOperateType(FireMessage.CANCEL_ORDER_RESP);
      messageFailure.setData(failureData);
      messageFailure.setUUID(fireMessage.getUUID());
      //发送消息
      try {
        chat.sendMessage(new Gson().toJson(messageFailure));
      } catch (SmackException.NotConnectedException e) {
        e.printStackTrace();
      }
      return;
    }
    //订单
    long orderId = req.getOrderId();
    PxOrderInfo orderInfo = DaoServiceUtil.getOrderInfoService()
        .queryBuilder()
        .where(PxOrderInfoDao.Properties.Id.eq(orderId))
        .unique();
    if (orderInfo == null){
      SimpleResp failureResp = new SimpleResp();
      failureResp.setDes("此订单已删除或合并，不能撤单");
      failureResp.setResult(SimpleResp.FAILURE);
      String failureData = new Gson().toJson(failureResp);
      FireMessage messageFailure = new FireMessage();
      messageFailure.setOperateType(FireMessage.CANCEL_ORDER_RESP);
      messageFailure.setData(failureData);
      messageFailure.setUUID(fireMessage.getUUID());
      //发送消息
      try {
        chat.sendMessage(new Gson().toJson(messageFailure));
      } catch (SmackException.NotConnectedException e) {
        e.printStackTrace();
      }
      return;
    }
    List<PxPayInfo> payInfoList = DaoServiceUtil.getPayInfoService()
        .queryBuilder()
        .where(PxPayInfoDao.Properties.PxOrderInfoId.eq(orderId))
        .list();
    if (payInfoList != null && payInfoList.size() != 0){
      SimpleResp failureResp = new SimpleResp();
      failureResp.setDes("有付款信息，不能撤单");
      failureResp.setResult(SimpleResp.FAILURE);
      String failureData = new Gson().toJson(failureResp);
      FireMessage messageFailure = new FireMessage();
      messageFailure.setOperateType(FireMessage.CANCEL_ORDER_RESP);
      messageFailure.setData(failureData);
      messageFailure.setUUID(fireMessage.getUUID());
      //发送消息
      try {
        chat.sendMessage(new Gson().toJson(messageFailure));
      } catch (SmackException.NotConnectedException e) {
        e.printStackTrace();
      }
      return;
    }
    //订单状态已经改变，不能撤单
    if (!orderInfo.getStatus().equals(PxOrderInfo.STATUS_UNFINISH)) {
      SimpleResp failureResp = new SimpleResp();
      failureResp.setDes("订单状态改变，不能撤单");
      failureResp.setResult(SimpleResp.FAILURE);
      String failureData = new Gson().toJson(failureResp);
      FireMessage messageFailure = new FireMessage();
      messageFailure.setOperateType(FireMessage.CANCEL_ORDER_RESP);
      messageFailure.setData(failureData);
      messageFailure.setUUID(fireMessage.getUUID());
      //发送消息
      try {
        chat.sendMessage(new Gson().toJson(messageFailure));
      } catch (SmackException.NotConnectedException e) {
        e.printStackTrace();
      }
      return;
    }
    //订单锁定 正在在线支付中  不能撤单
    if (orderInfo.getIsLock()){
      SimpleResp failureResp = new SimpleResp();
      failureResp.setDes("订单正在在线支付中，请于收银员核对.");
      failureResp.setResult(SimpleResp.FAILURE);
      String failureData = new Gson().toJson(failureResp);
      FireMessage messageFailure = new FireMessage();
      messageFailure.setOperateType(FireMessage.CANCEL_ORDER_RESP);
      messageFailure.setData(failureData);
      messageFailure.setUUID(fireMessage.getUUID());
      //发送消息
      try {
        chat.sendMessage(new Gson().toJson(messageFailure));
      } catch (SmackException.NotConnectedException e) {
        e.printStackTrace();
      }
      return;
    }
    //生成操作记录
    makeCancelRecord(orderInfo,waiter.getName());
    //撤销桌位单
    if (orderInfo.getOrderInfoType().equals(PxOrderInfo.ORDER_INFO_TYPE_TABLE)){
      revokeTableBill(orderInfo);
      //发送成功消息
      SimpleResp successResp = new SimpleResp();
      successResp.setDes("撤单成功");
      successResp.setResult(SimpleResp.SUCCESS);
      String successData = new Gson().toJson(successResp);
      FireMessage messageSuccess = new FireMessage();
      messageSuccess.setOperateType(FireMessage.CANCEL_ORDER_RESP);
      messageSuccess.setData(successData);
      messageSuccess.setUUID(fireMessage.getUUID());
      //发送消息
      try {
        chat.sendMessage(new Gson().toJson(messageSuccess));
      } catch (SmackException.NotConnectedException e) {
        e.printStackTrace();
      }
      //找单页面接收
      EventBus.getDefault().postSticky(new FindBillRefreshStatusEvent());
      //通知CashMenuFragment和CashBillFragment清空数据
      EventBus.getDefault().post(new RevokeOrFinishBillEvent().setOrderInfo(orderInfo));
    }
  }

  /**
   * 生成撤单记录
   */
  private void makeCancelRecord(PxOrderInfo orderInfo,String waiterName) {
    App app = (App) App.getContext();
    if (app == null) return;
    Office office = DaoServiceUtil.getOfficeService().queryBuilder().unique();
    PxOperationLog operationRecord = new PxOperationLog();
    operationRecord.setCid(office.getObjectId());
    //订单序列号
    operationRecord.setOrderNo(orderInfo.getOrderNo());
    //类型
    operationRecord.setType(PxOperationLog.TYPE_REVOKE);
    //操作人
    operationRecord.setOperater(waiterName);
    //操作时间
    operationRecord.setOperaterDate(new Date().getTime());
    //商品名
    StringBuilder stringBuilder = new StringBuilder();
    //价格
    double price = 0.0;
    List<PxOrderDetails> revokeList = DaoServiceUtil.getOrderDetailsService()
        .queryBuilder()
        .where(PxOrderDetailsDao.Properties.PxOrderInfoId.eq(orderInfo.getId()))
        .where(PxOrderDetailsDao.Properties.OrderStatus.eq(PxOrderDetails.ORDER_STATUS_ORDER))
        .list();
    if (revokeList == null || revokeList.size() == 0) return;
    for (PxOrderDetails details : revokeList) {
      StringBuilder sbDetails = new StringBuilder();
      sbDetails.append(details.getDbProduct().getName());
      if (PxProductInfo.IS_TWO_UNIT_TURE.equals(details.getDbProduct().getMultipleUnit())) {
        sbDetails.append("[" + details.getMultipleUnitNumber() + details.getDbProduct().getUnit() + "]");
      } else {
        sbDetails.append("[" + details.getNum() + details.getDbProduct().getUnit() + "]");
      }
      if (details.getDbFormatInfo() != null) {
        sbDetails.append(details.getDbFormatInfo().getName());
      }
      stringBuilder.append(sbDetails.toString() + " ");
      price += details.getReceivablePrice();
    }
    operationRecord.setProductName(stringBuilder.toString());
    operationRecord.setTotalPrice(price);
    DaoServiceUtil.getOperationRecordService().saveOrUpdate(operationRecord);
  }

  /**
   * 撤销桌位单
   */
  private void revokeTableBill(PxOrderInfo orderInfo) {
    //更改订单信息为撤单
    orderInfo.setStatus(PxOrderInfo.STATUS_CANCEL);
    orderInfo.setEndTime(new Date());
    DaoServiceUtil.getOrderInfoService().update(orderInfo);
    //更改桌台状态
    TableOrderRel unique = DaoServiceUtil.getTableOrderRelService()
        .queryBuilder()
        .where(TableOrderRelDao.Properties.PxOrderInfoId.eq(orderInfo.getId()))
        .unique();
    if (unique != null){
      PxTableInfo dbTable = unique.getDbTable();
      QueryBuilder<TableOrderRel> tableOrderRelQb = DaoServiceUtil.getTableOrderRelService().queryBuilder();
      tableOrderRelQb.where(TableOrderRelDao.Properties.PxTableInfoId.eq(dbTable.getId()));
      Join<TableOrderRel, PxOrderInfo> tableOrderRelJoin = tableOrderRelQb.join(TableOrderRelDao.Properties.PxOrderInfoId, PxOrderInfo.class);
      tableOrderRelJoin.where(PxOrderInfoDao.Properties.Status.eq(PxOrderInfo.STATUS_UNFINISH));
      long count = tableOrderRelQb.count();
      if (count == 0){
        dbTable.setStatus(PxTableInfo.STATUS_EMPTY);
        getTableInfoService().update(dbTable);
      }
    }
    //撤销Details
    revokeDetails(orderInfo);
  }

  /**
   * 撤销Details
   */
  private void revokeDetails(PxOrderInfo orderInfo) {
    //删除未下单的
    List<PxOrderDetails> delList = DaoServiceUtil.getOrderDetailsService()
        .queryBuilder()
        .where(PxOrderDetailsDao.Properties.PxOrderInfoId.eq(orderInfo.getId()))
        .where(PxOrderDetailsDao.Properties.OrderStatus.eq(PxOrderDetails.ORDER_STATUS_UNORDER))
        .list();
    for (PxOrderDetails details:delList){
      PxProductInfo dbProduct = details.getDbProduct();
      //沽清状态
      if (dbProduct.getStatus().equals(PxProductInfo.STATUS_STOP_SALE) || ((dbProduct.getStatus().equals(PxProductInfo.STATUS_ON_SALE)) && dbProduct.getOverPlus() != null && dbProduct.getOverPlus() != 0)) {
        if (dbProduct.getMultipleUnit().equals(PxProductInfo.IS_TWO_UNIT_TURE)) {
          dbProduct.setOverPlus(dbProduct.getOverPlus() + details.getMultipleUnitNumber());
        } else {
          dbProduct.setOverPlus(dbProduct.getOverPlus() + details.getNum());
        }
        dbProduct.setStatus(PxProductInfo.STATUS_ON_SALE);
        DaoServiceUtil.getProductInfoService().saveOrUpdate(dbProduct);
      }
    }
    if (delList != null && delList.size() != 0) {
      DaoServiceUtil.getOrderDetailsService().delete(delList);
    }

    //退菜时间
    Date refundDate = new Date();
    //Map
    SparseArray<PrintDetailsCollect> collectArray = new SparseArray<>();
    //存放打印机IP
    List<Long> printIdList = new ArrayList<>();
    //变更已下单状态
    List<PxOrderDetails> revokeList = DaoServiceUtil.getOrderDetailsService()
        .queryBuilder()
        .where(PxOrderDetailsDao.Properties.PxOrderInfoId.eq(orderInfo.getId()))
        .where(PxOrderDetailsDao.Properties.OrderStatus.eq(PxOrderDetails.ORDER_STATUS_ORDER))
        .list();
    for (PxOrderDetails details : revokeList) {
      details.setOrderStatus(PxOrderDetails.ORDER_STATUS_REFUND);
      //生成PrintDetails
      MakePrintDetails.makePrintDetails(details,refundDate,collectArray,details.getNum(),details.getMultipleUnitNumber(),orderInfo,true,printIdList);
    }
    DaoServiceUtil.getOrderDetailsService().saveOrUpdate(revokeList);
    //后厨打印
    //printCollect(collectArray,true,printIdList);
    PrintTaskManager.printKitchenTask(collectArray,printIdList,true);
  }

  /**
   * 移动桌台信息
   */
  //@formatter:off
  private void operateMoveTable(Chat chat, FireMessage fireMessage) {
    List<PxTableInfo> tableInfoList = getTableInfoService()
        .queryBuilder()
        .where(PxTableInfoDao.Properties.DelFlag.eq("0"))
        .where(PxTableInfoDao.Properties.Status.eq(PxTableInfo.STATUS_EMPTY))
        .orderAsc(PxTableInfoDao.Properties.Type,PxTableInfoDao.Properties.SortNo)
        .list();
    ArrayList<AppMoveTableItem> moveTableItems = new ArrayList<AppMoveTableItem>();
    for (PxTableInfo tableInfo : tableInfoList) {
      AppMoveTableItem moveTableItem = new AppMoveTableItem();
      //占用状态
      if (tableInfo.getStatus().equals(PxTableInfo.STATUS_EMPTY)) {
        moveTableItem.setIsOccupy(false);
      } else {
        moveTableItem.setIsOccupy(true);
      }
      //桌台id
      moveTableItem.setTableId(tableInfo.getObjectId());
      //桌台名称
      String type = tableInfo.getType();
      if (type != null){
      PxTableArea tableArea = DaoServiceUtil.getTableAreaService()
            .queryBuilder()
            .where(PxTableAreaDao.Properties.DelFlag.eq("0"))
            .where(PxTableAreaDao.Properties.Type.eq(type))
            .unique();
        moveTableItem.setTableName((tableArea == null ? "(大厅）": tableArea.getName() +" ") + tableInfo.getName());
      }else{
       moveTableItem.setTableName("(大厅）");
      }
      moveTableItems.add(moveTableItem);
    }
    MoveTableResp moveTableResp = new MoveTableResp();
    moveTableResp.setMoveTableItemList(moveTableItems);
    String data = new Gson().toJson(moveTableResp);
    FireMessage message = new FireMessage();
    message.setOperateType(FireMessage.MOVE_TABLE_RESP);
    message.setData(data);
    message.setUUID(fireMessage.getUUID());
    //发送消息
    try {
      chat.sendMessage(new Gson().toJson(message));
    } catch (SmackException.NotConnectedException e) {
      e.printStackTrace();
    }
    //找单页面接收
    EventBus.getDefault().postSticky(new FindBillRefreshStatusEvent());
  }



  /**
   * 请求改单
   */
  //@formatter:off
  private void operateModifyBill(Chat chat, FireMessage fireMessage) {
    String data = fireMessage.getData();
    ModifyBillReq req = new Gson().fromJson(data, ModifyBillReq.class);
    String tableId = req.getTableId();
    String moveTableId = req.getMoveTableId();
    PxTableInfo tableInfo = getTableInfoService()
        .queryBuilder()
        .where(PxTableInfoDao.Properties.ObjectId.eq(tableId))
        .where(PxTableInfoDao.Properties.DelFlag.eq("0"))
        .unique();
    PxTableInfo moveTableInfo = null;
    if (moveTableId != null) {
      moveTableInfo = getTableInfoService()
          .queryBuilder()
          .where(PxTableInfoDao.Properties.ObjectId.eq(moveTableId))
          .where(PxTableInfoDao.Properties.DelFlag.eq("0"))
          .unique();
    }

    //促销计划
    String promotioInfoId = req.getPromotioInfoId();
    PxPromotioInfo promotioInfo = null;
    if (!TextUtils.isEmpty(promotioInfoId)) {
      promotioInfo = DaoServiceUtil.getPromotionInfoService()
          .queryBuilder()
          .where(PxPromotioInfoDao.Properties.ObjectId.eq(promotioInfoId))
          .unique();
    }

    //有查不到的桌台
    if (tableInfo == null || (moveTableId != null && moveTableInfo == null)) {
      SimpleResp failureResp = new SimpleResp();
      failureResp.setDes("收银端桌台不同步");
      failureResp.setResult(SimpleResp.FAILURE);
      String failureData = new Gson().toJson(failureResp);
      FireMessage messageFailure = new FireMessage();
      messageFailure.setOperateType(FireMessage.MODIFY_BILL_RESP);
      messageFailure.setData(failureData);
      messageFailure.setUUID(fireMessage.getUUID());
      //发送消息
      try {
        chat.sendMessage(new Gson().toJson(messageFailure));
      } catch (SmackException.NotConnectedException e) {
        e.printStackTrace();
      }
      return;
    }
    //订单
    PxOrderInfo orderInfo = findOrder(tableInfo);
    //订单状态锁定 在线支付中
    if (orderInfo  != null && orderInfo.getIsLock()) {
      SimpleResp failureResp = new SimpleResp();
      failureResp.setDes("订单在线支付中，请于收银员核对.");
      failureResp.setResult(SimpleResp.FAILURE);
      String failureData = new Gson().toJson(failureResp);
      FireMessage messageFailure = new FireMessage();
      messageFailure.setOperateType(FireMessage.MODIFY_BILL_RESP);
      messageFailure.setData(failureData);
      messageFailure.setUUID(fireMessage.getUUID());
      //发送消息
      try {
        chat.sendMessage(new Gson().toJson(messageFailure));
      } catch (SmackException.NotConnectedException e) {
        e.printStackTrace();
      }
      return;
    }
    //原桌台闲置
    if (tableInfo.getStatus().equals(PxTableInfo.STATUS_EMPTY)) {
      SimpleResp failureResp = new SimpleResp();
      failureResp.setDes("原桌台处于闲置状态，请刷新桌台");
      failureResp.setResult(SimpleResp.FAILURE);
      String failureData = new Gson().toJson(failureResp);
      FireMessage messageFailure = new FireMessage();
      messageFailure.setOperateType(FireMessage.MODIFY_BILL_RESP);
      messageFailure.setData(failureData);
      messageFailure.setUUID(fireMessage.getUUID());
      //发送消息
      try {
        chat.sendMessage(new Gson().toJson(messageFailure));
      } catch (SmackException.NotConnectedException e) {
        e.printStackTrace();
      }
      return;
    }

    //备注
    String remarks = req.getRemarks();
    if (remarks == null){
      remarks = "";
    }

    //不移动桌台
    if (moveTableId == null && tableInfo != null) {
      long peopleNum = req.getPeopleNum();
      PxOrderInfo currentOrder = orderInfo;
      //修改促销计划
      if (isChangePromotio(currentOrder.getDbPromotioById(), promotioInfo)) {
        //删除不检查 检查促销计划
        if (promotioInfo != null) {
          boolean valid = PromotioDetailsHelp.isValid(promotioInfo);
          if (!valid) {
            SimpleResp failureResp = new SimpleResp();
            failureResp.setDes("促销计划无效,请同步数据!");
            failureResp.setResult(SimpleResp.FAILURE);
            String failureData = new Gson().toJson(failureResp);
            sendMessage(chat, FireMessage.MODIFY_BILL_RESP, failureData, fireMessage.getUUID());
            return;
          }
        }
        changeOrderPromotioInfo(currentOrder, promotioInfo);
        currentOrder.setDbPromotioInfo(promotioInfo);
      }
      currentOrder.setActualPeopleNumber((int) peopleNum);
      currentOrder.setRemarks(remarks);
      DaoServiceUtil.getOrderInfoService().update(currentOrder);
      //通知CashBill刷新
      EventBus.getDefault().post(new RefreshCashBillListEvent());
      //发送消息
      SimpleResp failureResp = new SimpleResp();
      failureResp.setDes("改单成功");
      failureResp.setResult(SimpleResp.SUCCESS);
      String failureData = new Gson().toJson(failureResp);
      FireMessage messageFailure = new FireMessage();
      messageFailure.setOperateType(FireMessage.MODIFY_BILL_RESP);
      messageFailure.setData(failureData);
      messageFailure.setUUID(fireMessage.getUUID());
      //发送消息
      try {
        chat.sendMessage(new Gson().toJson(messageFailure));
      } catch (SmackException.NotConnectedException e) {
        e.printStackTrace();
      }
    }
    //移动桌台
    else if (moveTableId != null && tableId.equals(moveTableId) == false) {
      if (moveTableInfo.getStatus().equals(PxTableInfo.STATUS_OCCUPIED)) {
        SimpleResp failureResp = new SimpleResp();
        failureResp.setDes("目标桌台处于占用状态，请刷新桌台");
        failureResp.setResult(SimpleResp.FAILURE);
        String failureData = new Gson().toJson(failureResp);
        FireMessage messageFailure = new FireMessage();
        messageFailure.setOperateType(FireMessage.MODIFY_BILL_RESP);
        messageFailure.setData(failureData);
        messageFailure.setUUID(fireMessage.getUUID());
        //发送消息
        try {
          chat.sendMessage(new Gson().toJson(messageFailure));
        } catch (SmackException.NotConnectedException e) {
          e.printStackTrace();
        }
        return;
      }

      if (orderInfo == null) {
        SimpleResp failureResp = new SimpleResp();
        failureResp.setDes("该桌台无订单，无法更改，请刷新");
        failureResp.setResult(SimpleResp.FAILURE);
        String failureData = new Gson().toJson(failureResp);
        FireMessage messageFailure = new FireMessage();
        messageFailure.setOperateType(FireMessage.MODIFY_BILL_RESP);
        messageFailure.setData(failureData);
        messageFailure.setUUID(fireMessage.getUUID());
        //发送消息
        try {
          chat.sendMessage(new Gson().toJson(messageFailure));
        } catch (SmackException.NotConnectedException e) {
          e.printStackTrace();
        }
        return;
      }

      //修改促销计划
      if (isChangePromotio(orderInfo.getDbPromotioById(), promotioInfo)) {
        //删除不检查 检查促销计划
        if (promotioInfo != null) {
          boolean valid = PromotioDetailsHelp.isValid(promotioInfo);
          if (!valid) {
            SimpleResp failureResp = new SimpleResp();
            failureResp.setDes("促销计划无效,请同步数据!");
            failureResp.setResult(SimpleResp.FAILURE);
            String failureData = new Gson().toJson(failureResp);
            sendMessage(chat, FireMessage.MODIFY_BILL_RESP, failureData, fireMessage.getUUID());
            return;
          }
        }
        changeOrderPromotioInfo(orderInfo, promotioInfo);
        orderInfo.setDbPromotioInfo(promotioInfo);
      }

      //人数
      orderInfo.setActualPeopleNumber((int) req.getPeopleNum());
      //备注
      orderInfo.setRemarks(remarks);
      DaoServiceUtil.getOrderInfoService().update(orderInfo);
      //恢复旧桌状态
      tableInfo.setStatus(PxTableInfo.STATUS_EMPTY);
      getTableInfoService().saveOrUpdate(tableInfo);
      //更新新桌状态
      moveTableInfo.setStatus(PxTableInfo.STATUS_OCCUPIED);
      getTableInfoService().saveOrUpdate(moveTableInfo);
      //更新桌台和订单rel
      TableOrderRel unique = DaoServiceUtil.getTableOrderRelService().queryBuilder().where(TableOrderRelDao.Properties.PxOrderInfoId.eq(orderInfo.getId())).unique();
      unique.setDbTable(moveTableInfo);
      DaoServiceUtil.getTableOrderRelService().saveOrUpdate(unique);
      //结束当前附加费计算
      if (orderInfo.getDbCurrentExtra() != null) {
        PxTableExtraRel tableExtraRel = DaoServiceUtil.getTableExtraRelService()
            .queryBuilder()
            .where(PxTableExtraRelDao.Properties.PxTableInfoId.eq(tableInfo.getId()))
            .where(PxTableExtraRelDao.Properties.DelFlag.eq("0"))
            .unique();
        if (tableExtraRel != null) {
          PxExtraCharge dbExtraCharge = tableExtraRel.getDbExtraCharge();
          if (dbExtraCharge != null && dbExtraCharge.getDelFlag().equals("0") && dbExtraCharge.getServiceStatus().equals(PxExtraCharge.ENABLE_TRUE) && dbExtraCharge.getServiceCharge() != null) {
            PxExtraDetails dbCurrentExtra = orderInfo.getDbCurrentExtra();
            long minutes = new Date().getTime() / 1000 / 60;
            long startMinutes = dbCurrentExtra.getStartTime().getTime() / 1000 / 60;
            long extraMinutes = minutes - startMinutes;
            //附加费价格
            double currentExtraPrice = Math.ceil((double) extraMinutes / dbExtraCharge.getMinutes() * dbExtraCharge.getServiceCharge().doubleValue());
            //更新价格
            dbCurrentExtra.setPrice(currentExtraPrice);
            //更新时间
            dbCurrentExtra.setStopTime(new Date());
            DaoServiceUtil.getExtraDetailsService().saveOrUpdate(dbCurrentExtra);
          }
        }
      }
      //查询新桌可用的附加费引用关系
      QueryBuilder<PxTableExtraRel> extraRelQb = DaoServiceUtil.getTableExtraRelService()
          .queryBuilder()
          .where(PxTableExtraRelDao.Properties.DelFlag.eq("0"))
          .where(PxTableExtraRelDao.Properties.PxTableInfoId.eq(moveTableInfo.getId()));
      Join<PxTableExtraRel, PxExtraCharge> extraJoin = extraRelQb.join(PxTableExtraRelDao.Properties.PxExtraChargeId, PxExtraCharge.class);
      extraJoin.where(PxExtraChargeDao.Properties.DelFlag.eq("0"));
      PxTableExtraRel rel = extraRelQb.unique();
      //如果新桌有可用附加费
      if (rel != null) {
        //附加费信息
        PxExtraCharge extraCharge = rel.getDbExtraCharge();
        if (extraCharge == null) {
          orderInfo.setDbCurrentExtra(null);
          tableAlteration(orderInfo, tableInfo, moveTableInfo, fireMessage, chat);
          return;
        }
        //新建附加费详情
        PxExtraDetails pxExtraDetails = new PxExtraDetails();
        //开始时间
        pxExtraDetails.setStartTime(new Date());
        //价格
        pxExtraDetails.setPrice((double) 0);
        //订单
        pxExtraDetails.setDbOrder(orderInfo);
        //附加费名
        pxExtraDetails.setExtraName(extraCharge.getName());
        //桌台名
        pxExtraDetails.setTableName(tableInfo.getName());
        //已付款
        pxExtraDetails.setPayPrice((double) 0);
        //已打印
        pxExtraDetails.setIsPrinted(false);
        //储存
        DaoServiceUtil.getExtraDetailsService().save(pxExtraDetails);
        //订单更新当前附加费详情
        orderInfo.setDbCurrentExtra(pxExtraDetails);
        //更新订单
        DaoServiceUtil.getOrderInfoService().saveOrUpdate(orderInfo);
      } else {
        orderInfo.setDbCurrentExtra(null);
        DaoServiceUtil.getOrderInfoService().saveOrUpdate(orderInfo);
      }
      tableAlteration(orderInfo, tableInfo, moveTableInfo, fireMessage, chat);
    }
  }


  /**
   * 桌台变更
   */
  private void tableAlteration(PxOrderInfo orderInfo, PxTableInfo tableInfo, PxTableInfo moveTableInfo, FireMessage fireMessage, Chat chat) {
    //上次换桌时间
    orderInfo.setLastMoveTableTime(new Date());
    //添加桌台变更信息
    PxTableAlteration pxTableAlteration = new PxTableAlteration();
    //操作时间
    pxTableAlteration.setOperateTime(new Date());
    //移动
    pxTableAlteration.setType(PxTableAlteration.TYPE_MOVE);
    //未打印
    pxTableAlteration.setIsPrinted(false);
    //未清空
    pxTableAlteration.setIsClear(false);
    //订单
    pxTableAlteration.setDbOrder(orderInfo);
    //原桌台
    pxTableAlteration.setDbOriginalTable(tableInfo);
    //目标桌台
    pxTableAlteration.setDbTargetTable(moveTableInfo);
    DaoServiceUtil.getTableAlterationService().saveOrUpdate(pxTableAlteration);
    //通知CashBill刷新
    EventBus.getDefault().post(new RefreshCashBillListEvent());
    //找单页面接收
    EventBus.getDefault().postSticky(new FindBillRefreshStatusEvent());
    //回执
    SimpleResp failureResp = new SimpleResp();
    failureResp.setDes("改单成功");
    failureResp.setResult(SimpleResp.SUCCESS);
    String failureData = new Gson().toJson(failureResp);
    FireMessage messageFailure = new FireMessage();
    messageFailure.setOperateType(FireMessage.MODIFY_BILL_RESP);
    messageFailure.setData(failureData);
    messageFailure.setUUID(fireMessage.getUUID());
    //发送消息
    try {
      chat.sendMessage(new Gson().toJson(messageFailure));
    } catch (SmackException.NotConnectedException e) {
      e.printStackTrace();
    }
    //后厨打印移并桌信息
    //printTableAlteration(pxTableAlteration);
    PrintTaskManager.printTableAlteration(pxTableAlteration);
  }



  /**
   * 请求加菜
   */
  //@formatter:off
  private void operateAddProd(Chat chat, FireMessage fireMessage) {
    String data = fireMessage.getData();
    AddProdReq req = new Gson().fromJson(data, AddProdReq.class);
    String tableId = req.getTableId();
    long orderId = req.getOrderId();
    //桌台检测
    PxTableInfo tableInfo = getTableInfoService()
        .queryBuilder()
        .where(PxTableInfoDao.Properties.ObjectId.eq(tableId))
        .where(PxTableInfoDao.Properties.DelFlag.eq("0"))
        .unique();
    if (tableInfo == null || tableInfo.getStatus().equals(PxTableInfo.STATUS_EMPTY)) {
      SimpleResp failureResp = new SimpleResp();
      failureResp.setDes("该桌台已删除或该桌台处于空闲状态");
      failureResp.setResult(SimpleResp.FAILURE);
      String failureData = new Gson().toJson(failureResp);
      FireMessage messageFailure = new FireMessage();
      messageFailure.setOperateType(FireMessage.ADD_PROD_RESP);
      messageFailure.setData(failureData);
      messageFailure.setUUID(fireMessage.getUUID());
      //发送消息
      try {
        chat.sendMessage(new Gson().toJson(messageFailure));
      } catch (SmackException.NotConnectedException e) {
        e.printStackTrace();
      }
      return;
    }
    //订单状态检测
    PxOrderInfo orderInfo = DaoServiceUtil.getOrderInfoService()
        .queryBuilder()
        .where(PxOrderInfoDao.Properties.Id.eq(orderId))
        .unique();
    if (orderInfo == null || orderInfo.getStatus().equals(PxOrderInfo.STATUS_UNFINISH) == false) {
      SimpleResp failureResp = new SimpleResp();
      failureResp.setDes("该订单已完结");
      failureResp.setResult(SimpleResp.FAILURE);
      String failureData = new Gson().toJson(failureResp);
      FireMessage messageFailure = new FireMessage();
      messageFailure.setOperateType(FireMessage.ADD_PROD_RESP);
      messageFailure.setData(failureData);
      messageFailure.setUUID(fireMessage.getUUID());
      //发送消息
      try {
        chat.sendMessage(new Gson().toJson(messageFailure));
      } catch (SmackException.NotConnectedException e) {
        e.printStackTrace();
      }
      return;
    }
    //订单状态锁定 在线支付中
    if (orderInfo.getIsLock()) {
      SimpleResp failureResp = new SimpleResp();
      failureResp.setDes("订单在线支付中，请于收银员核对.");
      failureResp.setResult(SimpleResp.FAILURE);
      String failureData = new Gson().toJson(failureResp);
      FireMessage messageFailure = new FireMessage();
      messageFailure.setOperateType(FireMessage.ADD_PROD_RESP);
      messageFailure.setData(failureData);
      messageFailure.setUUID(fireMessage.getUUID());
      //发送消息
      try {
        chat.sendMessage(new Gson().toJson(messageFailure));
      } catch (SmackException.NotConnectedException e) {
        e.printStackTrace();
      }
      return;
    }
    //订单与桌台匹配检测
    PxOrderInfo order = findOrder(tableInfo);
    if (order.getId() != orderId) {
      SimpleResp failureResp = new SimpleResp();
      failureResp.setDes("订单号与桌台不匹配，该桌台已变更订单");
      failureResp.setResult(SimpleResp.FAILURE);
      String failureData = new Gson().toJson(failureResp);
      FireMessage messageFailure = new FireMessage();
      messageFailure.setOperateType(FireMessage.ADD_PROD_RESP);
      messageFailure.setData(failureData);
      messageFailure.setUUID(fireMessage.getUUID());
      //发送消息
      try {
        chat.sendMessage(new Gson().toJson(messageFailure));
      } catch (SmackException.NotConnectedException e) {
        e.printStackTrace();
      }
      return;
    }

    //商品信息
    List<AppConfirmOrderDetails> confirmOrderDetailsList = req.getConfirmOrderDetailsList();
    for (AppConfirmOrderDetails confirmOrderDetails : confirmOrderDetailsList) {
      //商品id
      String prodId = confirmOrderDetails.getProdId();
      PxProductInfo productInfo = DaoServiceUtil.getProductInfoService()
          .queryBuilder()
          .where(PxProductInfoDao.Properties.ObjectId.eq(prodId))
          .where(PxProductInfoDao.Properties.DelFlag.eq("0"))
          .whereOr(PxProductInfoDao.Properties.Shelf.isNull(), PxProductInfoDao.Properties.Shelf.eq(PxProductInfo.SHELF_PUT_AWAY))
          .unique();

      //商品检测
      if (productInfo == null){
        SimpleResp confirmOrderResp = new SimpleResp();
        confirmOrderResp.setResult(SimpleResp.FAILURE);
        confirmOrderResp.setDes("商品与收银端不同步,请先更新数据");
        String failureData = new Gson().toJson(confirmOrderResp);
        FireMessage messageFailure = new FireMessage();
        messageFailure.setOperateType(FireMessage.CONFIRM_ORDER_RESP);
        messageFailure.setData(failureData);
        messageFailure.setUUID(fireMessage.getUUID());
        //发送消息
        try {
          chat.sendMessage(new Gson().toJson(messageFailure));
        } catch (SmackException.NotConnectedException e) {
          e.printStackTrace();
        }
        return;
      }

      //分类是否存在检测
      PxProductCategory dbCategory = productInfo.getDbCategory();
      if (dbCategory == null || dbCategory.getDelFlag().equals("1") || "1".equals(dbCategory.getShelf())) {
        SimpleResp confirmOrderResp = new SimpleResp();
        confirmOrderResp.setResult(SimpleResp.FAILURE);
        confirmOrderResp.setDes("分类与收银端不同步,请先更新数据");
        String failureData = new Gson().toJson(confirmOrderResp);
        FireMessage messageFailure = new FireMessage();
        messageFailure.setOperateType(FireMessage.ADD_PROD_RESP);
        messageFailure.setData(failureData);
        messageFailure.setUUID(fireMessage.getUUID());
        //发送消息
        try {
          chat.sendMessage(new Gson().toJson(messageFailure));
        } catch (SmackException.NotConnectedException e) {
          e.printStackTrace();
        }
        return;
      }

      //规格id
      String formatId = confirmOrderDetails.getFormatId();
      PxFormatInfo formatInfo = null;
      if (formatId != null) {
        formatInfo = DaoServiceUtil.getFormatInfoService()
            .queryBuilder()
            .where(PxFormatInfoDao.Properties.ObjectId.eq(formatId))
            .where(PxFormatInfoDao.Properties.DelFlag.eq("0"))
            .unique();
      }
      //规格rel
      PxProductFormatRel formatRel = null;
      if (productInfo != null && formatInfo != null) {
        formatRel = DaoServiceUtil.getProductFormatRelService()
            .queryBuilder()
            .where(PxProductFormatRelDao.Properties.PxFormatInfoId.eq(formatInfo.getId()))
            .where(PxProductFormatRelDao.Properties.PxProductInfoId.eq(productInfo.getId()))
            .where(PxProductFormatRelDao.Properties.DelFlag.eq("0"))
            .unique();
      }
      //做法id
      String methodId = confirmOrderDetails.getMethodId();
      PxMethodInfo methodInfo = null;
      if (methodId != null) {
        methodInfo = DaoServiceUtil.getMethodInfoService()
            .queryBuilder()
            .where(PxMethodInfoDao.Properties.ObjectId.eq(methodId))
            .where(PxMethodInfoDao.Properties.DelFlag.eq("0"))
            .unique();
      }
      //做法rel
      PxProductMethodRef methodRef = null;
      if (productInfo != null && methodInfo != null) {
        methodRef = DaoServiceUtil.getProductMethodRelService()
            .queryBuilder()
            .where(PxProductMethodRefDao.Properties.PxMethodInfoId.eq(methodInfo.getId()))
            .where(PxProductMethodRefDao.Properties.PxProductInfoId.eq(productInfo.getId()))
            .where(PxProductMethodRefDao.Properties.DelFlag.eq("0"))
            .unique();
      }

      //查询可用的规格引用关系
      QueryBuilder<PxProductFormatRel> formatRelQb = DaoServiceUtil.getProductFormatRelService()
          .queryBuilder()
          .where(PxProductFormatRelDao.Properties.DelFlag.eq("0"))
          .where(PxProductFormatRelDao.Properties.PxProductInfoId.eq(productInfo.getId()));
      Join<PxProductFormatRel, PxFormatInfo> formatJoin =
          formatRelQb.join(PxProductFormatRelDao.Properties.PxFormatInfoId, PxFormatInfo.class);
      formatJoin.where(PxFormatInfoDao.Properties.DelFlag.eq("0"));
      long countFormat = formatRelQb.count();
      if (formatId == null && countFormat != 0) {
        SimpleResp confirmOrderResp = new SimpleResp();
        confirmOrderResp.setResult(SimpleResp.FAILURE);
        confirmOrderResp.setDes(productInfo.getName() + "已添加规格,请先更新数据");
        String failureData = new Gson().toJson(confirmOrderResp);
        FireMessage messageFailure = new FireMessage();
        messageFailure.setOperateType(FireMessage.CONFIRM_ORDER_RESP);
        messageFailure.setData(failureData);
        messageFailure.setUUID(fireMessage.getUUID());
        //发送消息
        try {
          chat.sendMessage(new Gson().toJson(messageFailure));
        } catch (SmackException.NotConnectedException e) {
          e.printStackTrace();
        }
        return;
      }

      //商品是否存在检测
      if ((formatId != null && (formatInfo == null || formatRel == null)) || (methodId != null && (methodInfo == null || methodRef == null))) {
        SimpleResp confirmOrderResp = new SimpleResp();
        confirmOrderResp.setResult(SimpleResp.FAILURE);
        confirmOrderResp.setDes(productInfo.getName() + "规格或做法与收银端不同步,请先更新数据");
        String failureData = new Gson().toJson(confirmOrderResp);
        FireMessage messageFailure = new FireMessage();
        messageFailure.setOperateType(FireMessage.ADD_PROD_RESP);
        messageFailure.setData(failureData);
        messageFailure.setUUID(fireMessage.getUUID());
        //发送消息
        try {
          chat.sendMessage(new Gson().toJson(messageFailure));
        } catch (SmackException.NotConnectedException e) {
          e.printStackTrace();
        }
        return;
      }
      //商品沽清检测
      if (formatInfo != null) {
        PxProductFormatRel rel = DaoServiceUtil.getProductFormatRelService()
            .queryBuilder()
            .where(PxProductFormatRelDao.Properties.PxProductInfoId.eq(productInfo.getId()))
            .where(PxProductFormatRelDao.Properties.PxFormatInfoId.eq(formatInfo.getId()))
            .unique();
        if (rel != null && PxProductFormatRel.STATUS_STOP_SALE.equals(rel.getStatus())) {
          SimpleResp confirmOrderResp = new SimpleResp();
          confirmOrderResp.setResult(SimpleResp.FAILURE);
          confirmOrderResp.setDes(
              rel.getDbProduct().getName() + rel.getDbFormat().getName() + "已沽清,不能添加");
          String failureData = new Gson().toJson(confirmOrderResp);
          FireMessage messageFailure = new FireMessage();
          messageFailure.setOperateType(FireMessage.ADD_PROD_RESP);
          messageFailure.setData(failureData);
          messageFailure.setUUID(fireMessage.getUUID());
          //发送消息
          try {
            chat.sendMessage(new Gson().toJson(messageFailure));
          } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
          }
          return;
        }
      }
      //商品沽清检测
      if (productInfo.getStatus().equals(PxProductInfo.STATUS_STOP_SALE)) {
        SimpleResp confirmOrderResp = new SimpleResp();
        confirmOrderResp.setResult(SimpleResp.FAILURE);
        confirmOrderResp.setDes(productInfo.getName() + "已沽清,不能添加");
        String failureData = new Gson().toJson(confirmOrderResp);
        FireMessage messageFailure = new FireMessage();
        messageFailure.setOperateType(FireMessage.ADD_PROD_RESP);
        messageFailure.setData(failureData);
        messageFailure.setUUID(fireMessage.getUUID());
        //发送消息
        try {
          chat.sendMessage(new Gson().toJson(messageFailure));
        } catch (SmackException.NotConnectedException e) {
          e.printStackTrace();
        }
        return;
      }

      //规格余量检测
      if (formatInfo != null) {
        PxProductFormatRel rel = DaoServiceUtil.getProductFormatRelService()
            .queryBuilder()
            .where(PxProductFormatRelDao.Properties.PxProductInfoId.eq(productInfo.getId()))
            .where(PxProductFormatRelDao.Properties.PxFormatInfoId.eq(formatInfo.getId()))
            .unique();
        if (PxProductFormatRel.STATUS_ON_SALE.equals(rel.getStatus())) {
          if (productInfo.getMultipleUnit().equals(PxProductInfo.IS_TWO_UNIT_TURE)) {
            if (rel.getStock() != null && rel.getStock() < confirmOrderDetails.getMultNum()) {
              SimpleResp confirmOrderResp = new SimpleResp();
              confirmOrderResp.setResult(SimpleResp.FAILURE);
              confirmOrderResp.setDes(
                  rel.getDbProduct().getName() + rel.getDbFormat().getName() + "剩余数量不足,不能添加");
              String failureData = new Gson().toJson(confirmOrderResp);
              FireMessage messageFailure = new FireMessage();
              messageFailure.setOperateType(FireMessage.ADD_PROD_RESP);
              messageFailure.setData(failureData);
              messageFailure.setUUID(fireMessage.getUUID());
              //发送消息
              try {
                chat.sendMessage(new Gson().toJson(messageFailure));
              } catch (SmackException.NotConnectedException e) {
                e.printStackTrace();
              }
              return;
            }
          } else {
            if (rel.getStock() != null && rel.getStock() < confirmOrderDetails.getNum()) {
              SimpleResp confirmOrderResp = new SimpleResp();
              confirmOrderResp.setResult(SimpleResp.FAILURE);
              confirmOrderResp.setDes(
                  rel.getDbProduct().getName() + rel.getDbFormat().getName() + "剩余数量不足,不能添加");
              String failureData = new Gson().toJson(confirmOrderResp);
              FireMessage messageFailure = new FireMessage();
              messageFailure.setOperateType(FireMessage.ADD_PROD_RESP);
              messageFailure.setData(failureData);
              messageFailure.setUUID(fireMessage.getUUID());
              //发送消息
              try {
                chat.sendMessage(new Gson().toJson(messageFailure));
              } catch (SmackException.NotConnectedException e) {
                e.printStackTrace();
              }
              return;
            }
          }
        }
      } else {
        //商品余量检测
        if (productInfo.getStatus().equals(PxProductInfo.STATUS_ON_SALE)) {
          if (productInfo.getMultipleUnit().equals(PxProductInfo.IS_TWO_UNIT_TURE)
              && productInfo.getOverPlus() != null
              && productInfo.getOverPlus() < confirmOrderDetails.getMultNum()) {
            SimpleResp confirmOrderResp = new SimpleResp();
            confirmOrderResp.setResult(SimpleResp.FAILURE);
            confirmOrderResp.setDes(productInfo.getName() + "剩余数量不足,不能添加");
            String failureData = new Gson().toJson(confirmOrderResp);
            FireMessage messageFailure = new FireMessage();
            messageFailure.setOperateType(FireMessage.ADD_PROD_RESP);
            messageFailure.setData(failureData);
            messageFailure.setUUID(fireMessage.getUUID());
            //发送消息
            try {
              chat.sendMessage(new Gson().toJson(messageFailure));
            } catch (SmackException.NotConnectedException e) {
              e.printStackTrace();
            }
            return;
          } else if (productInfo.getMultipleUnit().equals(PxProductInfo.IS_TWO_UNIT_FALSE)
              && productInfo.getOverPlus() != null
              && productInfo.getOverPlus() < confirmOrderDetails.getNum()) {
            SimpleResp confirmOrderResp = new SimpleResp();
            confirmOrderResp.setResult(SimpleResp.FAILURE);
            confirmOrderResp.setDes(productInfo.getName() + "剩余数量不足,不能添加");
            String failureData = new Gson().toJson(confirmOrderResp);
            FireMessage messageFailure = new FireMessage();
            messageFailure.setOperateType(FireMessage.ADD_PROD_RESP);
            messageFailure.setData(failureData);
            messageFailure.setUUID(fireMessage.getUUID());
            //发送消息
            try {
              chat.sendMessage(new Gson().toJson(messageFailure));
            } catch (SmackException.NotConnectedException e) {
              e.printStackTrace();
            }
            return;
          }
        }
      }

      //修改商品销量
      if (productInfo.getSaleNum() == null) {
        productInfo.setSaleNum(confirmOrderDetails.getNum());
      } else {
        productInfo.setSaleNum(productInfo.getSaleNum() + confirmOrderDetails.getNum());
      }
      DaoServiceUtil.getProductInfoService().saveOrUpdate(productInfo);
    }

    //下单时间
    Date orderTime = new Date();
    //Map
    SparseArray<PrintDetailsCollect> collectArray = new SparseArray<>();
    //存放打印机IP
    List<Long> ipList = new ArrayList<>();
    for (AppConfirmOrderDetails confirmOrderDetails : confirmOrderDetailsList) {
      //商品id
      String prodId = confirmOrderDetails.getProdId();
      PxProductInfo productInfo = DaoServiceUtil.getProductInfoService()
          .queryBuilder()
          .where(PxProductInfoDao.Properties.ObjectId.eq(prodId))
          .unique();
      //规格id
      String formatId = confirmOrderDetails.getFormatId();
      PxFormatInfo formatInfo = null;
      PxProductFormatRel formatRel = null;
      if (formatId != null) {
        formatInfo = DaoServiceUtil.getFormatInfoService()
            .queryBuilder()
            .where(PxFormatInfoDao.Properties.ObjectId.eq(formatId))
            .unique();
        //规格价格
        if (formatInfo != null){
        formatRel = DaoServiceUtil.getProductFormatRelService()
            .queryBuilder()
            .where(PxProductFormatRelDao.Properties.DelFlag.eq("0"))
            .where(PxProductFormatRelDao.Properties.PxFormatInfoId.eq(formatInfo.getId()))
            .where(PxProductFormatRelDao.Properties.PxProductInfoId.eq(productInfo.getId()))
            .unique();
        }
      }
      //做法id
      String methodId = confirmOrderDetails.getMethodId();
      PxMethodInfo methodInfo = null;
      if (methodId != null) {
        methodInfo = DaoServiceUtil.getMethodInfoService()
            .queryBuilder()
            .where(PxMethodInfoDao.Properties.ObjectId.eq(methodId))
            .unique();
      }
      //促销计划
      PxPromotioDetails validPromotioDetails = PromotioDetailsHelp.getValidPromotioDetails(orderInfo.getDbPromotioById(),formatInfo,productInfo);
      double unitPrice  = formatRel == null ? productInfo.getPrice() : formatRel.getPrice();
      double unitVipPrice  = formatRel == null ? productInfo.getVipPrice() : formatRel.getVipPrice();
      if (validPromotioDetails!= null) {
        unitPrice = validPromotioDetails.getPromotionalPrice();
        unitVipPrice= validPromotioDetails.getPromotionalPrice();
      }

      //数量
      int num = confirmOrderDetails.getNum();
      //多单位数量
      double multNum = confirmOrderDetails.getMultNum();
      //延迟
      boolean isDelay = confirmOrderDetails.getIsDelay() == 1;

      //新建Details
      PxOrderDetails details = new PxOrderDetails();
      //默认不清空
      details.setIsClear(false);
      //数量
      details.setNum((double) num);
      //多单位数量
      details.setMultipleUnitNumber((double) multNum);
      //订单
      details.setDbOrder(orderInfo);
      //折扣率
      details.setCurrentDiscRate();
      //单价(是否使用会员价)
      details.setUnitPrice(unitPrice);
      details.setUnitVipPrice(unitVipPrice);
      //价格
      if (productInfo.getMultipleUnit().equals(PxProductInfo.IS_TWO_UNIT_TURE)) {
        details.setPrice(details.getUnitPrice() * details.getMultipleUnitNumber());
        details.setVipPrice(details.getUnitVipPrice() * details.getMultipleUnitNumber());
      } else {
        details.setPrice(details.getUnitPrice() * details.getNum());
        details.setVipPrice(details.getUnitVipPrice() * details.getNum());
      }
      //商品
      details.setDbProduct(productInfo);
      //下单状态
      details.setOrderStatus(PxOrderDetails.ORDER_STATUS_ORDER);
      //商品状态
      if (isDelay) {
        details.setStatus(PxOrderDetails.STATUS_DELAY);
      } else {
        details.setStatus(PxOrderDetails.STATUS_ORIDINARY);
      }
      //已上菜
      details.setIsServing(false);
      //折扣率
      details.setCurrentDiscRate();
      //规格信息
      details.setDbFormatInfo(formatInfo);
      //做法
      details.setDbMethodInfo(methodInfo);
      //套餐details
      details.setIsComboDetails(PxOrderDetails.IS_COMBO_FALSE);
      //套餐类details
      details.setInCombo(PxOrderDetails.IN_COMBO_FALSE);
      //赠品
      details.setIsGift(PxOrderDetails.GIFT_FALSE);
      //备注
      if (confirmOrderDetails.getRemarks() == null || confirmOrderDetails.getRemarks().equals("")) {
        details.setRemarks("");
      } else {
        details.setRemarks(confirmOrderDetails.getRemarks());
      }
      //未上菜
      details.setIsServing(false);
      //objId
      details.setObjectId(UUID.randomUUID().toString().replace("-","") + System.currentTimeMillis());
      //储存
      DaoServiceUtil.getOrderDetailsService().save(details);

      //生成printDetails
      MakePrintDetails.makePrintDetails(details, orderTime, collectArray, details.getNum(), details.getMultipleUnitNumber(), orderInfo, false, ipList);

      //修改剩余数量
      if (formatInfo != null) {
        PxProductFormatRel productFormatRel = DaoServiceUtil.getProductFormatRelService()
            .queryBuilder()
            .where(PxProductFormatRelDao.Properties.PxProductInfoId.eq(productInfo.getId()))
            .where(PxProductFormatRelDao.Properties.PxFormatInfoId.eq(formatInfo.getId()))
            .unique();
        if (productFormatRel != null) {
          if (PxProductFormatRel.STATUS_ON_SALE.equals(productFormatRel.getStatus())) {
            if (productInfo.getMultipleUnit().equals(PxProductInfo.IS_TWO_UNIT_TURE)) {
              if (productFormatRel.getStock() != null) {
                productFormatRel.setStock(productFormatRel.getStock() - multNum);
              } else {
                productFormatRel.setStock(null);
              }
            } else {
              if (productFormatRel.getStock() != null) {
                productFormatRel.setStock(productFormatRel.getStock() - num);
              } else {
                productFormatRel.setStock(null);
              }
            }
            if (productFormatRel.getStock() != null && productFormatRel.getStock() == 0.0) {
              productFormatRel.setStatus(PxProductFormatRel.STATUS_STOP_SALE);
            }
            //储存
            DaoServiceUtil.getProductFormatRelService().saveOrUpdate(productFormatRel);
          }
        }
      } else {
        if (productInfo.getStatus().equals(PxProductInfo.STATUS_ON_SALE)
            && productInfo.getOverPlus() != null && productInfo.getOverPlus() != 0) {
          if (productInfo.getMultipleUnit().equals(PxProductInfo.IS_TWO_UNIT_TURE)) {
            productInfo.setOverPlus(productInfo.getOverPlus() - multNum);
          } else {
            productInfo.setOverPlus(productInfo.getOverPlus() - num);
          }
          if (productInfo.getOverPlus() == 0) {
            productInfo.setStatus(PxProductInfo.STATUS_STOP_SALE);
          }
          DaoServiceUtil.getProductInfoService().saveOrUpdate(productInfo);
        }
      }
    }
    //回执
    SimpleResp failureResp = new SimpleResp();
    failureResp.setDes("加菜成功");
    failureResp.setResult(SimpleResp.SUCCESS);
    String failureData = new Gson().toJson(failureResp);
    FireMessage messageFailure = new FireMessage();
    messageFailure.setOperateType(FireMessage.ADD_PROD_RESP);
    messageFailure.setData(failureData);
    messageFailure.setUUID(fireMessage.getUUID());
    //发送消息
    try {
      chat.sendMessage(new Gson().toJson(messageFailure));
    } catch (SmackException.NotConnectedException e) {
      e.printStackTrace();
    }
    //由客单页面接收
    EventBus.getDefault().post(new RefreshCashBillListEvent());
    //由菜单列表接收,更新菜品状态
    EventBus.getDefault().post(new UpdateProdInfoListStatusEvent().setFromWaiter(true));

    //后厨打印
    //printCollect(collectArray, false, ipList);
    PrintTaskManager.printKitchenTask(collectArray,ipList,false);
  }

  /**
   * 退菜
   */
  //@formatter:off
  private void operateRefundProd(Chat chat, FireMessage fireMessage) {
    String data = fireMessage.getData();
    RefundProdReq req = new Gson().fromJson(data, RefundProdReq.class);
    //服务生
    String waiterId = req.getWaiterId();
    User waiter = DaoServiceUtil.getUserService()
        .queryBuilder()
        .where(UserDao.Properties.DelFlag.eq("0"))
        .where(UserDao.Properties.ObjectId.eq(waiterId))
        .unique();
    if (waiter == null) {
      SimpleResp failureResp = new SimpleResp();
      failureResp.setDes("收银端无此服务生，请同步数据");
      failureResp.setResult(SimpleResp.FAILURE);
      String failureData = new Gson().toJson(failureResp);
      FireMessage messageFailure = new FireMessage();
      messageFailure.setOperateType(FireMessage.REFUND_PROD_RESP);
      messageFailure.setData(failureData);
      messageFailure.setUUID(fireMessage.getUUID());
      //发送消息
      try {
        chat.sendMessage(new Gson().toJson(messageFailure));
      } catch (SmackException.NotConnectedException e) {
        e.printStackTrace();
      }
      return;
    }
    //退菜权限
    String canRetreat = waiter.getCanRetreat();
    if (canRetreat != null && canRetreat.equals(User.CAN_NOT_RETREAT)) {
      SimpleResp failureResp = new SimpleResp();
      failureResp.setDes("此服务生无退菜权限");
      failureResp.setResult(SimpleResp.FAILURE);
      String failureData = new Gson().toJson(failureResp);
      FireMessage messageFailure = new FireMessage();
      messageFailure.setOperateType(FireMessage.REFUND_PROD_RESP);
      messageFailure.setData(failureData);
      messageFailure.setUUID(fireMessage.getUUID());
      //发送消息
      try {
        chat.sendMessage(new Gson().toJson(messageFailure));
      } catch (SmackException.NotConnectedException e) {
        e.printStackTrace();
      }
      return;
    }
    //订单
    long orderId = req.getOrderId();
    PxOrderInfo orderInfo = DaoServiceUtil.getOrderInfoService()
        .queryBuilder()
        .where(PxOrderInfoDao.Properties.Id.eq(orderId))
        .unique();
    if (orderInfo == null || orderInfo.getStatus().equals(PxOrderInfo.STATUS_UNFINISH) == false) {
      SimpleResp failureResp = new SimpleResp();
      failureResp.setDes("该订单已完结");
      failureResp.setResult(SimpleResp.FAILURE);
      String failureData = new Gson().toJson(failureResp);
      FireMessage messageFailure = new FireMessage();
      messageFailure.setOperateType(FireMessage.REFUND_PROD_RESP);
      messageFailure.setData(failureData);
      messageFailure.setUUID(fireMessage.getUUID());
      //发送消息
      try {
        chat.sendMessage(new Gson().toJson(messageFailure));
      } catch (SmackException.NotConnectedException e) {
        e.printStackTrace();
      }
      return;
    }
    //订单状态锁定 在线支付中
    if (orderInfo.getIsLock()) {
      SimpleResp failureResp = new SimpleResp();
      failureResp.setDes("订单在线支付中，请于收银员核对.");
      failureResp.setResult(SimpleResp.FAILURE);
      String failureData = new Gson().toJson(failureResp);
      FireMessage messageFailure = new FireMessage();
      messageFailure.setOperateType(FireMessage.REFUND_PROD_RESP);
      messageFailure.setData(failureData);
      messageFailure.setUUID(fireMessage.getUUID());
      //发送消息
      try {
        chat.sendMessage(new Gson().toJson(messageFailure));
      } catch (SmackException.NotConnectedException e) {
        e.printStackTrace();
      }
      return;
    }
    //桌台
    String tableId = req.getTableId();
    PxTableInfo tableInfo = getTableInfoService()
        .queryBuilder()
        .where(PxTableInfoDao.Properties.ObjectId.eq(tableId))
        .where(PxTableInfoDao.Properties.DelFlag.eq("0"))
        .unique();
    if (tableInfo == null) {
      SimpleResp failureResp = new SimpleResp();
      failureResp.setDes("此桌台不可用");
      failureResp.setResult(SimpleResp.FAILURE);
      String failureData = new Gson().toJson(failureResp);
      FireMessage messageFailure = new FireMessage();
      messageFailure.setOperateType(FireMessage.REFUND_PROD_RESP);
      messageFailure.setData(failureData);
      messageFailure.setUUID(fireMessage.getUUID());
      //发送消息
      try {
        chat.sendMessage(new Gson().toJson(messageFailure));
      } catch (SmackException.NotConnectedException e) {
        e.printStackTrace();
      }
      return;
    }
    TableOrderRel unique = DaoServiceUtil
        .getTableOrderRelService()
        .queryBuilder()
        .where(TableOrderRelDao.Properties.PxOrderInfoId.eq(orderInfo.getId()))
        .unique();
    if (tableId.equals(unique.getDbTable().getObjectId()) == false) {
      SimpleResp failureResp = new SimpleResp();
      failureResp.setDes("此订单已切换桌台，退菜失败");
      failureResp.setResult(SimpleResp.FAILURE);
      String failureData = new Gson().toJson(failureResp);
      FireMessage messageFailure = new FireMessage();
      messageFailure.setOperateType(FireMessage.REFUND_PROD_RESP);
      messageFailure.setData(failureData);
      messageFailure.setUUID(fireMessage.getUUID());
      //发送消息
      try {
        chat.sendMessage(new Gson().toJson(messageFailure));
      } catch (SmackException.NotConnectedException e) {
        e.printStackTrace();
      }
      return;
    }

    //Details校验
    if (req.getObjId() == null){
      SimpleResp failureResp = new SimpleResp();
      failureResp.setDes("单据id为空，请升级版本");
      failureResp.setResult(SimpleResp.FAILURE);
      String failureData = new Gson().toJson(failureResp);
      FireMessage messageFailure = new FireMessage();
      messageFailure.setOperateType(FireMessage.REFUND_PROD_RESP);
      messageFailure.setData(failureData);
      messageFailure.setUUID(fireMessage.getUUID());
      //发送消息
      try {
        chat.sendMessage(new Gson().toJson(messageFailure));
      } catch (SmackException.NotConnectedException e) {
        e.printStackTrace();
      }
      return;
    }
    PxOrderDetails details = DaoServiceUtil.getOrderDetailsService().queryBuilder().where(PxOrderDetailsDao.Properties.ObjectId.eq(req.getObjId())).unique();
    if (details == null){
      SimpleResp failureResp = new SimpleResp();
      failureResp.setDes("不存在该单据");
      failureResp.setResult(SimpleResp.FAILURE);
      String failureData = new Gson().toJson(failureResp);
      FireMessage messageFailure = new FireMessage();
      messageFailure.setOperateType(FireMessage.REFUND_PROD_RESP);
      messageFailure.setData(failureData);
      messageFailure.setUUID(fireMessage.getUUID());
      //发送消息
      try {
        chat.sendMessage(new Gson().toJson(messageFailure));
      } catch (SmackException.NotConnectedException e) {
        e.printStackTrace();
      }
      return;
    }
    if (details.getOrderStatus().equals(PxOrderDetails.ORDER_STATUS_REFUND)){
      SimpleResp failureResp = new SimpleResp();
      failureResp.setDes("所选单据已退单，请刷新");
      failureResp.setResult(SimpleResp.FAILURE);
      String failureData = new Gson().toJson(failureResp);
      FireMessage messageFailure = new FireMessage();
      messageFailure.setOperateType(FireMessage.REFUND_PROD_RESP);
      messageFailure.setData(failureData);
      messageFailure.setUUID(fireMessage.getUUID());
      //发送消息
      try {
        chat.sendMessage(new Gson().toJson(messageFailure));
      } catch (SmackException.NotConnectedException e) {
        e.printStackTrace();
      }
      return;
    }
    if (req.getRefundNum() > details.getNum() || req.getRefundMultipleNum() > details.getMultipleUnitNumber()){
      SimpleResp failureResp = new SimpleResp();
      failureResp.setDes("所退数量超过已有数量，请重试");
      failureResp.setResult(SimpleResp.FAILURE);
      String failureData = new Gson().toJson(failureResp);
      FireMessage messageFailure = new FireMessage();
      messageFailure.setOperateType(FireMessage.REFUND_PROD_RESP);
      messageFailure.setData(failureData);
      messageFailure.setUUID(fireMessage.getUUID());
      //发送消息
      try {
        chat.sendMessage(new Gson().toJson(messageFailure));
      } catch (SmackException.NotConnectedException e) {
        e.printStackTrace();
      }
      return;
    }
    //退菜原因
    PxOptReason refundReason = null;
    if (req.getRefundReasonObjId() != null){
      refundReason = DaoServiceUtil.getOptReasonService()
        .queryBuilder()
        .where(PxOptReasonDao.Properties.ObjectId.eq(req.getRefundReasonObjId()))
        .unique();
    }
    //退菜时间
    Date refundDate = new Date();
    //Map
    SparseArray<PrintDetailsCollect> collectArray = new SparseArray<>();
    List<Long> printIdList = new ArrayList<>();

    //生成PrintDetails
    details.setPrintProd(details.getDbProduct());
    details.setPrintOrder(details.getDbOrder());
    details.setPrintReason(details.getDbReason());
    details.setPrintFormat(details.getDbFormatInfo());
    details.setPrintMethod(details.getDbMethodInfo());
    //PrintDetails
    MakePrintDetails.makeNotMergePrintDetails(details, refundDate, collectArray, (double) req.getRefundNum(), req.getRefundMultipleNum(), printIdList);
    //生成操作记录
    makeRefundRecord(details, refundDate, (int) req.getRefundNum(), req.getRefundMultipleNum(), refundReason, waiter.getName());
    //数量
    details.setNum(details.getNum() - req.getRefundNum());
    details.setMultipleUnitNumber(details.getMultipleUnitNumber() - req.getRefundMultipleNum());
    if (details.getNum().doubleValue() == 0 && details.getMultipleUnitNumber().doubleValue() == 0) {
      details.setOrderStatus(PxOrderDetails.ORDER_STATUS_REFUND);
    }
    //退货数量
    if (details.getRefundNum() == null) {
      details.setRefundNum((double)req.getRefundNum());
    } else {
      details.setRefundNum(details.getRefundNum() + req.getRefundNum());
    }
    if (details.getRefundMultNum() == null) {
      details.setRefundMultNum(req.getRefundMultipleNum());
    } else {
      details.setRefundMultNum(details.getRefundMultNum() + req.getRefundMultipleNum());
    }
    //价格
    if (details.getDbProduct().getMultipleUnit().equals(PxProductInfo.IS_TWO_UNIT_TURE)) {
      details.setPrice(details.getUnitPrice() * details.getMultipleUnitNumber());
      details.setVipPrice(details.getUnitVipPrice() * details.getMultipleUnitNumber());
    } else {
      details.setPrice(details.getUnitPrice() * details.getNum());
      details.setVipPrice(details.getUnitVipPrice() * details.getNum());
    }

    //存储
    DaoServiceUtil.getOrderDetailsService().saveOrUpdate(details);
    //后厨打印
    //printCollect(collectArray, true, printIdList);
    PrintTaskManager.printKitchenTask(collectArray,printIdList,true);

    //刷新数据
    EventBus.getDefault().post(new RefreshCashBillListEvent());
    //回执
    SimpleResp failureResp = new SimpleResp();
    failureResp.setDes("退菜成功");
    failureResp.setResult(SimpleResp.SUCCESS);
    String failureData = new Gson().toJson(failureResp);
    FireMessage messageFailure = new FireMessage();
    messageFailure.setOperateType(FireMessage.REFUND_PROD_RESP);
    messageFailure.setData(failureData);
    messageFailure.setUUID(fireMessage.getUUID());
    //发送消息
    try {
      chat.sendMessage(new Gson().toJson(messageFailure));
    } catch (SmackException.NotConnectedException e) {
      e.printStackTrace();
    }
  }


  /**
   * 生成退菜记录
   */
  //@formatter:on
  private void makeRefundRecord(PxOrderDetails details, Date refundDate, Integer num,
      Double multNum, PxOptReason reason, String waiterName) {
    App app = (App) App.getContext();
    if (app == null) return;
    Office office = DaoServiceUtil.getOfficeService().queryBuilder().unique();
    PxOperationLog operationRecord = new PxOperationLog();
    operationRecord.setCid(office.getObjectId());
    //操作日期
    operationRecord.setOperaterDate(refundDate.getTime());
    //操作人员
    operationRecord.setOperater(waiterName);
    //订单序列号
    operationRecord.setOrderNo(details.getDbOrder().getOrderNo());
    //类型
    operationRecord.setType(PxOperationLog.TYPE_REFUND);
    //名称
    StringBuilder sb = new StringBuilder();
    sb.append(details.getDbProduct().getName());
    if (PxProductInfo.IS_TWO_UNIT_TURE.equals(details.getDbProduct().getMultipleUnit())) {
      sb.append("[" + multNum + details.getDbProduct().getUnit() + "]");
    } else {
      sb.append("[" + num + details.getDbProduct().getUnit() + "]");
    }
    if (details.getDbFormatInfo() != null) {
      sb.append(details.getDbFormatInfo().getName());
    }
    operationRecord.setProductName(sb.toString());
    //操作缘由
    if (reason != null) {
      operationRecord.setRemarks(reason.getName());
    }
    //双单位 价格
    if (details.getDbProduct().getMultipleUnit().equals(PxProductInfo.IS_TWO_UNIT_TURE)) {
      if (PxOrderInfo.USE_VIP_CARD_TRUE.equals(details.getDbOrder().getUseVipCard())) {
        operationRecord.setTotalPrice(details.getUnitVipPrice() * multNum);
      } else {
        operationRecord.setTotalPrice(details.getUnitPrice() * multNum);
      }
    }
    //非双单位 价格
    else {
      if (PxOrderInfo.USE_VIP_CARD_TRUE.equals(details.getDbOrder().getUseVipCard())) {
        operationRecord.setTotalPrice(details.getUnitVipPrice() * num);
      } else {
        operationRecord.setTotalPrice(details.getUnitPrice() * num);
      }
    }
    DaoServiceUtil.getOperationRecordService().saveOrUpdate(operationRecord);
  }

  /**
   * 划菜 标记已上/未上
   */
  private void operateServingTag(Chat chat, FireMessage fireMessage) {
    String data = fireMessage.getData();
    ServingProdReq req = new Gson().fromJson(data, ServingProdReq.class);
    long orderId = req.getOrderId();
    PxOrderInfo orderInfo = DaoServiceUtil.getOrderInfoService()
        .queryBuilder()
        .where(PxOrderInfoDao.Properties.Id.eq(orderId))
        .unique();
    if (orderInfo == null || orderInfo.getStatus().equals(PxOrderInfo.STATUS_UNFINISH) == false) {
      SimpleResp failureResp = new SimpleResp();
      failureResp.setDes("该订单已完结");
      failureResp.setResult(SimpleResp.FAILURE);
      String failureData = new Gson().toJson(failureResp);
      FireMessage messageFailure = new FireMessage();
      messageFailure.setOperateType(FireMessage.SERVING_PROD_RESP);
      messageFailure.setData(failureData);
      messageFailure.setUUID(fireMessage.getUUID());
      //发送消息
      try {
        chat.sendMessage(new Gson().toJson(messageFailure));
      } catch (SmackException.NotConnectedException e) {
        e.printStackTrace();
      }
      return;
    }
    //订单状态锁定 在线支付中
    if (orderInfo.getIsLock()) {
      SimpleResp failureResp = new SimpleResp();
      failureResp.setDes("订单在线支付中，请于收银员核对");
      failureResp.setResult(SimpleResp.FAILURE);
      String failureData = new Gson().toJson(failureResp);
      FireMessage messageFailure = new FireMessage();
      messageFailure.setOperateType(FireMessage.SERVING_PROD_RESP);
      messageFailure.setData(failureData);
      messageFailure.setUUID(fireMessage.getUUID());
      //发送消息
      try {
        chat.sendMessage(new Gson().toJson(messageFailure));
      } catch (SmackException.NotConnectedException e) {
        e.printStackTrace();
      }
      return;
    }
    String tableId = req.getTableId();
    PxTableInfo tableInfo = getTableInfoService().queryBuilder()
        .where(PxTableInfoDao.Properties.ObjectId.eq(tableId))
        .where(PxTableInfoDao.Properties.DelFlag.eq("0"))
        .unique();
    if (tableInfo == null) {
      SimpleResp failureResp = new SimpleResp();
      failureResp.setDes("此桌台不可用");
      failureResp.setResult(SimpleResp.FAILURE);
      String failureData = new Gson().toJson(failureResp);
      FireMessage messageFailure = new FireMessage();
      messageFailure.setOperateType(FireMessage.SERVING_PROD_RESP);
      messageFailure.setData(failureData);
      messageFailure.setUUID(fireMessage.getUUID());
      //发送消息
      try {
        chat.sendMessage(new Gson().toJson(messageFailure));
      } catch (SmackException.NotConnectedException e) {
        e.printStackTrace();
      }
      return;
    }
    TableOrderRel unique = DaoServiceUtil.getTableOrderRelService()
        .queryBuilder()
        .where(TableOrderRelDao.Properties.PxOrderInfoId.eq(orderInfo.getId()))
        .unique();
    if (tableId.equals(unique.getDbTable().getObjectId()) == false) {
      SimpleResp failureResp = new SimpleResp();
      failureResp.setDes("此订单已切换桌台，标记失败");
      failureResp.setResult(SimpleResp.FAILURE);
      String failureData = new Gson().toJson(failureResp);
      FireMessage messageFailure = new FireMessage();
      messageFailure.setOperateType(FireMessage.SERVING_PROD_RESP);
      messageFailure.setData(failureData);
      messageFailure.setUUID(fireMessage.getUUID());
      //发送消息
      try {
        chat.sendMessage(new Gson().toJson(messageFailure));
      } catch (SmackException.NotConnectedException e) {
        e.printStackTrace();
      }
      return;
    }

    if (req.getObjId() == null) {
      SimpleResp failureResp = new SimpleResp();
      failureResp.setDes("单据id为空，请升级");
      failureResp.setResult(SimpleResp.FAILURE);
      String failureData = new Gson().toJson(failureResp);
      FireMessage messageFailure = new FireMessage();
      messageFailure.setOperateType(FireMessage.SERVING_PROD_RESP);
      messageFailure.setData(failureData);
      messageFailure.setUUID(fireMessage.getUUID());
      //发送消息
      try {
        chat.sendMessage(new Gson().toJson(messageFailure));
      } catch (SmackException.NotConnectedException e) {
        e.printStackTrace();
      }
      return;
    }
    PxOrderDetails orderDetails = DaoServiceUtil.getOrderDetailsService()
        .queryBuilder()
        .where(PxOrderDetailsDao.Properties.ObjectId.eq(req.getObjId()))
        .unique();
    if (orderDetails.getStatus().equals(PxOrderDetails.ORDER_STATUS_REFUND)) {
      SimpleResp failureResp = new SimpleResp();
      failureResp.setDes("此商品已退单");
      failureResp.setResult(SimpleResp.FAILURE);
      String failureData = new Gson().toJson(failureResp);
      FireMessage messageFailure = new FireMessage();
      messageFailure.setOperateType(FireMessage.SERVING_PROD_RESP);
      messageFailure.setData(failureData);
      messageFailure.setUUID(fireMessage.getUUID());
      //发送消息
      try {
        chat.sendMessage(new Gson().toJson(messageFailure));
      } catch (SmackException.NotConnectedException e) {
        e.printStackTrace();
      }
      return;
    }

    //成功
    orderDetails.setIsServing(req.getIsServing() == 1);
    DaoServiceUtil.getOrderDetailsService().saveOrUpdate(orderDetails);
    //回执
    SimpleResp successResp = new SimpleResp();
    successResp.setDes("标记成功");
    successResp.setResult(SimpleResp.SUCCESS);
    String successData = new Gson().toJson(successResp);
    FireMessage messageSuccess = new FireMessage();
    messageSuccess.setOperateType(FireMessage.SERVING_PROD_RESP);
    messageSuccess.setData(successData);
    messageSuccess.setUUID(fireMessage.getUUID());
    //发送消息
    try {
      chat.sendMessage(new Gson().toJson(messageSuccess));
    } catch (SmackException.NotConnectedException e) {
      e.printStackTrace();
    }
    //更新页面
    EventBus.getDefault().post(new RefreshCashBillListEvent());
  }

  /**
   * UserTableRel 的所有空桌台
   */
  //@formatter:on
  private void operateAllEmptyTable(Chat chat, FireMessage fireMessage) {
    Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
    String data = fireMessage.getData();
    AllTableReq req = gson.fromJson(data, AllTableReq.class);
    //模糊查询
    String like = req.getLike();
    List<PxTableInfo> tableInfoList = null;
    if (like == null || like.trim().isEmpty()) {
      tableInfoList = DaoServiceUtil.getTableInfoService()
          .queryBuilder()
          .where(PxTableInfoDao.Properties.DelFlag.eq("0"))
          .where(PxTableInfoDao.Properties.Status.eq(PxTableInfo.STATUS_EMPTY))
          .list();
    } else {
      tableInfoList = DaoServiceUtil.getTableInfoService()
          .queryBuilder()
          .where(PxTableInfoDao.Properties.DelFlag.eq("0"))
          .where(PxTableInfoDao.Properties.Status.eq(PxTableInfo.STATUS_EMPTY))
          .where(PxTableInfoDao.Properties.Name.like("%" + like + "%"))
          .list();
    }
    AllTableResp resp = new AllTableResp(tableInfoList);
    sendMessage(chat, FireMessage.ALL_EMPTY_TABLE_RESP, gson.toJson(resp), fireMessage.getUUID());
  }

  /**
   * UserTableRel 的所有已占用桌台
   */
  private void operateAllOccupiedTable(Chat chat, FireMessage fireMessage) {
    Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
    String data = fireMessage.getData();
    UUID uuid = fireMessage.getUUID();
    AllTableReq req = gson.fromJson(data, AllTableReq.class);
    String like = req.getLike();
    //获取桌台 状态list
    List<PxTableInfo> tableInfoList = addOrderToOccupiedTableList(like);
    AllTableResp allTableResp = new AllTableResp(tableInfoList);

    sendMessage(chat, FireMessage.ALL_OCCUPIED_TABLE_RESP, gson.toJson(allTableResp), uuid);
  }

  /**
   * send FireMessage
   */
  private void sendMessage(Chat chat, int type, String data, UUID uuid) {
    FireMessage fireMessage = new FireMessage(type, data, uuid);
    sendMessage(chat, fireMessage);
  }

  private void sendMessage(Chat chat, FireMessage fireMessage) {
    try {
      chat.sendMessage(new Gson().toJson(fireMessage));
    } catch (SmackException.NotConnectedException e) {
      Logger.e(e.toString());
      e.printStackTrace();
    }
  }


  /**
   * 修改订单的促销计划信息
   */
  private void changeOrderPromotioInfo(PxOrderInfo orderInfo, PxPromotioInfo promotioInfo) {
    List<PxOrderDetails> dbOrderDetailsList = DaoServiceUtil.getOrderDetailsService()
        .queryBuilder()
        .where(PxOrderDetailsDao.Properties.PxOrderInfoId.eq(orderInfo.getId()))
        .list();
    for (PxOrderDetails details : dbOrderDetailsList) {
      PxProductInfo dbProduct = details.getDbProduct();
      //双单位
      boolean isTwoUnit = dbProduct.getMultipleUnit().equals(PxProductInfo.IS_TWO_UNIT_TURE);
      PxPromotioDetails validPromotioDetails =
          PromotioDetailsHelp.getValidPromotioDetails(promotioInfo, details.getDbFormatInfo(),
              details.getDbProduct());
      Pair<Double, Double> pricePair = getOrderDetailsUnitPrice(details, validPromotioDetails);
      //
      details.setUnitPrice(pricePair.first);
      details.setUnitVipPrice(pricePair.second);
      //
      details.setPrice(details.getUnitPrice() * (isTwoUnit ? details.getMultipleUnitNumber()
          : details.getNum()));
      details.setVipPrice(details.getUnitVipPrice() * (isTwoUnit ? details.getMultipleUnitNumber()
          : details.getNum()));
    }
    DaoServiceUtil.getOrderDetailsService().update(dbOrderDetailsList);
  }

  /**
   * 获取该订单详情 UnitPrice + UnitVipPrice
   */
  private Pair<Double, Double> getOrderDetailsUnitPrice(PxOrderDetails orderDetails,
      PxPromotioDetails promotioDetails) {
    if (promotioDetails != null) {
      return new Pair<>(promotioDetails.getPromotionalPrice(),
          promotioDetails.getPromotionalPrice());
    }
    PxFormatInfo formatInfo = orderDetails.getDbFormatInfo();
    PxProductInfo dbProduct = orderDetails.getDbProduct();
    //没规格
    if (formatInfo == null) return new Pair<>(dbProduct.getPrice(), dbProduct.getVipPrice());

    WhereCondition formatCondition = PxProductFormatRelDao.Properties.PxFormatInfoId.isNull();
    if (formatInfo != null) {
      formatCondition = PxProductFormatRelDao.Properties.PxFormatInfoId.eq(formatInfo.getId());
    }
    //规格价格
    PxProductFormatRel formatRel = DaoServiceUtil.getProductFormatRelService()
        .queryBuilder()
        .where(PxProductFormatRelDao.Properties.DelFlag.eq("0"))
        .where(formatCondition)
        .where(PxProductFormatRelDao.Properties.PxProductInfoId.eq(dbProduct.getId()))
        .unique();
    if (formatRel != null) return new Pair<>(formatRel.getPrice(), formatRel.getVipPrice());
    return new Pair<>(dbProduct.getPrice(), dbProduct.getVipPrice());
  }

  /**
   * 促销计划 是否发生变化
   *
   * @return false 没发生变化
   */
  private boolean isChangePromotio(PxPromotioInfo prePromotioInfo,
      PxPromotioInfo currentPromotioInfo) {
    if (prePromotioInfo != null) {
      if (currentPromotioInfo == null) {
        return true;
      } else {
        return !prePromotioInfo.getObjectId().equals(currentPromotioInfo.getObjectId());
      }
    } else {
      return currentPromotioInfo != null;
    }
  }
}