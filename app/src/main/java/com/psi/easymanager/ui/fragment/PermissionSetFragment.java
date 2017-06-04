package com.psi.easymanager.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.psi.easymanager.R;
import com.psi.easymanager.ui.activity.MoreActivity;

import butterknife.ButterKnife;

/**
 * Created by zjq on 2016/3/17.
 * 更多-权限配置
 */
public class PermissionSetFragment extends BaseFragment {
  private static final String PERMISSION_SET_FRAGMENT_PARAM = "param";
  private String mParam;//MainActivity参数
  private MoreActivity mAct;

  public static PermissionSetFragment newInstance(String mParam) {
    Bundle bundle = new Bundle();
    bundle.putString("param", mParam);
    PermissionSetFragment fragment = new PermissionSetFragment();
    fragment.setArguments(bundle);
    return fragment;
  }

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    if (getArguments() != null) {
      mParam = getArguments().getString(PERMISSION_SET_FRAGMENT_PARAM);
    }
    mAct = (MoreActivity) getActivity();
  }

  @Nullable @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_permission_set, null);
    ButterKnife.bind(this, view);
    return view;
  }

  @Override public void onActivityCreated(@Nullable Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
  }

  /**
   * 重置注入
   */
  @Override public void onDestroyView() {
    super.onDestroyView();
    ButterKnife.unbind(this);
  }
}
