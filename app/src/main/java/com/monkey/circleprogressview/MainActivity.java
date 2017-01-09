package com.monkey.circleprogressview;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final CircleProgressView view = (CircleProgressView) findViewById(R.id.circle_progress_view);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                float progress = (float) (Math.random() * 100);
                view.setProgress(progress);
            }
        });
    }
}
