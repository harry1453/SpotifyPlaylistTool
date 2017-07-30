package com.reissgrvs.spotifyplaylisttool.SpotifyAPI;

import java.util.Map;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.Header;
import retrofit.http.Multipart;
import retrofit.http.POST;

/**
 * Created by Reiss on 15/07/2017.
 */

public interface SpotifyAuthService {

    @FormUrlEncoded
    @POST("/api/token")
    void requestAccessTokens(@Field("grant_type") String grantType , @Field("code") String code ,@Field("redirect_uri") String redirect, Callback<AccessTokenInfo> callback);


    @FormUrlEncoded
    @POST("/api/token")
    void refreshAccessToken(@Field("grant_type") String grantType , @Field("refresh_token") String code , Callback<AccessTokenInfo> callback);

    @FormUrlEncoded
    @POST("/api/token")
    AccessTokenInfo refreshAccessToken(@Field("grant_type") String grantType , @Field("refresh_token") String code );

    @FormUrlEncoded
    @POST("/api/token")
    AccessTokenInfo requestAccessTokens(@Field("grant_type") String grantType , @Field("code") String code ,@Field("redirect_uri") String redirect);


}
