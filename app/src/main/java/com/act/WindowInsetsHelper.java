package com.act;

import android.view.View;
import android.view.ViewGroup;

import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class WindowInsetsHelper {

    /**
     * Apply system bar insets as PADDING to the view
     * Best for: ScrollViews, RecyclerViews, or containers where content should not go behind system bars
     */
    public static void applySystemBarInsets(View view) {
        ViewCompat.setOnApplyWindowInsetsListener(view, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    /**
     * Apply system bar insets as MARGIN to the view
     * Best for: Fixed position views like toolbars, buttons, or FABs
     */
    public static void applySystemBarInsetsWithMargin(View view) {
        ViewCompat.setOnApplyWindowInsetsListener(view, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
            params.setMargins(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            v.setLayoutParams(params);
            return insets;
        });
    }

    /**
     * Apply ONLY top inset (status bar)
     * Best for: Toolbars or headers that need space only at the top
     */
    public static void applyTopInset(View view) {
        ViewCompat.setOnApplyWindowInsetsListener(view, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(v.getPaddingLeft(), systemBars.top, v.getPaddingRight(), v.getPaddingBottom());
            return insets;
        });
    }

    /**
     * Apply ONLY bottom inset (navigation bar)
     * Best for: Bottom navigation, buttons at bottom, or FABs
     */
    public static void applyBottomInset(View view) {
        ViewCompat.setOnApplyWindowInsetsListener(view, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(v.getPaddingLeft(), v.getPaddingTop(), v.getPaddingRight(), systemBars.bottom);
            return insets;
        });
    }

    /**
     * Apply top and bottom insets only (ignore left/right)
     * Best for: Full-width scrollable content
     */
    public static void applyVerticalInsets(View view) {
        ViewCompat.setOnApplyWindowInsetsListener(view, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(v.getPaddingLeft(), systemBars.top, v.getPaddingRight(), systemBars.bottom);
            return insets;
        });
    }

    /**
     * Apply horizontal insets only (ignore top/bottom)
     * Best for: Landscape orientation or horizontally scrolling content
     */
    public static void applyHorizontalInsets(View view) {
        ViewCompat.setOnApplyWindowInsetsListener(view, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, v.getPaddingTop(), systemBars.right, v.getPaddingBottom());
            return insets;
        });
    }

    /**
     * Apply top inset as margin
     * Best for: Toolbars that need to be positioned below status bar
     */
    public static void applyTopInsetWithMargin(View view) {
        ViewCompat.setOnApplyWindowInsetsListener(view, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
            params.topMargin = systemBars.top;
            v.setLayoutParams(params);
            return insets;
        });
    }

    /**
     * Apply bottom inset as margin
     * Best for: Bottom navigation or FABs
     */
    public static void applyBottomInsetWithMargin(View view) {
        ViewCompat.setOnApplyWindowInsetsListener(view, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
            params.bottomMargin = systemBars.bottom;
            v.setLayoutParams(params);
            return insets;
        });
    }

    /**
     * Consume the insets (prevent them from being passed to child views)
     * Use this when you've handled the insets and don't want children to receive them
     */
    public static void applySystemBarInsetsAndConsume(View view) {
        ViewCompat.setOnApplyWindowInsetsListener(view, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return WindowInsetsCompat.CONSUMED;
        });
    }

    /**
     * Clear any previous inset listeners
     */
    public static void clearInsetListener(View view) {
        ViewCompat.setOnApplyWindowInsetsListener(view, null);
    }
}