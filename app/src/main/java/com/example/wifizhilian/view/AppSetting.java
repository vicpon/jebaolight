package com.example.wifizhilian.view;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.transition.Visibility;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.example.wifizhilian.R;
import com.example.wifizhilian.MainActivity;
import com.example.wifizhilian.SysApp;
import com.example.wifizhilian.data.AppConfig;
import com.example.wifizhilian.libs.ComUtils;
import com.example.wifizhilian.libs.HttpClient;
import com.example.wifizhilian.libs.JsonGet;
import com.example.wifizhilian.libs.NetInterface;
import com.example.wifizhilian.libs.xLog;
import java.io.File;
import org.json.JSONObject;

public class AppSetting extends LinearLayout {
    protected static final String TAG = "AppSetting";
    Runnable checkNewApp = new C03152();
    Runnable downNewApp = new C03163();
    private CheckBox mAutoView;
    private Context mContext;
    private int mCurVerCode;
    private String mDownAppUrl = "";
    private Handler mHandler = new C03141();
    private PageHead mPageHead;
    private CheckBox mRememberView;
    private Button mUpdateView;
    private TextView mVerView;

    /* renamed from: com.example.wifizhilian.view.AppSetting$1 */
    class C03141 extends Handler {
        C03141() {
        }

        public void handleMessage(Message msg) {
            xLog.m5i(AppSetting.TAG, "handleMessage what:" + msg.what);
            switch (msg.what) {
                case NetInterface.Net_GetNewVer /*20050*/:
                    JSONObject json = (JSONObject) msg.obj;
                    int verCode = JsonGet.getInt(json, "VerCode");
                    String verName = JsonGet.getStr(json, "VerName");
                    AppSetting.this.mDownAppUrl = JsonGet.getStr(json, "DownPath");
                    AppSetting.this.mUpdateView.setText(AppSetting.this.getResources().getString(R.string.lable_appinfo_update) + " " + verName);
                    AppSetting.this.mUpdateView.setVisibility(VISIBLE);
                    return;
                case HttpClient.Net_DownFile_Over /*20094*/:
                    String path = (String) msg.obj;
                    Intent intent = new Intent();
                    //intent.addFlags(268435456);
                    intent.addFlags(Intent.FLAG_RECEIVER_FOREGROUND);
                    intent.setAction("android.intent.action.VIEW");
                    intent.setDataAndType(Uri.fromFile(new File(path)), "application/vnd.android.package-archive");
                    AppSetting.this.mContext.startActivity(intent);
                    return;
                case HttpClient.Net_DownFile_Progress /*20095*/:
                    AppSetting.this.mUpdateView.setText(AppSetting.this.getResources().getString(R.string.lable_appinfo_downprogress) + " " + ((Integer) msg.obj).intValue() + "%");
                    return;
                default:
                    return;
            }
        }
    }

    /* renamed from: com.example.wifizhilian.view.AppSetting$2 */
    class C03152 implements Runnable {
        C03152() {
        }

        public void run() {
            new NetInterface(AppSetting.this.mContext, AppSetting.this.mHandler).getNewVer(AppSetting.this.mCurVerCode);
        }
    }

    /* renamed from: com.example.wifizhilian.view.AppSetting$3 */
    class C03163 implements Runnable {
        C03163() {
        }

        public void run() {
            if (!ComUtils.StrIsEmpty(AppSetting.this.mDownAppUrl)) {
                AppSetting.this.mUpdateView.setEnabled(false);
                new NetInterface(AppSetting.this.mContext, AppSetting.this.mHandler).downFile(AppSetting.this.mDownAppUrl);
            }
        }
    }

    /* renamed from: com.example.wifizhilian.view.AppSetting$4 */
    class C03174 implements OnClickListener {
        C03174() {
        }

        public void onClick(View v) {
            ((MainActivity) AppSetting.this.mContext).SelectMenuItem(R.string.menu_item_mydevice);
        }
    }

    /* renamed from: com.example.wifizhilian.view.AppSetting$7 */
    class C03207 implements OnClickListener {
        C03207() {
        }

        public void onClick(View v) {
            AppSetting.this.mHandler.post(AppSetting.this.downNewApp);
        }
    }

    public AppSetting(Context context) {
        super(context);
        this.mContext = context;
        //View layout = ((LayoutInflater) this.mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.page_appsetting, this);
        View layout = ((LayoutInflater) this.mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.page_appsetting, this);
        this.mPageHead = (PageHead) findViewById(R.id.AppSettingpageHead);
        this.mPageHead.LeftBnt.setImageResource(R.drawable.menu_2);
        this.mPageHead.LeftBnt.setOnClickListener(new C03174());
        this.mPageHead.RightBnt.setVisibility(INVISIBLE);
        this.mPageHead.TitleView.setText(R.string.menu_item_setting);
        this.mRememberView = (CheckBox) findViewById(R.id.SetRemembercheckBox);
        this.mAutoView = (CheckBox) findViewById(R.id.SetAutocheckBox);
        this.mVerView = (TextView) findViewById(R.id.AppVertextView);
        final AppConfig _config = SysApp.getMe().getConfig();
        this.mRememberView.setChecked(_config.isRemember());
        this.mAutoView.setChecked(_config.isAutoLogin());
        this.mRememberView.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                _config.setRemember(isChecked);
            }
        });
        this.mAutoView.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                _config.setAutoLogin(isChecked);
            }
        });
        try {
            PackageInfo info = this.mContext.getPackageManager().getPackageInfo(this.mContext.getPackageName(), 0);
            this.mVerView.setText(info.versionName);
            this.mCurVerCode = info.versionCode;
        } catch (NameNotFoundException e) {
            System.out.print(e.toString());
        }
        this.mUpdateView = (Button) findViewById(R.id.UpdateAppbutton);
        this.mUpdateView.setOnClickListener(new C03207());
        this.mHandler.post(this.checkNewApp);
    }
}
