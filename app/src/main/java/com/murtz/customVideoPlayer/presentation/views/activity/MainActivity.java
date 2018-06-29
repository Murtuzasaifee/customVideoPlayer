package com.murtz.customVideoPlayer.presentation.views.activity;

/**
 * Created by Murtuza.Saifee on 29-Jun-18.
 */

import android.content.Intent;
import android.os.Bundle;

import com.murtz.customVideoPlayer.R;
import com.murtz.customVideoPlayer.presentation.views.fragment.HomeFragment;

import java.lang.ref.WeakReference;

public class MainActivity extends BaseActivity implements HomeFragment.FragmentInteractionListener{

    private WeakReference<MainActivity> mainActivityWR;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mainActivityWR = new WeakReference<>(this);
        navigateToFragments(false, new HomeFragment(), HomeFragment.TAG);
    }

    /**
     * Opens Video Player Activity
     * */
    @Override
    public void openVideoPlayer(String videoUrl) {
        Intent videoPlayerIntent = new Intent(mainActivityWR.get(), VideoPlayerActivity.class);
        startActivity(videoPlayerIntent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mainActivityWR = null;
    }
}
