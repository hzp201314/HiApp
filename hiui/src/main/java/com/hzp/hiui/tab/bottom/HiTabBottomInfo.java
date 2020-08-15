package com.hzp.hiui.tab.bottom;

import android.graphics.Bitmap;
import android.graphics.Color;

import androidx.fragment.app.Fragment;

/**
 *
 */
public class HiTabBottomInfo {
    public enum TabType {
        BITMAP, ICON
    }

    public Class<? extends Fragment> fragment;
    public String name;
    public Bitmap defaultBitmap;
    public Bitmap selectedBitmap;
    public String iconFont;

    public String defaultIconName;
    public String selectIconName;
    public Color defaultColor;
    public Color tintColor;
    public TabType tabType;


    public HiTabBottomInfo(String name, Bitmap defaultBitmap, Bitmap selectedBitmap) {
        this.name = name;
        this.defaultBitmap = defaultBitmap;
        this.selectedBitmap = selectedBitmap;
    }
}
