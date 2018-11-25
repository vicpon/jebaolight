package com.example.wifizhilian.view;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import com.example.wifizhilian.R;

public class LoadingDialog {
    public static Dialog createLoadingDialog(Context context, String msg) {
        View view = LayoutInflater.from(context).inflate(R.layout.view_load_dialog, null);
        LinearLayout layout = (LinearLayout) view.findViewById(R.id.dialog_view);
        TextView tipText = (TextView) view.findViewById(R.id.tipTextView);
        ((ImageView) view.findViewById(R.id.img)).startAnimation(AnimationUtils.loadAnimation(context, R.anim.dialog_load_animation));
        tipText.setText(msg);
        Dialog loadingDialog = new Dialog(context, R.style.loading_dialog);
        loadingDialog.setCancelable(false);
        loadingDialog.setContentView(layout, new LayoutParams(-1, -1));
        return loadingDialog;
    }
}
