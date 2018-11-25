package com.example.wifizhilian.view;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.view.KeyEvent;
import com.example.wifizhilian.R;

public class WaitDialog {
    public boolean isOpen = false;
    private Context mContext;
    private Dialog mWaitDialog;

    /* renamed from: com.example.wifizhilian.view.WaitDialog$1 */
    class C03521 implements OnKeyListener {
        C03521() {
        }

        public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
            WaitDialog.this.Close();
            return false;
        }
    }

    public WaitDialog(Context context) {
        this.mContext = context;
    }

    public void Show() {
        Show((int) R.string.str_please_wait);
    }

    public void Show(int res) {
        Show(this.mContext.getResources().getString(res));
    }

    public void Show(String msg) {
        if (this.mWaitDialog == null) {
            this.isOpen = true;
            this.mWaitDialog = LoadingDialog.createLoadingDialog(this.mContext, msg);
            this.mWaitDialog.setOnKeyListener(new C03521());
            this.mWaitDialog.show();
        }
    }

    public void Close() {
        this.isOpen = false;
        if (this.mWaitDialog != null) {
            this.mWaitDialog.dismiss();
            this.mWaitDialog = null;
        }
    }
}
