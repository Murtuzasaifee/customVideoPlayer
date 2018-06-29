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

import java.lang.ref.WeakReference;

public class VideoPlayerActivity extends Activity {

    private SimpleExoPlayerView simpleExoPlayerView;
    private SimpleExoPlayer player;
    private DataSource.Factory mediaDataSourceFactory;
    private DefaultTrackSelector trackSelector;
    private boolean shouldAutoPlay;
    private BandwidthMeter bandwidthMeter;
    private ImageView ivHideControllerButton;
    private WeakReference<VideoPlayerActivity> videoPlayerActivityWR;


    private String videoUrl = "http://intigralvod1-vh.akamaihd.net/i/3rdparty/Season2017_2018/10_12_2017_Hilal_fath/Highlights/high_,256,512,768,1200,.mp4.csmil/master.m3u8";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);
        videoPlayerActivityWR = new WeakReference<>(this);
        shouldAutoPlay = true;
        bandwidthMeter = new DefaultBandwidthMeter();
        mediaDataSourceFactory = new DefaultDataSourceFactory(videoPlayerActivityWR.get(), Util.getUserAgent(this, "mediaPlayerSample"), (TransferListener<? super DataSource>) bandwidthMeter);
        ivHideControllerButton =  findViewById(R.id.exo_controller);

    }

    @Override
    public void onStart() {
        super.onStart();
        if (Util.SDK_INT > 23) {
            initializePlayer();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if ((Util.SDK_INT <= 23 || player == null)) {
            initializePlayer();
        }
    }

    /**
     * Initialize Player and the resources
     * */
    private void initializePlayer() {

        simpleExoPlayerView =  findViewById(R.id.videoPlayerView);
        simpleExoPlayerView.requestFocus();

        TrackSelection.Factory videoTrackSelectionFactory =
                new AdaptiveTrackSelection.Factory(bandwidthMeter);

        trackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);

        //Creates Exo Player instance//
        player = ExoPlayerFactory.newSimpleInstance(videoPlayerActivityWR.get(), trackSelector);
        //Set Exo Player instance to the View
        simpleExoPlayerView.setPlayer(player);
        player.setPlayWhenReady(shouldAutoPlay);

        //Creates and set the HLS media source to the player//
        MediaSource mediaSource = new HlsMediaSource(Uri.parse(videoUrl),
                mediaDataSourceFactory, null, null);
        player.prepare(mediaSource);

        ivHideControllerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                simpleExoPlayerView.hideController();
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        if (Util.SDK_INT <= 23) {
            releasePlayer();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (Util.SDK_INT > 23) {
            releasePlayer();
        }
    }

    /**
     * Release Player and resources
     * */
    private void releasePlayer() {
        if (player != null) {
            shouldAutoPlay = player.getPlayWhenReady();
            player.release();
            player = null;
            trackSelector = null;
        }
    }
}