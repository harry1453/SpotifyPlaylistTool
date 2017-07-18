package com.reissgrvs.spotifyplaylisttool.UpdateService;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import com.reissgrvs.spotifyplaylisttool.SpotifyAPI.AccessTokenInfo;
import com.reissgrvs.spotifyplaylisttool.SpotifyAPI.SpotifyAPIManager;
import com.reissgrvs.spotifyplaylisttool.SpotifyAPI.TokenStore;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Reiss on 18/07/2017.
 */

public class UpdateMultiplaylistService extends com.firebase.jobdispatcher.JobService {

    private AsyncTask mBackgroundTask;

    @Override
    public boolean onStartJob(final com.firebase.jobdispatcher.JobParameters jobParameters) {

        mBackgroundTask = new AsyncTask() {

            @Override
            protected Object doInBackground(Object[] params) {
                Context context = UpdateMultiplaylistService.this;
                //TODO: Do tasks

                //Refresh Auth Token
                //Once refreshed update every multiplaylist

                return null;
            }

            @Override
            protected void onPostExecute(Object o) {

                jobFinished(jobParameters, false);
            }
        };

        mBackgroundTask.execute();
        return true;
    }


    @Override
    public boolean onStopJob(com.firebase.jobdispatcher.JobParameters jobParameters) {
        if (mBackgroundTask != null) mBackgroundTask.cancel(true);
        return true;
    }


}
