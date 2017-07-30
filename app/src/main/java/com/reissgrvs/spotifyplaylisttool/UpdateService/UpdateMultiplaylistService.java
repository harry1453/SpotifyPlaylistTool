package com.reissgrvs.spotifyplaylisttool.UpdateService;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.reissgrvs.spotifyplaylisttool.MultiplaylistList.MultiplaylistUpdater;
import com.reissgrvs.spotifyplaylisttool.SpotifyAPI.TokenStore;
import java.util.concurrent.ExecutionException;

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
                Log.d("UpdateTesting", "doInBackground");
                //TODO: Update all multiplaylists
                final Context context = UpdateMultiplaylistService.this;
                TokenStore.refreshAuthToken(context);
                MultiplaylistUpdater.updateAllMultiplaylists(context);
                return true;
            }

            @Override
            protected void onPostExecute(Object o) {
                Log.d("UpdateTesting", "onPostExecute for background task");
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
