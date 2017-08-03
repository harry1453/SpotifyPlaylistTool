package com.reissgrvs.spotifyplaylisttool.SpotifyAPI;

import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.POST;


interface SpotifyAuthService {

    @FormUrlEncoded
    @POST("/api/token")
    AccessTokenInfo refreshAccessToken(@Field("grant_type") String grantType , @Field("refresh_token") String code );

    @FormUrlEncoded
    @POST("/api/token")
    AccessTokenInfo requestAccessTokens(@Field("grant_type") String grantType , @Field("code") String code ,@Field("redirect_uri") String redirect);


}
