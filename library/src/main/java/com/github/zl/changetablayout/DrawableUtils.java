/*
 * Copyright 2017 simplezhli
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.zl.changetablayout;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;

class DrawableUtils {

    static Drawable getTabDrawable(Context context, int drawableResId, int tint) {
      Drawable drawable = getDrawableWithIntrinsicBounds(context, drawableResId).mutate();
      DrawableCompat.setTint(drawable, tint);
      return drawable;
    }

    private static Drawable getDrawableWithIntrinsicBounds(Context context, int drawableResId) {
      Drawable drawable = getDrawable(context, drawableResId);
      drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
      return drawable;
    }

    static Drawable getDrawable(Context context, int drawableResId) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return context.getDrawable(drawableResId);
        } else {
            try {
                return VectorDrawableCompat.create(context.getResources(), drawableResId, null);
            }catch (Resources.NotFoundException e){
                return ContextCompat.getDrawable(context, drawableResId);
            }
        }
    }

}
