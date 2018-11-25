package com.example.wifizhilian.view;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.graphics.Point;
import android.support.v4.internal.view.SupportMenu;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import com.example.wifizhilian.libs.ComUtils;
import java.util.ArrayList;

public class SingShowView extends View {
    private static final int CIRCLE_SIZE = 20;
    private static final int DEFAULT_COLOR = -16777216;
    private static final int FONT_SIZE = 8;
    private static final String TAG = "LineGraphicView";
    private int canvasHeight;
    private int canvasWidth;
    private DisplayMetrics dm;
    private int fontsize;
    private int gridheight;
    private int gridwidh;
    private boolean isChange;
    private boolean isInit;
    private boolean isMeasure;
    private int mBottomVal;
    private int[] mColorArr;
    private Context mContext;
    private int mHeightVal;
    private Linestyle mStyle;
    private int marginBottom;
    private int marginLeft;
    private int marginRight;
    private int marginTop;
    private int maxValue;
    private Resources res;
    private int xHrNum;
    private int xHrVal;
    private ArrayList<String> xRawDatas;
    private String xRawInfo;
    private int yHrNum;
    private int yHrVal;
    private String yRawInfo;

    private enum Linestyle {
        Line,
        Curve
    }

    public SingShowView(Context context) {
        this(context, null);
    }

