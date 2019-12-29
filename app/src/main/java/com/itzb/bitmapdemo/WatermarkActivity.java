package com.itzb.bitmapdemo;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

public class WatermarkActivity extends AppCompatActivity {

    private Bitmap srcBitmap;
    private ImageView ivWatermark;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_watermark);
        ivWatermark = findViewById(R.id.iv_watermark);
        final EditText etWatermark = findViewById(R.id.et_watermark);
        Button btWatermark = findViewById(R.id.bt_watermark);
        srcBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.pic);
        Bitmap watermarkBitmap = addWatermark(srcBitmap, "水印");
        ivWatermark.setImageBitmap(watermarkBitmap);

        btWatermark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String string = etWatermark.getText().toString();
                Bitmap watermarkBitmap = addWatermark(srcBitmap, string);
                ivWatermark.setImageBitmap(watermarkBitmap);
            }
        });
    }

    private Bitmap addWatermark(Bitmap srcBitmap, String watermark) {
        Paint paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setTextSize(50);
        Bitmap resultBitmap = Bitmap.createBitmap(srcBitmap.getWidth(), srcBitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(resultBitmap);
        canvas.drawBitmap(srcBitmap, 0, 0, null);
        float watermarkWidth = paint.measureText(watermark);
        canvas.drawText(watermark, srcBitmap.getWidth() - watermarkWidth - 20, srcBitmap.getHeight() - 30, paint);
        return resultBitmap;
    }
}
