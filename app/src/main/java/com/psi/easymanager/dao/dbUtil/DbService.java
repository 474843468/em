package com.psi.easymanager.dao.dbUtil;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.query.Join;
import de.greenrobot.dao.query.QueryBuilder;
import de.greenrobot.dao.query.WhereCondition;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 * Created by zjq on 2016/3/31.
 * DAO基础泛型
 */
public class DbService<T, K> {
  private AbstractDao<T, K> mDao;

  public DbService(AbstractDao dao) {
    mDao = dao;
  }

  public void save(T item) {
    mDao.insert(item);
  }

  public void save(T... items) {
    mDao.insertInTx(items);
  }

  public void save(List<T> items) {
    mDao.insertInTx(items);
  }

  public void saveOrUpdate(T item) {
    mDao.insertOrReplace(item);
  }

  public void saveOrUpdate(T... items) {
    mDao.insertOrReplaceInTx(items);
  }

  public void saveOrUpdate(List<T> items) {
    mDao.insertOrReplaceInTx(items);
  }

  public void deleteByKey(K key) {
    mDao.deleteByKey(key);
  }

  public void delete(T item) {
    mDao.delete(item);
  }

  public void delete(T... items) {
    mDao.deleteInTx(items);
  }

  public void delete(List<T> items) {
    mDao.deleteInTx(items);
  }

  public void deleteAll() {
    mDao.deleteAll();
  }

  public void update(T item) {
    mDao.update(item);
  }

  public void update(T... items) {
    mDao.updateInTx(items);
  }

  public void update(List<T> items) {
    mDao.updateInTx(items);
  }

  public T query(K key) {
    return mDao.load(key);
  }

  public List<T> queryAll() {
    return mDao.loadAll();
  }

  public List<T> query(String where, String... params) {

    return mDao.queryRaw(where, params);
  }

  public QueryBuilder<T> queryBuilder() {

    return mDao.queryBuilder();
  }

  public long count() {
    return mDao.count();
  }

  public void refresh(T item) {
    mDao.refresh(item);
  }

  public void detach(T item) {
    mDao.detach(item);
  }

  //
  public List<WhereCondition> createConditions(HashMap<Property, Object> map) {
    Set<Property> keySet = map.keySet();
    List<WhereCondition> conditionList = new ArrayList<>();
    for (Property property : keySet) {
      conditionList.add(property.eq(map.get(property)));
    }
    return conditionList;
  }

  public void joinConditionList(Join join, List<WhereCondition> conditionList) {
    for (WhereCondition condition : conditionList) {
      join.where(condition);
    }
  }

  public QueryBuilder<T> whereConditionList(List<WhereCondition> conditionList) {
    QueryBuilder<T> queryBuilder = queryBuilder();
    for (WhereCondition condition : conditionList) {
      queryBuilder.where(condition);
    }
    return queryBuilder;
  }

  public QueryBuilder<T> whereConditionMap(HashMap<Property, Object> map) {
    List<WhereCondition> conditionList = createConditions(map);
    QueryBuilder<T> queryBuilder = queryBuilder();
    for (WhereCondition condition : conditionList) {
      queryBuilder.where(condition);
    }
    return queryBuilder;
  }

  public QueryBuilder<T> whereOrCondition(QueryBuilder<T> qb, WhereCondition condition1,
      WhereCondition condition2, WhereCondition... conditions) {
    qb.whereOr(condition1, condition2, conditions);
    return qb;
  }
}