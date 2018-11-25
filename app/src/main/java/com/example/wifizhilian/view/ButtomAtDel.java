package com.example.wifizhilian.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.example.wifizhilian.R;

public class ButtomAtDel extends LinearLayout {
    private static final int DEFAULT_ICON_SIZE = 200;
    private static final int DEFAULT_OPT_SIZE = 50;
    public static final int ICON_ON_CLICK = 20001;
    public static final int OPTION_ON_CLICK = 20002;
    private Handler mHandler;
    private String mKey = "";
    private int mTag;
    private ImageButton vIcon;
    private ImageButton vOptbnt;
    private TextView vTitle;

    /* renamed from: com.example.wifizhilian.view.ButtomAtDel$1 */
    class C03211 implements OnClickListener {
        C03211() {
        }

        public void onClick(View v) {
            if (ButtomAtDel.this.mHandler != null) {
                Message msg = new Message();
                msg.what = 20002;
                msg.obj = Integer.valueOf(ButtomAtDel.this.mTag);
                ButtomAtDel.this.mHandler.sendMessage(msg);
            }
        }
    }

    /* renamed from: com.example.wifizhilian.view.ButtomAtDel$2 */
    class C03222 implements OnClickListener {
        C03222() {
        }

        public void onClick(View v) {
            if (ButtomAtDel.this.mHandler != null) {
                Message msg = new Message();
                msg.what = 20001;
                msg.obj = Integer.valueOf(ButtomAtDel.this.mTag);
                ButtomAtDel.this.mHandler.sendMessage(msg);
            }
        }
    }

    public ButtomAtDel(Context context) {
        super(context, null);
    }

    public ButtomAtDel(Context context, AttributeSet attrs) {
        super(context, attrs);
        View layout = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.view_buttom_atdel, this);
//        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.ButtomAtDel);
//        int icon_size = (int) array.getDimension(0, 200.0f);
//        int opt_size = (int) array.getDimension(1, 50.0f);
        this.vIcon = (ImageButton) layout.findViewById(R.id.imageIcon);
        this.vOptbnt = (ImageButton) layout.findViewById(R.id.imageOpt);
        this.vTitle = (TextView) layout.findViewById(R.id.textTitle);
//        setIconSize(icon_size, opt_size);
//        array.recycle();
        this.vOptbnt.setOnClickListener(new C03211());
        this.vIcon.setOnClickListener(new C03222());
    }

    public void setIconSize(int icon_size, int opt_size) {
        LayoutParams lp = (LayoutParams) this.vIcon.getLayoutParams();
        lp.height = icon_size;
        lp.width = icon_size;
        this.vIcon.setLayoutParams(lp);
        LayoutParams olp = (LayoutParams) this.vOptbnt.getLayoutParams();
        olp.height = opt_size;
        olp.width = opt_size;
        this.vOptbnt.setLayoutParams(olp);
    }

    public void setHandler(Handler handler, int tag) {
        this.mHandler = handler;
        this.mTag = tag;
    }

    public void setTitle(String title) {
        this.vTitle.setText(title);
    }

    public void setTitle(int title) {
        this.vTitle.setText(title);
    }

    public void setIcon(Drawable res) {
        this.vIcon.setImageDrawable(res);
    }

    public void setIcon(int res) {
        this.vIcon.setImageResource(res);
    }

    public void showDelIcon(boolean val) {
        if (val) {
            this.vOptbnt.setVisibility(View.VISIBLE);
        } else {
            this.vOptbnt.setVisibility(View.INVISIBLE);
        }
    }

    public String getKey() {
        return this.mKey;
    }

    public void setKey(String mKey) {
        this.mKey = mKey;
    }
}
