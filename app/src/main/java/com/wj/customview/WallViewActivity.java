package com.wj.customview;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.wj.customview.wave.WaveView;

public class WallViewActivity extends AppCompatActivity {

    private WaveView waveView;
    private boolean isWave = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wall_view);

        waveView = findViewById(R.id.waveView);
        waveView.setProgressAnim(53);

    }

    @Override
    protected void onPause() {
        super.onPause();
        waveView.switchWave(false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        waveView.switchWave(true);
    }

    public void switchWave(View view) {
        waveView.switchWave(isWave = !isWave);
    }
}
