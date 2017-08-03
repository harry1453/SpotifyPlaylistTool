package com.reissgrvs.spotifyplaylisttool.SpotifyAPI;

import android.os.Parcel;
import android.os.Parcelable;


public class AccessTokenInfo implements Parcelable{
    private String access_token;
    private String expires_in;
    private String refresh_token;
    private String token_type;
    private String scope;


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


    private AccessTokenInfo(Parcel in) {
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

    String getRefreshToken() {
        return refresh_token;
    }

    String getExpiry() {
        return expires_in;
    }

    String getAccessToken() {
        return access_token;
    }
}
