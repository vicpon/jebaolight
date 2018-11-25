package com.songnick.blogdemo.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Rect;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.support.v4.widget.ViewDragHelper.Callback;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

public class SwitchLayout extends FrameLayout {
    private int mDragDistance;
    private ViewDragHelper mDragHelper;
    private Callback mDragHelperCallback;
    private float mScaleFraction;

    public enum Status {
        Open,
        Middle,
        Close
    }

    /* renamed from: com.songnick.blogdemo.widget.SwitchLayout$1 */
    class C06321 extends Callback {
        private int distance = 0;

        C06321() {
        }

        public boolean tryCaptureView(View arg0, int arg1) {
            return true;
        }

        public int clampViewPositionHorizontal(View child, int left, int dx) {
            if (child == SwitchLayout.this.getSurfaceView()) {
                if (left < SwitchLayout.this.getPaddingLeft()) {
                    return SwitchLayout.this.getPaddingLeft();
                }
                if (left > SwitchLayout.this.getPaddingLeft() + SwitchLayout.this.mDragDistance) {
                    return SwitchLayout.this.getPaddingLeft() + SwitchLayout.this.mDragDistance;
                }
                return left;
            } else if (child != SwitchLayout.this.getBottomView() || left <= SwitchLayout.this.getPaddingLeft()) {
                return left;
            } else {
                return SwitchLayout.this.getPaddingLeft();
            }
        }

        public int getViewHorizontalDragRange(View child) {
            return SwitchLayout.this.mDragDistance;
        }

        @SuppressLint({"NewApi"})
        public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
            this.distance += dx;
            if (changedView == SwitchLayout.this.getSurfaceView()) {
                SwitchLayout.this.processViewPositionChange(this.distance);
            } else if (changedView == SwitchLayout.this.getBottomView()) {
                SwitchLayout.this.processViewPositionChange(this.distance);
            }
            SwitchLayout.this.invalidate();
        }

        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            super.onViewReleased(releasedChild, xvel, yvel);
            SwitchLayout.this.handleSurfaceViewReleased(xvel, yvel);
        }
    }

    public SwitchLayout(Context context) {
        super(context);
        this.mDragHelper = null;
        this.mDragDistance = 0;
        this.mScaleFraction = 0.2f;
        this.mDragHelperCallback = new C06321();
    }

    public SwitchLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SwitchLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mDragHelper = null;
        this.mDragDistance = 0;
        this.mScaleFraction = 0.2f;
        this.mDragHelperCallback = new C06321();
        this.mDragHelper = ViewDragHelper.create(this, this.mDragHelperCallback);
    }

    @SuppressLint({"NewApi"})
    private void processViewPositionChange(int distance) {
        getSurfaceView().layout(distance, (int) (((float) distance) * this.mScaleFraction), getSurfaceView().getMeasuredWidth() + distance, getSurfaceView().getMeasuredHeight() - ((int) (((float) distance) * this.mScaleFraction)));
        Rect rect = computBottomRelayout(distance);
        getBottomView().layout(rect.left, rect.top, rect.right, rect.bottom);
        getBottomView().setAlpha((((float) distance) * 1.0f) / ((float) this.mDragDistance));
    }

    private Rect computBottomRelayout(int distance) {
        Rect rect = computeBottomView(false);
        rect.left = 0;
        rect.top -= (int) ((((((float) distance) * 1.0f) / ((float) this.mDragDistance)) * ((float) this.mDragDistance)) * this.mScaleFraction);
        rect.right += (int) ((((((float) distance) * 1.0f) / ((float) this.mDragDistance)) * ((float) this.mDragDistance)) * this.mScaleFraction);
        rect.bottom += (int) ((((((float) distance) * 1.0f) / ((float) this.mDragDistance)) * ((float) this.mDragDistance)) * this.mScaleFraction);
        return rect;
    }

    private void handleSurfaceViewReleased(float xvel, float yvel) {
        if (xvel > 0.0f) {
            processSurfaceViewReleased(true);
        } else if (xvel < 0.0f) {
            processSurfaceViewReleased(false);
        } else {
            this.mDragHelper.settleCapturedViewAt(0, 0);
        }
    }

    private void processSurfaceViewReleased(boolean open) {
        Rect rect = computSurfaceView(open);
        this.mDragHelper.smoothSlideViewTo(getSurfaceView(), rect.left, rect.top);
        invalidate();
    }

    public Status getViewStatus() {
        if (getSurfaceView().getLeft() == getPaddingLeft() && getSurfaceView().getTop() == getPaddingTop()) {
            return Status.Close;
        }
        if (getSurfaceView().getLeft() == getPaddingLeft() + this.mDragDistance) {
            return Status.Open;
        }
        return Status.Middle;
    }

    public int getDragDistance() {
        return this.mDragDistance;
    }

    public void setDragDistance(int dragDistance) {
        this.mDragDistance = dragDistance;
        requestLayout();
    }

    public void setScaleFraction(float scaleF) {
        this.mScaleFraction = scaleF;
    }

    public Rect computSurfaceView(boolean open) {
        int l = getPaddingLeft();
        int t = getPaddingTop();
        if (open) {
            l = getPaddingLeft() + this.mDragDistance;
        }
        return new Rect(l, t, getMeasuredWidth() + l, getMeasuredHeight() + t);
    }

    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return this.mDragHelper.shouldInterceptTouchEvent(ev);
    }

    public boolean onTouchEvent(MotionEvent event) {
        this.mDragHelper.processTouchEvent(event);
        return true;
    }

    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        layoutBottomView();
    }

    private void layoutBottomView() {
        Rect rect = computeBottomView(false);
        getBottomView().layout(rect.left, rect.top, rect.right, rect.bottom);
        invalidate();
    }

    private Rect computeBottomView(boolean open) {
        Rect rect = new Rect();
        rect.left = getPaddingLeft();
        rect.top = (int) (((float) this.mDragDistance) * this.mScaleFraction);
        if (!open) {
            rect.right = (int) (((float) (rect.left + getBottomView().getMeasuredWidth())) - (((float) this.mDragDistance) * this.mScaleFraction));
        }
        rect.bottom = getMeasuredHeight() - rect.top;
        return rect;
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        this.mDragDistance = getBottomView().getMeasuredWidth();
    }

    public void computeScroll() {
        super.computeScroll();
        if (this.mDragHelper.continueSettling(true)) {
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    public ViewGroup getSurfaceView() {
        return (ViewGroup) getChildAt(1);
    }

    public ViewGroup getBottomView() {
        return (ViewGroup) getChildAt(0);
    }

    public void ShowLeft() {
        processViewPositionChange(400);
    }

    public void CloseLeft() {
        processViewPositionChange(0);
    }
}
