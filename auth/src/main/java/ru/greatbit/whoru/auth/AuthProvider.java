package ru.greatbit.whoru.auth;

import ru.greatbit.whoru.auth.error.UnauthorizedException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface AuthProvider {

    /**
     * Perform authentication
     */
    Session doAuth(HttpServletRequest request, HttpServletResponse response);

    /**
     * Perform logging out
     */
    void doLogout(HttpServletRequest request, HttpServletResponse response);

    /**
     * Verify that session is still valid
     */
    boolean isAuthenticated(HttpServletRequest request) throws UnauthorizedException;

    /**
     * Extract session from provider when user is not yet recognized
     */
    Session getSession(HttpServletRequest request) throws UnauthorizedException;

    /**
     * Redirect to a URI if request not authenticated. E.g. - OAUTH login page.
     */
    RedirectResponse redirectNotAuthTo(HttpServletRequest request);

    /**
     * Verify that username is still the same as the login within the session
     */
    boolean verifyLogin(HttpServletRequest request, String login);

    void doAuthByOnetimeToken(String token, HttpServletRequest request, HttpServletResponse response) throws Exception;
}

