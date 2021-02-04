package com.google.android.material.theme;

import android.content.Context;
import android.util.AttributeSet;
import androidx.appcompat.app.AppCompatViewInflater;
import androidx.appcompat.widget.AppCompatButton;
import com.google.android.material.button.MaterialButton;

public class MaterialComponentsViewInflater extends AppCompatViewInflater {
    /* access modifiers changed from: protected */
    @Override // androidx.appcompat.app.AppCompatViewInflater
    public AppCompatButton createButton(Context context, AttributeSet attrs) {
        return new MaterialButton(context, attrs);
    }
}
