package com.reissgrvs.spotifyplaylisttool.SpotifyAPI;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Reiss on 15/07/2017.
 */

public class AccessTokenInfo implements Parcelable{
    public String access_token;
    public String token_type;
    public String scope;
    public String expires_in;
    public String refresh_token;


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(access_token);
        dest.writeString(token_type);
        dest.writeString(scope);
        dest.writeString(expires_in);
        dest.writeString(refresh_token);
    }

    public AccessTokenInfo(){}

    protected AccessTokenInfo(Parcel in) {
        this.access_token = in.readString();
        this.token_type = in.readString();
        this.scope = in.readString();
        this.expires_in = in.readString();
        this.refresh_token = in.readString();
    }

    public static final Creator<AccessTokenInfo> CREATOR = new Creator<AccessTokenInfo>() {
        @Override
        public AccessTokenInfo createFromParcel(Parcel in) {
            return new AccessTokenInfo(in);
        }

        @Override
        public AccessTokenInfo[] newArray(int size) {
            return new AccessTokenInfo[size];
        }
    };
}
