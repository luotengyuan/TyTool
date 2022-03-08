package com.lois.tytool;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.ContextWrapper;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.IntDef;
import androidx.annotation.NonNull;
import androidx.annotation.StringRes;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Field;

/**
 * @Description 吐司显示类
 * @Author Luo.T.Y
 * @Date 2017-09-22
 * @Time 18:35
 */
public final class TyToast {

    /**
     * 通过Handler，使方法在子线程调用也能正常显示Toast
     */
    private static final Handler HANDLER = new Handler(Looper.getMainLooper());
    /**
     * 当字符为空的时候替代字符串
     */
    private static final String NULL = "null";

    /**
     * 全局的Toast对象
     */
    private static Toast mToast;

    /**
     * 全局的一些参数
     */
    private static int mGravity = -1;
    private static int mXOffset = -1;
    private static int mYOffset = -1;

    /**
     * 全局默认的Toast视图获取接口实现
     */
    private static ToastViewGetter mDefaultGetter = new TyToastViewGetter();

    public static void showShort(final CharSequence text) {
        show(text == null ? NULL : text, Toast.LENGTH_SHORT, ToastStyle.NORMAL, null);
    }

    public static void showShort(final CharSequence text, @ToastStyle int style) {
        show(text == null ? NULL : text, Toast.LENGTH_SHORT, style, null);
    }

    public static void showShort(final CharSequence text, ToastViewGetter getter) {
        show(text == null ? NULL : text, Toast.LENGTH_SHORT, ToastStyle.NORMAL, getter);
    }

    public static void showShort(final CharSequence text, @ToastStyle int style, ToastViewGetter getter) {
        show(text == null ? NULL : text, Toast.LENGTH_SHORT, style, getter);
    }

    public static void showShort(@StringRes final int resId) {
        show(resId, Toast.LENGTH_SHORT, ToastStyle.NORMAL, null);
    }

    public static void showShort(@StringRes final int resId, @ToastStyle int style) {
        show(resId, Toast.LENGTH_SHORT, style, null);
    }

    public static void showShort(@StringRes final int resId, ToastViewGetter getter) {
        show(resId, Toast.LENGTH_SHORT, ToastStyle.NORMAL, getter);
    }

    public static void showShort(@StringRes final int resId, @ToastStyle int style, ToastViewGetter getter) {
        show(resId, Toast.LENGTH_SHORT, style, getter);
    }

    public static void showShort(@StringRes final int resId, final Object... args) {
        show(resId, Toast.LENGTH_SHORT, ToastStyle.NORMAL, null, args);
    }

    public static void showShort(final String format, final Object... args) {
        show(format, Toast.LENGTH_SHORT, ToastStyle.NORMAL, null, args);
    }

    public static void showLong(final CharSequence text) {
        show(text == null ? NULL : text, Toast.LENGTH_LONG, ToastStyle.NORMAL, null);
    }

    public static void showLong(final CharSequence text, @ToastStyle int style) {
        show(text == null ? NULL : text, Toast.LENGTH_LONG, style, null);
    }

    public static void showLong(final CharSequence text, ToastViewGetter getter) {
        show(text == null ? NULL : text, Toast.LENGTH_LONG, ToastStyle.NORMAL, getter);
    }

    public static void showLong(final CharSequence text, @ToastStyle int style, ToastViewGetter getter) {
        show(text == null ? NULL : text, Toast.LENGTH_LONG, style, getter);
    }

    public static void showLong(@StringRes final int resId) {
        show(resId, Toast.LENGTH_LONG, ToastStyle.NORMAL, null);
    }

    public static void showLong(@StringRes final int resId, @ToastStyle int style) {
        show(resId, Toast.LENGTH_LONG, style, null);
    }

    public static void showLong(@StringRes final int resId, ToastViewGetter getter) {
        show(resId, Toast.LENGTH_LONG, ToastStyle.NORMAL, getter);
    }

    public static void showLong(@StringRes final int resId, @ToastStyle int style, ToastViewGetter getter) {
        show(resId, Toast.LENGTH_LONG, style, getter);
    }

    public static void showLong(@StringRes final int resId, final Object... args) {
        show(resId, Toast.LENGTH_LONG, ToastStyle.NORMAL, null, args);
    }

    public static void showLong(final String format, final Object... args) {
        show(format, Toast.LENGTH_LONG, ToastStyle.NORMAL, null, args);
    }

    public static void setGravity(final int gravity, final int xOffset, final int yOffset) {
        mGravity = gravity;
        mXOffset = xOffset;
        mYOffset = yOffset;
    }

    public static void setToastViewGetter(ToastViewGetter toastViewGetter) {
        TyToast.mDefaultGetter = toastViewGetter;
    }

    public static ToastViewGetter getToastViewCallback() {
        return mDefaultGetter;
    }

    public static void cancel() {
        if (mToast != null) {
            mToast.cancel();
        }
    }

    /*---------------------------------- 内部方法 --------------------------------------*/

    private static void show(
            @StringRes final int resId,
            final int duration,
            @ToastStyle final int style,
            ToastViewGetter getter
    ) {
        show(TyTool.getInstance().getContext().getResources().getText(resId).toString(), duration, style, getter);
    }

    private static void show(
            @StringRes final int resId,
            final int duration,
            @ToastStyle final int style,
            ToastViewGetter getter,
            final Object... args
    ) {
        show(String.format(TyTool.getInstance().getContext().getResources().getString(resId), args), duration, style, getter);
    }

