package com.example.wifizhilian.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import com.example.wifizhilian.R;
import com.example.wifizhilian.libs.xLog;

@SuppressLint({"NewApi"})
public class LineArecProgressObj extends LinearLayout {
    private static final String TAG = "LineProgressObj";
    private int MaxVal = 255;
    private boolean isInitLayout = false;
    private int isTouchObj = 0;
    private ImageView mArecLineView;
    private int mDotViewRadius;
    private ImageView mLeftDotView;
    private int mLeftVal = 0;
    private TextView mLeftValView;
    private int mLeftX;
    private CirProgressListener mListener = null;
    private ImageView mRightDotView;
    private int mRightVal = 0;
    private TextView mRightValView;
    private int mRightX;
    private int mWidth;

    public LineArecProgressObj(Context context) {
        super(context, null);
    }

    public LineArecProgressObj(Context context, AttributeSet attrs) {
        super(context, attrs);
        View layout = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.linearecprogress_myobj, this);
        this.mLeftDotView = (ImageView) layout.findViewById(R.id.LeftDotimageView);
        this.mRightDotView = (ImageView) layout.findViewById(R.id.RightDotimageView);
        this.mArecLineView = (ImageView) layout.findViewById(R.id.ArecLine1imageView);
        this.mLeftValView = (TextView) layout.findViewById(R.id.LeftValtextView);
        this.mRightValView = (TextView) layout.findViewById(R.id.RightValtextView);
    }

    public int getLeftRate() {
        return (this.mLeftVal * 100) / this.MaxVal;
    }

    public int getRightRate() {
        return (this.mRightVal * 100) / this.MaxVal;
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
                if (Math.abs(cX - this.mRightX) > Math.abs(cX - this.mLeftX)) {
                    this.isTouchObj = 1;
                    upLeftData(cX);
                } else if ((this.mRightVal * 100) / this.MaxVal <= 98 || (this.mLeftX * 100) / this.MaxVal <= 85) {
                    this.isTouchObj = 2;
                    upRightData(cX);
                } else {
                    this.isTouchObj = 1;
                    upLeftData(cX);
                }
                if (this.mListener != null && this.isTouchObj > 0) {
                    int i;
                    CirProgressListener cirProgressListener = this.mListener;
                    int i2 = this.isTouchObj;
                    if (this.isTouchObj == 1) {
                        i = this.mLeftVal;
                    } else {
                        i = this.mRightVal;
                    }
                    cirProgressListener.onArecTouchIng(i2, i);
                    break;
                }
                break;
            case 1:
                if (this.mListener != null && this.isTouchObj > 0) {
                    this.mListener.onArecTouchOver(this.isTouchObj, this.isTouchObj == 1 ? this.mLeftVal : this.mRightVal);
                }
                this.isTouchObj = 0;
                break;
            case 2:
                cX = (int) ev.getX();
                if (cX < 0) {
                    cX = 0;
                }
                if (cX > this.mWidth) {
                    cX = this.mWidth;
                }
                if (this.isTouchObj > 0) {
                    if (this.isTouchObj == 1) {
                        upLeftData(cX);
                    } else {
                        upRightData(cX);
                    }
                    if (this.mListener != null) {
                        this.mListener.onArecTouchIng(this.isTouchObj, this.isTouchObj == 1 ? this.mLeftVal : this.mRightVal);
                        break;
                    }
                }
                break;
        }
        return true;
    }

    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int dotW = this.mLeftDotView.getWidth();
        if (dotW == 0) {
            dotW = (int) (((float) (b - t)) * 1.25f);
        }
        this.mDotViewRadius = dotW / 2;
        this.mWidth = (r - l) - dotW;
        if (!this.isInitLayout && this.mWidth > 0) {
            xLog.m5i(TAG, "onLayout mWidth:" + this.mWidth + "   isInitLayout:" + this.isInitLayout);
            if (this.mLeftVal > 0) {
                drawLeftDot((this.mLeftVal * this.mWidth) / this.MaxVal);
            }
            drawRightDot((this.mRightVal * this.mWidth) / this.MaxVal);
            this.isInitLayout = true;
        }
        super.onLayout(changed, l, t, r, b);
    }

    private void upLeftData(int cx) {
        this.mLeftVal = (this.MaxVal * cx) / this.mWidth;
        if (this.mLeftVal > this.mRightVal) {
            this.mLeftVal = this.mRightVal;
        }
        drawLeftDot(cx);
    }

    private void upRightData(int cx) {
        this.mRightVal = (this.MaxVal * cx) / this.mWidth;
        if (this.mRightVal < this.mLeftVal) {
            this.mRightVal = this.mLeftVal;
        }
        drawRightDot(cx);
    }

    private void drawLeftDot(int cx) {
        if (this.mWidth > 0) {
            this.mLeftX = cx - this.mDotViewRadius;
            if (this.mLeftX < 0) {
                this.mLeftX = 0;
            } else if (this.mLeftX > this.mRightX) {
                this.mLeftX = this.mRightX;
            }
            this.mLeftDotView.setX((float) this.mLeftX);
            this.mArecLineView.setLayoutParams(new LayoutParams(this.mRightX - this.mLeftX, -1));
            this.mArecLineView.setX((float) this.mLeftX);
            this.mLeftValView.setText(getLeftRate() + "%");
        }
    }

    private void drawRightDot(int cx) {
        if (this.mWidth > 0) {
            this.mRightX = cx - this.mDotViewRadius;
            if (this.mRightX < this.mLeftX) {
                this.mRightX = this.mLeftX;
            } else if (this.mRightX > this.mWidth) {
                this.mRightX = this.mWidth;
            }
            this.mRightDotView.setX((float) this.mRightX);
            this.mArecLineView.setLayoutParams(new LayoutParams(this.mRightX - this.mLeftX, -1));
            this.mArecLineView.setX((float) this.mLeftX);
            this.mRightValView.setText(getRightRate() + "%");
        }
    }

    public void setMaxVal(int val) {
        this.MaxVal = val;
    }

    public int getMaxVal() {
        return this.MaxVal;
    }

    public int getLeftVal() {
        return this.mLeftVal;
    }

    public void setLeftVal(int leftVal) {
        this.mLeftVal = leftVal;
        if (this.mLeftVal < 0) {
            this.mLeftVal = 0;
        }
        if (this.mLeftVal > this.mRightVal) {
            this.mLeftVal = this.mRightVal;
        }
        drawLeftDot((this.mLeftVal * this.mWidth) / this.MaxVal);
    }

    public int getRightVal() {
        return this.mRightVal;
    }

    public void setRightVal(int rightVal) {
        this.mRightVal = rightVal;
        if (this.mRightVal > this.MaxVal) {
            this.mRightVal = this.MaxVal;
        }
        if (this.mRightVal < this.mLeftVal) {
            this.mRightVal = this.mLeftVal;
        }
        drawRightDot((this.mRightVal * this.mWidth) / this.MaxVal);
    }
}
