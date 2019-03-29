package ru.greatbit.whoru.auth.providers;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import ru.greatbit.whoru.auth.providers.jira.GroupsWrapper;
import ru.greatbit.whoru.auth.providers.jira.JiraUser;

import java.util.List;

public interface JiraRestAuthClient {

    @GET("myself")
    Call<JiraUser> getSelfUser();

    @GET("groups/picker")
    Call<GroupsWrapper> getGroups(@Query("query") String query);

    @GET("user/search")
    Call<List<JiraUser>> getUsers(@Query("query") String query);
}
