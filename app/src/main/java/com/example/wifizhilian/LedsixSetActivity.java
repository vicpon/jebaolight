package com.example.wifizhilian;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;
import com.example.wifizhilian.data.AppConfig;
import com.example.wifizhilian.data.BindDevObj;
import com.example.wifizhilian.data.DevSetObj;
import com.example.wifizhilian.libs.ComUtils;
import com.example.wifizhilian.libs.NetInterface;
import com.example.wifizhilian.view.LineGraphicView;
import com.example.wifizhilian.view.PageHead;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class LedsixSetActivity extends Activity {
    private static final String LED_INIT_VAL = "180F0F0F0F0F0F0F0F0F0F0F0F0F0F0F0F0F0F0F0F0F0F0F0F0F0F0F0F0F0F0F0F0F0F0F0F0F0F0F0F0F0F0F0F0F0F0F0F0F0F0F0F0F0F0F0F0F0F0F0F0F0F0F0F0F0F0F0F0F0F0F0F0F0F0F0F0F0F0F0F0F0F0F0F0F0F0F0F0F0F0F0F0F0F0F0F0F0F0F0F0F0F0F0F0F0F0F0F0F0F0F0F0F0F0F0F0F0F0F0F0F0F0F0F0F0F0F0F0F0F0F0F0F0F0F0F0F0F0F0F0F0F0F0F";
    private static final String LED_INIT_VAL1 = "18000000000000000000000000000000000000000000000000000000000000000000000000000100000000020401010000060905020001090D060301020C10070302050F140904030612160C06030915190F07050C181B1209060F181B1209060F15190F07050C12160C0603090F14090403060C1007030205090D06030102060905020001020401010000000000000000";
    private static final String LED_INIT_VAL2 = "180000000000000000000000000000000000000000000000000000000000000000000000000101000000000403020100010904060200050D060903010610070C03020714090F040309160C1206030C190F1507050F1B12180906121B1218090612190F1507050F160C1206030C14090F04030910070C0302070D0609030106090406020005040302010001000000000000";
    private static final String TAG = "LedsixSetActivity";
    private TextView mDateView;
    private TextView mDayView;
    private int mDefaultDay;
    private DevSetObj mDevSetObj;
    private Handler mHandler = new C02451();
    private LineGraphicView mLineGraphicView;
    private BindDevObj mOptionDev;
    private PageHead mPageHead;
    private TextView mTimeView;
    private Runnable timeRun = new C02462();

    /* renamed from: com.example.wifizhilian.LedsixSetActivity$1 */
    class C02451 extends Handler {
        C02451() {
        }

        public void handleMessage(Message msg) {
            switch (msg.what) {
                case NetInterface.Net_DataSubmit_OK /*20003*/:
                    Toast.makeText(LedsixSetActivity.this, LedsixSetActivity.this.getResources().getString(R.string.str_datasubmit_ok), 0).show();
                    return;
                case NetInterface.Net_EditRunTask_OK /*20009*/:
                    Toast.makeText(LedsixSetActivity.this, LedsixSetActivity.this.getResources().getString(R.string.str_datasubmit_ok), 0).show();
                    return;
                case NetInterface.Net_RunTaskData_OK /*20010*/:
                    Toast.makeText(LedsixSetActivity.this, LedsixSetActivity.this.getResources().getString(R.string.str_datasubmit_ok), 0).show();
                    LedsixSetActivity.this.SetTongDongTimeData();
                    return;
                case NetInterface.Net_DataSubmit_FAIL /*20014*/:
                    Toast.makeText(LedsixSetActivity.this, LedsixSetActivity.this.getResources().getString(R.string.str_submit_failure), 0).show();
                    return;
                case NetInterface.Net_Data_Error /*20019*/:
                    Toast.makeText(LedsixSetActivity.this, LedsixSetActivity.this.getResources().getString(R.string.str_error_net), 0).show();
                    return;
                default:
                    return;
            }
        }
    }

    /* renamed from: com.example.wifizhilian.LedsixSetActivity$2 */
    class C02462 implements Runnable {
        C02462() {
        }

        public void run() {
            String formatStr;
            if (LedsixSetActivity.this.isZh()) {
                formatStr = "yyyy-MM-dd";
            } else {
                formatStr = "MM-dd-yyyy";
            }
            Date d = new Date(System.currentTimeMillis());
            LedsixSetActivity.this.mDateView.setText(new SimpleDateFormat(formatStr).format(d));
            LedsixSetActivity.this.mTimeView.setText(new SimpleDateFormat("HH:mm").format(d));
            LedsixSetActivity.this.mHandler.postDelayed(LedsixSetActivity.this.timeRun, 1000);
        }
    }

    /* renamed from: com.example.wifizhilian.LedsixSetActivity$3 */
    class C02473 implements OnClickListener {
        C02473() {
        }

        public void onClick(View v) {
            LedsixSetActivity.this.setResult(110002, new Intent());
            LedsixSetActivity.this.finish();
        }
    }

    private boolean isZh() {
        if (getResources().getConfiguration().locale.getLanguage().endsWith("zh")) {
            return true;
        }
        return false;
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ledsix_set);
        Intent intent1 = getIntent();
        String devKey = intent1.getStringExtra("chipid");
        this.mDefaultDay = Integer.parseInt(intent1.getStringExtra("no"));
        this.mOptionDev = SysApp.getMe().getDevByChipid(devKey);
        String name = this.mOptionDev.getNickName();
        if (ComUtils.StrIsEmpty(name)) {
            name = SysApp.getMe().getDevName(this.mOptionDev.getProduct(), this.mOptionDev.getSSID());
        }
        this.mPageHead = (PageHead) findViewById(R.id.LedTwoSetpageHead);
        this.mPageHead.LeftBnt.setImageResource(R.drawable.back);
        this.mPageHead.LeftBnt.setOnClickListener(new C02473());
        this.mPageHead.RightBnt.setVisibility(8);
        this.mPageHead.TitleView.setText(name + getResources().getString(R.string.title_activity_ledtwo_set));
        this.mLineGraphicView = (LineGraphicView) findViewById(R.id.SetlineGraphicView);
        this.mDayView = (TextView) findViewById(R.id.DaytextView);
        this.mDateView = (TextView) findViewById(R.id.DatetextView);
        this.mTimeView = (TextView) findViewById(R.id.TimetextView);
        this.mLineGraphicView.setCoordsValue(30, 4);
        this.mLineGraphicView.setColorArr(new int[]{-16763956, -5921371, -11802657, -13369498, -65485, -10066228});
        this.mLineGraphicView.setXRawTitle("00,,,,04,,,,08,,,,12,,,,16,,,,20,,,,");
        changeDay(0);
        this.mHandler.post(this.timeRun);
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode != 4 || event.getAction() != 0) {
            return super.onKeyDown(keyCode, event);
        }
        setResult(110002, new Intent());
        finish();
        return true;
    }

    private void SetTongDongTimeData() {
        byte[] data = ComUtils.hexStringToBytes(this.mDevSetObj.getData());
        Bundle bundle = new Bundle();
        bundle.putSerializable("DATA", data);
        Intent i = new Intent(AppConfig.SEND_CMD_TODEV);
        i.putExtra("DEVID", this.mOptionDev.getNetID());
        i.putExtra("CODE", 11);
        i.putExtras(bundle);
        sendBroadcast(i);
    }

    private void initGraphicData() {
        this.mDevSetObj = this.mOptionDev.getSetByIndex(this.mDefaultDay);
        if (this.mDevSetObj == null || ComUtils.StrIsEmpty(this.mDevSetObj.getData())) {
            this.mDevSetObj = new DevSetObj();
            this.mDevSetObj.setChipID(this.mOptionDev.getChipid());
            this.mDevSetObj.setDay(this.mDefaultDay);
            this.mDevSetObj.setDevID(this.mOptionDev.getNetID());
            this.mDevSetObj.setNickName(getResources().getString(R.string.lable_title_program) + (this.mDefaultDay + 1));
            this.mDevSetObj.setState(0);
            switch (this.mDefaultDay) {
                case 0:
                    this.mDevSetObj.setData(LED_INIT_VAL1);
                    break;
                case 1:
                    this.mDevSetObj.setData(LED_INIT_VAL2);
                    break;
                default:
                    this.mDevSetObj.setData(LED_INIT_VAL);
                    break;
            }
        }
        if (this.mDevSetObj.getState() == 2) {
            this.mDevSetObj.setData(LED_INIT_VAL);
        }
        updateGraphicData(this.mDevSetObj.getData());
    }

    private void updateGraphicData(String hexData) {
        this.mLineGraphicView.clearData();
        byte[] data = ComUtils.hexStringToBytes(hexData);
        int lNum = (data.length - 1) / data[0];
        for (int ii = 0; ii < lNum; ii++) {
            ArrayList<Integer> lList = new ArrayList();
            for (byte ff = (byte) 0; ff < data[0]; ff++) {
                lList.add(Integer.valueOf(ComUtils.byte2Unint(data[((ff * lNum) + ii) + 1])));
            }
            this.mLineGraphicView.addData(lList);
        }
    }

    public void BntClick(View v) {
        switch (v.getId()) {
            case R.id.Deletebutton:
                if (this.mDevSetObj.getID() > 0) {
                    this.mOptionDev.delSetByIndex(this.mDefaultDay);
                    initGraphicData();
                    this.mLineGraphicView.invalidate();
                    new NetInterface(this, this.mHandler).deleDevSetData(this.mOptionDev, String.valueOf(this.mDefaultDay));
                    return;
                }
                return;
            case R.id.LeftimageView:
                if (this.mLineGraphicView.isChange()) {
                    showSaveDialog(-1);
                    return;
                } else {
                    changeDay(-1);
                    return;
                }
            case R.id.RighthimageView:
                if (this.mLineGraphicView.isChange()) {
                    showSaveDialog(1);
                    return;
                } else {
                    changeDay(1);
                    return;
                }
            case R.id.Runbutton:
                RunCurData();
                return;
            case R.id.Savebutton:
                SaveChangeData();
                return;
            case R.id.button_Reserve1:
                updateGraphicData(LED_INIT_VAL1);
                return;
            case R.id.button_Reserve2:
                updateGraphicData(LED_INIT_VAL2);
                return;
            default:
                return;
        }
    }

    private void changeDay(int num) {
        this.mDefaultDay += num;
        if (this.mDefaultDay < 0) {
            this.mDefaultDay = 7;
        } else if (this.mDefaultDay > 7) {
            this.mDefaultDay = 0;
        }
        initGraphicData();
        this.mLineGraphicView.invalidate();
        this.mDayView.setText(this.mDevSetObj.getNickName());
    }

    private void showSaveDialog(final int num) {
        new Builder(this).setMessage(R.string.lable_save_data).setPositiveButton(R.string.str_OK, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                LedsixSetActivity.this.SaveChangeData();
                LedsixSetActivity.this.changeDay(num);
            }
        }).setNegativeButton(R.string.bnt_giveup, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                LedsixSetActivity.this.changeDay(num);
            }
        }).show();
    }

    private void SaveChangeData() {
        ArrayList<ArrayList<Integer>> data = this.mLineGraphicView.getData();
        int timeSize = ((ArrayList) data.get(0)).size();
        String dataStr = ComUtils.bytesToHexString(ComUtils.int2ByteArr(timeSize, 1));
        for (int ii = 0; ii < timeSize; ii++) {
            for (int ff = 0; ff < data.size(); ff++) {
                dataStr = dataStr + ComUtils.bytesToHexString(ComUtils.int2ByteArr(((Integer) ((ArrayList) data.get(ff)).get(ii)).intValue(), 1));
            }
        }
        this.mDevSetObj.setData(dataStr.toUpperCase());
        this.mOptionDev.addSet(this.mDevSetObj);
        new NetInterface(this, this.mHandler).saveDevSetData(this.mOptionDev, this.mDevSetObj);
        this.mLineGraphicView.setChange(false);
    }

    private void RunCurData() {
        for (int ii = 0; ii < this.mOptionDev.getSetList().size(); ii++) {
            if (((DevSetObj) this.mOptionDev.getSetList().get(ii)).getDay() == this.mDefaultDay) {
                ((DevSetObj) this.mOptionDev.getSetList().get(ii)).setRun(true);
            } else {
                ((DevSetObj) this.mOptionDev.getSetList().get(ii)).setRun(false);
            }
        }
        new NetInterface(this, this.mHandler).setRunTask(this.mOptionDev, this.mDefaultDay);
    }
}
