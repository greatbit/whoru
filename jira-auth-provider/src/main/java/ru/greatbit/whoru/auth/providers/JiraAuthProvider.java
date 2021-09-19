package ru.greatbit.whoru.auth.providers;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.greatbit.whoru.auth.Person;
import ru.greatbit.whoru.auth.RedirectResponse;
import ru.greatbit.whoru.auth.Session;
import ru.greatbit.whoru.auth.error.UnauthorizedException;
import ru.greatbit.whoru.auth.providers.jira.Group;
import ru.greatbit.whoru.auth.providers.jira.JiraUser;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toSet;
import static org.springframework.util.StringUtils.isEmpty;
import static ru.greatbit.utils.string.StringUtils.emptyIfNull;

@Service
public class JiraAuthProvider extends BaseAuthProvider {

    @Value("${jira.ui.endpoint}")
    private String jiraUiEndpoint;

    @Value("${jira.api.endpoint}")
    private String jiraApiEndpoint;

    @Value("${jira.api.timeout}")
    private long jiraApiTimeout;

    @Value("${auth.admin.logins}")
    private String adminLogins;

    private Set<String> adminLoginsSet = new HashSet<>();


    @PostConstruct
    private void postConstruct(){
        adminLoginsSet = Stream.of(emptyIfNull(adminLogins).split(",")).
                map(String::trim).
                collect(toSet());
        if (!isEmpty(adminLogin)){
            adminLoginsSet.add(adminLogin);
        }
    }

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
        return new RedirectResponse(getLoginUrl(request), "retpath", false);
    }

    @Override
    public boolean isAuthenticated(HttpServletRequest request) throws UnauthorizedException {
        try {
            return getJiraUser(request) != null;
        } catch (IOException e) {
            logger.warn("Couldn't get users session from Jira", e);
            return false;
        }
    }

    @Override
    public Session getSession(HttpServletRequest request) throws UnauthorizedException {
        try {
            JiraUser jiraUser = getJiraUser(request);
            return (Session) new Session().
                    withId(UUID.randomUUID().toString()).
                    withLogin(jiraUser.getName()).withName(jiraUser.getDisplayName()).
                    withIsAdmin(isJiraUserAdmin(jiraUser)).
                    withPerson(
                        new Person().withActive(true).
                                withId(jiraUser.getName()).
                                withFirstName(jiraUser.getDisplayName()).
                                withGroups(jiraUser.getGroups().getItems())
                    );
        } catch (IOException e) {
            throw new UnauthorizedException("Couldn't get users session from Jira", e);
        }
    }

    private boolean isJiraUserAdmin(JiraUser jiraUser) {
        return adminLoginsSet.contains(jiraUser.getName());
    }

    @Override
    public void doAuthByOnetimeToken(String token, HttpServletRequest request, HttpServletResponse response) throws Exception {
        throw new UnsupportedOperationException("Jira doesn't allow one-time tokens authentication");
    }

    @Override
    public Set<String> suggestGroups(HttpServletRequest request, String literal) {
        try {
            return getCLient(request).getGroups(literal).execute().body().getGroups().stream().
                    map(Group::getName).collect(toSet());
        } catch (IOException e) {
            return Collections.emptySet();
        }
    }

    @Override
    public Set<String> getAllUsers(HttpServletRequest request) {
        return suggestUser(request, "");
    }

    @Override
    public Set<String> suggestUser(HttpServletRequest request, String literal) {
        try {
            return getCLient(request).getUsers(literal).execute().body().stream().
                    map(JiraUser::getKey).
                    collect(Collectors.toSet());
        } catch (IOException e) {
            return Collections.emptySet();
        }
    }

    private String getLoginUrl(HttpServletRequest request){
        String redirectUrl = jiraUiEndpoint + "/login.jsp";
        String retpath = request.getParameter("retpath");
        if (retpath != null){
            redirectUrl+="?os_destination=" + retpath;
        }
        return redirectUrl;
    }

    private JiraUser getJiraUser(HttpServletRequest request) throws IOException {
        return getCLient(request).getSelfUser().execute().body();
    }

    private JiraRestAuthClient getCLient(HttpServletRequest request){
        return  HttpClientBuilder.builder(jiraApiEndpoint, jiraApiTimeout, request).build().
                create(JiraRestAuthClient.class);
    }
}
