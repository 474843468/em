package com.psi.easymanager.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

/**
 *
 Scrollview 嵌套 RecyclerView 及在Android 5.1版本滑动时 惯性消失问题


 scrollview 嵌套recyclerview 时，recyclerview不显示，这就需要我们自己计算recyclerview的高度，比如：

 ViewGroup.LayoutParams mParams = recyclerView.getLayoutParams();
 mParams.height = (CommonUtils.getScreenWidthPX(getActivity()) * 480 / 720 + CommonUtils.dipToPixels(40)) * num +
 CommonUtils.dipToPixels(8);
 mParams.width = CommonUtils.getScreenWidthPX(getActivity());
 recyclerView.setLayoutParams(mParams);
 这中方法适合item高度比较好计算的情形，但要遇到里面的item高度不一定这就需要我们重写recyclerview的高度了，
 以前嵌套listview的时候我们只需重写listview 然后重写

 @Override
 protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
 // TODO Auto-generated method stub
 int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
 MeasureSpec.AT_MOST);
 super.onMeasure(widthMeasureSpec, expandSpec);
 }
 但是这种方法在recyclerview重写不管用。
 我们此时要重写的的是LinearLayoutManager或GridLayoutManager
 */
public class NestGridView extends GridView {
  public NestGridView(Context context) {
    super(context);
  }

  public NestGridView(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public NestGridView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
  }

  @Override protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,MeasureSpec.AT_MOST);
    super.onMeasure(widthMeasureSpec, expandSpec);
  }
}
