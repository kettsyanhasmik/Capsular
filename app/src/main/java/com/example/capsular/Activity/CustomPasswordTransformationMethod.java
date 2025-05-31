package com.example.capsular.Activity;

import android.text.method.PasswordTransformationMethod;
import android.view.View;

public class CustomPasswordTransformationMethod extends PasswordTransformationMethod {
    @Override
    public CharSequence getTransformation(CharSequence source, View view) {
        return new PasswordCharSequence(source);
    }

    private static class PasswordCharSequence implements CharSequence {
        private final CharSequence source;

        PasswordCharSequence(CharSequence source) {
            this.source = source;
        }

        public char charAt(int index) {
            return '●';
        }

        public int length() {
            return source.length();
        }

        public CharSequence subSequence(int start, int end) {
            return source.subSequence(start, end);
        }
    }
}