    private static void show(
            final String format,
            final int duration,
            @ToastStyle final int style,
            ToastViewGetter getter,
            final Object... args
    ) {
        String text = null;
        if (format == null) {
            text = NULL;
        } else {
            try {
                text = String.format(format, args);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (text == null) {
                text = NULL;
            }
        }
        show(text, duration, style, getter);
    }

    private static void show(
            final CharSequence text,
            final int duration,
            @ToastStyle final int style,
            final ToastViewGetter getter
    ) {
        HANDLER.post(new Runnable() {
            @SuppressLint("ShowToast")
            @Override
            public void run() {
                cancel();
                mToast = Toast.makeText(TyTool.getInstance().getContext(), text, duration);
                if (mGravity != -1 || mXOffset != -1 || mYOffset != -1) {
                    mToast.setGravity(mGravity, mXOffset, mYOffset);
                }
                if (style != ToastStyle.SYSTEM) {
                    View view;
                    if (getter != null && (view = getter.getView(text, style)) != null) {
                        mToast.setView(view);
                    } else if (mDefaultGetter != null && (view = mDefaultGetter.getView(text, style)) != null) {
                        mToast.setView(view);
                    }
                }
                showToast();
            }
        });
    }

    private static void showToast() {
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.N_MR1) {
            try {
                // noinspection JavaReflectionMemberAccess
                Field field = View.class.getDeclaredField("mContext");
                field.setAccessible(true);
                field.set(mToast.getView(), new ApplicationContextWrapperForApi25());
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
        }
        mToast.show();
    }

    private static final class ApplicationContextWrapperForApi25 extends ContextWrapper {

        ApplicationContextWrapperForApi25() {
            super(TyTool.getInstance().getContext());
        }

        @Override
        public Context getApplicationContext() {
            return this;
        }

        @Override
        public Object getSystemService(@NonNull String name) {
            if (Context.WINDOW_SERVICE.equals(name)) {
                return new WindowManagerWrapper(
                        (WindowManager) getBaseContext().getSystemService(name)
                );
            }
            return super.getSystemService(name);
        }

        private static final class WindowManagerWrapper implements WindowManager {

            private final WindowManager base;

            private WindowManagerWrapper(@NonNull WindowManager base) {
                this.base = base;
            }

            @Override
            public Display getDefaultDisplay() {
                return base.getDefaultDisplay();
            }

            @Override
            public void removeViewImmediate(View view) {
                base.removeViewImmediate(view);
            }

            @Override
            public void addView(View view, ViewGroup.LayoutParams params) {
                try {
                    base.addView(view, params);
                } catch (BadTokenException e) {
                    Log.e("WindowManagerWrapper", e.getMessage());
                } catch (Throwable throwable) {
                    Log.e("WindowManagerWrapper", "[addView]", throwable);
                }
            }

            @Override
            public void updateViewLayout(View view, ViewGroup.LayoutParams params) {
                base.updateViewLayout(view, params);
            }

            @Override
            public void removeView(View view) {
                base.removeView(view);
            }
        }
    }

    /**
     * 自定义Toast视图接口
     */
    public interface ToastViewGetter {
        View getView(CharSequence text, @ToastStyle int style);
    }

    /**
     * Toast样式类型全局Global
     */
    @IntDef({ToastStyle.SYSTEM, ToastStyle.NORMAL, ToastStyle.SUCCESS, ToastStyle.WARN, ToastStyle.FAIL})
    @Retention(RetentionPolicy.SOURCE)
    public @interface ToastStyle {
        /**
         * 系统原始样式
         */
        int SYSTEM = 0;
        /**
         * 自定义NORMAL样式
         */
        int NORMAL = 1;
        /**
         * 自定义SUCCESS样式
         */
        int SUCCESS = 2;
        /**
         * 自定义WARN样式
         */
        int WARN = 3;
        /**
         * 自定义FAIL样式
         */
        int FAIL = 4;
    }

    private static class TyToastViewGetter implements ToastViewGetter {
        @Override
        public View getView(CharSequence text, int style) {
            View view = LayoutInflater.from(TyTool.getInstance().getContext()).inflate(R.layout.layout_ty_toast_view_getter, null);
            if (view != null) {
                TextView tv = view.findViewById(R.id.tv_toast_info);
                tv.setText(text);
                ImageView img = view.findViewById(R.id.iv_toast_img);
                RelativeLayout shape = view.findViewById(R.id.rl_toast_shape);
                if (style == ToastStyle.FAIL) {
                    img.setBackgroundResource(R.drawable.ic_toast_fail);
                    shape.setBackgroundResource(R.drawable.shape_ty_toast_bg_fail);
                } else if (style == ToastStyle.WARN) {
                    img.setBackgroundResource(R.drawable.ic_toast_warn);
                    shape.setBackgroundResource(R.drawable.shape_ty_toast_bg_warn);
                } else if (style == ToastStyle.SUCCESS) {
                    img.setBackgroundResource(R.drawable.ic_toast_success);
                    shape.setBackgroundResource(R.drawable.shape_ty_toast_bg_success);
                } else {
                    img.setBackgroundResource(R.drawable.ic_toast_normal);
                    shape.setBackgroundResource(R.drawable.shape_ty_toast_bg_normal);
                }
                return view;
            }
            return null;
        }
    }

    private TyToast() {
        throw new UnsupportedOperationException("u can't initialize me!");
    }
}
