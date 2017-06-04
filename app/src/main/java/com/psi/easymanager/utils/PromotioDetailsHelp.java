package com.psi.easymanager.utils;

import com.psi.easymanager.dao.PxPromotioDetailsDao;
import com.psi.easymanager.dao.PxPromotioInfoDao;
import com.psi.easymanager.dao.dbUtil.DaoServiceUtil;
import com.psi.easymanager.module.PxFormatInfo;
import com.psi.easymanager.module.PxProductInfo;
import com.psi.easymanager.module.PxPromotioDetails;
import com.psi.easymanager.module.PxPromotioInfo;
import de.greenrobot.dao.query.WhereCondition;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * User: ylw
 * Date: 2017-02-09
 * Time: 12:00
 * 获取促销计划
 */
public class PromotioDetailsHelp {
  //@formatter:off
  /**
   * 获取促销计划详情
   */
  public static PxPromotioDetails getValidPromotioDetails(PxPromotioInfo promotioInfo, PxFormatInfo formatInfo, PxProductInfo productInfo) {
    if (promotioInfo == null) return null;
    WhereCondition formatCondition = PxPromotioDetailsDao.Properties.PxFormatId.isNull();
    if (formatInfo != null) {
      formatCondition = PxPromotioDetailsDao.Properties.PxFormatId.eq(formatInfo.getId());
    }
    PxPromotioDetails promotioDetails = DaoServiceUtil.getPromotionDetailsService()
        .queryBuilder()
        .where(PxPromotioDetailsDao.Properties.DelFlag.eq("0"))
        .where(PxPromotioDetailsDao.Properties.PxProductInfoId.eq(productInfo.getId()))
        .where(PxPromotioDetailsDao.Properties.PxPromotioInfoId.eq(promotioInfo.getId()))
        .where(formatCondition)
        .unique();
    return promotioDetails;
  }

