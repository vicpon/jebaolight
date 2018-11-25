package com.example.wifizhilian.view;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.example.wifizhilian.R;
import com.example.wifizhilian.data.KeyValueObj;
import java.util.ArrayList;
import java.util.List;

public class SlideListView extends RelativeLayout {
    private static final String TAG = "SlideListView";
    private OnClickListener bntClick = new C03471();
    private ViewGroup group;
    private ImageView imageView;
    private ImageView[] imageViews;
    private ViewGroup layout;
    private List<BntItemHolder> mBntData = new ArrayList();
    private int mBntNum = 8;
    private int mBntPadding;
    private int mBntSize;
    private Context mContext;
    private Handler mEventHandler;
    private int mHandlerWhat = 0;
    private ArrayList<View> pageViews;
    private ViewPager viewPager;

    /* renamed from: com.example.wifizhilian.view.SlideListView$1 */
    class C03471 implements OnClickListener {
        C03471() {
        }

        public void onClick(View v) {
            if (SlideListView.this.mEventHandler != null) {
                Message msg = new Message();
                msg.what = SlideListView.this.mHandlerWhat;
                msg.obj = v.getTag();
                SlideListView.this.mEventHandler.sendMessage(msg);
            }
        }
    }

    public final class BntItemHolder {
        public ImageView icon;
        public TextView title;
    }

    class GuidePageAdapter extends PagerAdapter {
        private List<View> viewList;

        public GuidePageAdapter(List<View> viewList) {
            this.viewList = viewList;
        }

        public int getCount() {
            return this.viewList.size();
        }

        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        public int getItemPosition(Object object) {
            return super.getItemPosition(object);
        }

        public void destroyItem(View arg0, int arg1, Object arg2) {
            ((ViewPager) arg0).removeView((View) this.viewList.get(arg1));
        }

        public Object instantiateItem(View arg0, int arg1) {
            ((ViewPager) arg0).addView((View) this.viewList.get(arg1));
            return this.viewList.get(arg1);
        }

        public void restoreState(Parcelable arg0, ClassLoader arg1) {
        }

        public Parcelable saveState() {
            return null;
        }

        public void startUpdate(View arg0) {
        }

        public void finishUpdate(View arg0) {
        }
    }

    class GuidePageChangeListener implements OnPageChangeListener {
        GuidePageChangeListener() {
        }

        public void onPageScrollStateChanged(int arg0) {
        }

        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }

        public void onPageSelected(int arg0) {
            for (int i = 0; i < SlideListView.this.imageViews.length; i++) {
                if (arg0 == i) {
                    SlideListView.this.imageViews[i].setBackgroundResource(R.drawable.page_indicator_focused);
                } else {
                    SlideListView.this.imageViews[i].setBackgroundResource(R.drawable.page_indicator_unfocused);
                }
            }
        }
    }

    public SlideListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        this.mBntSize = getResources().getDimensionPixelSize(R.dimen.slidelist_bnt_size);
        this.mBntPadding = getResources().getDimensionPixelSize(R.dimen.slidelist_bnt_padding);
        this.layout = (ViewGroup) ((LayoutInflater) this.mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.view_slide_listv, this);
        this.group = (ViewGroup) this.layout.findViewById(R.id.viewGroup);
        this.viewPager = (ViewPager) this.layout.findViewById(R.id.guidePages);
        initData();
    }

    private void initData() {
        this.pageViews = new ArrayList();
        int ii = 0;
        LayoutParams imgtp = new LayoutParams(this.mBntSize, this.mBntSize);
        imgtp.setMargins(0, this.mBntPadding, 0, 0);
        //imgtp.gravity = 1;
        ViewGroup.LayoutParams txttp = new LayoutParams(-2, -2);
        //txttp.gravity = 1;
        LayoutParams coltp = new LayoutParams(-2, -2);
        //coltp.weight = 0.5f;
        LayoutParams layoutParams = new LayoutParams(-1, -2);
        layoutParams.setMargins(this.mBntPadding, 0, this.mBntPadding, 0);
        while (ii < this.mBntNum) {
            LinearLayout pagelay = new LinearLayout(this.mContext);
            pagelay.setOrientation(LinearLayout.VERTICAL);
            for (int jj = 0; jj < 2 && ii < this.mBntNum; jj++) {
                LinearLayout linearLayout = new LinearLayout(this.mContext);
                for (int ff = 0; ff < 2 && ii < this.mBntNum; ff++) {
                    BntItemHolder item = new BntItemHolder();
                    LinearLayout collay = new LinearLayout(this.mContext);
                    collay.setOrientation(LinearLayout.VERTICAL);
                    ImageView imageView = new ImageView(this.mContext);
                    imageView.setScaleType(ScaleType.FIT_XY);
                    imageView.setImageResource(R.drawable.add_bnt);
                    imageView.setTag(Integer.valueOf(ii));
                    imageView.setOnClickListener(this.bntClick);
                    View textView = new TextView(this.mContext);
                    collay.addView(imageView, imgtp);
                    collay.addView(textView, txttp);
                    collay.setWeightSum(0.5f);
                    linearLayout.addView(collay, coltp);
                    item.icon = imageView;
                    item.title = (TextView) textView;
                    this.mBntData.add(item);
                    ii++;
                }
                pagelay.addView(linearLayout, layoutParams);
            }
            this.pageViews.add(pagelay);
        }
        this.imageViews = new ImageView[this.pageViews.size()];
        for (int i = 0; i < this.pageViews.size(); i++) {
            LayoutParams margin = new LayoutParams(getResources().getDimensionPixelSize(R.dimen.slidelist_dot_size), getResources().getDimensionPixelSize(R.dimen.slidelist_dot_size));
            margin.setMargins(10, 0, 0, 0);
            this.imageView = new ImageView(this.mContext);
            this.imageView.setLayoutParams(new RelativeLayout.LayoutParams(15, 15));
            this.imageViews[i] = this.imageView;
            if (i == 0) {
                this.imageViews[i].setBackgroundResource(R.drawable.page_indicator_focused);
            } else {
                this.imageViews[i].setBackgroundResource(R.drawable.page_indicator_unfocused);
            }
            this.group.addView(this.imageViews[i], margin);
        }
        this.viewPager.setAdapter(new GuidePageAdapter(this.pageViews));
        this.viewPager.setOnPageChangeListener(new GuidePageChangeListener());
    }

    public void setData(List<KeyValueObj> data) {
        for (int ii = 0; ii < data.size(); ii++) {
            if (ii < this.mBntNum) {
                ((BntItemHolder) this.mBntData.get(ii)).icon.setImageResource(((KeyValueObj) data.get(ii)).IntVal);
                ((BntItemHolder) this.mBntData.get(ii)).title.setText(((KeyValueObj) data.get(ii)).StringVal);
            }
        }
    }

    public void setEventHandler(Handler val, int msgWhat) {
        this.mEventHandler = val;
        this.mHandlerWhat = msgWhat;
    }
}
