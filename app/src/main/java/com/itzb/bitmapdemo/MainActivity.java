package com.itzb.bitmapdemo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btnBlur;
    private Button btnWatermark;
    private Button btnHuge;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnBlur = findViewById(R.id.bt_blur);
        btnWatermark = findViewById(R.id.bt_watermark);
        btnHuge = findViewById(R.id.bt_huge);
        btnBlur.setOnClickListener(this);
        btnWatermark.setOnClickListener(this);
        btnHuge.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_blur:
                startActivity(new Intent(MainActivity.this, BlurActivity.class));
                break;
            case R.id.bt_watermark:
                startActivity(new Intent(MainActivity.this, WatermarkActivity.class));
                break;
            case R.id.bt_huge:
                startActivity(new Intent(MainActivity.this, HugeActivity.class));
                break;
        }
    }
}
