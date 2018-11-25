package com.example.wifizhilian.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.example.wifizhilian.R;

public class PageHead extends RelativeLayout {
    public ImageView LeftBnt;
    public ImageView RightBnt;
    public TextView TitleView;
    private Context mContext;
    private String title;

    public PageHead(Context context) {
        super(context);
        InitView();
    }

    public PageHead(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        InitView();
    }

    private void InitView() {
        View layout = ((LayoutInflater) this.mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.view_pagehead, this);
        this.LeftBnt = (ImageView) findViewById(R.id.HeadLeftimageView);
        this.RightBnt = (ImageView) findViewById(R.id.HeadRightimageView);
        this.TitleView = (TextView) findViewById(R.id.HeadTitletextView);
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
        this.TitleView.setText(title);
    }
}
