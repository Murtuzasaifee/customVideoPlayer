package com.murtz.customVideoPlayer.presentation.views.fragment;


import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

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
import com.murtz.customVideoPlayer.datamodel.LineupsModel;
import com.murtz.customVideoPlayer.datamodel.PlayersModel;
import com.murtz.customVideoPlayer.network.Connection;
import com.murtz.customVideoPlayer.presentation.utils.AppConstants;
import com.murtz.customVideoPlayer.presentation.views.activity.BaseActivity;
import com.murtz.customVideoPlayer.presentation.views.adapter.PlayersRVAdapter;

import java.lang.ref.WeakReference;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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
    private WeakReference<BaseActivity> activityWR;
    private FragmentInteractionListener listener;
    private boolean isOverlayVisible;
    private Call lineAPICall;
    private Button showListBtn, homeTeamBtn, awayTeamBtn;
    private LineupsModel lineupsModel;
    private RecyclerView playersRV, homePlayersRV, awayPlayersRV;
    private View overlayView;

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
        activityWR = new WeakReference<>((BaseActivity) getActivity());

        shouldAutoPlay = true;
        bandwidthMeter = new DefaultBandwidthMeter();
        mediaDataSourceFactory = new DefaultDataSourceFactory(activityWR.get(), Util.getUserAgent(activityWR.get(), getString(R.string.app_name)), (TransferListener<? super DataSource>) bandwidthMeter);
        ivHideControllerButton = parentView.findViewById(R.id.exo_controller);
        initPlayersRV();
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
        overlayView = parentView.findViewById(R.id.overlayView);
        setButtonListeners();
        getLineups();
        return parentView;
    }

    /**
     * Initialize Recycler View of Players list for
     * both mobile and tablet
     */
    private void initPlayersRV() {
        if (!getResources().getBoolean(R.bool.isTablet)) {
            GridLayoutManager gridLayoutManager = new GridLayoutManager(activityWR.get(), 3);
            playersRV = parentView.findViewById(R.id.playerRV);
            playersRV.setLayoutManager(gridLayoutManager);
        } else {
            GridLayoutManager homeGridLayoutManager = new GridLayoutManager(activityWR.get(), 2);
            GridLayoutManager awayGridLayoutManager = new GridLayoutManager(activityWR.get(), 2);
            homePlayersRV = parentView.findViewById(R.id.homePlayerRV);
            awayPlayersRV = parentView.findViewById(R.id.awayPlayerRV);
            homePlayersRV.setLayoutManager(homeGridLayoutManager);
            awayPlayersRV.setLayoutManager(awayGridLayoutManager);
        }
    }

    /**
     * Set the Players adapter depending on the type of data
     */
    private void setPlayersRVAdapter(List<PlayersModel> playersModelList) {
        PlayersRVAdapter playersRVAdapter = new PlayersRVAdapter(playersModelList, clickListener);
        playersRV.setAdapter(playersRVAdapter);
    }


    /**
     * Set the Players adapter for both home and
     * away teams for Tablets
     */
    private void setBothTeamsRVAdapter() {
        PlayersRVAdapter homePlayerRVAdapter = new PlayersRVAdapter(lineupsModel.getLineups().getData().getHomeTeam().getPlayers(), clickListener);
        homePlayersRV.setAdapter(homePlayerRVAdapter);

        PlayersRVAdapter awayPlayerRVAdapter = new PlayersRVAdapter(lineupsModel.getLineups().getData().getAwayTeam().getPlayers(), clickListener);
        awayPlayersRV.setAdapter(awayPlayerRVAdapter);
    }

    /**
     * Setup listeners for buttons
     */
    private void setButtonListeners() {
        parentView.findViewById(R.id.closeBtn).setOnClickListener(clickListener);

        showListBtn = parentView.findViewById(R.id.showListBtn);
        showListBtn.setOnClickListener(clickListener);

        if (!getResources().getBoolean(R.bool.isTablet)){
            homeTeamBtn = parentView.findViewById(R.id.homeTeamBtn);
            homeTeamBtn.setOnClickListener(clickListener);

            awayTeamBtn = parentView.findViewById(R.id.awayTeamBtn);
            awayTeamBtn.setOnClickListener(clickListener);
        }
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
        player = ExoPlayerFactory.newSimpleInstance(activityWR.get(), trackSelector);
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

    /**
     * Play or pause player
     */
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

    /**
     * Click listeners of all the views
     */
    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.closeBtn:
                    closeScreen();
                    break;

                case R.id.showListBtn:
                    showHideOverlay();
                    break;

                case R.id.homeTeamBtn:
                    if (lineupsModel != null)
                        toggleTeams(R.drawable.round_border_fill,
                                R.drawable.round_border_hollow,
                                lineupsModel.getLineups().getData().getHomeTeam().getPlayers());
                    break;

                case R.id.awayTeamBtn:
                    if (lineupsModel != null)
                        toggleTeams(R.drawable.round_border_hollow,
                                R.drawable.round_border_fill,
                                lineupsModel.getLineups().getData().getAwayTeam().getPlayers());
                    break;

                case R.id.playerCard:
                    showHideOverlay();
                    break;
            }
        }
    };


    /**
     * Show hide overlay view.
     */
    private void showHideOverlay() {
        if (!getResources().getBoolean(R.bool.isTablet))
            showHideMobileOverlay();
        else
            showHideTabletOverlay();
    }

    /**
     * Toggle Mobile Overlay View
     * */
    private void showHideMobileOverlay() {
        Animation slideInRight = AnimationUtils.loadAnimation(activityWR.get(), R.anim.anim_slide_in_right);
        Animation slideOutRight = AnimationUtils.loadAnimation(activityWR.get(), R.anim.anim_slide_out_right);
        slideInRight.setAnimationListener(animListener);
        slideOutRight.setAnimationListener(animListener);

        if (isOverlayVisible) {
            overlayView.startAnimation(slideOutRight);
            showListBtn.setText(R.string.showList);
            playPausePlayer(true);
        } else {
            overlayView.setVisibility(View.VISIBLE);
            //perform click of home team button to set the home team list
            //when user first opens the overlay
            homeTeamBtn.performClick();

            overlayView.startAnimation(slideInRight);
            showListBtn.setText(R.string.hideList);
            playPausePlayer(false);
        }
    }

    /**
     * Toggle Tablet Overlay View
     * */
    private void showHideTabletOverlay() {
        Animation slideInRight = AnimationUtils.loadAnimation(activityWR.get(), R.anim.anim_slide_in_right);
        Animation slideOutRight = AnimationUtils.loadAnimation(activityWR.get(), R.anim.anim_slide_out_right);
        Animation slideInLeft = AnimationUtils.loadAnimation(activityWR.get(), R.anim.anim_slide_in_left);
        Animation slideOutLeft = AnimationUtils.loadAnimation(activityWR.get(), R.anim.anim_slide_out_left);
        slideInRight.setAnimationListener(animListener);
        slideOutRight.setAnimationListener(animListener);
        slideInLeft.setAnimationListener(animListener);
        slideOutLeft.setAnimationListener(animListener);

        View homeTeamView = parentView.findViewById(R.id.homeTeamView);
        View awayTeamView = parentView.findViewById(R.id.awayTeamView);

        if (isOverlayVisible) {
            homeTeamView.startAnimation(slideOutLeft);
            awayTeamView.startAnimation(slideOutRight);
            showListBtn.setText(R.string.showList);
            playPausePlayer(true);
        } else {
            overlayView.setVisibility(View.VISIBLE);
            setBothTeamsRVAdapter();
            homeTeamView.startAnimation(slideInLeft);
            awayTeamView.startAnimation(slideInRight);
            showListBtn.setText(R.string.hideList);
            playPausePlayer(false);
        }
        isOverlayVisible = !isOverlayVisible;
    }


    /**
     * Animation Listener for overlay
     */
    private Animation.AnimationListener animListener = new Animation.AnimationListener() {
        @Override
        public void onAnimationStart(Animation animation) {

        }

        @Override
        public void onAnimationEnd(Animation animation) {
            if (isOverlayVisible)
                overlayView.setVisibility(View.GONE);
            else
                overlayView.setVisibility(View.VISIBLE);

            isOverlayVisible = !isOverlayVisible;
        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }
    };


    /**
     * Toggle the theme for home button and away button
     * And set the adapter with respective team data
     */
    private void toggleTeams(int homeBtnTheme, int awayBtnTheme, List<PlayersModel> playersList) {
        homeTeamBtn.setBackgroundResource(homeBtnTheme);
        awayTeamBtn.setBackgroundResource(awayBtnTheme);
        setPlayersRVAdapter(playersList);
    }


    /**
     * Api Call to get the lineups
     */
    private void getLineups() {
        lineAPICall = Connection.getConnectionInstance().getLineups(getLineupsAPICallback);
    }


    /**
     * Release resources and close activity
     */
    private void closeScreen() {
        releasePlayer();
        activityWR.get().finish();
    }


    /**
     * Callback to handle the response of Lineups
     */
    private Callback<LineupsModel> getLineupsAPICallback = new Callback<LineupsModel>() {
        @Override
        public void onResponse(Call<LineupsModel> call, Response<LineupsModel> response) {
            if (response.isSuccessful() && response.body() != null && response.body().getLineups().isSuccess())
                lineupsModel = response.body();
            else
                Toast.makeText(activityWR.get(), getString(R.string.serviceNotAvailable), Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onFailure(Call<LineupsModel> call, Throwable t) {
            Toast.makeText(activityWR.get(), getString(R.string.serviceNotAvailable), Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    public void onDetach() {
        super.onDetach();

        //Cancel the API call if the request is in flight mode.//
        if (lineAPICall != null)
            lineAPICall.cancel();

        getLineupsAPICallback = null;
        clickListener = null;
        activityWR = null;
        animListener = null;
    }
}
