package ru.greatbit.whoru.auth.providers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.greatbit.whoru.auth.Session;
import ru.greatbit.whoru.auth.error.UnauthorizedException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Collections;
import java.util.Set;

import static java.lang.String.format;
import static ru.greatbit.whoru.auth.providers.Command.REQUEST_TOKEN;

@Service
public class JiraAuthProvider extends BaseAuthProvider {

    @Autowired
    private JiraOAuthClient jiraOAuthClient;

    @Override
    public Session authImpl(HttpServletRequest request, HttpServletResponse response) {
        String requestToken;
        try {
            if (requestContainsRequestTokenVerificationCode(request)){
                //ToDo: getRequestCode and request user /rest/api/2/myself, persist in ession
            }
            requestToken = jiraOAuthClient.getAndAuthorizeTemporaryToken();
            String redirectUrl = jiraOAuthClient.getRedirectOauthUrl(requestToken);
            response.sendRedirect(redirectUrl);
        } catch (Exception e) {
            throw new UnauthorizedException("Unable to retrieve session from Jira", e);
        }
        return null;
    }

    private boolean requestContainsRequestTokenVerificationCode(HttpServletRequest request) {
        //Todo: implement
        return false;
    }

    @Override
    public boolean isAuthenticated(HttpServletRequest request) throws UnauthorizedException {
        return super.isAuthenticated(request);
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
}