    public SingShowView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mStyle = Linestyle.Line;
        this.gridwidh = 0;
        this.gridheight = 0;
        this.xHrNum = 1;
        this.yHrNum = 16;
        this.xHrVal = 0;
        this.yHrVal = 0;
        this.isMeasure = true;
        this.isChange = false;
        this.isInit = false;
        this.xRawInfo = "";
        this.yRawInfo = "";
        this.mBottomVal = 0;
        this.mColorArr = new int[]{SupportMenu.CATEGORY_MASK, -16738048};
        this.mContext = context;
        initView();
    }

    private void initView() {
        this.res = this.mContext.getResources();
        this.dm = new DisplayMetrics();
        ((WindowManager) this.mContext.getSystemService("window")).getDefaultDisplay().getMetrics(this.dm);
    }

    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        if (this.isMeasure) {
            this.canvasHeight = getHeight();
            this.canvasWidth = getWidth();
            this.marginLeft = dip2px(20.0f);
            this.marginRight = dip2px(20.0f);
            this.marginTop = dip2px(30.0f);
            this.marginBottom = dip2px(40.0f);
            this.fontsize = dip2px(8.0f);
            if (this.gridheight == 0) {
                this.gridheight = (this.canvasHeight - this.marginBottom) - this.marginTop;
            }
            this.gridwidh = this.canvasWidth - this.marginLeft;
            if (this.xHrVal == 0 || this.yHrVal == 0) {
                this.xHrVal = this.gridwidh / this.yHrNum;
                this.yHrVal = this.gridheight / this.xHrNum;
            }
            this.isMeasure = false;
        }
    }

    protected void onDraw(Canvas canvas) {
        Paint mPaint = new Paint(1);
        mPaint.setColor(-16777216);
        drawAllXLine(canvas, mPaint);
        drawAllYLine(canvas, mPaint);
        drawMoreInfo(canvas);
        this.isInit = true;
    }

    private void drawAllXLine(Canvas canvas, Paint mPaint) {
        for (int i = 0; i < this.xHrNum; i++) {
            int y = (this.canvasHeight - (this.yHrVal * i)) - this.marginBottom;
            canvas.drawLine((float) this.marginLeft, (float) y, (float) this.gridwidh, (float) y, mPaint);
        }
    }

    private void drawAllYLine(Canvas canvas, Paint mPaint) {
        for (int i = 0; i < this.yHrNum; i++) {
            int x = this.marginLeft + (this.xHrVal * i);
            canvas.drawLine((float) x, (float) this.marginTop, (float) x, (float) (this.gridheight + this.marginTop), mPaint);
            if (this.xRawDatas.size() > i) {
                drawText((String) this.xRawDatas.get(i), x - this.fontsize, (this.gridheight + this.marginTop) + (this.fontsize * 2), canvas);
            }
        }
    }

    private void drawDataPoint(Canvas canvas, Paint mPaint) {
        mPaint.setColor(this.mColorArr[0]);
        mPaint.setStrokeWidth((float) dip2px(3.0f));
        mPaint.setStyle(Style.STROKE);
        mPaint.setStyle(Style.FILL);
    }

    private void drawMoreInfo(Canvas canvas) {
        int infoX = this.marginLeft;
        int fsize = dip2px(10.0f);
        Paint mPaint = new Paint(1);
        mPaint.setColor(-16777216);
        mPaint.setStyle(Style.FILL);
        mPaint.setTextSize((float) fsize);
        mPaint.setTextAlign(Align.LEFT);
        if (!ComUtils.StrIsEmpty(this.yRawInfo)) {
            canvas.drawText(this.yRawInfo, (float) infoX, (float) (this.marginTop - (fsize / 2)), mPaint);
            infoX = (int) (((double) (getTextWidth(mPaint, this.yRawInfo) + infoX)) + (((double) fsize) * 1.5d));
        }
        if (!ComUtils.StrIsEmpty(this.xRawInfo)) {
            Canvas canvas2 = canvas;
            canvas2.drawText(this.xRawInfo, (float) (this.gridwidh - getTextWidth(mPaint, this.xRawInfo)), (float) ((this.gridheight + this.marginTop) + fsize), mPaint);
        }
        int minH = (this.mBottomVal * this.gridheight) / this.maxValue;
        int maxH = ((this.mHeightVal * this.gridheight) / this.maxValue) - minH;
        int wp = (this.yHrNum - 1) * this.xHrVal;
        int ys = ((this.canvasHeight - this.marginBottom) - (maxH / 2)) - minH;
        float angular = 6.2831855f / ((float) wp);
        Point startp = new Point();
        Point endp = new Point();
        startp.x = this.marginLeft;
        startp.y = this.canvasHeight - this.marginBottom;
        for (int ii = 0; ii < wp; ii++) {
            endp.x = this.marginLeft + ii;
            endp.y = ys - ((int) (((float) (maxH / 2)) * ((float) Math.sin((double) (((float) ii) * angular)))));
            canvas.drawLine((float) startp.x, (float) startp.y, (float) endp.x, (float) endp.y, mPaint);
            startp.x = endp.x;
            startp.y = endp.y;
        }
    }

    private void drawText(String text, int x, int y, Canvas canvas) {
        Paint p = new Paint(1);
        p.setTextSize((float) dip2px((float) this.fontsize));
        p.setColor(-16777216);
        p.setTextAlign(Align.LEFT);
        canvas.drawText(text, (float) x, (float) y, p);
    }

    public void setXRawTitle(String Titles) {
        this.xRawDatas = new ArrayList();
        String[] titles = Titles.split(",");
        for (Object add : titles) {
            this.xRawDatas.add((String) add);
        }
    }

    public void setXRawTitle(ArrayList<String> xRawData) {
        this.xRawDatas = xRawData;
    }

    public void setCoordsValue(int maxValue, int xLinNum, int ylineNum) {
        this.maxValue = maxValue;
        this.xHrNum = xLinNum;
        this.yHrNum = ylineNum;
        this.mHeightVal = maxValue;
        this.mBottomVal = 0;
    }

    public void setMaxValue(int maxValue) {
        this.maxValue = maxValue;
    }

    public void setMarginTop(int marginTop) {
        this.marginTop = marginTop;
    }

    public void setMarginBot(int marginBottom) {
        this.marginBottom = marginBottom;
    }

    public void setMstyle(Linestyle mStyle) {
        this.mStyle = mStyle;
    }

    private int dip2px(float dpValue) {
        return (int) ((this.dm.xdpi / 160.0f) * dpValue);
    }

    public void setColorArr(int[] mColorArr) {
        this.mColorArr = mColorArr;
    }

    public String getXRawInfo() {
        return this.xRawInfo;
    }

    public void setXRawInfo(String xRawInfo) {
        this.xRawInfo = xRawInfo;
    }

    public String getYRawInfo() {
        return this.yRawInfo;
    }

    public void setYRawInfo(String yRawInfo) {
        this.yRawInfo = yRawInfo;
    }

    private int getTextWidth(Paint paint, String str) {
        int iRet = 0;
        if (str != null && str.length() > 0) {
            int len = str.length();
            float[] widths = new float[len];
            paint.getTextWidths(str, widths);
            for (int j = 0; j < len; j++) {
                iRet += (int) Math.ceil((double) widths[j]);
            }
        }
        return iRet;
    }

    public int getHeightVal() {
        return this.mHeightVal;
    }

    public void setHeightVal(int mHeightVal) {
        this.mHeightVal = mHeightVal;
        if (this.mHeightVal > this.maxValue) {
            this.mHeightVal = this.maxValue;
        }
        if (this.mHeightVal < this.mBottomVal) {
            this.mHeightVal = this.mBottomVal;
        }
        invalidate();
    }

    public int getBottomval() {
        return this.mBottomVal;
    }

    public void setBottomval(int mBottomval) {
        this.mBottomVal = mBottomval;
        if (this.mBottomVal < 0) {
            this.mBottomVal = 0;
        }
        if (this.mBottomVal > this.mHeightVal) {
            this.mBottomVal = this.mHeightVal;
        }
        invalidate();
    }

    public int getXHrNum() {
        return this.xHrNum;
    }

    public void setXHrNum(int xHrNum) {
        this.xHrNum = xHrNum;
    }

    public int getYHrNum() {
        return this.yHrNum;
    }

    public void setYHrNum(int yHrNum) {
        this.yHrNum = yHrNum;
    }
}
