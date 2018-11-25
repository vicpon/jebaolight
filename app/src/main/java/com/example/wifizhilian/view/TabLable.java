package com.example.wifizhilian.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.example.wifizhilian.R;

public class TabLable extends LinearLayout {
    private Context mContext;

    public TabLable(Context context, int title, int icon) {
        super(context);
        this.mContext = context;
        View layout = ((LayoutInflater) this.mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.view_tablable, this);
        ((ImageView) findViewById(R.id.IconimageView)).setImageResource(icon);
        ((TextView) findViewById(R.id.TitletextView)).setText(title);
    }
}
