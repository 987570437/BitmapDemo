package com.itzb.bitmapdemo;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapRegionDecoder;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.Scroller;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class HugeActivity extends AppCompatActivity {

    private HugeView hugeView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_huge);
        hugeView = findViewById(R.id.huge_view);

        InputStream inputStream = getResources().openRawResource(R.raw.huge);
        hugeView.setImage(inputStream);
    }
}
