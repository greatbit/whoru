package ru.greatbit.whoru.auth.providers;

import ru.greatbit.whoru.auth.*;
import ru.greatbit.whoru.auth.error.UnauthorizedException;
import ru.greatbit.whoru.auth.utils.HttpUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static java.lang.String.format;
import static ru.greatbit.utils.string.StringUtils.emptyIfNull;
import static ru.greatbit.whoru.auth.utils.HttpUtils.TOKEN_KEY;
import static ru.greatbit.whoru.auth.utils.HttpUtils.getTokenValueFromHeaders;
import static ru.greatbit.whoru.auth.utils.HttpUtils.isTokenAccessRequest;
import static org.springframework.util.StringUtils.isEmpty;


public abstract class BaseAuthProvider implements AuthProvider {

    public static final String IP_HEADER = "X-Real-IP";
    public static final String PARAM_LOGIN = "login";
    public static final String PARAM_PASSWORD = "password";

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    protected SessionProvider sessionProvider;

    @Value("${auth.domain}")
    protected String authDomain;

    @Value("${auth.session.ttl}")
    protected int sessionTtl;

    @Value("${auth.admin.login}")
    protected String adminLogin;

    @Value("${auth.admin.password}")
    protected String adminPassword;

    @Value("${auth.admin.token}")
    protected String adminToken;

    @Value("${auth.login.page.url:/login}")
    protected String loginPageUrl;

    @Value("${auth.create.user.page.url:/user/create}")
    protected String createUserPageUrl;

    @Value("${auth.all.users.page.url:/user}")
    protected String allUsersPageUrl;

    @Value("${auth.change.password.page.url:/user/changepass}")
    protected String changePasswordUrl;

    @Override
    public Session doAuth(HttpServletRequest request, HttpServletResponse response){
        final String login = emptyIfNull(request.getParameter(PARAM_LOGIN));
        final String password = emptyIfNull(request.getParameter(PARAM_PASSWORD));
        final String token = emptyIfNull(request.getHeader(TOKEN_KEY));
        if ((login.equals(adminLogin) && password.equals(adminPassword)) || token.equals(adminToken)){
            Session adminSession = (Session) new Session().withIsAdmin(true).
                    withId(
                            isEmpty(token) ? UUID.randomUUID().toString() : token
                    ).
                    withLogin(adminLogin).withName(adminLogin).
                    withPerson(
                            new Person().withActive(true).withId(adminLogin).withFirstName(adminLogin)
                    );
            sessionProvider.addSession(adminSession);
            if (response != null){
                response.addCookie(HttpUtils.createCookie(HttpUtils.SESSION_ID, adminSession.getId(), authDomain, sessionTtl));
            }
            return adminSession;
        } else {
            Session session = authImpl(request, response);
            response.addCookie(HttpUtils.createCookie(HttpUtils.SESSION_ID, session.getId(), authDomain, sessionTtl));
            return session;
        }
    }

    public abstract Session authImpl(HttpServletRequest request, HttpServletResponse response);



    @Override
    public void doLogout(HttpServletRequest request, HttpServletResponse response) {
        try {
            if (isTokenAccessRequest(request)) {
                String id = getTokenValueFromHeaders(request);
                sessionProvider.removeSession(id);
                return;
            }

            Cookie sid = HttpUtils.findCookie(request, HttpUtils.SESSION_ID);
            final Cookie cookie = new Cookie(HttpUtils.SESSION_ID, null);
            cookie.setPath("/");
            cookie.setMaxAge(0);
            cookie.setDomain(authDomain);
            cookie.setHttpOnly(false);
            response.addCookie(cookie);
            sessionProvider.removeSession(sid.getValue());
        } catch (Exception e) {
            logger.error("Failed to logout", e);
        }
    }

    @Override
    public boolean isAuthenticated(HttpServletRequest request) throws UnauthorizedException {
        if (isTokenAccessRequest(request)) {
            String id = getTokenValueFromHeaders(request);
            return sessionProvider.sessionExists(id);
        }
        Cookie sid = HttpUtils.findCookie(request, HttpUtils.SESSION_ID);
        return sid != null && sessionProvider.sessionExists(sid.getValue());
    }


    @Override
    public Session getSession(HttpServletRequest request) throws UnauthorizedException {
        if (isTokenAccessRequest(request)) {
            String id = getTokenValueFromHeaders(request);
            if (sessionProvider.sessionExists(id)) {
                return sessionProvider.getSessionById(id);
            } else {
                throw new UnauthorizedException("Can't get session");
            }
        }

        Cookie sid = HttpUtils.findCookie(request, HttpUtils.SESSION_ID);
        if (sid != null && sessionProvider.sessionExists(sid.getValue())){
            return sessionProvider.getSessionById(sid.getValue());
        }
        throw new UnauthorizedException("Can't get session");
    }

    @Override
    public boolean verifyLogin(HttpServletRequest request, String login) {
        return !isEmpty(login) && login.equals(getSession(request).getLogin());
    }

    protected void sendRedirect(HttpServletRequest request, HttpServletResponse response) throws IOException {
        final String retPath = request.getParameterMap().containsKey("retpath") ? request.getParameter("retpath") : "";
        if (!isEmpty(retPath)) {
            response.sendRedirect(retPath);
        }
    }

    @Override
    public RedirectResponse redirectCreateUserTo(HttpServletRequest request) {
        return new RedirectResponse(createUserPageUrl, "retpath", false);
    }

    @Override
    public RedirectResponse redirectNotAuthTo(HttpServletRequest request) {
        return new RedirectResponse(loginPageUrl, "retpath", false);
    }

    @Override
    public RedirectResponse redirectViewAllUsersTo(HttpServletRequest request) {
        return new RedirectResponse(allUsersPageUrl, "retpath", false);
    }

    @Override
    public RedirectResponse redirectChangePasswordTo(HttpServletRequest request) {
        return new RedirectResponse(changePasswordUrl, "retpath", false);
    }

    @Override
    public void doAuthByOnetimeToken(String token, HttpServletRequest request, HttpServletResponse response) throws Exception {

    }

    @Override
    public Set<String> suggestGroups(HttpServletRequest request, String literal) {
        return null;
    }

    @Override
    public Set<String> getAllUsers(HttpServletRequest request) {
        return null;
    }

    @Override
    public Set<String> suggestUser(HttpServletRequest request, String literal) {
        return null;
    }

    @Override
    public Set<String> getAllGroups(HttpServletRequest request) {
        return new HashSet<>();
    }
}
