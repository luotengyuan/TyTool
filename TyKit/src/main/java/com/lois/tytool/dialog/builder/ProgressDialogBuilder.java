package com.lois.tytool.dialog.builder;

import android.content.Context;

import com.lois.tytool.dialog.core.DialogBuilder;
import com.lois.tytool.dialog.core.DialogType;

public class ProgressDialogBuilder extends DialogBuilder<ProgressDialogBuilder> {
    private OnProgressListener onProgressListener;

    public interface OnProgressListener {
        void onProgress(int progress);
    }

    public static class ProgressHandler {
        OnProgressListener listener;

        public void setProgress(int progress) {
            listener.onProgress(progress);
        }
    }

    protected ProgressDialogBuilder(Context context, int dialogType) {
        super(context, dialogType);
    }

    public static ProgressDialogBuilder getProgressDialogBuilder0(Context context) {
        return new ProgressDialogBuilder(context, DialogType.DIALOG_PROGRESS);
    }

    public static ProgressDialogBuilder getProgressDialogDonutBuilder(Context context) {
        return new ProgressDialogBuilder(context, DialogType.DIALOG_DONUT_PROGRESS);
    }

    public static ProgressDialogBuilder getProgressDialogCircleBuilder(Context context) {
        return new ProgressDialogBuilder(context, DialogType.DIALOG_CIRCLE_PROGRESS);
    }

    public static ProgressDialogBuilder getProgressDialogArcBuilder(Context context) {
        return new ProgressDialogBuilder(context, DialogType.DIALOG_ARC_PROGRESS);
    }

    public OnProgressListener getOnProgressListener() {
        return onProgressListener;
    }

    public ProgressDialogBuilder setOnProgressListener(OnProgressListener onProgressListener) {
        this.onProgressListener = onProgressListener;
        return this;
    }

    public ProgressDialogBuilder progressHandler(ProgressHandler progressHandler) {
        progressHandler.listener = new OnProgressListener() {
            @Override
            public void onProgress(int progress) {
                onProgressListener.onProgress(progress);
            }
        };
        return this;
    }

}
