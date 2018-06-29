package com.murtz.customVideoPlayer.presentation.views.fragment;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.murtz.customVideoPlayer.BuildConfig;
import com.murtz.customVideoPlayer.R;
import com.murtz.customVideoPlayer.presentation.views.activity.BaseActivity;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {

    public static final String TAG = "HomeFragment";
    private View parentView;
    private BaseActivity activity;
    private FragmentInteractionListener listener;

    public HomeFragment() {
        // Required empty public constructor
    }

    public interface FragmentInteractionListener{
        void openVideoPlayer(String videoUrl);
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //Avoid recreation of screen if already rendered//
        if (parentView != null)
            return parentView;

        parentView = inflater.inflate(R.layout.fragment_home, container, false);
        parentView.findViewById(R.id.videoPlayerBtn).setOnClickListener(clickListener);
        return parentView;
    }

    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
           listener.openVideoPlayer(BuildConfig.VIDEO_URL);
        }
    };

    @Override
    public void onDetach() {
        super.onDetach();
        clickListener = null;
        activity =null;
    }
}
