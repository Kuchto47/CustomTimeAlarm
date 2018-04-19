/*
 * Copyright 2012 Jay Weisskopf
 *
 * Licensed under the MIT License (see LICENSE.txt)
 */

package com.project.pv239.customtimealarm.fragments;

import android.content.Context;
import android.content.res.TypedArray;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import com.project.pv239.customtimealarm.R;

import java.util.Locale;

/**
 * @author Jay Weisskopf
 */
public class SliderPreference extends DialogPreference {

	public final static int MAXIMUM = 12*60-1;
	public final static int MINIMUM = 0;
	protected final static int SEEKBAR_RESOLUTION = 12*60-1;

	protected int mValue;
	protected int mSeekBarValue;

	public SliderPreference(Context context, AttributeSet attrs) {
		super(context, attrs);
		setup();
	}

	public SliderPreference(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		setup();
	}

	private void setup() {
		setDialogLayoutResource(R.layout.slider_preference_dialog);
	}

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return a.getInteger(index, 480);
    }

    @Override
    protected void onSetInitialValue(boolean restoreValue, Object defaultValue) {
        setValue(restoreValue ? getPersistedInt(mValue) : (Integer) defaultValue);
    }

    public int getValue() {
        return mValue;
    }


    public void setValue(int value) {
        value = Math.max(MINIMUM, Math.min(value, MAXIMUM)); // clamp to [MINIMUM, MAXIMUM]
        if (shouldPersist()) {
            persistInt(value);
        }
        if (value != mValue) {
            mValue = value;
            notifyChanged();
        }
    }
	@Override
    protected View onCreateDialogView() {
        mSeekBarValue = getValue();
        final View view = super.onCreateDialogView();
        TextView text = view.findViewById(R.id.slider_text);
        String s =  String.format(Locale.getDefault(),"%02d:%02d", mSeekBarValue/60, mSeekBarValue%60);
        text.setText(s);
        CircularSeekBar seekbar = view.findViewById(R.id.slider_preference_seekbar);
        seekbar.setMax(SEEKBAR_RESOLUTION);
        seekbar.setProgress(mSeekBarValue);
        seekbar.setOnSeekBarChangeListener(new CircularSeekBar.OnCircularSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(CircularSeekBar seekBar) {
            }

            @Override
            public void onStartTrackingTouch(CircularSeekBar seekBar) {
            }

            @Override
            public void onProgressChanged(CircularSeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    SliderPreference.this.mSeekBarValue = progress;
                    TextView text = view.findViewById(R.id.slider_text);
                    String s =  String.format(Locale.getDefault(),"%02d:%02d", mSeekBarValue/60, mSeekBarValue%60);
                    text.setText(s);
                }
            }
        });
        return view;
    }

	@Override
	protected void onDialogClosed(boolean positiveResult) {
	    if (positiveResult) {
            setValue(mSeekBarValue);
        }
		super.onDialogClosed(positiveResult);
	}

}
