package com.murtz.customVideoPlayer.presentation.views.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.murtz.customVideoPlayer.R;

import java.lang.ref.WeakReference;

public class MainActivity extends AppCompatActivity {

    private WeakReference<MainActivity> mainActivityWR;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mainActivityWR = new WeakReference<>(this);
        findViewById(R.id.videoPlayerBtn).setOnClickListener(clickListener);
    }

    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            openVideoPlayer();
        }
    };

    /**
     * Opens Video Player Activity
     * */
    private void openVideoPlayer() {
        Intent videoPlayerIntent = new Intent(mainActivityWR.get(), VideoPlayerActivity.class);
        startActivity(videoPlayerIntent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        clickListener = null;
        mainActivityWR = null;
    }
}
