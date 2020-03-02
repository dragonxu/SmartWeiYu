package com.example.smartouwei;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.example.smartbathroom.R;
import com.example.smartouwei.view.RotationLoadingView;

/**
 * Created by Gabriel on 2018/12/21.
 */
public class LoadingDialog extends Dialog {
    public LoadingDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.load);
        RotationLoadingView rlv = findViewById(R.id.rlv);
        rlv.startRotationAnimation();
    }
}
