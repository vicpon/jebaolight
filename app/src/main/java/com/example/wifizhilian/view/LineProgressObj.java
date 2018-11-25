package com.example.wifizhilian.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.example.wifizhilian.R;

public class LineProgressObj extends LinearLayout {
    private static final String TAG = "LineProgressObj";
    private int MaxVal = 255;
    private ImageView mDotView;
    private int mDotViewRadius;
    private ImageView mLineView;
    private CirProgressListener mListener = null;
    private int mProgresVal;
    private TextView mProgresView;
    private int mWidth;

    public LineProgressObj(Context context) {
        super(context, null);
    }

    public LineProgressObj(Context context, AttributeSet attrs) {
        super(context, attrs);
        View layout = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.lineprogress_myobj, this);
        this.mDotView = (ImageView) layout.findViewById(R.id.DotimageView);
        this.mLineView = (ImageView) layout.findViewById(R.id.ProgLineBgimageView);
        this.mProgresView = (TextView) layout.findViewById(R.id.ProgressValtextView);
    }

    public int getProgres() {
        return this.mProgresVal;
    }

    public int getRate() {
        return (this.mProgresVal * 100) / this.MaxVal;
    }

    public void setProgres(int progres) {
        this.mProgresVal = progres;
        if (this.mProgresVal < 0) {
            this.mProgresVal = 0;
        }
        if (this.mProgresVal > this.MaxVal) {
            this.mProgresVal = this.MaxVal;
        }
        drawProgress((this.mProgresVal * this.mWidth) / this.MaxVal);
    }

    public void onTouchListener(CirProgressListener l) {
        this.mListener = l;
    }

    public boolean onTouchEvent(MotionEvent ev) {
        int cX;
        switch (ev.getAction()) {
            case 0:
                cX = (int) ev.getX();
                if (cX < 0) {
                    cX = 0;
                }
                if (cX > this.mWidth) {
                    cX = this.mWidth;
                }
                drawProgress(cX);
                this.mProgresVal = (this.MaxVal * cX) / this.mWidth;
                if (this.mListener != null) {
                    this.mListener.onTouchIng(this.mProgresVal);
                    break;
                }
                break;
            case 1:
                if (this.mListener != null) {
                    this.mListener.onTouchOver(this.mProgresVal);
                    break;
                }
                break;
            case 2:
                cX = (int) ev.getX();
                if (cX < 0) {
                    cX = 0;
                }
                if (cX > this.mWidth) {
                    cX = this.mWidth;
                }
                drawProgress(cX);
                this.mProgresVal = (this.MaxVal * cX) / this.mWidth;
                if (this.mListener != null) {
                    this.mListener.onTouchIng(this.mProgresVal);
                    break;
                }
                break;
        }
        return true;
    }

    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int dotW = this.mDotView.getWidth();
        if (dotW == 0) {
            dotW = (int) (((float) (b - t)) * 1.25f);
        }
        this.mDotViewRadius = dotW / 2;
        this.mWidth = (r - l) - dotW;
        if (this.mProgresVal > 0) {
            drawProgress((this.mProgresVal * this.mWidth) / this.MaxVal);
        }
        super.onLayout(changed, l, t, r, b);
    }

    private void drawProgress(int cx) {
        int pointx = cx - this.mDotViewRadius;
        if (pointx < 0) {
            pointx = 0;
        } else if (pointx > this.mWidth) {
            pointx = this.mWidth;
        }
        this.mDotView.setX((float) pointx);
        this.mLineView.setX((float) pointx);
        this.mProgresView.setText(getRate() + "%");
    }

    public void setMaxVal(int val) {
        this.MaxVal = val;
    }

    public int getMaxVal(int val) {
        return this.MaxVal;
    }
}
