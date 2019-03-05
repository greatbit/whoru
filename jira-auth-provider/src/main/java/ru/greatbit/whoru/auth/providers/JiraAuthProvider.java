package ru.greatbit.whoru.auth.providers;

import ru.greatbit.whoru.auth.Session;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Set;

public class JiraAuthProvider extends BaseAuthProvider {
    @Override
    public Session authImpl(HttpServletRequest request, HttpServletResponse response) {
        return null;
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
