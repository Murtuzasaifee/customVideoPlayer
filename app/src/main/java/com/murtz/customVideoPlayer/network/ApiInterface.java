package com.murtz.customVideoPlayer.network;

import com.murtz.customVideoPlayer.datamodel.LineupsModel;

import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Created by Murtuza.Saifee on 29-Jun-18.
 */

public interface ApiInterface {

    @GET("sample.json")
    Call<LineupsModel> lineups();
}

