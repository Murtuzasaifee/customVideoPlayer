package com.murtz.customVideoPlayer.presentation.views.activity;

/**
 * Created by Murtuza.Saifee on 29-Jun-18.
 */

import android.content.Intent;
import android.os.Bundle;

import com.murtz.customVideoPlayer.BuildConfig;
import com.murtz.customVideoPlayer.R;
import com.murtz.customVideoPlayer.presentation.utils.AppConstants;
import com.murtz.customVideoPlayer.presentation.views.fragment.HomeFragment;

import java.lang.ref.WeakReference;

public class HomeActivity extends BaseActivity implements HomeFragment.FragmentInteractionListener {

    private WeakReference<HomeActivity> mainActivityWR;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mainActivityWR = new WeakReference<>(this);
        navigateToFragments(false, new HomeFragment(), HomeFragment.TAG);
    }

    /**
     * Opens Video Player Activity
     */
    @Override
    public void openVideoPlayer(String videoUrl) {
        Intent videoPlayerIntent = new Intent(mainActivityWR.get(), VideoPlayerActivity.class);
        videoPlayerIntent.putExtra(AppConstants.VIDEO_URL, videoUrl);
        startActivity(videoPlayerIntent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mainActivityWR = null;
    }
}
