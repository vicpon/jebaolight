package com.example.wifizhilian;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TextView;
import android.widget.Toast;
import com.example.wifizhilian.ScanAPListActivity.MyAdapter;
import com.example.wifizhilian.data.AppConfig;
import com.example.wifizhilian.data.AppConfig.EnumNetType;
import com.example.wifizhilian.data.BindDevObj;
import com.example.wifizhilian.data.DevGroupObj;
import com.example.wifizhilian.data.DevSetObj;
import com.example.wifizhilian.data.DevTranObj;
import com.example.wifizhilian.data.KeyValueObj;
import com.example.wifizhilian.libs.ComUtils;
import com.example.wifizhilian.libs.NetInterface;
import com.example.wifizhilian.view.CirProgressListener;
import com.example.wifizhilian.view.LineProgressObj;
import com.example.wifizhilian.view.PageHead;
import com.example.wifizhilian.view.SlideListView;
import com.example.wifizhilian.R;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class LedsixColorActivity extends Activity {
    public static final int FOUR_MESSAGE_WHAT = 110004;
    private static final int LED_MAX_VAL = 30;
    protected static final String TAG = "LedsixColorActivity";
    public static final int THREE_MESSAGE_WHAT = 110003;
    public static final int TWO_MESSAGE_WHAT = 110002;
    private MyAdapter mAdapter;
    private LineProgressObj mBuleProgressView;
    private long mChangeTime = 0;
    int mCurMode = 1;
    int mCurModeVal = 0;
    private List<Map<String, Object>> mData;
    private EditText mDevNameView;
    private SlideListView mFourSlideView;
    private Handler mHandler = new C02401();
    private LineProgressObj mLed03ProgressView;
    private LineProgressObj mLed04ProgressView;
    private LineProgressObj mLed05ProgressView;
    private LineProgressObj mLed06ProgressView;
    private LineProgressObj mModeProgressView;
    private NetInterface mNetInterface;
    private BindDevObj mOptionDev;
    private PageHead mPageHead;
    private List<View> mTabHostBnts = new ArrayList();
    private SlideListView mTwoSlideView;
    private LineProgressObj mWhiteProgressView;
    int[] modeBnt = new int[]{R.id.button_custom, R.id.button_m1, R.id.button_m2, R.id.button_m3, R.id.button_m4, R.id.button_m5};
    int[] tabIds = new int[]{R.id.widget_layout_one, R.id.widget_layout_two, R.id.widget_layout_four, R.id.widget_layout_five};
    int[] tabImage = new int[]{R.drawable.tabmanual, R.drawable.program, R.drawable.connec, R.drawable.devset};
    int[] tabImage1 = new int[]{R.drawable.tabmanual_1, R.drawable.program_1, R.drawable.connec_1, R.drawable.devset_1};
    private TabHost tabhost;
    View[] tabs = new View[4];
    Runnable timeRefresh = new C02412();
    int[] title = new int[]{R.string.lable_title_manual, R.string.lable_title_program, R.string.lable_title_devgroup, R.string.lable_title_setting};

    /* renamed from: com.example.wifizhilian.LedsixColorActivity$1 */
    class C02401 extends Handler {
        C02401() {
        }

        @SuppressLint("WrongConstant")
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case NetInterface.Net_DataSubmit_OK /*20003*/:
                    LedsixColorActivity.this.RefreshTwoSlide();
                    Toast.makeText(LedsixColorActivity.this, LedsixColorActivity.this.getResources().getString(R.string.str_datasubmit_ok), 0).show();
                    return;
                case NetInterface.Net_GetDevSet_OK /*20005*/:
                    LedsixColorActivity.this.RefreshTwoSlide();
                    if (LedsixColorActivity.this.mOptionDev.getGroups().size() > 0) {
                        LedsixColorActivity.this.RefreshFourSlide();
                        return;
                    }
                    return;
                case NetInterface.Net_GetDevSon_OK /*20006*/:
                    LedsixColorActivity.this.RefreshFourSlide();
                    return;
                case NetInterface.Net_EditDevSon_OK /*20008*/:
                    LedsixColorActivity.this.RefreshFourSlide();
                    LedsixColorActivity.this.SetTongDongSonID(LedsixColorActivity.this.mOptionDev.getNetID());
                    return;
                case NetInterface.Net_DataSubmit_FAIL /*20014*/:
                    Toast.makeText(LedsixColorActivity.this, LedsixColorActivity.this.getResources().getString(R.string.str_submit_failure), 0).show();
                    return;
                case NetInterface.Net_Data_Error /*20019*/:
                    Toast.makeText(LedsixColorActivity.this, LedsixColorActivity.this.getResources().getString(R.string.str_error_net), 0).show();
                    return;
                case NetInterface.Net_UnHerbindDev /*20041*/:
                    Toast.makeText(LedsixColorActivity.this, LedsixColorActivity.this.getResources().getString(R.string.str_datasubmit_ok), 0).show();
                    return;
                case 110002:
                    Intent twoi = new Intent(LedsixColorActivity.this, LedsixSetActivity.class);
                    twoi.putExtra("chipid", LedsixColorActivity.this.mOptionDev.getChipid());
                    twoi.putExtra("no", String.valueOf(msg.obj));
                    LedsixColorActivity.this.startActivityForResult(twoi, 110002);
                    return;
//                case 110003:
//                    Intent threei = new Intent(LedsixColorActivity.this, LedtwoTranActivity.class);
//                    threei.putExtra("chipid", LedsixColorActivity.this.mOptionDev.getChipid());
//                    threei.putExtra("no", String.valueOf(msg.obj));
//                    LedsixColorActivity.this.startActivityForResult(threei, 110003);
//                    return;
//                case 110004:
//                    Intent fouri = new Intent(LedsixColorActivity.this, DeviceSelectActivity.class);
//                    fouri.putExtra("chipid", LedsixColorActivity.this.mOptionDev.getChipid());
//                    fouri.putExtra("no", (Integer) msg.obj);
//                    fouri.putExtra("snum", 8);
//                    LedsixColorActivity.this.startActivityForResult(fouri, 110004);
//                    return;
                default:
                    return;
            }
        }
    }

    /* renamed from: com.example.wifizhilian.LedsixColorActivity$2 */
    class C02412 implements Runnable {
        C02412() {
        }

        public void run() {
            BindDevObj obj = UDPServer.getByChipID(LedsixColorActivity.this.mOptionDev.getChipid());
            if (obj != null && obj.getWordPara() != null && obj.getWordPara().length >= 8 && System.currentTimeMillis() - LedsixColorActivity.this.mChangeTime > 5000) {
                int v1 = ComUtils.byte2Unint(obj.getWordPara()[0]);
                int v2 = ComUtils.byte2Unint(obj.getWordPara()[1]);
                int v3 = ComUtils.byte2Unint(obj.getWordPara()[2]);
                int v4 = ComUtils.byte2Unint(obj.getWordPara()[3]);
                int v5 = ComUtils.byte2Unint(obj.getWordPara()[4]);
                int v6 = ComUtils.byte2Unint(obj.getWordPara()[5]);
                int state = ComUtils.byte2Unint(obj.getWordPara()[6]);
                int modeVal = ComUtils.byte2Unint(obj.getWordPara()[7]);
                if (LedsixColorActivity.this.mCurMode != modeVal) {
                    LedsixColorActivity.this.mCurMode = modeVal;
                    if (LedsixColorActivity.this.mCurMode <= 0) {
                        LedsixColorActivity.this.mCurMode = 1;
                    }
                    LedsixColorActivity.this.SetModePage(LedsixColorActivity.this.findViewById(LedsixColorActivity.this.modeBnt[LedsixColorActivity.this.mCurMode - 1]));
                }
                if (modeVal > 1 && obj.getWordPara() != null && obj.getWordPara().length > 8) {
                    LedsixColorActivity.this.mCurModeVal = ComUtils.byte2Unint(obj.getWordPara()[8]);
                    LedsixColorActivity.this.mModeProgressView.setProgres(LedsixColorActivity.this.mCurModeVal);
                }
                LedsixColorActivity.this.mWhiteProgressView.setProgres(v2);
                LedsixColorActivity.this.mBuleProgressView.setProgres(v1);
                LedsixColorActivity.this.mLed03ProgressView.setProgres(v3);
                LedsixColorActivity.this.mLed04ProgressView.setProgres(v4);
                LedsixColorActivity.this.mLed05ProgressView.setProgres(v5);
                LedsixColorActivity.this.mLed06ProgressView.setProgres(v6);
                if (state == 15) {
                    LedsixColorActivity.this.ChangOnOffBnt(0, false);
                } else {
                    LedsixColorActivity.this.ChangOnOffBnt(1, false);
                }
            }
            LedsixColorActivity.this.mHandler.postDelayed(LedsixColorActivity.this.timeRefresh, 1000);
        }
    }

    /* renamed from: com.example.wifizhilian.LedsixColorActivity$3 */
    class C02423 implements OnClickListener {
        C02423() {
        }

        public void onClick(View v) {
            LedsixColorActivity.this.finish();
        }
    }

    /* renamed from: com.example.wifizhilian.LedsixColorActivity$4 */
    class C02434 implements OnClickListener {
        C02434() {
        }

        public void onClick(View v) {
            int i;
            int tmpV = Integer.parseInt(LedsixColorActivity.this.mPageHead.RightBnt.getTag().toString());
            LedsixColorActivity ledsixColorActivity = LedsixColorActivity.this;
            if (tmpV == 1) {
                i = 0;
            } else {
                i = 1;
            }
            ledsixColorActivity.ChangOnOffBnt(i, true);
        }
    }

    /* renamed from: com.example.wifizhilian.LedsixColorActivity$5 */
    class C02445 implements OnTabChangeListener {
        C02445() {
        }

        public void onTabChanged(String tabId) {
            int curIndex = LedsixColorActivity.this.tabhost.getCurrentTab();
            switch (curIndex) {
                case 1:
                    LedsixColorActivity.this.RefreshTwoSlide();
                    break;
                case 2:
                    LedsixColorActivity.this.RefreshThreeSlide();
                    break;
                case 3:
                    LedsixColorActivity.this.RefreshFourSlide();
                    break;
            }
            for (int i = 0; i < LedsixColorActivity.this.tabs.length; i++) {
                if (i == curIndex) {
                    ((ImageView) LedsixColorActivity.this.tabs[i].findViewById(R.id.TabBntIconimageView)).setImageResource(LedsixColorActivity.this.tabImage1[i]);
                } else {
                    ((ImageView) LedsixColorActivity.this.tabs[i].findViewById(R.id.TabBntIconimageView)).setImageResource(LedsixColorActivity.this.tabImage[i]);
                }
            }
        }
    }

    /* renamed from: com.example.wifizhilian.LedsixColorActivity$6 */
    class C05486 extends CirProgressListener {
        C05486() {
        }

        public void onTouchOver(int v) {
            LedsixColorActivity.this.SetTongDongVal(LedsixColorActivity.this.mOptionDev.getNetID());
            super.onTouchOver(v);
        }
    }

    /* renamed from: com.example.wifizhilian.LedsixColorActivity$7 */
    class C05497 extends CirProgressListener {
        C05497() {
        }

        public void onTouchOver(int v) {
            LedsixColorActivity.this.SetTongDongVal(LedsixColorActivity.this.mOptionDev.getNetID());
            super.onTouchOver(v);
        }
    }

    /* renamed from: com.example.wifizhilian.LedsixColorActivity$8 */
    class C05508 extends CirProgressListener {
        C05508() {
        }

        public void onTouchOver(int v) {
            LedsixColorActivity.this.SetTongDongVal(LedsixColorActivity.this.mOptionDev.getNetID());
            super.onTouchOver(v);
        }
    }

    /* renamed from: com.example.wifizhilian.LedsixColorActivity$9 */
    class C05519 extends CirProgressListener {
        C05519() {
        }

        public void onTouchOver(int v) {
            LedsixColorActivity.this.SetTongDongVal(LedsixColorActivity.this.mOptionDev.getNetID());
            super.onTouchOver(v);
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ledsix_color);
        this.mOptionDev = SysApp.getMe().getDevByChipid(getIntent().getStringExtra("chipid"));
        String name = this.mOptionDev.getNickName();
        if (ComUtils.StrIsEmpty(name)) {
            name = SysApp.getMe().getDevName(this.mOptionDev.getProduct(), this.mOptionDev.getSSID());
        }
        this.mPageHead = (PageHead) findViewById(R.id.LedTwopageHead);
        this.mPageHead.LeftBnt.setImageResource(R.drawable.back);
        this.mPageHead.LeftBnt.setOnClickListener(new C02423());
        this.mPageHead.RightBnt.setImageResource(R.drawable.icon_on);
        this.mPageHead.RightBnt.setTag(Integer.valueOf(1));
        this.mPageHead.RightBnt.setOnClickListener(new C02434());
        this.mPageHead.TitleView.setText(name);
        this.tabhost = (TabHost) findViewById(R.id.Maintabhost);
        this.tabhost.setup();
        for (int i = 0; i < this.tabs.length; i++) {
            this.tabs[i] = LayoutInflater.from(this).inflate(R.layout.view_tabhost_bnt, null);
            ((TextView) this.tabs[i].findViewById(R.id.TabBntTitletextView)).setText(this.title[i]);
            if (i == 0) {
                ((ImageView) this.tabs[i].findViewById(R.id.TabBntIconimageView)).setImageResource(this.tabImage1[i]);
            } else {
                ((ImageView) this.tabs[i].findViewById(R.id.TabBntIconimageView)).setImageResource(this.tabImage[i]);
            }
            this.tabhost.addTab(this.tabhost.newTabSpec(getResources().getString(this.title[i])).setIndicator(this.tabs[i]).setContent(this.tabIds[i]));
        }
        this.tabhost.setOnTabChangedListener(new C02445());
        this.mWhiteProgressView = (LineProgressObj) findViewById(R.id.WhitelineProgress);
        this.mWhiteProgressView.setMaxVal(30);
        this.mWhiteProgressView.onTouchListener(new C05486());
        this.mBuleProgressView = (LineProgressObj) findViewById(R.id.BluelineProgress);
        this.mBuleProgressView.setMaxVal(30);
        this.mBuleProgressView.onTouchListener(new C05497());
        this.mLed03ProgressView = (LineProgressObj) findViewById(R.id.Led03LineProgress);
        this.mLed03ProgressView.setMaxVal(30);
        this.mLed03ProgressView.onTouchListener(new C05508());
        this.mLed04ProgressView = (LineProgressObj) findViewById(R.id.Led04LineProgress);
        this.mLed04ProgressView.setMaxVal(30);
        this.mLed04ProgressView.onTouchListener(new C05519());
        this.mLed05ProgressView = (LineProgressObj) findViewById(R.id.Led05LineProgress);
        this.mLed05ProgressView.setMaxVal(30);
        this.mLed05ProgressView.onTouchListener(new CirProgressListener() {
            public void onTouchOver(int v) {
                LedsixColorActivity.this.SetTongDongVal(LedsixColorActivity.this.mOptionDev.getNetID());
                super.onTouchOver(v);
            }
        });
        this.mLed06ProgressView = (LineProgressObj) findViewById(R.id.Led06LineProgress);
        this.mLed06ProgressView.setMaxVal(30);
        this.mLed06ProgressView.onTouchListener(new CirProgressListener() {
            public void onTouchOver(int v) {
                LedsixColorActivity.this.SetTongDongVal(LedsixColorActivity.this.mOptionDev.getNetID());
                super.onTouchOver(v);
            }
        });
        this.mModeProgressView = (LineProgressObj) findViewById(R.id.ModelineProgress);
        this.mModeProgressView.setMaxVal(30);
        this.mModeProgressView.onTouchListener(new CirProgressListener() {
            public void onTouchOver(int v) {
                if (v < 10) {
                    v = 10;
                    LedsixColorActivity.this.mModeProgressView.setProgres(10);
                }
                LedsixColorActivity.this.SetTongDongVal(LedsixColorActivity.this.mOptionDev.getNetID());
                super.onTouchOver(v);
            }
        });
        this.mModeProgressView.setProgres(10);
        this.mDevNameView = (EditText) findViewById(R.id.DevNameeditText);
        this.mDevNameView.setText(name);
        this.mTwoSlideView = (SlideListView) findViewById(R.id.TwoslideListView);
        this.mFourSlideView = (SlideListView) findViewById(R.id.FourslideListView);
        this.mTwoSlideView.setEventHandler(this.mHandler, 110002);
        this.mFourSlideView.setEventHandler(this.mHandler, 110004);
        this.mHandler.postDelayed(this.timeRefresh, 500);
        this.mNetInterface = new NetInterface(this, this.mHandler);
        this.mNetInterface.getDevSetData(this.mOptionDev);
        Button tBnt = (Button) findViewById(R.id.UnHerBindbutton);
        BindDevObj dObj = UDPServer.getByChipID(this.mOptionDev.getChipid());
        if (dObj == null || !dObj.getNetType().equals(EnumNetType.Lan)) {
            tBnt.setVisibility(View.GONE);
        } else {
            tBnt.setVisibility(View.VISIBLE);
        }
    }

    public void onWindowFocusChanged(boolean hasFocus) {
        if (hasFocus) {
            this.mCurMode = 1;
            initModeButton();
            findViewById(R.id.button_custom).setBackgroundResource(R.color.green);
            findViewById(R.id.CustomProgressLayout).setVisibility(View.VISIBLE);
            findViewById(R.id.ModeProgressLayout).setVisibility(View.GONE);
        }
    }

    private void ChangOnOffBnt(int val, boolean isWrite) {
        int isOn;
        if (val == 0) {
            this.mPageHead.RightBnt.setImageResource(R.drawable.icon_off);
            isOn = 0;
        } else {
            this.mPageHead.RightBnt.setImageResource(R.drawable.icon_on);
            isOn = 1;
        }
        this.mPageHead.RightBnt.setTag(Integer.valueOf(isOn));
        if (isWrite) {
            this.mChangeTime = System.currentTimeMillis();
            byte[] data = new byte[]{(byte) isOn};
            Bundle bundle = new Bundle();
            bundle.putSerializable("DATA", data);
            Intent i = new Intent(AppConfig.SEND_CMD_TODEV);
            i.putExtra("DEVID", this.mOptionDev.getNetID());
            i.putExtra("CODE", 21);
            i.putExtras(bundle);
            sendBroadcast(i);
        }
    }

    private void RefreshTwoSlide() {
        List<KeyValueObj> kvData = new ArrayList();
        for (int ii = 0; ii < 8; ii++) {
            KeyValueObj kv = new KeyValueObj();
            DevSetObj set = this.mOptionDev.getSetByIndex(ii);
            if (set != null) {
                if (set.isRun()) {
                    kv.IntVal = R.drawable.quick_set_run;
                } else {
                    kv.IntVal = R.drawable.quick_set;
                }
                kv.StringVal = set.getNickName();
            } else {
                kv.IntVal = R.drawable.add_bnt;
                kv.StringVal = "";
            }
            kv.Key = ii;
            kvData.add(kv);
        }
        this.mTwoSlideView.setData(kvData);
    }

    private void RefreshThreeSlide() {
        List<KeyValueObj> kvData = new ArrayList();
        for (int ii = 0; ii < 8; ii++) {
            KeyValueObj kv = new KeyValueObj();
            DevTranObj obj = this.mOptionDev.getTranByIndex(ii);
            if (obj != null) {
                kv.IntVal = R.drawable.quick_set;
                kv.StringVal = obj.getNickName();
            } else {
                kv.IntVal = R.drawable.add_bnt;
                kv.StringVal = "";
            }
            kv.Key = ii;
            kvData.add(kv);
        }
    }

    private void RefreshFourSlide() {
        int ii;
        List<KeyValueObj> kvData = new ArrayList();
        KeyValueObj mkv = new KeyValueObj();
        mkv.IntVal = R.drawable.led_dong;
        mkv.StringVal = this.mOptionDev.getNickName();
        mkv.Key = 0;
        kvData.add(mkv);
        for (ii = 0; ii < this.mOptionDev.getGroups().size(); ii++) {
            BindDevObj sDev = SysApp.getMe().getDevByNetid(((DevGroupObj) this.mOptionDev.getGroups().get(ii)).getSonID());
            if (sDev != null) {
                String name = sDev.getNickName();
                if (ComUtils.StrIsEmpty(name)) {
                    name = SysApp.getMe().getDevName(sDev.getProduct(), sDev.getSSID());
                }
                KeyValueObj kv = new KeyValueObj();
                kv.IntVal = R.drawable.led_dong;
                kv.StringVal = name;
                kv.Key = ii + 1;
                kvData.add(kv);
            }
        }
        for (ii = kvData.size(); ii < 8; ii++) {
            KeyValueObj kv = new KeyValueObj();
            kv.IntVal = R.drawable.add_bnt;
            kv.StringVal = "";
            kv.Key = ii;
            kvData.add(kv);
        }
        this.mFourSlideView.setData(kvData);
    }

    private void SetTongDongVal(int did) {
        byte[] data;
        this.mChangeTime = System.currentTimeMillis();
        if (this.mCurMode == 1) {
            int v1 = this.mWhiteProgressView.getProgres();
            int v2 = this.mBuleProgressView.getProgres();
            int v3 = this.mLed03ProgressView.getProgres();
            int v4 = this.mLed04ProgressView.getProgres();
            int v5 = this.mLed05ProgressView.getProgres();
            int v6 = this.mLed06ProgressView.getProgres();
            data = new byte[]{(byte) this.mCurMode, (byte) v2, (byte) v1, (byte) v3, (byte) v4, (byte) v5, (byte) v6};
        } else {
            int modeVal = this.mModeProgressView.getProgres();
            data = new byte[]{(byte) this.mCurMode, (byte) modeVal};
        }
        Bundle bundle = new Bundle();
        bundle.putSerializable("DATA", data);
        Intent i = new Intent(AppConfig.SEND_CMD_TODEV);
        i.putExtra("DEVID", did);
        i.putExtra("CODE", 10);
        i.putExtras(bundle);
        sendBroadcast(i);
    }

    private void SetTongDongHtoA(int did, int v1) {
        byte[] data = ComUtils.intToByteArray(v1);
        Bundle bundle = new Bundle();
        bundle.putSerializable("DATA", data);
        Intent i = new Intent(AppConfig.SEND_CMD_TODEV);
        i.putExtra("DEVID", did);
        i.putExtra("CODE", 12);
        i.putExtras(bundle);
        sendBroadcast(i);
    }

    private void SetTongDongSonID(int did) {
        ByteBuffer buf = ByteBuffer.allocate(this.mOptionDev.getGroups().size() * 4);
        for (int ii = 0; ii < this.mOptionDev.getGroups().size(); ii++) {
            buf.put(ComUtils.int2ByteArr(((DevGroupObj) this.mOptionDev.getGroups().get(ii)).getSonID(), 4));
        }
        Bundle bundle = new Bundle();
        bundle.putSerializable("DATA", buf.array());
        Intent i = new Intent(AppConfig.SEND_CMD_TODEV);
        i.putExtra("DEVID", did);
        i.putExtra("CODE", 13);
        i.putExtras(bundle);
        sendBroadcast(i);
    }

    public void BntClick(View v) {
        SetModePage(v);
        switch (v.getId()) {
            case R.id.button_custom:
            case R.id.button_m1:
            case R.id.button_m2:
            case R.id.button_m3:
            case R.id.button_m4:
            case R.id.button_m5:
                SetTongDongVal(this.mOptionDev.getNetID());
                return;
            default:
                return;
        }
    }

    public void SetModePage(View v) {
        switch (v.getId()) {
            case R.id.SaveExtbutton:
                SaveExtInfo();
                return;
            case R.id.UnHerBindbutton:
                UnHerBind();
                return;
            case R.id.button_custom:
                this.mCurMode = 1;
                SetTongDongVal(this.mOptionDev.getNetID());
                initModeButton();
                findViewById(R.id.button_custom).setBackgroundResource(R.color.green);
                findViewById(R.id.CustomProgressLayout).setVisibility(View.VISIBLE);
                findViewById(R.id.ModeProgressLayout).setVisibility(View.GONE);
                return;
            case R.id.button_m1:
                this.mCurMode = 2;
                initModeButton();
                findViewById(R.id.button_m1).setBackgroundResource(R.color.green);
                findViewById(R.id.ModeProgressLayout).setVisibility(View.VISIBLE);
                findViewById(R.id.CustomProgressLayout).setVisibility(View.GONE);
                return;
            case R.id.button_m2:
                this.mCurMode = 3;
                initModeButton();
                findViewById(R.id.button_m2).setBackgroundResource(R.color.green);
                findViewById(R.id.ModeProgressLayout).setVisibility(View.VISIBLE);
                findViewById(R.id.CustomProgressLayout).setVisibility(View.GONE);
                return;
            case R.id.button_m3:
                this.mCurMode = 4;
                initModeButton();
                findViewById(R.id.button_m3).setBackgroundResource(R.color.green);
                findViewById(R.id.ModeProgressLayout).setVisibility(View.VISIBLE);
                findViewById(R.id.CustomProgressLayout).setVisibility(View.GONE);
                return;
            case R.id.button_m4:
                this.mCurMode = 5;
                initModeButton();
                findViewById(R.id.button_m4).setBackgroundResource(R.color.green);
                findViewById(R.id.ModeProgressLayout).setVisibility(View.VISIBLE);
                findViewById(R.id.CustomProgressLayout).setVisibility(View.GONE);
                return;
            case R.id.button_m5:
                this.mCurMode = 6;
                initModeButton();
                findViewById(R.id.button_m5).setBackgroundResource(R.color.green);
                findViewById(R.id.ModeProgressLayout).setVisibility(View.VISIBLE);
                findViewById(R.id.CustomProgressLayout).setVisibility(View.GONE);
                return;
            default:
                return;
        }
    }

    private void initModeButton() {
        for (int findViewById : this.modeBnt) {
            findViewById(findViewById).setBackgroundResource(R.color.white);
        }
    }

    private void SaveExtInfo() {
        String name = this.mDevNameView.getText().toString();
        this.mOptionDev.setNickName(name);
        this.mOptionDev.setMtoPTime(30);
        sendBroadcast(new Intent(AppConfig.DEVICE_LIST_CHANG));
        this.mNetInterface.saveDevExtInfo(this.mOptionDev, name, 30);
        SetTongDongHtoA(this.mOptionDev.getNetID(), 30);
    }

    private void UnHerBind() {
        this.mNetInterface.UnHerBindDevice(this.mOptionDev);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 110002:
                RefreshTwoSlide();
                return;
            case 110004:
                if (data != null) {
                    int ii;
                    this.mOptionDev.initGroupUpdate();
                    String selectDid = data.getStringExtra("selectDid");
                    if (ComUtils.StrIsEmpty(selectDid)) {
                        this.mOptionDev.setGroup(0);
                    } else {
                        String[] ids = selectDid.split(",");
                        for (ii = 0; ii < ids.length; ii++) {
                            if (!ComUtils.StrIsEmpty(ids[ii])) {
                                int nid = Integer.parseInt(ids[ii]);
                                DevGroupObj obj = this.mOptionDev.getGroup(nid);
                                if (obj != null) {
                                    obj.setUpdate(true);
                                } else {
                                    obj = new DevGroupObj();
                                    obj.setChipid(this.mOptionDev.getChipid());
                                    obj.setID(0);
                                    obj.setMainNetid(this.mOptionDev.getNetID());
                                    obj.setSonID(nid);
                                    obj.setUpdate(true);
                                    this.mOptionDev.getGroups().add(obj);
                                }
                                SysApp.getMe().getDevByNetid(nid).setGroup(2);
                            }
                        }
                        this.mOptionDev.setGroup(1);
                    }
                    String removeId = "";
                    for (ii = this.mOptionDev.getGroups().size() - 1; ii >= 0; ii--) {
                        if (!((DevGroupObj) this.mOptionDev.getGroups().get(ii)).isUpdate()) {
                            if (!removeId.equals("")) {
                                removeId = removeId + ",";
                            }
                            removeId = removeId + ((DevGroupObj) this.mOptionDev.getGroups().get(ii)).getSonID();
                            SysApp.getMe().getDevByNetid(((DevGroupObj) this.mOptionDev.getGroups().get(ii)).getSonID()).setGroup(0);
                            this.mOptionDev.getGroups().remove(ii);
                        }
                    }
                    RefreshFourSlide();
                    sendBroadcast(new Intent(AppConfig.DEVICE_LIST_CHANG));
                    this.mNetInterface.modiDevGroup(this.mOptionDev, selectDid, removeId);
                    return;
                }
                return;
            default:
                return;
        }
    }

    protected void onResume() {
        super.onResume();
    }

    protected void onDestroy() {
        this.mHandler.removeCallbacks(this.timeRefresh);
        super.onDestroy();
    }

    private void ClearSetData(String titleid) {
        this.mOptionDev.delSetByIndex(Integer.parseInt(titleid));
        this.mNetInterface.deleDevSetData(this.mOptionDev, titleid);
    }
}
