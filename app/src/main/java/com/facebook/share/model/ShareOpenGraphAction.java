package com.facebook.share.model;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.support.annotation.Nullable;

public final class ShareOpenGraphAction extends ShareOpenGraphValueContainer<ShareOpenGraphAction, ShareOpenGraphAction.Builder> {
    public static final Creator<ShareOpenGraphAction> CREATOR = new C04681();

    /* renamed from: com.facebook.share.model.ShareOpenGraphAction$1 */
    static class C04681 implements Creator<ShareOpenGraphAction> {
        C04681() {
        }

        public ShareOpenGraphAction createFromParcel(Parcel in) {
            return new ShareOpenGraphAction(in);
        }

        public ShareOpenGraphAction[] newArray(int size) {
            return new ShareOpenGraphAction[size];
        }
    }

    public static final class Builder extends com.facebook.share.model.ShareOpenGraphValueContainer.Builder<ShareOpenGraphAction, Builder> {
        private static final String ACTION_TYPE_KEY = "og:type";

        public Builder setActionType(String actionType) {
            putString(ACTION_TYPE_KEY, actionType);
            return this;
        }

        public ShareOpenGraphAction build() {
            return null;
        }

        public Builder readFrom(ShareOpenGraphAction model) {
            if (model == null) {
                return this;
            }
            return ((Builder) super.readFrom((ShareOpenGraphAction) model)).setActionType(model.getActionType());
        }

        Builder readFrom(Parcel parcel) {
            return readFrom((ShareOpenGraphAction) parcel.readParcelable(ShareOpenGraphAction.class.getClassLoader()));
        }
    }

    private ShareOpenGraphAction(Builder builder) {
        super((com.facebook.share.model.ShareOpenGraphValueContainer.Builder) builder);
    }

    ShareOpenGraphAction(Parcel in) {
        super(in);
    }

    @Nullable
    public String getActionType() {
        return getString("og:type");
    }
}
