package com.murtz.customVideoPlayer.presentation.views.activity;

/**
 * Created by Murtuza.Saifee on 29-Jun-18.
 */


import android.content.Intent;
import android.os.Bundle;

import com.murtz.customVideoPlayer.R;
import com.murtz.customVideoPlayer.presentation.utils.AppConstants;
import com.murtz.customVideoPlayer.presentation.views.fragment.VideoPlayerFragment;

import java.lang.ref.WeakReference;

public class VideoPlayerActivity extends BaseActivity {

    private WeakReference<VideoPlayerActivity> videoPlayerActivityWR;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);
        videoPlayerActivityWR = new WeakReference<>(this);
        navigateToFirstFrag();
    }

    private void navigateToFirstFrag() {
        Intent intent = getIntent();
        if (intent != null) {
            String videoUrl = intent.getStringExtra(AppConstants.VIDEO_URL);
            VideoPlayerFragment videoPlayerFragment = new VideoPlayerFragment();
            Bundle bundle = new Bundle();
            bundle.putString(AppConstants.VIDEO_URL, videoUrl);
            videoPlayerFragment.setArguments(bundle);
            navigateToFragments(false, videoPlayerFragment, VideoPlayerFragment.TAG);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        videoPlayerActivityWR = null;
    }
}