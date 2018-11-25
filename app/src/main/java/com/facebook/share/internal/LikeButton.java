package com.facebook.share.internal;

import android.content.Context;
import android.util.AttributeSet;

import com.example.wifizhilian.R;
import com.facebook.FacebookButtonBase;
//import com.facebook.common.C0395R;
import com.facebook.internal.AnalyticsEvents;

@Deprecated
public class LikeButton extends FacebookButtonBase {
    @Deprecated
    public LikeButton(Context context, boolean isLiked) {
        super(context, null, 0, 0, AnalyticsEvents.EVENT_LIKE_BUTTON_CREATE, AnalyticsEvents.EVENT_LIKE_BUTTON_DID_TAP);
        setSelected(isLiked);
    }

    @Deprecated
    public void setSelected(boolean selected) {
        super.setSelected(selected);
        updateForLikeStatus();
    }

    protected void configureButton(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super.configureButton(context, attrs, defStyleAttr, defStyleRes);
        updateForLikeStatus();
    }

    protected int getDefaultRequestCode() {
        return 0;
    }

    protected int getDefaultStyleResource() {
        //return C0395R.style.com_facebook_button_like;
        return R.style.com_facebook_button_like;
    }

    private void updateForLikeStatus() {
        if (isSelected()) {
//            setCompoundDrawablesWithIntrinsicBounds(C0395R.drawable.com_facebook_button_like_icon_selected, 0, 0, 0);
//            setText(getResources().getString(C0395R.string.com_facebook_like_button_liked));
            return;
        }
//        setCompoundDrawablesWithIntrinsicBounds(C0395R.drawable.com_facebook_button_icon, 0, 0, 0);
//        setText(getResources().getString(C0395R.string.com_facebook_like_button_not_liked));
    }
}
