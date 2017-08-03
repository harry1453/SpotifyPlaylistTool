package com.reissgrvs.spotifyplaylisttool;

import kaaes.spotify.webapi.android.models.Track;



//Shit work around class so I could use HashSets for playlist membership checking
public class CustomTrack {
    public final Track mTrack;

    public CustomTrack(Track track){
        mTrack = track;
    }

    @Override
    public int hashCode() {
        int hash = 7;

        for (int i = 0; i < mTrack.uri.length(); i++) {
            hash = hash*31 + mTrack.uri.charAt(i);
        }
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof CustomTrack) && ((CustomTrack) obj).mTrack.id.equals(mTrack.id);
    }
}
