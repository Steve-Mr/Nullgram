/*
 * Copyright (C) 2019-2024 qwq233 <qwq233@qwq2333.top>
 * https://github.com/qwq233/Nullgram
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this software.
 *  If not, see
 * <https://www.gnu.org/licenses/>
 */

package org.telegram.ui.ActionBar;

import android.app.Dialog;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.RectF;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowInsets;
import android.view.WindowManager;
import android.widget.FrameLayout;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.R;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.LaunchActivity;

public class BottomSheetTabDialog extends Dialog {

    public static BottomSheetTabsOverlay.Sheet checkSheet(BottomSheetTabsOverlay.Sheet sheet) {
        BaseFragment fragment = LaunchActivity.getSafeLastFragment();
        if (fragment == null) return sheet;
        if (AndroidUtilities.isTablet() || AndroidUtilities.hasDialogOnTop(fragment)) {
            final BottomSheetTabDialog dialog = new BottomSheetTabDialog(sheet);
            if (sheet.setDialog(dialog)) {
                dialog.windowView.putView();
                return sheet;
            }
        }
        return sheet;
    }

    public final BottomSheetTabsOverlay.Sheet sheet;
    public final BottomSheetTabsOverlay.SheetView sheetView;

    public final WindowView windowView;

    public BottomSheetTabDialog(BottomSheetTabsOverlay.Sheet sheet) {
        super(sheet.getWindowView().getContext(), R.style.TransparentDialog);

        this.sheet = sheet;
        this.sheetView = sheet.getWindowView();

        setContentView(windowView = new WindowView(sheetView), new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Window window = getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN | WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN | WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        }
        window.setWindowAnimations(R.style.DialogNoAnimation);

        WindowManager.LayoutParams params = window.getAttributes();
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        params.gravity = Gravity.TOP | Gravity.LEFT;
        params.dimAmount = 0;
        params.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        params.softInputMode = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE;
        params.height = ViewGroup.LayoutParams.MATCH_PARENT;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            params.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
        }
        window.setAttributes(params);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.setStatusBarColor(Color.TRANSPARENT);
        }

        windowView.setFitsSystemWindows(true);
        windowView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        windowView.setPadding(0, 0, 0, 0);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            windowView.setOnApplyWindowInsetsListener((v, insets) -> {
                v.setPadding(0, 0, 0, insets.getSystemWindowInsetBottom());
                if (Build.VERSION.SDK_INT >= 30) {
                    return WindowInsets.CONSUMED;
                } else {
                    return insets.consumeSystemWindowInsets();
                }
            });
        }
    }

    public void updateNavigationBarColor() {
        final int color = sheet.getNavigationBarColor(0);
        AndroidUtilities.setNavigationBarColor(getWindow(), color);
        AndroidUtilities.setLightNavigationBar(getWindow(), AndroidUtilities.computePerceivedBrightness(color) >= .721f);
    }

    public static class WindowView extends FrameLayout implements BottomSheetTabsOverlay.SheetView {

        public final BottomSheetTabsOverlay.SheetView sheetView;

        public WindowView(BottomSheetTabsOverlay.SheetView sheetView) {
            super(sheetView.getContext());
            this.sheetView = sheetView;
        }

        public void putView() {
            View view = (View) sheetView;
            AndroidUtilities.removeFromParent(view);
            addView(view, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT, Gravity.FILL));
        }

        @Override
        public void setDrawingFromOverlay(boolean value) {
            sheetView.setDrawingFromOverlay(value);
        }

        @Override
        public RectF getRect() {
            return sheetView.getRect();
        }

        @Override
        public float drawInto(Canvas canvas, RectF finalRect, float progress, RectF clipRect, float alpha, boolean opening) {
            return sheetView.drawInto(canvas, finalRect, progress, clipRect, alpha, opening);
        }

    }

    private boolean attached;
    public void attach() {
        if (attached) return;
        attached = true;
        try {
            super.show();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public void detach() {
        sheet.setDialog(null);
        if (!attached) return;
        attached = false;
        try {
            super.dismiss();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    @Override
    public void dismiss() {
        sheet.dismiss(false);
    }

}