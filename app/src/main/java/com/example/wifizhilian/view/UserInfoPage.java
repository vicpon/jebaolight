package com.example.wifizhilian.view;

import android.annotation.SuppressLint;
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
import android.widget.TextView;
import android.widget.Toast;
import com.example.wifizhilian.R;
import com.example.wifizhilian.MainActivity;
import com.example.wifizhilian.SysApp;
import com.example.wifizhilian.data.AppConfig;
import com.example.wifizhilian.libs.ComUtils;
import com.example.wifizhilian.libs.NetInterface;

public class UserInfoPage extends LinearLayout {
    private TextView mAccountView;
    private Context mContext;
    private EditText mEmailView;
    Handler mHandler = new C03481();
    private EditText mNickNameView;
    private PageHead mPageHead;
    private EditText mPasswordView;
    private EditText mPhoneView;
    Runnable mSaveInfoRun = new C03492();
    private Button mSubmitView;
    private ProgressDialog mWaitDialog;

    /* renamed from: com.example.wifizhilian.view.UserInfoPage$1 */
    class C03481 extends Handler {
        C03481() {
        }

        @SuppressLint("WrongConstant")
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case NetInterface.Net_DataSubmit_OK /*20003*/:
                    Toast.makeText(UserInfoPage.this.mContext, R.string.str_datasubmit_ok, 0).show();
                    UserInfoPage.this.mPasswordView.setText("");
                    break;
                case NetInterface.Net_Data_Error /*20019*/:
                    Toast.makeText(UserInfoPage.this.mContext, R.string.str_error_net, 0).show();
                    break;
            }
            UserInfoPage.this.mWaitDialog.dismiss();
            UserInfoPage.this.mWaitDialog = null;
        }
    }

    /* renamed from: com.example.wifizhilian.view.UserInfoPage$2 */
    class C03492 implements Runnable {
        C03492() {
        }

        public void run() {
            AppConfig _config = SysApp.getMe().getConfig();
            String NickName = UserInfoPage.this.mNickNameView.getText().toString();
            String password = UserInfoPage.this.mPasswordView.getText().toString();
            String phone = UserInfoPage.this.mPhoneView.getText().toString();
            new NetInterface(UserInfoPage.this.mContext, UserInfoPage.this.mHandler).SaveInfo(NickName, password, UserInfoPage.this.mEmailView.getText().toString(), phone);
        }
    }

    /* renamed from: com.example.wifizhilian.view.UserInfoPage$3 */
    class C03503 implements OnClickListener {
        C03503() {
        }

        public void onClick(View v) {
            ((MainActivity) UserInfoPage.this.mContext).SelectMenuItem(R.string.menu_item_mydevice);
        }
    }

    /* renamed from: com.example.wifizhilian.view.UserInfoPage$4 */
    class C03514 implements OnClickListener {
        C03514() {
        }

        @SuppressLint("WrongConstant")
        public void onClick(View v) {
            if (ComUtils.StrIsEmpty(UserInfoPage.this.mNickNameView.getText().toString())) {
                Toast.makeText(UserInfoPage.this.mContext, R.string.str_input_nickname, 0).show();
            } else if (ComUtils.checkEmail(UserInfoPage.this.mEmailView.getText().toString())) {
                UserInfoPage.this.ShowWaitDialog(UserInfoPage.this.getResources().getString(R.string.str_submit_sys));
                new Thread(UserInfoPage.this.mSaveInfoRun).start();
            } else {
                Toast.makeText(UserInfoPage.this.mContext, R.string.str_error_email, 0).show();
            }
        }
    }

    public UserInfoPage(Context context) {
        super(context);
        this.mContext = context;
        AppConfig _config = SysApp.getMe().getConfig();
        View layout = ((LayoutInflater) this.mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.page_userinfo, this);
        this.mPageHead = (PageHead) findViewById(R.id.UserInfopageHead);
        this.mPageHead.LeftBnt.setImageResource(R.drawable.menu_2);
        this.mPageHead.LeftBnt.setOnClickListener(new C03503());
        this.mPageHead.RightBnt.setVisibility(View.INVISIBLE);
        this.mPageHead.TitleView.setText(R.string.menu_item_user);
        this.mAccountView = (TextView) findViewById(R.id.AccounttextView);
        this.mPasswordView = (EditText) findViewById(R.id.PassWordeditText);
        this.mNickNameView = (EditText) findViewById(R.id.NickNameEditText);
        this.mPhoneView = (EditText) findViewById(R.id.PhoneEditText);
        this.mEmailView = (EditText) findViewById(R.id.EmailEditText);
        this.mAccountView.setText(_config.getAccount());
        this.mNickNameView.setText(_config.getNickName());
        this.mPhoneView.setText(_config.getPhone());
        this.mEmailView.setText(_config.getEMail());
        this.mSubmitView = (Button) findViewById(R.id.Submitbutton);
        this.mSubmitView.setOnClickListener(new C03514());
    }

    private void ShowWaitDialog(String info) {
        this.mWaitDialog = ProgressDialog.show(this.mContext, getResources().getString(R.string.str_title_wait), info);
    }
}
