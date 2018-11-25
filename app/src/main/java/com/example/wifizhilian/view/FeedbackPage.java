package com.example.wifizhilian.view;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;
import com.example.wifizhilian.R;
import com.example.wifizhilian.MainActivity;
import com.example.wifizhilian.SysApp;
import com.example.wifizhilian.data.AppConfig;
import com.example.wifizhilian.libs.NetInterface;

public class FeedbackPage extends LinearLayout {
    private EditText mContentView;
    private Context mContext;
    Handler mHandler = new C03231();
    private PageHead mPageHead;
    Runnable mSaveInfoRun = new C03242();
    private Button mSubmitView;
    private ProgressDialog mWaitDialog;

    /* renamed from: com.example.wifizhilian.view.FeedbackPage$1 */
    class C03231 extends Handler {
        C03231() {
        }

        public void handleMessage(Message msg) {
            switch (msg.what) {
                case NetInterface.Net_DataSubmit_OK /*20003*/:
                    Toast.makeText(FeedbackPage.this.mContext, R.string.str_datasubmit_ok, Toast.LENGTH_SHORT).show();
                    break;
                case NetInterface.Net_Data_Error /*20019*/:
                    Toast.makeText(FeedbackPage.this.mContext, R.string.str_error_net, Toast.LENGTH_SHORT).show();
                    break;
            }
            FeedbackPage.this.mWaitDialog.dismiss();
            FeedbackPage.this.mWaitDialog = null;
        }
    }

    /* renamed from: com.example.wifizhilian.view.FeedbackPage$2 */
    class C03242 implements Runnable {
        C03242() {
        }

        public void run() {
            new NetInterface(FeedbackPage.this.mContext, FeedbackPage.this.mHandler).SaveFeedback("", FeedbackPage.this.mContentView.getText().toString());
        }
    }

    /* renamed from: com.example.wifizhilian.view.FeedbackPage$3 */
    class C03253 implements OnClickListener {
        C03253() {
        }

        public void onClick(View v) {
            ((MainActivity) FeedbackPage.this.mContext).SelectMenuItem(R.string.menu_item_mydevice);
        }
    }

    /* renamed from: com.example.wifizhilian.view.FeedbackPage$4 */
    class C03264 implements OnClickListener {
        C03264() {
        }

        public void onClick(View v) {
            if (FeedbackPage.this.mContentView.getText().toString().length() < 20) {
                Toast.makeText(FeedbackPage.this.mContext, R.string.str_error_content, Toast.LENGTH_SHORT).show();
                return;
            }
            FeedbackPage.this.ShowWaitDialog(FeedbackPage.this.getResources().getString(R.string.str_submit_sys));
            new Thread(FeedbackPage.this.mSaveInfoRun).start();
        }
    }

    public FeedbackPage(Context context) {
        super(context);
        this.mContext = context;
        AppConfig _config = SysApp.getMe().getConfig();
        View layout = ((LayoutInflater) this.mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.page_feedback, this);
        this.mPageHead = (PageHead) findViewById(R.id.FeedBackpageHead);
        this.mPageHead.LeftBnt.setImageResource(R.drawable.menu_2);
        this.mPageHead.LeftBnt.setOnClickListener(new C03253());
        this.mPageHead.RightBnt.setVisibility(INVISIBLE);
        this.mPageHead.TitleView.setText(R.string.menu_item_feedback);
        this.mContentView = (EditText) findViewById(R.id.ContenteditText);
        this.mSubmitView = (Button) findViewById(R.id.Submitbutton);
        this.mSubmitView.setOnClickListener(new C03264());
    }

    private void ShowWaitDialog(String info) {
        this.mWaitDialog = ProgressDialog.show(this.mContext, getResources().getString(R.string.str_title_wait), info);
    }
}
