package com.ka.billingsystem.java;

import android.text.InputFilter;
import android.text.Spanned;

public class RangeFilter implements InputFilter {
    private final int minValue;
    private final int maxValue;

    public RangeFilter(int minValue, int maxValue) {
        this.minValue = minValue;
        this.maxValue = maxValue;
    }

    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
        try {
            int input = Integer.parseInt(dest.toString() + source.toString());
            if (isInRange(input)) {
                return null;
            }
        } catch (NumberFormatException ignored) {
        }
        return "";
    }

    private boolean isInRange(int value) {
        return value >= minValue && value <= maxValue;
    }
}
