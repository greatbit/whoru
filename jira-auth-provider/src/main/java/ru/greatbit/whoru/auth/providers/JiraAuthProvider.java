package ru.greatbit.whoru.auth.providers;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.greatbit.whoru.auth.RedirectResponse;
import ru.greatbit.whoru.auth.Session;
import ru.greatbit.whoru.auth.error.UnauthorizedException;
import ru.greatbit.whoru.auth.providers.jira.JiraUser;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Set;

@Service
public class JiraAuthProvider extends BaseAuthProvider {

    @Value("${jira.ui.endpoint}")
    private String jiraUiEndpoint;

    @Value("${jira.api.endpoint}")
    private String jiraApiEndpoint;

    @Value("${jira.api.timeout}")
    private long jiraApiTimeout;

    @Override
    public Session authImpl(HttpServletRequest request, HttpServletResponse response) {
        try {
            response.sendRedirect(getLoginUrl(request));
        } catch (IOException e) {
            logger.warn("Unable to send redirect to Jira login page", e);
            return null;
        }
        return null;
    }

    @Override
    public RedirectResponse redirectNotAuthTo(HttpServletRequest request) {
        return new RedirectResponse(getLoginUrl(request), "retpath");
    }

    @Override
    public boolean isAuthenticated(HttpServletRequest request) throws UnauthorizedException {
        JiraUser jiraUser;
        try {
            jiraUser = HttpClientBuilder.builder(jiraApiEndpoint, jiraApiTimeout, request).build().
                    create(JiraRestAuthClient.class).getSelfUser().execute().body();
        } catch (Exception e){
            logger.warn("Couldn't get users session from Jira", e);
            return false;
        }
        return jiraUser != null;
    }

    @Override
    public void doAuthByOnetimeToken(String token, HttpServletRequest request, HttpServletResponse response) throws Exception {
        throw new UnsupportedOperationException("Jira doesn't allow one-time tokens authentication");
    }

    @Override
    public Set<String> suggestGroups(String literal) {
        return null;
    }

    @Override
    public Set<String> getAllUsers() {
        return null;
    }

    @Override
    public Set<String> suggestUser(String literal) {
        return null;
    }

    private String getLoginUrl(HttpServletRequest request){
        String redirectUrl = jiraUiEndpoint + "/login.jsp";
        String retpath = request.getParameter("retpath");
        if (retpath != null){
            redirectUrl+="?os_destination=" + retpath;
        }
        return redirectUrl;
    }
}
