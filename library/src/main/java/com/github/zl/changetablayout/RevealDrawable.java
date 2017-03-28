package com.github.zl.changetablayout;

import android.content.res.Resources;
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
 */
class RevealDrawable extends Drawable implements Drawable.Callback {

    private RevealState mRevealState;
    private final Rect mTmpRect = new Rect();

    static final int HORIZONTAL = 1;
    static final int VERTICAL = 2;

    RevealDrawable(Drawable unselected, Drawable selected, int orientation) {
        this(null, null);

        mRevealState.mUnselectedDrawable = unselected;
        mRevealState.mSelectedDrawable = selected;
        mRevealState.mOrientation = orientation;

        if (unselected != null) {
            unselected.setCallback(this);
        }
        if (selected != null) {
            selected.setCallback(this);
        }
    }

    public void setOrientation(int orientation){
        mRevealState.mOrientation = orientation;
    }

    @Override
    public void invalidateDrawable(@NonNull Drawable who) {
        final Callback callback = getCallback();
        if (callback != null) {
            callback.invalidateDrawable(this);
        }
    }

    @Override
    public void scheduleDrawable(@NonNull Drawable who, @NonNull Runnable what, long when) {
        final Callback callback = getCallback();
        if (callback != null) {
            callback.scheduleDrawable(this, what, when);
        }
    }

    @Override
    public void unscheduleDrawable(@NonNull Drawable who, @NonNull Runnable what) {
        final Callback callback = getCallback();
        if (callback != null) {
            callback.unscheduleDrawable(this, what);
        }
    }

    @Override
    public int getChangingConfigurations() {
        return super.getChangingConfigurations()
                | mRevealState.mChangingConfigurations
                | mRevealState.mUnselectedDrawable.getChangingConfigurations()
                | mRevealState.mSelectedDrawable.getChangingConfigurations();
    }

    @Override
    public boolean getPadding(@NonNull Rect padding) {
        // XXX need to adjust padding!
        return mRevealState.mSelectedDrawable.getPadding(padding);
    }

    @Override
    public boolean setVisible(boolean visible, boolean restart) {
        mRevealState.mUnselectedDrawable.setVisible(visible, restart);
        mRevealState.mSelectedDrawable.setVisible(visible, restart);
        return super.setVisible(visible, restart);
    }

    @Override
    public void setAlpha(int alpha) {

    }

    @Override
    public void setColorFilter(ColorFilter cf) {

    }

    @Override
    public int getOpacity() {
        return PixelFormat.UNKNOWN;
    }

    @Override
    protected void onBoundsChange(Rect bounds) {
        mRevealState.mUnselectedDrawable.setBounds(bounds);
        mRevealState.mSelectedDrawable.setBounds(bounds);
    }

    @Override
    protected boolean onLevelChange(int level) {
        mRevealState.mUnselectedDrawable.setLevel(level);
        mRevealState.mSelectedDrawable.setLevel(level);
        invalidateSelf();
        return true;
    }

