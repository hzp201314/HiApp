package com.hzp.hi.library.log;

import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.hzp.hi.library.util.HiDisplayUtil;

/**
 * 视图打印器辅助类
 * 控制视图显示隐藏逻辑
 */
public class HiViewPrinterProvider {
    private FrameLayout rootView;
    private View floatingView;
    private boolean isOpen;
    private FrameLayout logView;
    private RecyclerView recyclerView;

    public HiViewPrinterProvider(FrameLayout rootView, RecyclerView recyclerView) {
        this.rootView = rootView;
        this.recyclerView = recyclerView;
    }

    private static final String TAG_FLOATING_VIEW = "TAG_FLOATING_VIEW";
    private static final String TAG_LOG_VIEW = "TAG_LOG_VIEW";

    /**
     * 展示悬浮窗
     */
    public void showFloatingView() {
        if(rootView.findViewWithTag(TAG_FLOATING_VIEW)!=null){
            return;
        }
        FrameLayout.LayoutParams params =
                new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.gravity= Gravity.BOTTOM|Gravity.END;
        View floatingView = genFloatingView();
        floatingView.setTag(TAG_FLOATING_VIEW);
        floatingView.setBackgroundColor(Color.BLACK);
        floatingView.setAlpha(0.8f);
        params.bottomMargin= HiDisplayUtil.dp2px(100, rootView.getResources());
        rootView.addView(genFloatingView(),params);
    }

    /**
     * 展示LogView
     */
    public void showLogView() {
        if (rootView.findViewWithTag(TAG_LOG_VIEW) != null) {
            return;
        }
        FrameLayout.LayoutParams params =
                new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, HiDisplayUtil.dp2px(160, rootView.getResources()));
        params.gravity = Gravity.BOTTOM;
        View logView = genLogView();
        logView.setTag(TAG_LOG_VIEW);
        rootView.addView(genLogView(), params);
        isOpen = true;
    }


    /**
     * 关闭LogView
     */
    public void closeLogView() {
        isOpen = false;
        rootView.removeView(genLogView());
    }

    /**
     * 关闭悬浮按钮
     */
    public void closeFloatingView() {
        rootView.removeView(genFloatingView());
    }

    /*创建悬浮窗*/
    private View genFloatingView() {
        if (floatingView != null) {
            return floatingView;
        }
        TextView textView = new TextView(rootView.getContext());
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isOpen) {
                    showLogView();
                }
            }
        });
        textView.setText("HiLog");
        return floatingView = textView;
    }

    /*创建logView*/
    private View genLogView() {
        if (logView != null) {
            return logView;
        }
        FrameLayout logView = new FrameLayout(rootView.getContext());
        logView.setBackgroundColor(Color.BLACK);
        logView.addView(recyclerView);

        FrameLayout.LayoutParams params =
                new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.END;
        TextView closeView = new TextView(rootView.getContext());
        closeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeLogView();
            }
        });
        closeView.setText("Close");
        logView.addView(closeView, params);
        return this.logView = logView;
    }
}
