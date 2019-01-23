package org.michiganhackers.michiganhackers;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;

public final class Util {
    // Private constructor to prevent instantiation
    private Util(){}

    public static Drawable getThemedDrawable(int attributeId, Context context) {
        TypedArray arr = context.getTheme().obtainStyledAttributes(R.style.BaseTheme, new int[] {attributeId});
        int resourceId = arr.getResourceId(0, 0);
        return context.getResources().getDrawable(resourceId);
    }
}