  /**
   * 获取 当前时间有效促销计划
   */
   //@formatter:off
  public static List<PxPromotioInfo> getPromotioInfoList() {
    //指定日期可以有开始没结束或有结束没开始
    //指定时间的不行
    List<PxPromotioInfo> allList = new ArrayList<>();

    Date currentDate = new Date();
    String currnetWeek = TimeUtils.getWeekOfDate(currentDate);
    //1.长期有效
    List<PxPromotioInfo> longList = DaoServiceUtil.getPromotionInfoService()
        .queryBuilder()
        .where(PxPromotioInfoDao.Properties.DelFlag.eq("0"))
        .where(PxPromotioInfoDao.Properties.Type.eq(PxPromotioInfo.TYPE_LONG_TIME))
        .list();
    if (!longList.isEmpty()) {
      allList.addAll(longList);
    }

    //指定

    //2.不定日期不定时间定周几
    List<PxPromotioInfo> noDateNoTime = DaoServiceUtil.getPromotionInfoService()
        .queryBuilder()
        .where(PxPromotioInfoDao.Properties.DelFlag.eq("0"))
        .where(PxPromotioInfoDao.Properties.Type.eq(PxPromotioInfo.TYPE_APPOINT_TIME))
        .where(PxPromotioInfoDao.Properties.StartDate.isNull())
        .where(PxPromotioInfoDao.Properties.EndDate.isNull())
        .whereOr(PxPromotioInfoDao.Properties.StartTime.isNull(), PxPromotioInfoDao.Properties.StartTime.eq(""))
        .whereOr(PxPromotioInfoDao.Properties.EndTime.isNull(), PxPromotioInfoDao.Properties.EndTime.eq(""))
        .where(PxPromotioInfoDao.Properties.Weekly.like("%" + currnetWeek + "%"))
        .list();

    if (!noDateNoTime.isEmpty()) {
      allList.addAll(noDateNoTime);
    }
    //3.不定日期不定时间不定周几 TODO 不存在

    //4.不定日期定时间不定周几 TODO 时间过滤
    //5.不定日期定时间定周几  TODO 时间过滤
    List<PxPromotioInfo> noDateHasTime = DaoServiceUtil.getPromotionInfoService()
        .queryBuilder()
        .where(PxPromotioInfoDao.Properties.DelFlag.eq("0"))
        .where(PxPromotioInfoDao.Properties.Type.eq(PxPromotioInfo.TYPE_APPOINT_TIME))
        .where(PxPromotioInfoDao.Properties.StartDate.isNull())
        .where(PxPromotioInfoDao.Properties.EndDate.isNull())
        .whereOr(PxPromotioInfoDao.Properties.StartTime.isNotNull(),PxPromotioInfoDao.Properties.StartTime.notEq(""))
        .whereOr(PxPromotioInfoDao.Properties.EndTime.isNotNull(),PxPromotioInfoDao.Properties.EndTime.notEq(""))
        .whereOr(PxPromotioInfoDao.Properties.Weekly.isNull(),
            PxPromotioInfoDao.Properties.Weekly.eq(""),
            PxPromotioInfoDao.Properties.Weekly.like("%" + currnetWeek + "%"))
        .list();



    //6.定日期定时间定不定周几 TODO 时间过滤
    //7.定日期定时间定周几 TODO 时间过滤 没开始日期有结束
    List<PxPromotioInfo> noStartHasEndHasTime = DaoServiceUtil.getPromotionInfoService()
        .queryBuilder()
        .where(PxPromotioInfoDao.Properties.DelFlag.eq("0"))
        .where(PxPromotioInfoDao.Properties.Type.eq(PxPromotioInfo.TYPE_APPOINT_TIME))
        .where(PxPromotioInfoDao.Properties.StartDate.isNull())
        .where(PxPromotioInfoDao.Properties.EndDate.ge(currentDate))
        .whereOr(PxPromotioInfoDao.Properties.StartTime.isNotNull(),PxPromotioInfoDao.Properties.StartTime.notEq(""))
        .whereOr(PxPromotioInfoDao.Properties.EndTime.isNotNull(),PxPromotioInfoDao.Properties.EndTime.notEq(""))
        .whereOr(PxPromotioInfoDao.Properties.Weekly.isNull(),
            PxPromotioInfoDao.Properties.Weekly.eq(""),
            PxPromotioInfoDao.Properties.Weekly.like("%" + currnetWeek + "%"))
        .list();
    //日期 有开始 没结束
   List<PxPromotioInfo> hasStartNoEndHasTime = DaoServiceUtil.getPromotionInfoService()
        .queryBuilder()
        .where(PxPromotioInfoDao.Properties.DelFlag.eq("0"))
        .where(PxPromotioInfoDao.Properties.Type.eq(PxPromotioInfo.TYPE_APPOINT_TIME))
        .where(PxPromotioInfoDao.Properties.StartDate.le(currentDate))
        .where(PxPromotioInfoDao.Properties.EndDate.isNull())
        .whereOr(PxPromotioInfoDao.Properties.StartTime.isNotNull(),PxPromotioInfoDao.Properties.StartTime.notEq(""))
        .whereOr(PxPromotioInfoDao.Properties.EndTime.isNotNull(),PxPromotioInfoDao.Properties.EndTime.notEq(""))
        .whereOr(PxPromotioInfoDao.Properties.Weekly.isNull(),
            PxPromotioInfoDao.Properties.Weekly.eq(""),
            PxPromotioInfoDao.Properties.Weekly.like("%" + currnetWeek + "%"))
        .list();
    //日期 有开始 有结束
    List<PxPromotioInfo> hasStartHasEndHasTime = DaoServiceUtil.getPromotionInfoService()
        .queryBuilder()
        .where(PxPromotioInfoDao.Properties.DelFlag.eq("0"))
        .where(PxPromotioInfoDao.Properties.Type.eq(PxPromotioInfo.TYPE_APPOINT_TIME))
        .where(PxPromotioInfoDao.Properties.StartDate.le(currentDate))
        .where(PxPromotioInfoDao.Properties.EndDate.ge(currentDate))
        .whereOr(PxPromotioInfoDao.Properties.StartTime.isNotNull(),PxPromotioInfoDao.Properties.StartTime.notEq(""))
        .whereOr(PxPromotioInfoDao.Properties.EndTime.isNotNull(),PxPromotioInfoDao.Properties.EndTime.notEq(""))
        .whereOr(PxPromotioInfoDao.Properties.Weekly.isNull(),
            PxPromotioInfoDao.Properties.Weekly.eq(""),
            PxPromotioInfoDao.Properties.Weekly.like("%" + currnetWeek + "%"))
        .list();

    List<PxPromotioInfo> filterTime = new ArrayList<>();
    filterTime.addAll(noDateHasTime);
    filterTime.addAll(noStartHasEndHasTime);
    filterTime.addAll(hasStartNoEndHasTime);
    filterTime.addAll(hasStartHasEndHasTime);

    Calendar calendar = Calendar.getInstance();
    int hour = calendar.get(Calendar.HOUR_OF_DAY);//获取小时
    int mine = calendar.get(Calendar.MINUTE);//获取分钟
    int second = calendar.get(Calendar.SECOND);
    int allCurrentTime = hour * 3600 + mine * 60 + second;

    for (PxPromotioInfo info : filterTime) {
      String startTime = info.getStartTime();
      String endTime = info.getEndTime();
      if (startTime == null || endTime == null || endTime.trim().isEmpty() || startTime.trim().isEmpty()) {
        continue;
      }
      String[] start = startTime.split(":");
      String[] end = endTime.split(":");

      int startHour = Integer.parseInt(start[0]);
      int endHour = Integer.parseInt(end[0]);
      int startMine = Integer.parseInt(start[1]);
      int endMine = Integer.parseInt(end[1]);
      int startSecond = Integer.parseInt(start[2]);
      int endSecond = Integer.parseInt(end[2]);

      int allStartTime = startHour * 3600 + startMine * 60 + startSecond;
      int allEndTime = endHour * 3600 + endMine * 60 + endSecond;

      if (allCurrentTime >= allStartTime && allCurrentTime <= allEndTime){
        allList.add(info);
      }
    }

    //8.定日期不定时间不定周几
    //9.定日期不定时间定周几  没开始有结束
    List<PxPromotioInfo> noStartHasEndNoTime = DaoServiceUtil.getPromotionInfoService()
        .queryBuilder()
        .where(PxPromotioInfoDao.Properties.DelFlag.eq("0"))
        .where(PxPromotioInfoDao.Properties.Type.eq(PxPromotioInfo.TYPE_APPOINT_TIME))
        .where(PxPromotioInfoDao.Properties.StartDate.isNull())
        .where(PxPromotioInfoDao.Properties.EndDate.ge(currentDate))
        .whereOr(PxPromotioInfoDao.Properties.StartTime.isNull(), PxPromotioInfoDao.Properties.StartTime.eq(""))
        .whereOr(PxPromotioInfoDao.Properties.EndTime.isNull(), PxPromotioInfoDao.Properties.EndTime.eq(""))
        .whereOr(PxPromotioInfoDao.Properties.Weekly.isNull(),
            PxPromotioInfoDao.Properties.Weekly.eq(""),
            PxPromotioInfoDao.Properties.Weekly.like("%" + currnetWeek + "%"))
        .list();

    if (!noStartHasEndNoTime.isEmpty()) {
      allList.addAll(noStartHasEndNoTime);
    }
    //日期 有开始没结束
     List<PxPromotioInfo> hasStartNoEndNoTime = DaoServiceUtil.getPromotionInfoService()
        .queryBuilder()
        .where(PxPromotioInfoDao.Properties.DelFlag.eq("0"))
        .where(PxPromotioInfoDao.Properties.Type.eq(PxPromotioInfo.TYPE_APPOINT_TIME))
        .where(PxPromotioInfoDao.Properties.StartDate.le(currentDate))
        .where(PxPromotioInfoDao.Properties.EndDate.ge(currentDate))
        .whereOr(PxPromotioInfoDao.Properties.StartTime.isNull(), PxPromotioInfoDao.Properties.StartTime.eq(""))
        .whereOr(PxPromotioInfoDao.Properties.EndTime.isNull(), PxPromotioInfoDao.Properties.EndTime.eq(""))
        .whereOr(PxPromotioInfoDao.Properties.Weekly.isNull(),
            PxPromotioInfoDao.Properties.Weekly.eq(""),
            PxPromotioInfoDao.Properties.Weekly.like("%" + currnetWeek + "%"))
        .list();

    if (!hasStartNoEndNoTime.isEmpty()){
      allList.addAll(hasStartNoEndNoTime);
    }
    //有开始有结束
     List<PxPromotioInfo> hasStartHasEndNoTime = DaoServiceUtil.getPromotionInfoService()
        .queryBuilder()
        .where(PxPromotioInfoDao.Properties.DelFlag.eq("0"))
        .where(PxPromotioInfoDao.Properties.Type.eq(PxPromotioInfo.TYPE_APPOINT_TIME))
        .where(PxPromotioInfoDao.Properties.StartDate.le(currentDate))
        .where(PxPromotioInfoDao.Properties.EndDate.isNull())
        .whereOr(PxPromotioInfoDao.Properties.StartTime.isNull(), PxPromotioInfoDao.Properties.StartTime.eq(""))
        .whereOr(PxPromotioInfoDao.Properties.EndTime.isNull(), PxPromotioInfoDao.Properties.EndTime.eq(""))
        .whereOr(PxPromotioInfoDao.Properties.Weekly.isNull(),
            PxPromotioInfoDao.Properties.Weekly.eq(""),
            PxPromotioInfoDao.Properties.Weekly.like("%" + currnetWeek + "%"))
        .list();
    if (!hasStartHasEndNoTime.isEmpty()){
      allList.addAll(hasStartHasEndNoTime);
    }
    return allList;
  }

