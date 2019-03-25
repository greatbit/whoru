package ru.greatbit.whoru.auth.providers;

import retrofit2.Call;
import retrofit2.http.GET;
import ru.greatbit.whoru.auth.providers.jira.JiraUser;

public interface JiraRestAuthClient {
    @GET("myself")
    Call<JiraUser> getSelfUser();
}
