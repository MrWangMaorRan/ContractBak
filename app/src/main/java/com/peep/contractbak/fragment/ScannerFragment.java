package com.peep.contractbak.fragment;

import android.app.AlertDialog;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.zxing.Result;
import com.google.zxing.client.result.ParsedResult;
import com.mylhyl.zxing.scanner.OnScannerCompletionListener;
import com.mylhyl.zxing.scanner.ScannerView;
import com.mylhyl.zxing.scanner.common.Scanner;
import com.peep.contractbak.R;
import com.peep.contractbak.activity.ConnectActivity;
import com.peep.contractbak.utils.ConstantUtils;

/**
 * 扫描
 */
public class ScannerFragment extends Fragment implements OnScannerCompletionListener {

    private ConnectActivity connectActivity;
    public static final String EXTRA_SCAN_FULL_SCREEN = "EXTRA_SCAN_FULL_SCREEN";
    public static final String EXTRA_HIDE_LASER_FRAME = "EXTRA_HIDE_LASER_FRAME";

    public static final int EXTRA_LASER_LINE_MODE_0 = 0;
    public static final int EXTRA_LASER_LINE_MODE_1 = 1;
    public static final int EXTRA_LASER_LINE_MODE_2 = 2;

    public static final int EXTRA_SCAN_MODE_0 = 0;
    public static final int EXTRA_SCAN_MODE_1 = 1;
    public static final int EXTRA_SCAN_MODE_2 = 2;

    public static final int APPLY_READ_EXTERNAL_STORAGE = 0x111;

    private ScannerView mScannerView;
    private Result mLastResult;
    private boolean showThumbnail;
    private View baseView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        baseView = inflater.inflate(R.layout.fragment_scanner,null);
        return baseView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        this.connectActivity = (ConnectActivity) getActivity();
        initBase();
    }

    public void initBase() {
        init();
        mScannerView = (ScannerView) baseView.findViewById(R.id.scanner_view);
        mScannerView.setOnScannerCompletionListener(this);
        mScannerView.setMediaResId(R.raw.beep);//设置扫描成功的声音
        mScannerView.setDrawText(getResources().getString(R.string.Scan_QR_code), true);
        mScannerView.setDrawTextColor(Color.GRAY);
        //二维码
        mScannerView.setScanMode(Scanner.ScanMode.QR_CODE_MODE);

        //显示扫描成功后的缩略图
        mScannerView.isShowResThumbnail(showThumbnail);
//        mScannerView.isScanInvert(true);//扫描反色二维码
//        mScannerView.setCameraFacing(CameraFacing.FRONT);
//        mScannerView.setLaserMoveSpeed(1);//速度

//        mScannerView.setLaserFrameTopMargin(100);//扫描框与屏幕上方距离
//        mScannerView.setLaserFrameSize(400, 400);//扫描框大小
//        mScannerView.setLaserFrameCornerLength(25);//设置4角长度
//        mScannerView.setLaserLineHeight(5);//设置扫描线高度
//        mScannerView.setLaserFrameCornerWidth(5);
        mScannerView.setLaserLineResId(R.mipmap.wx_scan_line);//线图
    }
    private void init(){
        baseView.findViewById(R.id.topbar_leftbtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               connectActivity.changeFragment(0);
               connectActivity.removeDisconnect();
            }
        });
        //((TextView)baseView.findViewById(R.id.topbar_title)).setText(getResources().getString(R.string.saoyisao));
        baseView.findViewById(R.id.newsphone_download).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                final AlertDialog dialog = builder
                        .setView(R.layout.download_erweima) //自定义的布局文件
                        .create();
                dialog.setCanceledOnTouchOutside(true);
                dialog.getWindow().setBackgroundDrawableResource(R.color.touming);
                dialog.show();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == APPLY_READ_EXTERNAL_STORAGE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                PickPictureTotalActivity.gotoActivity(ScannerActivity.this);
            } else {
                Toast.makeText(connectActivity, "请给予权限", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if(!hidden){
            mScannerView.onResume();
            resetStatusView();
        }
    }

    @Override
    public void onResume() {
        mScannerView.onResume();
        resetStatusView();
        super.onResume();
    }

    @Override
    public void onPause() {
        mScannerView.onPause();
        super.onPause();
    }


    public boolean onKeyDown(int keyCode, KeyEvent event) {

        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                if (mLastResult != null) {
                    restartPreviewAfterDelay(0L);

                    return true;
                }
                break;
        }
        return true;
    }

    private void restartPreviewAfterDelay(long delayMS) {
        mScannerView.restartPreviewAfterDelay(delayMS);
        resetStatusView();
    }

    private void resetStatusView() {
        mLastResult = null;
    }


    @Override
    public void onScannerCompletion(Result rawResult, ParsedResult parsedResult, Bitmap barcode) {
        Log.d("tag","最终扫描结果" + parsedResult.toString());
        if(parsedResult.toString().startsWith("p2p://")){
            ConstantUtils.CLIENT_ALLOW_LINK = true; //允许客户端连接
            connectActivity.changeFragment(0);
            connectActivity.showLoadingAnim();
            connectActivity.startScanAsClient();
            ConstantUtils.REMOTE_SERIP = parsedResult.toString().replace("p2p://","");
        }

    }
}