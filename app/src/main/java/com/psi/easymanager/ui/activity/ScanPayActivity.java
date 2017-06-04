package com.psi.easymanager.ui.activity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.google.zxing.Result;
import com.google.zxing.client.result.AddressBookParsedResult;
import com.google.zxing.client.result.ISBNParsedResult;
import com.google.zxing.client.result.ParsedResult;
import com.google.zxing.client.result.ParsedResultType;
import com.google.zxing.client.result.ProductParsedResult;
import com.google.zxing.client.result.TextParsedResult;
import com.google.zxing.client.result.URIParsedResult;
import com.mylhyl.zxing.scanner.OnScannerCompletionListener;
import com.mylhyl.zxing.scanner.ScannerView;
import com.orhanobut.logger.Logger;
import com.psi.easymanager.R;
import com.psi.easymanager.event.ScanCodeEvent;
import com.psi.easymanager.utils.ToastUtils;
import org.greenrobot.eventbus.EventBus;

public class ScanPayActivity extends BaseActivity implements OnScannerCompletionListener {

  @Bind(R.id.scanner_view) ScannerView mScannerView;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    ButterKnife.bind(this);
    init();
  }

  @Override protected void onResume() {
    super.onResume();
    mScannerView.onResume();
  }

  @Override protected void onPause() {
    mScannerView.onPause();
    super.onPause();
  }

  @Override protected void onDestroy() {
    super.onDestroy();
    ButterKnife.unbind(this);
  }

  @Override protected int provideContentViewId() {
    return R.layout.activity_scan_pay;
  }

  private void init() {
    mScannerView.setOnScannerCompletionListener(this);
    mScannerView.setMediaResId(R.raw.beep);//设置扫描成功的声音

    //        mScannerView.setLaserFrameTopMargin(100);//扫描框与屏幕上方距离
    //        mScannerView.setLaserFrameSize(200, 200);//扫描框大小
    //        mScannerView.setLaserFrameCornerLength(25);//设置4角长度
    //        mScannerView.setLaserLineHeight(5);//设置扫描线高度
    mScannerView.setLaserLineResId(R.mipmap.ic_scan_line);//线图
    //switch (0) {
    //  case 0:
    //    mScannerView.setLaserLineResId(R.mipmap.ic_scan_line);//线图
    //    break;
    //  case 2:
    //    //mScannerView.setLaserGridLineResId(R.mipmap.zfb_grid_scan_line);//网格图
    //    mScannerView.setLaserFrameBoundColor(0xFF26CEFF);//支付宝颜色
    //    break;
    //  case 3:
    //    mScannerView.setLaserColor(Color.RED);
    //    break;
    //}
  }

  @Override
  public void OnScannerCompletion(Result rawResult, ParsedResult parsedResult, Bitmap barcode) {
    if (rawResult == null) {
      ToastUtils.showShort(null, "未发现二维码");
      finish();
      return;
    }
    ParsedResultType type = parsedResult.getType();
    Logger.i("ParsedResultType: " + type);
    switch (type) {
      case ADDRESSBOOK:
        AddressBookParsedResult addressBook = (AddressBookParsedResult) parsedResult;
        break;
      case PRODUCT:
        ProductParsedResult product = (ProductParsedResult) parsedResult;
        Logger.i("productID: " + product.getProductID());
        break;
      case ISBN:
        ISBNParsedResult isbn = (ISBNParsedResult) parsedResult;
        Logger.i("isbn: " + isbn.getISBN());
        break;
      case URI:
        URIParsedResult uri = (URIParsedResult) parsedResult;
        Logger.i("uri: " + uri.getURI());
        break;
      case TEXT:
        TextParsedResult textParsedResult = (TextParsedResult) parsedResult;
        String text = textParsedResult.getText();
        Logger.i(":" + text);
        if (TextUtils.isEmpty(text) || text.length() < 11) {
          ToastUtils.showShort(null, "请扫描正确的二维码!");
        }
        EventBus.getDefault().post(new ScanCodeEvent(text));
        ScanPayActivity.this.finish();
        break;
      case GEO:
        break;
      case TEL:
        break;
      case SMS:
        break;
    }
  }
}
