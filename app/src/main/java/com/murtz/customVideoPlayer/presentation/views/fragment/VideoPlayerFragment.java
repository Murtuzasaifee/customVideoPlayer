package com.murtz.customVideoPlayer.presentation.views.fragment;


import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.murtz.customVideoPlayer.presentation.utils.AppConstants;
import com.murtz.customVideoPlayer.presentation.views.activity.BaseActivity;

/**
 * A simple {@link Fragment} subclass.
 */
public class VideoPlayerFragment extends Fragment {

    public static final String TAG = "VideoPlayerFragment";
    private SimpleExoPlayerView simpleExoPlayerView;
    private SimpleExoPlayer player;
    private DataSource.Factory mediaDataSourceFactory;
    private DefaultTrackSelector trackSelector;
    private boolean shouldAutoPlay;
    private BandwidthMeter bandwidthMeter;
    private ImageView ivHideControllerButton;
    private String videoUrl;
    private View parentView;
    private BaseActivity activity;
    private FragmentInteractionListener listener;
    private boolean isOverlayVisible;

    public VideoPlayerFragment() {
        // Required empty public constructor
    }

    public interface FragmentInteractionListener {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof FragmentInteractionListener) {
            listener = (FragmentInteractionListener) context;
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof FragmentInteractionListener) {
            listener = (FragmentInteractionListener) activity;
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        activity = (BaseActivity) getActivity();

        shouldAutoPlay = true;
        bandwidthMeter = new DefaultBandwidthMeter();
        mediaDataSourceFactory = new DefaultDataSourceFactory(activity, Util.getUserAgent(activity, getString(R.string.app_name)), (TransferListener<? super DataSource>) bandwidthMeter);
        ivHideControllerButton = parentView.findViewById(R.id.exo_controller);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Bundle bundle = getArguments();
        if (bundle != null)
            videoUrl = bundle.getString(AppConstants.VIDEO_URL);

        //Avoid recreation of screen if already rendered//
        if (parentView != null)
            return parentView;

        parentView = inflater.inflate(R.layout.fragment_video_player, container, false);
        setButtonListeners();
        return parentView;
    }

    private void setButtonListeners() {
        parentView.findViewById(R.id.closeBtn).setOnClickListener(clickListener);
        parentView.findViewById(R.id.showListBtn).setOnClickListener(clickListener);
        parentView.findViewById(R.id.homeTeamBtn).setOnClickListener(clickListener);
        parentView.findViewById(R.id.awayTeamBtn).setOnClickListener(clickListener);
    }

    @Override
    public void onStart() {
        super.onStart();
        initializePlayer();
    }

    @Override
    public void onResume() {
        super.onResume();
        playPausePlayer(true);
    }

    /**
     * Initialize Player and the resources
     */
    private void initializePlayer() {

        simpleExoPlayerView = parentView.findViewById(R.id.videoPlayerView);
        simpleExoPlayerView.requestFocus();

        TrackSelection.Factory videoTrackSelectionFactory =
                new AdaptiveTrackSelection.Factory(bandwidthMeter);

        trackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);

        //Creates Exo Player instance//
        player = ExoPlayerFactory.newSimpleInstance(activity, trackSelector);
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

    private void playPausePlayer(boolean isPlay) {
        if (player != null) {
            player.setPlayWhenReady(isPlay);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        playPausePlayer(false);
    }

    @Override
    public void onStop() {
        super.onStop();
        releasePlayer();
    }

    /**
     * Release Player and resources
     */
    private void releasePlayer() {
        if (player != null) {
            shouldAutoPlay = player.getPlayWhenReady();
            player.release();
            player = null;
            trackSelector = null;
        }
    }

    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.closeBtn:
                    releasePlayer();
                    activity.finish();
                    break;

                case R.id.showListBtn:
                    showHideOverlay();
                    break;

                case R.id.homeTeamBtn:
                    parentView.findViewById(R.id.homeTeamBtn).setBackgroundResource(R.drawable.round_border_fill);
                    parentView.findViewById(R.id.awayTeamBtn).setBackgroundResource(R.drawable.round_border_hollow);
                    break;

                case R.id.awayTeamBtn:
                    parentView.findViewById(R.id.homeTeamBtn).setBackgroundResource(R.drawable.round_border_hollow);
                    parentView.findViewById(R.id.awayTeamBtn).setBackgroundResource(R.drawable.round_border_fill);
                    break;
            }
        }
    };

    private void showHideOverlay() {
        if (isOverlayVisible) {
            playPausePlayer(true);
            parentView.findViewById(R.id.overlayView).setVisibility(View.GONE);
        } else {
            playPausePlayer(false);
            parentView.findViewById(R.id.overlayView).setVisibility(View.VISIBLE);
        }
        isOverlayVisible = !isOverlayVisible;
    }
}
