package com.facebook.share.model;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.support.annotation.Nullable;

public final class ShareOpenGraphContent extends ShareContent<ShareOpenGraphContent, ShareContent.Builder> {
    public static final Creator<ShareOpenGraphContent> CREATOR = new C04691();
    private final ShareOpenGraphAction action;
    private final String previewPropertyName;

    /* renamed from: com.facebook.share.model.ShareOpenGraphContent$1 */
    static class C04691 implements Creator<ShareOpenGraphContent> {
        C04691() {
        }

        public ShareOpenGraphContent createFromParcel(Parcel in) {
            return new ShareOpenGraphContent(in);
        }

        public ShareOpenGraphContent[] newArray(int size) {
            return new ShareOpenGraphContent[size];
        }
    }

    public static final class Builder extends com.facebook.share.model.ShareContent.Builder<ShareOpenGraphContent, Builder> {
        private ShareOpenGraphAction action;
        private String previewPropertyName;

        public Builder setAction(@Nullable ShareOpenGraphAction action) {
            ShareOpenGraphAction shareOpenGraphAction;
            if (action == null) {
                shareOpenGraphAction = null;
            } else {
                shareOpenGraphAction = new com.facebook.share.model.ShareOpenGraphAction.Builder().readFrom(action).build();
            }
            this.action = shareOpenGraphAction;
            return this;
        }

        public Builder setPreviewPropertyName(@Nullable String previewPropertyName) {
            this.previewPropertyName = previewPropertyName;
            return this;
        }

        public ShareOpenGraphContent build() {
            return null;
        }

        public Builder readFrom(ShareOpenGraphContent model) {
            if (model == null) {
                return this;
            }
            return ((Builder) super.readFrom((ShareOpenGraphContent) model)).setAction(model.getAction()).setPreviewPropertyName(model.getPreviewPropertyName());
        }
    }

    private ShareOpenGraphContent(Builder builder) {
        super((com.facebook.share.model.ShareContent.Builder) builder);
        this.action = builder.action;
        this.previewPropertyName = builder.previewPropertyName;
    }

    ShareOpenGraphContent(Parcel in) {
        super(in);
        this.action = new com.facebook.share.model.ShareOpenGraphAction.Builder().readFrom(in).build();
        this.previewPropertyName = in.readString();
    }

    @Nullable
    public ShareOpenGraphAction getAction() {
        return this.action;
    }

    @Nullable
    public String getPreviewPropertyName() {
        return this.previewPropertyName;
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {
        super.writeToParcel(out, flags);
        out.writeParcelable(this.action, 0);
        out.writeString(this.previewPropertyName);
    }
}
