package com.mudtusuario.custom;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.TextView;

public class CustomTextView extends android.support.v7.widget.AppCompatTextView {

    public CustomTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(defStyle);
    }

    public CustomTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CustomTextView(Context context) {
        super(context);
        init();
    }

    private void init() {
        int style = 0;
        if (!isInEditMode()) {
            if(getTypeface() != null)
                style = getTypeface().getStyle();

            if(style == Typeface.NORMAL) {
                Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "font/Avenir.otf");
                setTypeface(tf);
            }
            if(style == Typeface.BOLD) {
                Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "font/AvenirBold.otf");
                setTypeface(tf);
            }
        }
    }

    private void init(int style) {
        if (!isInEditMode()) {
            if(style == Typeface.NORMAL) {
                Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "font/Avenir.otf");
                setTypeface(tf);
            }
            if(style == Typeface.BOLD) {
                Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "font/AvenirBold.otf");
                setTypeface(tf);
            }
        }
    }
    
    @Override
    public boolean onSetAlpha(int alpha) {
      setTextColor(getTextColors().withAlpha(alpha));
      setHintTextColor(getHintTextColors().withAlpha(alpha));
      setLinkTextColor(getLinkTextColors().withAlpha(alpha));
      return true;
    }

    public void setTypeface(Typeface tf, int style) {
        if(style == Typeface.NORMAL) {
            super.setTypeface(Typeface.createFromAsset(getContext().getAssets(), "font/Avenir.otf"));
        }
        if(style == Typeface.BOLD) {
            super.setTypeface(Typeface.createFromAsset(getContext().getAssets(), "font/AvenirBold.otf"));
        }
    }

}
