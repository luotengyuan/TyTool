package com.lois.tytool;

import android.content.Context;

import com.lois.tytool.dialog.builder.AlertDialogBuilder;
import com.lois.tytool.dialog.builder.BottomSheetDialogBuilder;
import com.lois.tytool.dialog.builder.EditTextDialogBuilder;
import com.lois.tytool.dialog.builder.ListDialogBuilder;
import com.lois.tytool.dialog.builder.LoadingDialogBuilder;
import com.lois.tytool.dialog.builder.MessageDialogBuilder;
import com.lois.tytool.dialog.builder.OtherDialogBuilder;
import com.lois.tytool.dialog.builder.ProgressDialogBuilder;

/**
 * @Description TODO
 * @Author Luo.T.Y
 * @Date 2022/3/28
 * @Time 19:40
 */
public class TyDialog {

    public static LoadingDialogBuilder loadingDialog0(Context context) {
        return LoadingDialogBuilder.getLoadingDialogBuilder0(context);
    }

    public static LoadingDialogBuilder loadingDialog1(Context context) {
        return LoadingDialogBuilder.getLoadingDialogBuilder1(context);
    }

    public static LoadingDialogBuilder loadingDialog2(Context context) {
        return LoadingDialogBuilder.getLoadingDialogBuilder2(context);
    }

    public static ProgressDialogBuilder progressDialog(Context context) {
        return ProgressDialogBuilder.getProgressDialogBuilder0(context);
    }

    public static ProgressDialogBuilder progressDonutDialog(Context context) {
        return ProgressDialogBuilder.getProgressDialogDonutBuilder(context);
    }

    public static ProgressDialogBuilder progressCircleDialog(Context context) {
        return ProgressDialogBuilder.getProgressDialogCircleBuilder(context);
    }

    public static ProgressDialogBuilder progressArcDialog(Context context) {
        return ProgressDialogBuilder.getProgressDialogArcBuilder(context);
    }

    public static EditTextDialogBuilder editTextDialog(Context context) {
        return new EditTextDialogBuilder(context);
    }

    public static MessageDialogBuilder messageDialog(Context context) {
        return new MessageDialogBuilder(context);
    }

    public static BottomSheetDialogBuilder bottomSheetDialog(Context context) {
        return new BottomSheetDialogBuilder(context);
    }

    public static AlertDialogBuilder alertDialog(Context context) {
        return new AlertDialogBuilder(context);
    }

    public static ListDialogBuilder listDialog(Context context) {
        return new ListDialogBuilder(context);
    }

    public static OtherDialogBuilder otherDialog(Context context) {
        return new OtherDialogBuilder(context);
    }

}
