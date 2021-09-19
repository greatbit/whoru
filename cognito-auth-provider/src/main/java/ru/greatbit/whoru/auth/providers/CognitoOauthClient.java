package ru.greatbit.whoru.auth.providers;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface CognitoOauthClient {

    @POST("oauth2/token")
    @FormUrlEncoded
    Call<CognitoTokensResponse> getOauthTokens(
            @Field("grant_type") String grantType,
            @Field("client_id") String clientId,
            @Field("code") String authorisationCode,
            @Field("redirect_uri") String redirectUrl
    );

}
