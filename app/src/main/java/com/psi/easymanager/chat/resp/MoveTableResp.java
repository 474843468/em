package com.psi.easymanager.chat.resp;

import com.psi.easymanager.chat.AppMoveTableItem;
import java.io.Serializable;
import java.util.List;

/**
 * Created by zjq on 2016/5/9.
 */
public class MoveTableResp implements Serializable {
  private List<AppMoveTableItem> mMoveTableItemList;

  public List<AppMoveTableItem> getMoveTableItemList() {
    return mMoveTableItemList;
  }

  public void setMoveTableItemList(List<AppMoveTableItem> moveTableItemList) {
    mMoveTableItemList = moveTableItemList;
  }
}
