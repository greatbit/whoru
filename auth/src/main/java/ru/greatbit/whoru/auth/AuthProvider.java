package ru.greatbit.whoru.auth;

import ru.greatbit.whoru.auth.error.UnauthorizedException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Set;

public interface AuthProvider {

    /**
     * Perform authentication
     * @param request
     * @param response
     * @return
     */
    Session doAuth(HttpServletRequest request, HttpServletResponse response);

    /**
     * Perform logging out
     * @param request
     * @param response
     */
    void doLogout(HttpServletRequest request, HttpServletResponse response);

    /**
     * Verify that session is still valid
     * @param request
     * @return
     * @throws UnauthorizedException
     */
    boolean isAuthenticated(HttpServletRequest request) throws UnauthorizedException;

    /**
     * Extract session from provider when user is not yet recognized
     * @param request
     * @return
     * @throws UnauthorizedException
     */
    Session getSession(HttpServletRequest request) throws UnauthorizedException;

    /**
     * Redirect to a URI if request not authenticated. E.g. - OAUTH login page.
     * @param request
     * @return
     */
    RedirectResponse redirectNotAuthTo(HttpServletRequest request);

    /**
     * Redirect to a URI with user creation form.
     * @param request
     * @return
     */
    RedirectResponse redirectCreateUserTo(HttpServletRequest request);

    /**
     * Redirect to a URI with list of user.
     * @param request
     * @return
     */
    RedirectResponse redirectViewAllUsersTo(HttpServletRequest request);

    /**
     * Verify that username is still the same as the login within the session
     * @param request
     * @param login
     * @return
     */
    boolean verifyLogin(HttpServletRequest request, String login);

    /**
     * A possibility to login using one-time token. E.g. - autologin link from the e-mail
     * @param token
     * @param request
     * @param response
     * @throws Exception
     */
    void doAuthByOnetimeToken(String token, HttpServletRequest request, HttpServletResponse response) throws Exception;

    /**
     * Get all user groups
     * @param request
     * @return
     */
    Set<String> getAllGroups(HttpServletRequest request);

    /**
     * Find groups that match the literal. Used for suggestions in text-fields.
     * @param request
     * @param literal
     * @return
     */
    Set<String> suggestGroups(HttpServletRequest request, String literal);

    /**
     * Get all users
     * @param request
     * @return
     */
    Set<String> getAllUsers(HttpServletRequest request);

    /**
     * Find users that match the literal. Used for suggestions in text-fields.
     * @param request
     * @param literal
     * @return
     */
    Set<String> suggestUser(HttpServletRequest request, String literal);
}

