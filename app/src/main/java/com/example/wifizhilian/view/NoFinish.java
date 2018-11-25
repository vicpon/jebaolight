package com.example.wifizhilian.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import com.example.wifizhilian.R;
import com.example.wifizhilian.MainActivity;

public class NoFinish extends LinearLayout {
    private Context mContext;
    private PageHead mPageHead = ((PageHead) findViewById(R.id.NoFinishpageHead));

    /* renamed from: com.example.wifizhilian.view.NoFinish$1 */
    class C03461 implements OnClickListener {
        C03461() {
        }

        public void onClick(View v) {
            ((MainActivity) NoFinish.this.mContext).SelectMenuItem(R.string.menu_item_mydevice);
        }
    }

    public NoFinish(Context context, String title) {
        super(context);
        this.mContext = context;
        View layout = ((LayoutInflater) this.mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.page_nofinish, this);
        this.mPageHead.LeftBnt.setImageResource(R.drawable.menu_2);
        this.mPageHead.LeftBnt.setOnClickListener(new C03461());
        this.mPageHead.RightBnt.setVisibility(View.INVISIBLE);
        this.mPageHead.TitleView.setText(title);
    }
}