  /**
   * 判断促销计划是否有效
   */

  public static boolean isValid(PxPromotioInfo promotioInfo) {
    if (promotioInfo == null || !promotioInfo.getDelFlag().equals("0")) return false;
    //1.长期直接有效
    if (promotioInfo.getType().equals(PxPromotioInfo.TYPE_LONG_TIME)) return true;
    Date startDate = promotioInfo.getStartDate();
    Date endDate = promotioInfo.getEndDate();


    String startTime = promotioInfo.getStartTime();
    String endTime = promotioInfo.getEndTime();
    String weekly = promotioInfo.getWeekly();

    Date currentDate = new Date();
    long cutrrentLong = currentDate.getTime();
    String currnetWeek = TimeUtils.getWeekOfDate(currentDate);

    Calendar calendar = Calendar.getInstance();
    int hour = calendar.get(Calendar.HOUR_OF_DAY);//获取小时
    int mine = calendar.get(Calendar.MINUTE);//获取分钟
    int second = calendar.get(Calendar.SECOND);
    int allCurrentTime = hour * 3600 + mine * 60 + second;

    //2.不定日期  不定时间定周几
    //3.不定日期  不定时间不定周几 TODO 不存在

    //4.不定日期  定时间不定周几 TODO 时间过滤
    //5.不定日期  定时间定周几  TODO 时间过滤
    //@formatter:off
    if (startDate == null && endDate == null) { //不定日期
      if (startTime == null || startTime.trim().isEmpty() || endTime == null || endTime.trim().isEmpty()) { // 不定时间 定周几或不定周几
          return weekly == null || weekly.trim().isEmpty() || weekly.contains(currnetWeek);
      } else {//定时间 定周几或不定周几
         //@formatter:on
        String[] start = startTime.split(":");
        String[] end = endTime.split(":");

        int startHour = Integer.parseInt(start[0]);
        int endHour = Integer.parseInt(end[0]);
        int startMine = Integer.parseInt(start[1]);
        int endMine = Integer.parseInt(end[1]);
        int startSecond = Integer.parseInt(start[2]);
        int endSecond = Integer.parseInt(end[2]);

        int allStartTime = startHour * 3600 + startMine * 60 + startSecond;
        int allEndTime = endHour * 3600 + endMine * 60 + endSecond;

        if (allCurrentTime >= allStartTime && allCurrentTime <= allEndTime) {
          return weekly == null || weekly.trim().isEmpty() || weekly.contains(currnetWeek);
        }
      }
    } else {  //定日期
      //6.定日期定时间定不定周几 TODO 时间过滤
      //7.定日期定时间定周几 TODO 时间过滤
      //8.定日期不定时间不定周几
      //9.定日期不定时间定周几
      long startLong = startDate == null ? 0l : startDate.getTime();
      long endLong = endDate == null ? Long.MAX_VALUE : endDate.getTime();
      if (cutrrentLong >= startLong && cutrrentLong <= endLong) { // 日期范围内
        if (startTime == null || endTime == null || endTime.trim().isEmpty() || startTime.trim()
            .isEmpty()) { // 不定时间 定周几或不定周几
          return weekly == null || weekly.trim().isEmpty() || weekly.contains(currnetWeek);
        } else {//定时间 定周几或不定周几

          String[] start = startTime.split(":");
          String[] end = endTime.split(":");

          int startHour = Integer.parseInt(start[0]);
          int endHour = Integer.parseInt(end[0]);
          int startMine = Integer.parseInt(start[1]);
          int endMine = Integer.parseInt(end[1]);
          int startSecond = Integer.parseInt(start[2]);
          int endSecond = Integer.parseInt(end[2]);

          int allStartTime = startHour * 3600 + startMine * 60 + startSecond;
          int allEndTime = endHour * 3600 + endMine * 60 + endSecond;
          if (allCurrentTime >= allStartTime && allCurrentTime <= allEndTime) {
            return weekly == null || weekly.trim().isEmpty() || weekly.contains(currnetWeek);
          }
        }
      }
    }
    return false;
  }
}