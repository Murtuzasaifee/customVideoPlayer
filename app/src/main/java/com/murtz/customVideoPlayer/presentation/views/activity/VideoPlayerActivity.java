package com.murtz.customVideoPlayer.presentation.views.activity;

/**
 * Created by Murtuza.Saifee on 29-Jun-18.
 */


import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.TransferListener;
import com.google.android.exoplayer2.util.Util;
import com.murtz.customVideoPlayer.R;
import com.murtz.customVideoPlayer.presentation.views.fragment.VideoPlayerFragment;

import java.lang.ref.WeakReference;

public class VideoPlayerActivity extends BaseActivity {

    private WeakReference<VideoPlayerActivity> videoPlayerActivityWR;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);
        videoPlayerActivityWR = new WeakReference<>(this);
        navigateToFragments(false,new VideoPlayerFragment(), VideoPlayerFragment.TAG);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        videoPlayerActivityWR = null;
    }
}