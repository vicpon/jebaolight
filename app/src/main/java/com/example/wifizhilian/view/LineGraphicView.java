package com.example.wifizhilian.view;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Point;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import com.example.wifizhilian.libs.ComUtils;
import java.util.ArrayList;

public class LineGraphicView extends View {
    private static final int CIRCLE_SIZE = 20;
    private static final int DEFAULT_COLOR = -16777216;
    private static final int FONT_SIZE = 8;
    public static final int ON_TOUCH_OVER = 30001;
    private static final String TAG = "LineGraphicView";
    private ArrayList<ArrayList<PointObj>> Points;
    private int averageValue;
    private int canvasHeight;
    private int canvasWidth;
    private DisplayMetrics dm;
    private int fontsize;
    private int gridheight;
    private int gridwidh;
    private boolean isChange;
    private boolean isInit;
    private boolean isMeasure;
    private int[] mColorArr;
    private Context mContext;
    private int mCurLine;
    private PointObj mCurMovePoint;
    private Handler mHandler;
    private boolean mIsPress;
    private ArrayList<ArrayList<Integer>> mSourData;
    private Linestyle mStyle;
    private int marginBottom;
    private int marginLeft;
    private int marginRight;
    private int marginTop;
    private int maxValue;
    private Resources res;
    private ArrayList<PointObj> tagPoints;
    private int xHrVal;
    private ArrayList<String> xRawDatas;
    private String xRawInfo;
    private int yHrVal;
    private String yRawInfo;

    private enum Linestyle {
        Line,
        Curve
    }

    class PointObj {
        int Num;
        int Val;
        /* renamed from: X */
        int f9X;
        /* renamed from: Y */
        int f10Y;

        public PointObj(int val, int num, int x, int y) {
            this.Val = val;
            this.Num = num;
            this.f9X = x;
            this.f10Y = y;
        }
    }

    public LineGraphicView(Context context) {
        this(context, null);
    }

