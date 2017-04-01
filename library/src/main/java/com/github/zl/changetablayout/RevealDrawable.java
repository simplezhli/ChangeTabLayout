package com.github.zl.changetablayout;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.view.Gravity;

/**
 * A Drawable that transitions between two child Drawables based on this
 * Drawable's current level value.  The idea here is that the center value
 * (5000) will show the 'selected' Drawable, and any other value will show
 * a transitional value between the 'selected' Drawable and the 'unselected'
 * Drawable.
 *
 * Selected Unselected Value=2500 Value=7500
 *  |XXXX|    |    |     |XX  |     |  XX|
 *  |XXXX|    |    |     |XX  |     |  XX|
 *  |XXXX|    |    |     |XX  |     |  XX|
 *
 * Created by rharter on 4/18/14.
 * Updated by weilu on 2017/3/30. (Remove excess code.)
 */
class RevealDrawable extends Drawable{

    private int mOrientation;
    private Drawable mSelectedDrawable;
    private Drawable mUnselectedDrawable;
    private final Rect mTmpRect = new Rect();

    static final int HORIZONTAL = 1;
    static final int VERTICAL = 2;

    RevealDrawable(Drawable unselected, Drawable selected, int orientation) {

        mUnselectedDrawable = unselected;
        mSelectedDrawable = selected;
        mOrientation = orientation;
    }

    public void setOrientation(int orientation){
        mOrientation = orientation;
    }

    @Override
    public boolean getPadding(@NonNull Rect padding) {
        // XXX need to adjust padding!
        return mSelectedDrawable.getPadding(padding);
    }

    @Override
    public boolean setVisible(boolean visible, boolean restart) {
        mUnselectedDrawable.setVisible(visible, restart);
        mSelectedDrawable.setVisible(visible, restart);
        return super.setVisible(visible, restart);
    }

    @Override
    public void setAlpha(int alpha) {}

    @Override
    public void setColorFilter(ColorFilter cf) {}

    @Override
    public int getOpacity() {
        return PixelFormat.UNKNOWN;
    }

    @Override
    protected void onBoundsChange(Rect bounds) {
        mUnselectedDrawable.setBounds(bounds);
        mSelectedDrawable.setBounds(bounds);
    }

    @Override
    protected boolean onLevelChange(int level) {
        mUnselectedDrawable.setLevel(level);
        mSelectedDrawable.setLevel(level);
        invalidateSelf();
        return true;
    }

    @Override
    public void draw(@NonNull Canvas canvas) {

        // If level == 10000 || level == 0, just draw the unselected image
        int level = getLevel();
        if (level == 10000 || level == 0) {
            mUnselectedDrawable.draw(canvas);
        }

        // If level == 5000 just draw the selected image
        else if (level == 5000) {
            mSelectedDrawable.draw(canvas);
        }

        // Else, draw the transitional version
        else {
            final Rect r = mTmpRect;
            final Rect bounds = getBounds();

            { // Draw the unselected portion
                float value = (level / 5000f) - 1f;
                int w = bounds.width();
                if ((mOrientation & HORIZONTAL) != 0) {
                    w = (int) (w * Math.abs(value));
                }
                int h = bounds.height();
                if ((mOrientation & VERTICAL) != 0) {
                    h = (int) (h * Math.abs(value));
                }

                int gravity;
                if ((mOrientation & HORIZONTAL) != 0) {
                    gravity = value < 0 ? Gravity.LEFT : Gravity.RIGHT;
                }else {
                    gravity = value < 0 ? Gravity.BOTTOM : Gravity.TOP;
                }

                Gravity.apply(gravity, w, h, bounds, r);

                if (w > 0 && h > 0) {
                    canvas.save();
                    canvas.clipRect(r);
                    mUnselectedDrawable.draw(canvas);
                    canvas.restore();
                }
            }

            { // Draw the selected portion
                float value = (level / 5000f) - 1f;
                int w = bounds.width();
                if ((mOrientation & HORIZONTAL) != 0) {
                    w -= (int) (w * Math.abs(value));
                }
                int h = bounds.height();
                if ((mOrientation & VERTICAL) != 0) {
                    h -= (int) (h * Math.abs(value));
                }

                int gravity;
                if ((mOrientation & HORIZONTAL) != 0) {
                    gravity = value < 0 ? Gravity.RIGHT : Gravity.LEFT;
                }else {
                    gravity = value < 0 ? Gravity.TOP : Gravity.BOTTOM;
                }

                Gravity.apply(gravity, w, h, bounds, r);

                if (w > 0 && h > 0) {
                    canvas.save();
                    canvas.clipRect(r);
                    mSelectedDrawable.draw(canvas);
                    canvas.restore();
                }
            }
        }
    }

    @Override
    public int getIntrinsicWidth() {
        return Math.max(mSelectedDrawable.getIntrinsicWidth(),
                mUnselectedDrawable.getIntrinsicWidth());
    }

    @Override
    public int getIntrinsicHeight() {
        return Math.max(mSelectedDrawable.getIntrinsicHeight(),
                mUnselectedDrawable.getIntrinsicHeight());
    }

}
