package com.murtz.customVideoPlayer.presentation.views.activity;

/**
 * Created by Murtuza.Saifee on 29-Jun-18.
 */

import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

import com.murtz.customVideoPlayer.R;


public abstract class BaseActivity extends AppCompatActivity {

    public void navigateToFragments(boolean isAddToBackStack, Fragment fragment, String tag) {
        if (isAddToBackStack) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.contentView, fragment, tag)
                    .addToBackStack(tag)
                    .commitAllowingStateLoss();
        } else {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.contentView, fragment, tag)
                    .commitAllowingStateLoss();
        }
    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
