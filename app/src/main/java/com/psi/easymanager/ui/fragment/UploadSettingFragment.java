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
 * 更多-上传设置数据
 */
public class UploadSettingFragment extends BaseFragment {
  private static final String UPLOAD_SETTING_FRAGMENT_PARAM = "param";
  private String mParam;//MainActivity参数
  private MoreActivity mAct;

  public static UploadSettingFragment newInstance(String mParam) {
    Bundle bundle = new Bundle();
    bundle.putString("param", mParam);
    UploadSettingFragment fragment = new UploadSettingFragment();
    fragment.setArguments(bundle);
    return fragment;
  }

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    if (getArguments() != null) {
      mParam = getArguments().getString(UPLOAD_SETTING_FRAGMENT_PARAM);
    }
    mAct = (MoreActivity) getActivity();
  }

  @Nullable @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_upload_setting, null);
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