    @Override
    public void draw(@NonNull Canvas canvas) {

        // If level == 10000 || level == 0, just draw the unselected image
        int level = getLevel();
        if (level == 10000 || level == 0) {
            mRevealState.mUnselectedDrawable.draw(canvas);
        }

        // If level == 5000 just draw the selected image
        else if (level == 5000) {
            mRevealState.mSelectedDrawable.draw(canvas);
        }

        // Else, draw the transitional version
        else {
            final Rect r = mTmpRect;
            final Rect bounds = getBounds();

            { // Draw the unselected portion
                float value = (level / 5000f) - 1f;
                int w = bounds.width();
                if ((mRevealState.mOrientation & HORIZONTAL) != 0) {
                    w = (int) (w * Math.abs(value));
                }
                int h = bounds.height();
                if ((mRevealState.mOrientation & VERTICAL) != 0) {
                    h = (int) (h * Math.abs(value));
                }

                int gravity;
                if ((mRevealState.mOrientation & HORIZONTAL) != 0) {
                    gravity = value < 0 ? Gravity.LEFT : Gravity.RIGHT;
                }else {
                    gravity = value < 0 ? Gravity.BOTTOM : Gravity.TOP;
                }

                Gravity.apply(gravity, w, h, bounds, r);

                if (w > 0 && h > 0) {
                    canvas.save();
                    canvas.clipRect(r);
                    mRevealState.mUnselectedDrawable.draw(canvas);
                    canvas.restore();
                }
            }

            { // Draw the selected portion
                float value = (level / 5000f) - 1f;
                int w = bounds.width();
                if ((mRevealState.mOrientation & HORIZONTAL) != 0) {
                    w -= (int) (w * Math.abs(value));
                }
                int h = bounds.height();
                if ((mRevealState.mOrientation & VERTICAL) != 0) {
                    h -= (int) (h * Math.abs(value));
                }

                int gravity;
                if ((mRevealState.mOrientation & HORIZONTAL) != 0) {
                    gravity = value < 0 ? Gravity.RIGHT : Gravity.LEFT;
                }else {
                    gravity = value < 0 ? Gravity.TOP : Gravity.BOTTOM;
                }

                Gravity.apply(gravity, w, h, bounds, r);

                if (w > 0 && h > 0) {
                    canvas.save();
                    canvas.clipRect(r);
                    mRevealState.mSelectedDrawable.draw(canvas);
                    canvas.restore();
                }
            }
        }
    }

    @Override
    public int getIntrinsicWidth() {
        return Math.max(mRevealState.mSelectedDrawable.getIntrinsicWidth(),
                mRevealState.mUnselectedDrawable.getIntrinsicWidth());
    }

    @Override
    public int getIntrinsicHeight() {
        return Math.max(mRevealState.mSelectedDrawable.getIntrinsicHeight(),
                mRevealState.mUnselectedDrawable.getIntrinsicHeight());
    }

    @Override
    public ConstantState getConstantState() {
        if (mRevealState.canConstantState()) {
            mRevealState.mChangingConfigurations = getChangingConfigurations();
            return mRevealState;
        }
        return null;
    }

    private final static class RevealState extends ConstantState {
        Drawable mSelectedDrawable;
        Drawable mUnselectedDrawable;
        int mChangingConfigurations;
        int mOrientation;

        private boolean mCheckedConstantState;
        private boolean mCanConstantState;

        RevealState(RevealState orig, RevealDrawable owner, Resources res) {
            if (orig != null) {
                if (res != null) {
                    mSelectedDrawable = orig.mSelectedDrawable.getConstantState().newDrawable(res);
                    mUnselectedDrawable = orig.mUnselectedDrawable.getConstantState().newDrawable(res);
                } else {
                    mSelectedDrawable = orig.mSelectedDrawable.getConstantState().newDrawable();
                    mUnselectedDrawable = orig.mUnselectedDrawable.getConstantState().newDrawable();
                }
                mSelectedDrawable.setCallback(owner);
                mUnselectedDrawable.setCallback(owner);
                mOrientation = orig.mOrientation;
                mCheckedConstantState = mCanConstantState = true;
            }
        }

        @NonNull
        @Override
        public Drawable newDrawable() {
            return new RevealDrawable(this, null);
        }

        @NonNull
        @Override
        public Drawable newDrawable(Resources res) {
            return new RevealDrawable(this, res);
        }

        @Override
        public int getChangingConfigurations() {
            return mChangingConfigurations;
        }

        boolean canConstantState() {
            if (!mCheckedConstantState) {
                mCanConstantState = mSelectedDrawable.getConstantState() != null
                        && mUnselectedDrawable.getConstantState() != null;
                mCheckedConstantState = true;
            }
            return mCanConstantState;
        }
    }

    private RevealDrawable(RevealState state, Resources res) {
        mRevealState = new RevealState(state, this, res);
    }
}