    public LineGraphicView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mStyle = Linestyle.Line;
        this.gridwidh = 0;
        this.gridheight = 0;
        this.xHrVal = 0;
        this.yHrVal = 0;
        this.isMeasure = true;
        this.isChange = false;
        this.isInit = false;
        this.tagPoints = new ArrayList();
        this.mSourData = new ArrayList();
        this.Points = new ArrayList();
        this.mCurMovePoint = new PointObj(0, 0, 0, 0);
        this.mCurLine = 0;
        this.mIsPress = false;
        this.xRawInfo = "";
        this.yRawInfo = "";
        this.mHandler = null;
        this.mContext = context;
        initView();
    }

    private void initView() {
        this.res = this.mContext.getResources();
        this.dm = new DisplayMetrics();
        ((WindowManager) this.mContext.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(this.dm);
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
            if (this.mSourData.size() > 0 && (this.xHrVal == 0 || this.yHrVal == 0)) {
                this.xHrVal = this.gridwidh / (((ArrayList) this.mSourData.get(0)).size() + 1);
                this.yHrVal = this.gridheight / (this.maxValue / this.averageValue);
            }
            this.isMeasure = false;
        }
    }

    protected void onDraw(Canvas canvas) {
        if (!this.isInit) {
            initPoints();
        }
        Paint mPaint = new Paint(1);
        mPaint.setColor(-16777216);
        drawAllXLine(canvas, mPaint);
        drawAllYLine(canvas, mPaint);
        for (int ii = 0; ii < this.Points.size(); ii++) {
            if (ii != this.mCurLine) {
                drawDataPoint(canvas, mPaint, ii);
            }
        }
        drawDataPoint(canvas, mPaint, this.mCurLine);
        this.isInit = true;
    }

    public boolean onTouchEvent(MotionEvent event) {
        int currentX = (int) event.getX();
        int currentY = (int) event.getY();
        int y;
        if (event.getAction() == 0) {
            int x;
            y = 0;
            while (y < ((ArrayList) this.Points.get(this.mCurLine)).size()) {
                if (Math.abs(currentX - ((PointObj) ((ArrayList) this.Points.get(this.mCurLine)).get(y)).f9X) < 20 && Math.abs(currentY - ((PointObj) ((ArrayList) this.Points.get(this.mCurLine)).get(y)).f10Y) < 20) {
                    this.mCurMovePoint = (PointObj) ((ArrayList) this.Points.get(this.mCurLine)).get(y);
                    this.mIsPress = true;
                    break;
                }
                y++;
            }
            if (!this.mIsPress) {
                x = this.Points.size() - 1;
                while (x >= 0) {
                    y = 0;
                    while (y < ((ArrayList) this.Points.get(x)).size() && x != this.mCurLine) {
                        if (Math.abs(currentX - ((PointObj) ((ArrayList) this.Points.get(x)).get(y)).f9X) < 20 && Math.abs(currentY - ((PointObj) ((ArrayList) this.Points.get(x)).get(y)).f10Y) < 20) {
                            this.mCurMovePoint = (PointObj) ((ArrayList) this.Points.get(x)).get(y);
                            this.mCurLine = x;
                            this.mIsPress = true;
                            break;
                        }
                        y++;
                    }
                    if (this.mIsPress) {
                        break;
                    }
                    x--;
                }
            }
            if (!this.mIsPress) {
                x = 0;
                while (x < this.tagPoints.size()) {
                    if (Math.abs(currentX - ((PointObj) this.tagPoints.get(x)).f9X) < 80 && Math.abs(currentY - (((PointObj) this.tagPoints.get(x)).f10Y + 60)) < 120) {
                        this.mCurMovePoint = (PointObj) ((ArrayList) this.Points.get(x)).get(0);
                        this.mCurLine = x;
                        break;
                    }
                    x++;
                }
            }
        } else if (event.getAction() == 1) {
            if (this.mHandler != null) {
                this.mHandler.sendEmptyMessage(ON_TOUCH_OVER);
            }
            this.mIsPress = false;
        } else if (event.getAction() == 2 && this.mIsPress) {
            y = 0;
            while (y < ((ArrayList) this.Points.get(this.mCurLine)).size()) {
                if (Math.abs(currentX - ((PointObj) ((ArrayList) this.Points.get(this.mCurLine)).get(y)).f9X) < 20) {
                    int ycoord = currentY;
                    this.mCurMovePoint = (PointObj) ((ArrayList) this.Points.get(this.mCurLine)).get(y);
                    if (currentY < this.marginTop) {
                        ycoord = this.marginTop;
                    }
                    if (currentY > this.gridheight + this.marginTop) {
                        ycoord = this.gridheight + this.marginTop;
                    }
                    ((PointObj) ((ArrayList) this.Points.get(this.mCurLine)).get(y)).f10Y = ycoord;
                    int val = (((this.gridheight - ycoord) + this.marginTop) * this.maxValue) / this.gridheight;
                    if (val < 0) {
                        val = 0;
                    } else if (val > 255) {
                        val = 255;
                    }
                    ((PointObj) ((ArrayList) this.Points.get(this.mCurLine)).get(y)).Val = val;
                } else {
                    y++;
                }
            }
        }
        invalidate();
        return true;
    }

    private void initPoints() {
        int x;
        this.Points.clear();
        for (x = 0; x < this.mSourData.size(); x++) {
            ArrayList<PointObj> newLine = new ArrayList();
            for (int ii = 0; ii < ((ArrayList) this.mSourData.get(x)).size(); ii++) {
                int xcoord = this.marginLeft + (this.xHrVal * ii);
                int ycoord = (this.gridheight + this.marginTop) - ((this.gridheight * ((Integer) ((ArrayList) this.mSourData.get(x)).get(ii)).intValue()) / this.maxValue);
                newLine.add(new PointObj(((Integer) ((ArrayList) this.mSourData.get(x)).get(ii)).intValue(), ii, xcoord, ycoord));
            }
            this.Points.add(newLine);
        }
        this.tagPoints.clear();
        int tagX = this.marginLeft;
        int tagPSize = dip2px(10.0f);
        int colw = (this.gridwidh - tagX) / this.Points.size();
        for (x = 0; x < this.mSourData.size(); x++) {
            this.tagPoints.add(new PointObj(0, x, tagX + (colw * x), this.marginTop - tagPSize));
        }
    }

    private void drawAllXLine(Canvas canvas, Paint mPaint) {
        int linNum = this.maxValue / this.averageValue;
        for (int i = 0; i < linNum + 1; i++) {
            int y = (this.canvasHeight - (this.yHrVal * i)) - this.marginBottom;
            canvas.drawLine((float) this.marginLeft, (float) y, (float) this.gridwidh, (float) y, mPaint);
        }
    }

    private void drawAllYLine(Canvas canvas, Paint mPaint) {
        int linNum = ((ArrayList) this.Points.get(0)).size();
        for (int i = 0; i < linNum; i++) {
            int x = this.marginLeft + (this.xHrVal * i);
            canvas.drawLine((float) x, (float) this.marginTop, (float) x, (float) (this.gridheight + this.marginTop), mPaint);
            if (this.xRawDatas.size() > i) {
                drawText((String) this.xRawDatas.get(i), x - this.fontsize, (this.gridheight + this.marginTop) + (this.fontsize * 2), canvas);
            }
        }
    }

    private void drawDataPoint(Canvas canvas, Paint mPaint, int lineNum) {
        if (this.Points.size() > lineNum) {
            Point[] points = getPoints(lineNum);
            if (this.mColorArr == null || this.mColorArr.length <= lineNum) {
                mPaint.setColor(-16777216);
            } else {
                mPaint.setColor(this.mColorArr[lineNum]);
            }
            mPaint.setStrokeWidth((float) dip2px(3.0f));
            mPaint.setStyle(Style.STROKE);
            if (this.mStyle == Linestyle.Curve) {
                drawScrollLine(canvas, points, mPaint);
            } else {
                drawLine(canvas, points, mPaint);
            }
            mPaint.setStyle(Style.FILL);
            int i = 0;
            while (i < ((ArrayList) this.Points.get(lineNum)).size()) {
                if (this.mCurMovePoint != null && lineNum == this.mCurLine && i == this.mCurMovePoint.Num) {
                    canvas.drawCircle((float) points[i].x, (float) points[i].y, 20.0f, mPaint);
                    drawMoveInfo(canvas);
                } else {
                    canvas.drawCircle((float) points[i].x, (float) points[i].y, 10.0f, mPaint);
                }
                i++;
            }
        }
    }

    private void drawScrollLine(Canvas canvas, Point[] points, Paint mPaint) {
        Point startp = new Point();
        Point endp = new Point();
        for (int i = 0; i < points.length - 1; i++) {
            startp = points[i];
            endp = points[i + 1];
            int wt = (startp.x + endp.x) / 2;
            Point p3 = new Point();
            Point p4 = new Point();
            p3.y = startp.y;
            p3.x = wt;
            p4.y = endp.y;
            p4.x = wt;
            Path path = new Path();
            path.moveTo((float) startp.x, (float) startp.y);
            path.cubicTo((float) p3.x, (float) p3.y, (float) p4.x, (float) p4.y, (float) endp.x, (float) endp.y);
            canvas.drawPath(path, mPaint);
        }
    }

    private void drawLine(Canvas canvas, Point[] points, Paint mPaint) {
        Point startp = new Point();
        Point endp = new Point();
        for (int i = 0; i < points.length - 1; i++) {
            startp = points[i];
            endp = points[i + 1];
            canvas.drawLine((float) startp.x, (float) startp.y, (float) endp.x, (float) endp.y, mPaint);
        }
    }

    private void drawText(String text, int x, int y, Canvas canvas) {
        Paint p = new Paint(1);
        p.setTextSize((float) dip2px((float) this.fontsize));
        p.setColor(-16777216);
        p.setTextAlign(Align.LEFT);
        canvas.drawText(text, (float) x, (float) y, p);
    }

    private void drawMoveInfo(Canvas canvas) {
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
            canvas.drawText(this.xRawInfo, (float) (this.gridwidh - getTextWidth(mPaint, this.xRawInfo)), (float) ((this.gridheight + this.marginTop) + fsize), mPaint);
        }
        if (this.mCurMovePoint != null) {
            int ii = 0;
            while (ii < this.tagPoints.size()) {
                mPaint.setColor(-16777216);
                if (this.mColorArr == null || this.mColorArr.length <= ii) {
                    mPaint.setColor(-16777216);
                } else {
                    mPaint.setColor(this.mColorArr[ii]);
                }
                if (ii == this.mCurLine) {
                    canvas.drawCircle((float) ((PointObj) this.tagPoints.get(ii)).f9X, (float) ((PointObj) this.tagPoints.get(ii)).f10Y, 20.0f, mPaint);
                } else {
                    canvas.drawCircle((float) ((PointObj) this.tagPoints.get(ii)).f9X, (float) ((PointObj) this.tagPoints.get(ii)).f10Y, 10.0f, mPaint);
                }
                canvas.drawText(((((PointObj) ((ArrayList) this.Points.get(ii)).get(this.mCurMovePoint.Num)).Val * 100) / this.maxValue) + "%", (float) (((PointObj) this.tagPoints.get(ii)).f9X + fsize), (float) (this.marginTop - (fsize / 2)), mPaint);
                ii++;
            }
        }
    }

    private Point[] getPoints(int num) {
        Point[] points = new Point[((ArrayList) this.Points.get(num)).size()];
        for (int i = 0; i < ((ArrayList) this.Points.get(num)).size(); i++) {
            points[i] = new Point(((PointObj) ((ArrayList) this.Points.get(num)).get(i)).f9X, ((PointObj) ((ArrayList) this.Points.get(num)).get(i)).f10Y);
        }
        return points;
    }

    public void addData(ArrayList<Integer> lineData) {
        this.mSourData.add(lineData);
        invalidate();
    }

    public ArrayList<ArrayList<Integer>> getData() {
        if (this.Points.size() == 0) {
            return this.mSourData;
        }
        ArrayList<ArrayList<Integer>> reData = new ArrayList();
        for (int x = 0; x < this.Points.size(); x++) {
            ArrayList<Integer> newLine = new ArrayList();
            for (int y = 0; y < ((ArrayList) this.Points.get(x)).size(); y++) {
                newLine.add(Integer.valueOf(((PointObj) ((ArrayList) this.Points.get(x)).get(y)).Val));
            }
            reData.add(newLine);
        }
        return reData;
    }

    public void clearData() {
        this.isChange = false;
        this.isInit = false;
        this.mSourData.clear();
        this.Points.clear();
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

    public void setCoordsValue(int maxValue, int averageValue) {
        this.maxValue = maxValue;
        this.averageValue = averageValue;
    }

    public void setMaxValue(int maxValue) {
        this.maxValue = maxValue;
    }

    public void setAveValue(int averageValue) {
        this.averageValue = averageValue;
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

    public int[] getColorArr() {
        return this.mColorArr;
    }

    public void setColorArr(int[] mColorArr) {
        this.mColorArr = mColorArr;
    }

    public void setChange(boolean val) {
        this.isChange = val;
    }

    public boolean isChange() {
        return this.isChange;
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

    public void setHandler(Handler handler) {
        this.mHandler = handler;
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
}
