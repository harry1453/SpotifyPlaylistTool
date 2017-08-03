package com.reissgrvs.spotifyplaylisttool.UpdateService;

import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;
import com.reissgrvs.spotifyplaylisttool.PlaylistUpdateUtils.MultiplaylistUtils;
import com.reissgrvs.spotifyplaylisttool.SpotifyAPI.TokenStore;
import com.reissgrvs.spotifyplaylisttool.Util.MultiPlaylistStore;


public class UpdateService extends JobService {


    @Override
    public boolean onStartJob(JobParameters job) {

        MultiPlaylistStore.loadMultiPlaylistFile(this);
        MultiplaylistUtils.updateAllMultiplaylists(this, TokenStore.getUserId(this));

        return false;
    }

    @Override
    public boolean onStopJob(JobParameters job) {
        return false;
    }
}
